package com.example.batch_01.sample13;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class Sample13JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final CustomItemReader customItemReader;
    private int chunkSize = 5;

    @Bean
    public Job helloJob() {
        return new JobBuilder("sample13", jobRepository)
                .start(sample13_step01())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sample13_step01() {
        return new StepBuilder("sample13_step01",jobRepository)
                .<String, String>chunk(chunkSize,transactionManager)
                .reader(customItemReader)
                .writer(items -> System.out.println("items = " + items))
                .faultTolerant()
                .skip(CustomException.class)//해
                .skipLimit(4)
                .build();
    }


}