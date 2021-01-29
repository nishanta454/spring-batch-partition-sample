package com.sample;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BatchWriter implements ItemWriter<BatchRecord> {
    @Override
    public void write(List<? extends BatchRecord> arg0) throws Exception {
        log.info("Writting the Records");
    }
}
