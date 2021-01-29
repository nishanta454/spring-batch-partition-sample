package com.sample;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@StepScope
public class BatchReader implements ItemReader<BatchRecord> {

    @Value("#{stepExecutionContext['offset']}")
    private Long offset;

    @Value("#{stepExecutionContext['limit']}")
    private Long limit;

    @Autowired
    private BatchDao dao;

    private Queue<BatchRecord> records = null;

    @Override
    public BatchRecord read()
            throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        BatchRecord record  = null;
        if(!ObjectUtils.isEmpty(records)){
             record = records.poll();
        }
        return record;
    }

    @PostConstruct
    private void init() {
        List<String> rawRecords = dao.fetchUnmarkedRecords(offset, limit);
        if (!ObjectUtils.isEmpty(rawRecords)) {
            int status = dao.markRecords(rawRecords);
            if (status > 0) {
                records = new LinkedList<>(dao.fetchMarkedRecords(rawRecords));
            }
        }
    }
}
