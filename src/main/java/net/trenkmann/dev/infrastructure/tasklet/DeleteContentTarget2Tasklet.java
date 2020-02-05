package net.trenkmann.dev.infrastructure.tasklet;

import net.trenkmann.dev.repository.target2.Target2Repository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteContentTarget2Tasklet implements Tasklet {

  @Autowired Target2Repository target2Repository;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    target2Repository.deleteAll();
    return RepeatStatus.FINISHED;
  }
}
