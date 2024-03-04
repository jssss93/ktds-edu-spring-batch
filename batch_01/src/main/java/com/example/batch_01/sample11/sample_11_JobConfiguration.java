package com.example.batch_01.sample11;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch_01.sample01.User;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@RequiredArgsConstructor
public class sample_11_JobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private int chunkSize = 10;

    @Bean
    public Job sample11() {
        return new JobBuilder("sample11", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(sample11_step01())
                .build();
    }

    @SneakyThrows
    @Bean
    public Step sample11_step01() {
        return new StepBuilder("sample11_step01",jobRepository)
                .<User, User>chunk(chunkSize,transactionManager)
                .reader(sample11_reader())
                .processor(process())
                .writer(sample11_writer())
                .listener(new CustomChunkListener())
                .listener(new CustomItemReadListener())
                .listener(new CustomItemProcessorListener())
                .listener(new CustomItemWriterListener())
                .build();
    }


    @Bean
    public FlatFileItemReader<User> sample11_reader() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                .resource(new ClassPathResource("MOCK_DATA.data"))
                .delimited()
                .delimiter(",")
                .names("id", "first_name","last_name","email","gender","ip_address","country_code")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<User>() {{
                    setTargetType(User.class);
                }})
                .build();
    }


    @Bean
    public ItemProcessor<? super User, ? extends User> process() {
        return user -> {
            System.out.println(user.toString()+"from USA"+user.getLast_name()+" "+user.getFirst_name());
            return user;
        };
    }

    @Bean
    public JdbcBatchItemWriter<User> sample11_writer() throws SQLException {
        return new JdbcBatchItemWriterBuilder<User>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO user (id,first_name,last_name,email,gender,ip_address,country_code) VALUES (:id,:first_name,:last_name,:email,:gender,:ip_address,:country_code)")
                .dataSource(dataSource)
                .build();
    }
}