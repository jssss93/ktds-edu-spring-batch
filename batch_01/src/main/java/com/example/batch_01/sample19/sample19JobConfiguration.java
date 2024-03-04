package com.example.batch_01.sample19;

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
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class sample19JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private int chunkSize = 5;

    @Bean
    public Job sample19() {
        return new JobBuilder("sample19", jobRepository)
                .start(sample19_step01())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sample19_step01() {
        return new StepBuilder("sample19_step01",jobRepository)
                .<String, String>chunk(chunkSize,transactionManager)
                .reader(sample19_customItemReader())
                .processor(sample19_customItemProcessor1())
                .writer(items -> System.out.println("items = " + items))
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(2)
                .skip(RetryableException.class)
                .skipLimit(4)
                .build();
    }

    @Bean
    public ItemReader<String> sample19_customItemReader() {
        return new ItemReader<String>() {
            int i = 0;

            @Override
            public String read() throws RetryableException {
                i++;
                return i > 5 ? null : String.valueOf(i);
            }
        };
    }

    @Bean
    public ItemProcessor<? super String, String> sample19_customItemProcessor1() {
        return item -> {

            if (item.equals("3")) {
                System.out.println("itemProcessor : " + item);

                return item;
            }
            throw new RetryableException("Process Failed ");
        };
    }
}