package com.example.batch_01.common;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(BatchProperties.class)
public class BatchConfig {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = "spring.batch.job", name = "enabled", havingValue = "true", matchIfMissing = true)
  public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer,
                                                                   JobRepository jobRepository, BatchProperties properties) {
    JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
    String jobNames = properties.getJob().getName();
    if (StringUtils.hasText(jobNames)) {
      runner.setJobName(jobNames);
    }
    return runner;
  }

/*
  // batchDatasource 사용을 위한 수동 빈 등록
  @Bean
  @ConditionalOnMissingBean(BatchDataSourceScriptDatabaseInitializer.class)
  BatchDataSourceScriptDatabaseInitializer batchDataSourceInitializer(DataSource dataSource,
                                                                      @BatchDataSource ObjectProvider<DataSource> batchDataSource, BatchProperties properties) {
    return new BatchDataSourceScriptDatabaseInitializer(batchDataSource.getIfAvailable(() -> dataSource),
        properties.getJdbc());
  }

  @BatchDataSource
  @ConfigurationProperties(prefix = "spring.datasource.batch.hikari")
  @Bean("batchDataSource")
  public DataSource batchDataSource() {
      return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  public PlatformTransactionManager batchTransactionManager(
          @Qualifier("batchDataSource") DataSource batchDataSource) {
      return new DataSourceTransactionManager(batchDataSource);
  }

 */
}