package com.example.demo.sample00.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@Component
public class CustomJobAnnotationExecutionListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("job name : " + jobExecution.getJobInstance().getJobName() + " start");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long startTime = jobExecution.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTime = jobExecution.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long executionTime = TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);

        System.out.println("job name : " + jobName  + " end : "+ " execution time : "+executionTime+"s");

    }
}