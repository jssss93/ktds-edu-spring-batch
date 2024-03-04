package com.example.demo.sample00.reader;

import com.example.demo.sample00.model.User;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class UserFlatFileItemReader implements ItemReader<FlatFileItemReader<User>> {

    @Override
    public FlatFileItemReader<User> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
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
}
