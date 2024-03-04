package com.example.demo.sample00.processor;

import com.example.demo.sample00.config.Sample01BatchConfiguration;
import com.example.demo.sample00.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserItemProcessor implements ItemProcessor<User, User> {
    private static final Logger logger = LoggerFactory.getLogger(Sample01BatchConfiguration.class);
    @Override
    public User process(User user) throws Exception {
        //logger.info("item processed");
        if(user.getCountry_code().equals("US"))
        {
        	logger.info("{}\n{} {} from USA",user.toString(),user.getLast_name(),user.getFirst_name());
            return user;
        } else {
            return null;
        }
    }
}
