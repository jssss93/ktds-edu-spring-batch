package com.example.batch_01.sample16;

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
public class sample16JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private int chunkSize = 5;

    @Bean
    public Job sample16() {
        return new JobBuilder("sample16", jobRepository)
                .start(sample16_step01())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sample16_step01() {
        return new StepBuilder("sample16_step01",jobRepository)
                .<String, String>chunk(chunkSize,transactionManager)
                .reader(sample16_customItemReader())
                .processor(sample16_customItemProcessor1())
                .writer(sample16_customItemWriter())
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(2)
                .build();
    }

    @Bean
    public ItemReader<String> sample16_customItemReader() {
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
    public ItemProcessor<? super String, String> sample16_customItemProcessor1() {
        return item -> {
            System.out.println("itemProcessor : " + item);

            return item;
        };
    }


    @Bean
    public ItemWriter<? super String> sample16_customItemWriter() {
        return items -> {
            for (String item : items) {
                if (item.equals("4")){
                    throw new RetryableException("4");
                }
            }
            System.out.println("items = " + items);
        };
    }
}