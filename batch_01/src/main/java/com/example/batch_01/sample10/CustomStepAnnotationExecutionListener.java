package com.example.batch_01.sample10;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class CustomStepAnnotationExecutionListener {

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("step name : " + stepExecution.getStepName() + " start");
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        String stepName = stepExecution.getStepName();
        long startTime = stepExecution.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTime = stepExecution.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long executionTime = TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);

        System.out.println("step name : " + stepName  + " end : "+ " execution time : "+executionTime+"s");

    }
}