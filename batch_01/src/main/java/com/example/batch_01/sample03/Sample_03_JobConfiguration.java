package com.example.batch_01.sample03;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@RequiredArgsConstructor
public class Sample_03_JobConfiguration {

    @Bean
    public Job sample03(JobRepository jobRepository, Step jobStep, Step scopeStep02) {
        return new JobBuilder("sample03", jobRepository)
                .start(jobStep) // null로 넣으면 DI로 알아서 주입
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jobStep(JobLauncher jobLauncher,JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jobStep", jobRepository)
                .job(childJob(jobRepository,step1(jobRepository,transactionManager)))
                .launcher(jobLauncher)
                .parametersExtractor(jobParametersExtractor())
                // 리스너를 통해서 Step이 시작하기 전에 Step의 ExecutionContext에 name과 backtony 키밸류값 등록
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().putString("name", "backtony");
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return null;
                    }
                })
                .build();
    }

    // Step의 ExecutionContext에 name의 key를 갖는 키밸류 값을 꺼내서 
    // JobStep의 JobParameters로 만들어서 넘긴다.
    private JobParametersExtractor jobParametersExtractor() {
        DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
        extractor.setKeys(new String[]{"name"});
        return extractor;
    }

    @Bean
    public Job childJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("childJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    JobParameters jobParameters = stepContribution.getStepExecution().getJobExecution().getJobParameters();
                    System.out.println(jobParameters.getString("name"));
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
}