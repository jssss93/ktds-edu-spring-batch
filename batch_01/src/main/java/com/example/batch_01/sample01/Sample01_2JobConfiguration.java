package com.example.batch_01.sample01;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class Sample01_2JobConfiguration {
    private static final Logger log = LoggerFactory.getLogger(Sample01_2JobConfiguration.class);

    @Bean
    public Job scopeJob(JobRepository jobRepository, Step scopeStep01, Step scopeStep02) {
        return new JobBuilder("scopeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(scopeStep01)
                .next(scopeStep02)
                .build();
    }

    @Bean
    @JobScope
    public Step scopeStep01(@Value("#{jobParameters[requestDate]}") String requestDate
            ,JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("taskletStep", jobRepository)
                .tasklet((contribution, chunckContext)->{
                    System.out.println("scopeStep01Tasklet requestDate : "+requestDate);
                    log.info("scopeStep01Tasklet requestDate : {}",requestDate);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step scopeStep02(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("taskletStep", jobRepository)
                .tasklet(scopeStep02Tasklet(null), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet scopeStep02Tasklet(@Value("#{jobParameters[requestDate]}") String requestDate){
        return(contribution, chunckContext)->{
            System.out.println("scopeStep01Tasklet requestDate : "+requestDate);
            return RepeatStatus.FINISHED;
        };

    }

}
