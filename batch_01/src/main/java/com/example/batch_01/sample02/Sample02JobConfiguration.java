package com.example.batch_01.sample02;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Sample02JobConfiguration {

    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job sample02() {
        return new JobBuilder("sample02", jobRepository)
                .start(sample02_step01())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sample02_step01() {
        return new StepBuilder("sample02_step01",jobRepository)
                .tasklet(new CustomTasklet(),transactionManager)
                .startLimit(3) // sample02_step01 3번만 실행이 가능하다.
                .build();
    }
}