package com.example.batch_01.sample08;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class Sample08JobConfiguration {

    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job sample08() {
        return new JobBuilder("sample08", jobRepository)
                .start(sample_08_flowStep01())
                .next(sample_08_step02())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    // 스텝이지만 flow를 요소로 갖고 있는 flowStep
    @Bean
    public Step sample_08_flowStep01() {
        return new StepBuilder("sample_08_flowStep01",jobRepository)
                .flow(flow())
                .build();
    }

    @Bean
    public Flow flow() {
        FlowBuilder<Flow> builder = new FlowBuilder<>("sample_08_flow01");
        builder.start(sample_08_step01())
                .end();
        return builder.build();
    }


    @Bean
    public Step sample_08_step01() {
        return new StepBuilder("sample_08_step01",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample_08_step1 completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

    @Bean
    public Step sample_08_step02() {
        return new StepBuilder("sample_08_step02",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("sample_08_step02 completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
}