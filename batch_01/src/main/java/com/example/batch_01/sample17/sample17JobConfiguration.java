package com.example.batch_01.sample17;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch_01.sample16.RetryableException;
import com.example.batch_01.sample16.SkippableException;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class sample17JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private int chunkSize = 5;

    @Bean
    public Job sample17() {
        return new JobBuilder("sample17", jobRepository)
                .start(sample17_step01())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sample17_step01() {
        return new StepBuilder("sample17_step01",jobRepository)
                .<String, String>chunk(chunkSize,transactionManager)
                .reader(sample17_customItemReader())
                .processor(sample17_customItemProcessor1())
                .writer(sample17_customItemWriter())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(2)
                .build();
    }

    @Bean
    public ItemReader<String> sample17_customItemReader() {
        return new ItemReader<String>() {
            int i = 0;

            @Override
            public String read() throws SkippableException {
                i++;
                System.out.println("itemReader : " + i);
                return i > 5 ? null : String.valueOf(i);
            }
        };
    }

    @Bean
    public ItemProcessor<? super String, String> sample17_customItemProcessor1() {
        return item -> {


            if (item.equals("4")) {
                throw new RetryableException("Process Failed ");
            }
            System.out.println("itemProcessor : " + item);

            return item;
        };
    }

    @Bean
    public ItemWriter<? super String> sample17_customItemWriter() {
        return items -> {
            System.out.println("items = " + items);
        };
    }
}