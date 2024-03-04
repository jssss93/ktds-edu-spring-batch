package com.example.batch_01.sample05;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class CustomDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

        int randNum = (int) (Math.random() * 10) + 1;
        System.out.println("랜덤숫자 : "+randNum);
        if (randNum % 2 == 0) {
            System.out.println("짝수입니다!!!");
            return new FlowExecutionStatus("EVEN");
        } else {
            System.out.println("홀수입니다!!!");
            return new FlowExecutionStatus("ODD");
        }
    }
}