package com.example.batch_01.online_batch01;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SampleController {
    private final JobLauncher jobLauncher;
    private final ApplicationContext applicationContext;
    @GetMapping(value = "/get")
    public String sampleController(){
        System.out.println("get REQ");
        //TODO Business-Logic
        return "success";
    }

    @GetMapping("/startBatch/{jobName}")
    public String startBatch(@PathVariable String jobName) throws JobExecutionException, InterruptedException {
        System.out.println("startBatch REQ jobName :: "+jobName);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("startTime", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        Job job = this.applicationContext.getBean(jobName,Job.class);
        jobLauncher.run(job, jobParameters);

        return "";
    }
}
