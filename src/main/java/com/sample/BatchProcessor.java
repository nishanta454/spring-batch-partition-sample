package com.sample;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BatchProcessor implements ItemProcessor<BatchRecord, BatchRecord> {
    @Override
    public BatchRecord process(BatchRecord batchRecord) throws Exception {
        //log.info("Processing the Record");
        return batchRecord;
    }
}
