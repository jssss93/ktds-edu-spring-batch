package com.example.batch_01.sample07;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
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

@Configuration
@RequiredArgsConstructor
public class Sample_07JobConfiguration {

    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job sample07() {
        return new JobBuilder("sample07", jobRepository)
                .start(sample_07_flow01())
                    .on("FAILED")
                    .to(sample_07_flow02())
                .end()
                .build();
    }

    @Bean
    public Flow sample_07_flow01() {
        FlowBuilder<Flow> builder = new FlowBuilder<>("sample_07_flow01");
        builder.start(sample_07_step01())
                .end();
        return builder.build();
    }

    @Bean
    public Flow sample_07_flow02() {
        FlowBuilder<Flow> builder = new FlowBuilder<>("sample_07_flow02");
        builder.start(sample_07_step02())
                .end();
        return builder.build();
    }


    @Bean
    public Step sample_07_step01() {
        return new StepBuilder("sample_07_step01",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample_07_step01 completed");
                    stepContribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

    @Bean
    public Step sample_07_step02() {
        return new StepBuilder("sample_07_step02",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample_07_step02 completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

}