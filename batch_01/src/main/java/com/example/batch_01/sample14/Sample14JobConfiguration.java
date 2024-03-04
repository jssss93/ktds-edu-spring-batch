package com.example.batch_01.sample14;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class Sample14JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private int chunkSize = 5;

    @Bean
    public Job sample14() {
        return new JobBuilder("sample14", jobRepository)
                .start(sample14_step01())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sample14_step01() {
        return new StepBuilder("sample14_step01",jobRepository)
                .<String, String>chunk(chunkSize,transactionManager)
                .reader(sample14_customItemReader())
                .processor(customItemProcessor1())
                .writer(items -> System.out.println("items = " + items))
                .faultTolerant()
                .skip(CustomException.class)
                .skipLimit(3)
                .build();
    }

    @Bean
    public ItemReader<String> sample14_customItemReader() {
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
    public ItemProcessor<? super String, String> customItemProcessor1() {
        return item -> {
            System.out.println("itemProcessor " + item);

            if (item.equals("3")) {
                throw new CustomException("Process Failed ");

            }
            return item;
        };
    }
    
}