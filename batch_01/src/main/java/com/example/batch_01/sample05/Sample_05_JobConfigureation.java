package com.example.batch_01.sample05;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class Sample_05_JobConfigureation {

    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    @Bean
    public Job sample05() {
            return new JobBuilder("sample05", jobRepository)
                    .incrementer(new RunIdIncrementer())
                .start(step())
                .next(decider()) // step이 완료되면 decider를 실행
                    // decider의 반환값에 따른 분기
                    .from(decider()).on("ODD").to(oddSTep())
                    .from(decider()).on("EVEN").to(evenStep())
                .end() // SimpleFlow 객체 생성
                .build();
    }


    @Bean
    public JobExecutionDecider decider() {
        return new CustomDecider();
    }

    @Bean
    public Step step() {
        return new StepBuilder("step",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("step completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

    @Bean
    public Step evenStep() {
            return new StepBuilder("evenStep",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("even completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }



    @Bean
    public Step oddSTep() {
            return new StepBuilder("oddSTep",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("odd completed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
}