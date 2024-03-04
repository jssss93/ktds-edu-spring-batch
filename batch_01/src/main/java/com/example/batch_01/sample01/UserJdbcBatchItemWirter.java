package com.example.batch_01.sample01;

/*
@Component
@RequiredArgsConstructor
public class UserJdbcBatchItemWirter{

    private final DataSource dataSource;

    public JdbcBatchItemWriter<User> writer() throws SQLException {
        return new JdbcBatchItemWriterBuilder<User>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO user (id,first_name,last_name,email,gender,ip_address,country_code) VALUES (:id,:first_name,:last_name,:email,:gender,:ip_address,:country_code)")
                .dataSource(dataSource)
                .build();
    }

}



 */