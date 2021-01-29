package com.sample;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BatchDao {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String UNMARKED_COUNT_Q_STRING = "select count(1) from pincodes where hold_status=1 and proc_pod_id is null";

    private static final String UNMARKED_Q_STRING = "select pincode from pincodes where hold_status=1 and proc_pod_id is null  limit :limit offset :offset";

    private static final String MARK_Q_STRING = "update pincodes set proc_pod_id = 'a'  where pincode in (:pincodes)";

    private static final String MARKED_Q_STRING = "select pincode from pincodes where pincode in (:pincodes) and hold_status=1 and proc_pod_id = 'a'";

    public int fetchUnmarkedRecordCount() {
       return this.namedParameterJdbcTemplate.queryForObject(UNMARKED_COUNT_Q_STRING, new MapSqlParameterSource(), Integer.class);
    }

    public List<String> fetchUnmarkedRecords(Long offset, Long limit) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("offset", offset);
        params.addValue("limit", limit);
        return this.namedParameterJdbcTemplate.queryForList(UNMARKED_Q_STRING, params, String.class);
    }

    public int markRecords(List<String> records) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("pincodes", records);
        return this.namedParameterJdbcTemplate.update(MARK_Q_STRING, params);
    }

    public List<BatchRecord> fetchMarkedRecords(List<String> records) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("pincodes", records);
        return this.namedParameterJdbcTemplate.query(MARKED_Q_STRING, params,
                new BeanPropertyRowMapper<BatchRecord>(BatchRecord.class));
    }
}
