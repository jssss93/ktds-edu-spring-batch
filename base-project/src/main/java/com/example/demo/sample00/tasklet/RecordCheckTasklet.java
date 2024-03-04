package com.example.demo.sample00.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RecordCheckTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(RecordCheckTasklet.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        logger.info("Tasklet executed!");
        int records = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user;", Integer.class);
        logger.info("Number of USER Records: " + records);
        return RepeatStatus.FINISHED;
    }
}
