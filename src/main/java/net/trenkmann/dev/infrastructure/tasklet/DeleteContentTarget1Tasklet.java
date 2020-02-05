package net.trenkmann.dev.infrastructure.tasklet;

import net.trenkmann.dev.repository.target1.Target1Repository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteContentTarget1Tasklet implements Tasklet {

  @Autowired Target1Repository target1Repository;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    target1Repository.deleteAll();
    return RepeatStatus.FINISHED;
  }
}
