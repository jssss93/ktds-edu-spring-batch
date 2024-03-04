package com.example.batch_01.sample24;

import com.example.batch_01.sample21.Customer;
import com.example.batch_01.sample21.Customer2;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class sample24JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;
    private int chunkSize = 10;
    private int poolSize = 4 ;

    @Bean
    public Job sample24() {
        return new JobBuilder("sample24",jobRepository)
                .start(masterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step masterStep() {
        return new StepBuilder("sample24_master",jobRepository)
                .partitioner(slaveStep().getName(),partitioner()) // slaveStep에서 사용될 partitioner 구현체 등록
                .step(slaveStep()) // 파티셔닝 될 Step 등록(SlaveStep)
                .gridSize(poolSize) // StepExecution이 형성될 개수 = 파티션 되는 데이터 뭉텅이 수 = 스레드 풀 사이즈과 일치시키는게 좋음
                .taskExecutor(sample24_taskExecutor()) // MasterStep이 SlaveStep을 다루는 스레드 형성 방식
                .build();
    }

    
    @Bean
    // 데이터 파티셔닝 방식
    public Partitioner partitioner() {
        ColumnRangePartitioner partitioner = new ColumnRangePartitioner(); // 아래 코드쪽 클래스 코드 참고
        partitioner.setDataSource(dataSource);
        partitioner.setTable("Customer"); // 파티셔닝 할 테이블
        partitioner.setColumn("id"); // 파티셔닝 기준 컬럼
        return partitioner;
    }

    @Bean
    public Step slaveStep() {
        return new StepBuilder("sample24_slaveStep",jobRepository)
                .<Customer,Customer2>chunk(chunkSize,transactionManager)
                .reader(sample24_customItemReader(null,null))
                .writer(sample24_customItemWriter())
                .build();
    }

    @Bean
    public TaskExecutor sample24_taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(poolSize); // 기본 스레드 풀 크기
        taskExecutor.setMaxPoolSize(8); // 4개의 스레드가 이미 처리중인데 작업이 더 있을 경우 몇개까지 스레드를 늘릴 것인지
        taskExecutor.setThreadNamePrefix("async-thread"); // 스레드 이름 prefix
        return taskExecutor;
    }

    @Bean
    @StepScope
    // partitioner에서 stepExecutionContext에 데이터 정보를 넣어두기 때문에 런타임에 해당 과정이 발생
    // 따라서 해당 값을 사용하기 위해서는 Scope를 사용해서 프록시를 통한 지연 로딩을 사용해야 함.
    // 반환값은 ItemReader이 아닌 구현체를 사용해야 하는데 이는 아래서 설명
    public JpaPagingItemReader<? extends Customer> sample24_customItemReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
           @Value("#{stepExecutionContext['maxValue']}") Long maxValue
    ) {
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("minValue",minValue);
        parameters.put("maxValue",maxValue);

        return new JpaPagingItemReaderBuilder<Customer>()
                .name("customItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT c FROM Customer c WHERE :minValue <= c.id and c.id <= :maxValue order by c.id")
                .parameterValues(parameters)
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer2> sample24_customItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer2>()
                .dataSource(dataSource)
                .sql("insert into Customer2 values (:id, :age, :name)")
                .beanMapped()
                .build();
    }
}