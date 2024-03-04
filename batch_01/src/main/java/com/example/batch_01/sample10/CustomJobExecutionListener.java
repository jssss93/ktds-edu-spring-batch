package com.example.batch_01.sample10;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class CustomJobExecutionListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("job name : " + jobExecution.getJobInstance().getJobName() + " start");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long startTime = jobExecution.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTime = jobExecution.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long executionTime = TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);
        System.out.println("job name : " + jobName  + " end : "+ " execution time : "+executionTime+"s");
    }
}