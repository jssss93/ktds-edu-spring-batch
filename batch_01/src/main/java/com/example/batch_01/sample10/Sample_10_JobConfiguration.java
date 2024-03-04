package com.example.batch_01.sample10;

import com.example.batch_01.sample09.CustomStepListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class Sample_10_JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job sample10() {
        return new JobBuilder("sample10", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(sample10_step01())
                .listener(new CustomJobExecutionListener())
                //.listener(new CustomJobAnnotationExecutionListener()) // 애노테이션 방식
                .build();
    }
    @Bean
    public Step sample10_step01() {
        return new StepBuilder("sample10_step01",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample10_step01 completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .listener(new CustomStepListener())
                //.listener(new CustomStepAnnotationExecutionListener())
                .build();
    }
}