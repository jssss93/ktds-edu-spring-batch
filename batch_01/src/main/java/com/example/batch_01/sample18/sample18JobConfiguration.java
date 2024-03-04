package com.example.batch_01.sample18;

import com.example.batch_01.sample16.RetryableException;
import com.example.batch_01.sample16.SkippableException;
import lombok.RequiredArgsConstructor;
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

@Configuration
@RequiredArgsConstructor
public class sample18JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private int chunkSize = 5;

    @Bean
    public Job sample18() {
        return new JobBuilder("sample18", jobRepository)
                .start(sample18_step01())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sample18_step01() {
        return new StepBuilder("sample18_step01",jobRepository)
                .<String, String>chunk(chunkSize,transactionManager)
                .reader(sample18_customItemReader())
                .processor(sample18_customItemProcessor1())
                .writer(sample18_customItemWriter())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(2)
                .skip(RetryableException.class)
                .skipLimit(2)
                .build();
    }

    @Bean
    public ItemReader<String> sample18_customItemReader() {
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
    public ItemProcessor<? super String, String> sample18_customItemProcessor1() {
        return item -> {


            if (item.equals("4")) {
                throw new RetryableException("Process Failed ");
            }
            System.out.println("itemProcessor : " + item);

            return item;
        };
    }

    @Bean
    public ItemWriter<? super String> sample18_customItemWriter() {
        return items -> {
            System.out.println("items = " + items);
        };
    }
}