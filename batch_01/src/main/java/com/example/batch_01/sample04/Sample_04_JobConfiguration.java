package com.example.batch_01.sample04;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
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
public class Sample_04_JobConfiguration {

    @Bean
    public Job sample04_01(JobRepository jobRepository, Step jobStep, Step scopeStep02,PlatformTransactionManager transactionManager) {
        return new JobBuilder("sample04_01", jobRepository)
                .start(sample04_step1(jobRepository,transactionManager))
                .on("FAILED")
                .to(sample04_step2(jobRepository,transactionManager))
                .on("PASS")
                .stop()
                .end() // SimpleFlow 객체 생성
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Job sample04_02(JobRepository jobRepository, Step jobStep, Step scopeStep02,PlatformTransactionManager transactionManager) {
        return new JobBuilder("sample04_02", jobRepository)
                .start(sample04_step1(jobRepository,transactionManager))
                    .on("COMPLETED")
                    .to(sample04_step2(jobRepository,transactionManager))
                    .on("FAILED")
                    .to(sample04_step3(jobRepository,transactionManager))
                .from(sample04_step1(jobRepository,transactionManager))
                    .on("FAILED")
                    .end()
                .from(sample04_step2(jobRepository,transactionManager))
                    .on("COMPLETED")
                    .to(sample04_step4(jobRepository,transactionManager))
                    .next(sample04_step5(jobRepository,transactionManager))
                .end() // SimpleFlow 객체 생성
                .incrementer(new RunIdIncrementer())
                .build();
    }
    @Bean
    public Step sample04_step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sample04_step1",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("step1 completed");
                    stepContribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

    @Bean
    public Step sample04_step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sample04_step2",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample04_step2 completed");
                    stepContribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .listener(new PassCheckListener()) // 리스너 추가
                .build();
    }

    @Bean
    public Step sample04_step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sample04_step3",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample04_step3 completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .listener(new PassCheckListener()) // 리스너 추가
                .build();
    }

    @Bean
    public Step sample04_step4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sample04_step4",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample04_step4 completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .listener(new PassCheckListener()) // 리스너 추가
                .build();
    }

    @Bean
    public Step sample04_step5(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sample04_step5",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample04_step5 completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .listener(new PassCheckListener()) // 리스너 추가
                .build();
    }
}