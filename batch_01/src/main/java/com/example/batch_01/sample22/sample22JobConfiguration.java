package com.example.batch_01.sample22;

import com.example.batch_01.sample21.Customer;
import com.example.batch_01.sample21.Customer2;
import jakarta.persistence.EntityManagerFactory;
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
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class sample22JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;
    private int chunkSize = 10;

    @Bean
    public Job sample22() {
        return new JobBuilder("sample22", jobRepository)
                .start(sample22_step01())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sample22_step01() {
        return new StepBuilder("sample22_step01", jobRepository)
                .<Customer, Customer2>chunk(chunkSize,transactionManager)
                .reader(sample22_customItemReader())
                .processor(sample22_customItemProcessor())
                .writer(sample22_customItemWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4); // 기본 스레드 풀 크기
        taskExecutor.setMaxPoolSize(8); // 4개의 스레드가 이미 처리중인데 작업이 더 있을 경우 몇개까지 스레드를 늘릴 것인지
        taskExecutor.setThreadNamePrefix("async-thread"); // 스레드 이름 prefix
        return taskExecutor;
    }

    @Bean
    public ItemReader<? extends Customer> sample22_customItemReader() {
        return new JpaPagingItemReaderBuilder<Customer>()
                .name("sample22_customItemReader")
                .pageSize(chunkSize)
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from Customer c order by c.id")
                .build();
    }


    @Bean
    public ItemProcessor<Customer, Customer2> sample22_customItemProcessor() {
        return new ItemProcessor<Customer, Customer2>() {
            @Override
            public Customer2 process(Customer item) throws Exception {
                return Customer2.builder()
                        .id(item.getId())
                        .name(item.getName().toUpperCase())
                        .age(item.getAge())
                        .build();
            }
        };
    }
  

    @Bean
    public ItemWriter<Customer2> sample22_customItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer2>()
                .dataSource(dataSource)
                .sql("insert into Customer2 values (:id, :age, :name)")
                .beanMapped()
                .build();

    }

}