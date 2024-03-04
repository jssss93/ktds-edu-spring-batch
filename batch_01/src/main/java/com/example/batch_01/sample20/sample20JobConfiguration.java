package com.example.batch_01.sample20;

import com.example.batch_01.sample16.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class sample20JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private int chunkSize = 5;

    @Bean
    public Job sample20() {
        return new JobBuilder("sample20", jobRepository)
                .start(sample20_step01())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sample20_step01() {
        return new StepBuilder("sample20_step01",jobRepository)
                .<String, String>chunk(chunkSize,transactionManager)
                .reader(sample20_customItemReader())
                .processor(sample20_customItemProcessor())
                .writer(items -> System.out.println("items = " + items))
                .faultTolerant()
                .build();
    }

    @Bean
    public ItemReader<String> sample20_customItemReader() {
        return new ItemReader<String>() {
            int i = 0;

            @Override
            public String read() throws RetryableException {
                i++;
                System.out.println("itemReader = " + i);
                return i > 5 ? null : String.valueOf(i);
            }
        };
    }

    @Bean
    public ItemProcessor<? super String, String> sample20_customItemProcessor() {
        return new CustomItemProcessor_20(retryTemplate());
    }

    @Bean
    public RetryTemplate retryTemplate(){
        // retry 적용할 Exception Map에 담기
        Map<Class<? extends Throwable>,Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class, true); // true : retry , false : noRetry

        // retryLimit과 Exception 담은 map을 인수로
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2,exceptionClass);

        // retry 시도 간격
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000); // 2초

        // retryTemplate 생성 및 세팅
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        //retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}