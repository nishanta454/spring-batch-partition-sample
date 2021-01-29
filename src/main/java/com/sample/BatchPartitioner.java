package com.sample;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BatchPartitioner implements Partitioner {

    @Autowired
    private BatchDao dao;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        int count = dao.fetchUnmarkedRecordCount();

        int limit = count / gridSize + 1;

        Map<String, ExecutionContext> result = new HashMap<>();

        int number = 0;
        int offset = 0;

        while (offset <= count) {
            ExecutionContext value = new ExecutionContext();
            result.put("partition-" + number, value);
            value.putInt("offset", offset);
            value.putInt("limit", limit);
            offset += limit;
            number++;
        }
        return result;
    }
}
