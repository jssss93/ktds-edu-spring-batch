package com.example.batch_01.sample01;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class CustomJobParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if(parameters.getLong("run.id") != null){
            System.out.println(parameters.getLong("run.id"));
        }else{
            throw new JobParametersInvalidException("run.id parameters is not found");
        }
    }
}