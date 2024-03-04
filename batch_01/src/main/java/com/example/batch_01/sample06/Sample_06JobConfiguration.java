package com.example.batch_01.sample06;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Sample_06JobConfiguration {

    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job sample06() {
        return new JobBuilder("sample06", jobRepository)
                .start(flow1()) // SimpleFlow
                    .on("COMPLETED")
                    .to(flow2()) // SimpleFlow
                .end() // 전부를 포괄하는 SimpleFlow 생성
                .build();
    }

    @Bean
    public Flow flow1() {
        FlowBuilder<Flow> builder = new FlowBuilder<>("flow1");
        builder.start(sample06_step1())
                .end();
        return builder.build();
    }

    @Bean
    public Flow flow2() {
        FlowBuilder<Flow> builder = new FlowBuilder<>("flow2");
        builder.start(sample06_step2())
                .end();
        return builder.build();
    }


    @Bean
    public Step sample06_step1() {
        return new StepBuilder("sample06_step1",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample06_step1 completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

    @Bean
    public Step sample06_step2() {
        return new StepBuilder("sample06_step2",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample06_step2 completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
}