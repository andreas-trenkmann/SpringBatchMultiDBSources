package net.trenkmann.dev.config;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
      BatchConfiguration.class,
      FlywayConfiguration.class,
      SourceDataConfiguration.class,
      Target1DataConfiguration.class,
      Target2DataConfiguration.class
    })
//@TestPropertySource(locations = "classpath:environment.properties")
@Slf4j
public class BatchApplicationTestIT {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @BeforeEach
  public void clearJobExecutions() {

  }

//  @BeforeAll
//  void setup() {
//    jobLauncherTestUtils = new JobLauncherTestUtils();
//  }

  @Test
  public void testMyJob() throws Exception {
    // given
    JobParameters jobParameters = this.jobLauncherTestUtils.getUniqueJobParameters();

    // when
    JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(jobParameters);

    // then
    Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
  }


  /** Test for the entire Job with all steps a small subset of accountProtocol-Data */
  @Test
  public void jobLaunchTest() throws Exception {

    // Next is a complete Job Execution to test the entire step-chain with small setup
    HashMap<String, JobParameter> parameterMapOverall = new HashMap<>();
    parameterMapOverall.put("start.id", new JobParameter(1900000L, false));
    parameterMapOverall.putAll(jobLauncherTestUtils.getUniqueJobParameters().getParameters());
    JobParameters jobParametersOverall = new JobParameters(parameterMapOverall);

    JobExecution jobExecutionOverAll = jobLauncherTestUtils.launchJob(jobParametersOverall);
    log.info(
        "Execution Time {} Seconds in Minutes {}",
        (jobExecutionOverAll.getEndTime().getTime()
                - jobExecutionOverAll.getStartTime().getTime())
            / 1000,
        (jobExecutionOverAll.getEndTime().getTime()
                - jobExecutionOverAll.getStartTime().getTime())
            / (1000 * 60));

    Assertions.assertEquals("COMPLETED", jobExecutionOverAll.getExitStatus().getExitCode());
  }
}
