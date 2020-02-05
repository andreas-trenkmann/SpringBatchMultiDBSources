package net.trenkmann.dev.listener;

import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.metrics.BatchMetrics;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProtocolListener implements JobExecutionListener {

  private static final String BRACKETS = "+++++++++++++++++++++++++++++++++++++++++++++++++++++++";

  public void afterJob(JobExecution jobExecution) {
    StringBuilder protocol = new StringBuilder();
    protocol.append("\n" + BRACKETS + " \n")
    .append("Protocol for ").append(jobExecution.getJobInstance().getJobName()).append(" \n")
    .append("  Started     : " + jobExecution.getStartTime() + "\n")
    .append("  Finished    : " + jobExecution.getEndTime() + "\n")
    .append(
        "  Duration    : "
            + BatchMetrics.calculateDuration(jobExecution.getStartTime(), jobExecution.getEndTime())
                .toString()
            + "\n")
    .append("  Exit-Code   : " + jobExecution.getExitStatus().getExitCode() + "\n")
    .append("  Exit-Descr. : " + jobExecution.getExitStatus().getExitDescription() + "\n")
    .append("  Status      : " + jobExecution.getStatus() + "\n")
    .append(BRACKETS + " \n")

    .append("Job-Parameter: \n");
    JobParameters jp = jobExecution.getJobParameters();
    for (Entry<String, JobParameter> entry : jp.getParameters().entrySet()) {
      protocol.append("  " + entry.getKey() + "=" + entry.getValue() + "\n");
    }
    protocol.append(BRACKETS + " \n");

    for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
      protocol.append("\n" + BRACKETS + " \n")
      .append("Step " + stepExecution.getStepName() + " \n")
      .append("WriteCount: " + stepExecution.getWriteCount() + "\n")
      .append("Commits: " + stepExecution.getCommitCount() + "\n")
      .append("SkipCount: " + stepExecution.getSkipCount() + "\n")
      .append("Rollbacks: " + stepExecution.getRollbackCount() + "\n")
      .append("Filter: " + stepExecution.getFilterCount() + "\n")
      .append(BRACKETS + " \n");
    }
    log.info(protocol.toString());
  }

  public void beforeJob(JobExecution arg0) {
    // nothing to do
  }
}
