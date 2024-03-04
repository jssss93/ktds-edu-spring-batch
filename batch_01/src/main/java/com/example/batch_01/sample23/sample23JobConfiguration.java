package com.example.batch_01.sample23;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class sample23JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    @Bean
    public Job job(JobRepository jobRepository) {
        return new JobBuilder("sample23", jobRepository)
                .start(splitFlow())
                .next(sample23_step4())
                .build()        //builds FlowJobBuilder instance
                .build();       //builds Job instance
    }
    @Bean
    public Flow splitFlow() {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(sample23_taskExecutor())
                .add(flow1(), flow2())
                .build();
    }
    @Bean
    public Flow flow1() {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(sample23_step1())
                .next(sample23_step2())
                .build();
    }
    @Bean
    public Flow flow2() {
        return new FlowBuilder<SimpleFlow>("flow2")
                .start(sample23_step3())
                .build();
    }
    @Bean
    public Step sample23_step1() {
        return new StepBuilder("sample23_step1",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample23_step1");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
    @Bean
    public Step sample23_step2() {
        return new StepBuilder("sample23_step2",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample23_step2");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
    @Bean
    public Step sample23_step3() {
        return new StepBuilder("sample23_step3",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample23_step3");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
    @Bean
    public Step sample23_step4() {
        return new StepBuilder("sample23_step4",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample23_step4");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
    @Bean
    public TaskExecutor sample23_taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }
}