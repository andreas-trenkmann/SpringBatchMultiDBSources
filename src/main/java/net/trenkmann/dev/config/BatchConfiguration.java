package net.trenkmann.dev.config;

import javax.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import net.trenkmann.dev.infrastructure.process.SourceItemTransferToTarget1Processor;
import net.trenkmann.dev.infrastructure.process.SourceItemTransferToTarget2Processor;
import net.trenkmann.dev.infrastructure.tasklet.DeleteContentTarget1Tasklet;
import net.trenkmann.dev.infrastructure.tasklet.DeleteContentTarget2Tasklet;
import net.trenkmann.dev.infrastructure.writer.Target1ItemWriter;
import net.trenkmann.dev.infrastructure.writer.Target2ItemWriter;
import net.trenkmann.dev.listener.ProtocolListener;
import net.trenkmann.dev.model.source.SourceEntity;
import net.trenkmann.dev.model.target1.Target1Entity;
import net.trenkmann.dev.model.target2.Target2Entity;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableTransactionManagement
@ComponentScan(basePackages = {"net.trenkmann.dev.listener", "net.trenkmann.dev.*"})
@PropertySource(value = "classpath:/application.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:./application.properties", ignoreResourceNotFound = true)
public class BatchConfiguration {

  private static final int TRANSACTION_TIMEOUT_IN_SECONDS = 60;
  private static final int CHUNK_SIZE = 50;
  private static final int THREADS = 15;

  @Autowired public StepBuilderFactory stepBuilderFactory;

  @Autowired
  @Qualifier("sourceEntityManagerFactory")
  private EntityManagerFactory sourceEntityManagerFactory;

  @Autowired
  @Qualifier("target1Writer")
  private ItemWriter<Target1Entity> target1Writer;

  @Autowired
  @Qualifier("target2Writer")
  private ItemWriter<Target2Entity> target2Writer;

  /** Simple executor for parallel execution of processors within a step */
  @Bean("taskExecutor1")
  public TaskExecutor taskExecutor() {
    SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
    simpleAsyncTaskExecutor.setConcurrencyLimit(THREADS);
    log.info("limit of concurrency is set to {}", simpleAsyncTaskExecutor.getConcurrencyLimit());
    return simpleAsyncTaskExecutor;
  }

  /**
   * Main-method
   *
   * @param listener Protocol for report at job end
   * @return Job
   */
  @Bean
  public Job exportUserJob(
      ProtocolListener listener,
      JobBuilderFactory jobBuilderFactory,
      Step stepCopyTarget1,
      Step stepCopyTarget2,
      Step stepDeleteContentTarget1,
      Step stepDeleteContentTarget2) {

    log.info("batch Job started");

    return jobBuilderFactory
        .get("batchJob")
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .start(stepDeleteContentTarget1) // target1
        .next(stepCopyTarget1) // target1
        .next(stepDeleteContentTarget2) // target2
        .next(stepCopyTarget2) // target2
        .build();
  }

  /**
   * Delete all tuples in table target1
   *
   * @param transactionManager TransactionManager for this Step
   * @return Step Step definition
   */
  @Bean
  public Step stepDeleteContentTarget1(
      @Qualifier("sourceTransactionManager") PlatformTransactionManager transactionManager,
      Tasklet deleteContentTarget1Tasklet) {
    return stepBuilderFactory
        .get("stepDeleteContentTarget1")
        .allowStartIfComplete(true)
        .transactionManager(transactionManager)
        .tasklet(deleteContentTarget1Tasklet)
        .build();
  }

