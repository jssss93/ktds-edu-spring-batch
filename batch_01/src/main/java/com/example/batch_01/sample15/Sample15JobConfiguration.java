package com.example.batch_01.sample15;

import com.example.batch_01.sample13.CustomException;
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
public class Sample15JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private int chunkSize = 5;

    @Bean
    public Job sample15() {
        return new JobBuilder("sample15", jobRepository)
                .start(sample15_step01())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sample15_step01() {
        return new StepBuilder("sample15_step01",jobRepository)
                .<String, String>chunk(chunkSize,transactionManager)
                .reader(sample15_customItemReader())
                .processor(sample15_customItemProcessor())
                .writer(sample15_customItemWriter())
                .faultTolerant()
                .skip(CustomException.class)
                .skipLimit(3)
                .build();
    }

    @Bean
    public ItemReader<String> sample15_customItemReader() {
        return new ItemReader<String>() {
            int i = 0;

            @Override
            public String read() throws CustomException {
                i++;
                System.out.println("itemReader : " + i);
                return i > 5 ? null : String.valueOf(i);
            }
        };
    }

    @Bean
    public ItemProcessor<? super String, String> sample15_customItemProcessor() {
        return item -> {
            System.out.println("itemProcessor " + item);
            return item;
        };
    }

    @Bean
    public ItemWriter<? super String> sample15_customItemWriter() {
        return items -> {
        	System.out.println("writer");
            for (String item : items) {
                if (item.equals("4")){
                    throw new CustomException("4");
                }
            }
            System.out.println("items = " + items);
        };
    }

    
}