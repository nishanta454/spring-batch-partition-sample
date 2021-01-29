package com.sample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private BatchReader reader;

    @Autowired
    private BatchProcessor processor;

    @Autowired
    private BatchWriter writer;

    @Autowired
    private BatchPartitioner partitioner;

    @Bean
    public Step master() {
        return stepBuilderFactory.get("master").partitioner(slave().getName(), partitioner).step(slave()).gridSize(10)
                .taskExecutor(new SimpleAsyncTaskExecutor()).build();
    }

    @Bean
    public Step slave() {
        return stepBuilderFactory.get("slave").<BatchRecord, BatchRecord>chunk(1000).reader(reader).processor(processor)
                .writer(writer).build();
    }

    @Bean
    public Job pincodeJob() {
        return jobBuilderFactory.get("pincodeJob").start(master()).build();
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void perform() throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException {

        log.info("Job Started");

        JobParameters param = new JobParametersBuilder().addString("job_id", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        JobExecution execution = jobLauncher.run(pincodeJob(), param);

        log.info("Job finished with status {}", execution.getStatus());
    }
}