  /**
   * Copy TB_source to TB_Target1
   *
   * @param transactionManager TransactionManager for this Step
   * @return Step Step definition
   */
  @Bean
  public Step stepCopyTarget1(
      @Qualifier("taskExecutor1") TaskExecutor taskExecutor,
      @Qualifier("target1TransactionManager") PlatformTransactionManager transactionManager,
      ItemProcessor<SourceEntity, Target1Entity> processorTarget1) {

    DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
    attribute.setPropagationBehavior(Propagation.REQUIRED.value());
    attribute.setIsolationLevel(Isolation.DEFAULT.value());
    attribute.setTimeout(TRANSACTION_TIMEOUT_IN_SECONDS);

    return stepBuilderFactory
        .get("stepCopyTarget1")
        .transactionManager(transactionManager)
        .<SourceEntity, Target1Entity>chunk(CHUNK_SIZE)
        .reader(jpaPagingItemReaderSourceItems())
        .processor(processorTarget1)
        .writer(target1Writer)
        .transactionAttribute(attribute)
        .taskExecutor(taskExecutor)
        .build();
  }

  /**
   * Delete all tuples in table target2
   *
   * @param transactionManager TransactionManager for this Step
   * @return Step Step definition
   */
  @Bean
  public Step stepDeleteContentTarget2(
      @Qualifier("target2TransactionManager") PlatformTransactionManager transactionManager,
      Tasklet deleteContentTarget2Tasklet) {
    return stepBuilderFactory
        .get("stepDeleteContentTarget2")
        .transactionManager(transactionManager)
        .tasklet(deleteContentTarget2Tasklet)
        .build();
  }

  /**
   * Copy TB_source to TB_Target2
   *
   * @param transactionManager TransactionManager for this Step
   * @return Step Step definition
   */
  @Bean
  public Step stepCopyTarget2(
      @Qualifier("taskExecutor1") TaskExecutor taskExecutor,
      @Qualifier("target2TransactionManager") PlatformTransactionManager transactionManager,
      ItemProcessor<SourceEntity, Target2Entity> processorTarget2) {

    DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
    attribute.setPropagationBehavior(Propagation.REQUIRED.value());
    attribute.setIsolationLevel(Isolation.DEFAULT.value());
    attribute.setTimeout(TRANSACTION_TIMEOUT_IN_SECONDS);
    return stepBuilderFactory
        .get("stepCopyTarget2")
        .transactionManager(transactionManager)
        .<SourceEntity, Target2Entity>chunk(CHUNK_SIZE)
        .reader(getSourceEntityItemStreamReader())
        .processor(processorTarget2)
        .writer(target2Writer)
        .transactionAttribute(attribute)
        .taskExecutor(taskExecutor)
        .build();
  }

  public ItemStreamReader<SourceEntity> jpaPagingItemReaderSourceItems() {
    return getSourceEntityItemStreamReader();
  }

  private ItemStreamReader<SourceEntity> getSourceEntityItemStreamReader() {
    JpaPagingItemReader<SourceEntity> itemReader = new JpaPagingItemReader<>();
    itemReader.setEntityManagerFactory(sourceEntityManagerFactory);
    itemReader.setQueryString("SELECT s FROM SourceEntity s");
    itemReader.setTransacted(true);
    itemReader.setPageSize(CHUNK_SIZE);
    return itemReader;
  }

  @Bean
  public ItemProcessor<SourceEntity, Target1Entity> processorTarget1() {
    return new SourceItemTransferToTarget1Processor();
  }

  @Bean
  public ItemProcessor<SourceEntity, Target2Entity> processorTarget2() {
    return new SourceItemTransferToTarget2Processor();
  }

  @Bean
  @StepScope
  public ItemWriter<Target1Entity> target1Writer() {
    return new Target1ItemWriter();
  }

  @Bean
  @StepScope
  public ItemWriter<Target2Entity> target2Writer() {
    return new Target2ItemWriter();
  }

  @Bean
  public Tasklet deleteContentTarget1Tasklet() {
    return new DeleteContentTarget1Tasklet();
  }

  @Bean
  public Tasklet deleteContentTarget2Tasklet() {
    return new DeleteContentTarget2Tasklet();
  }
}
