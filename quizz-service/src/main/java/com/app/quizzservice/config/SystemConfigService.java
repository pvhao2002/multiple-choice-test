package com.app.quizzservice.config;

import com.app.quizzservice.config.db.ReadDB;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Log
@Service
public class SystemConfigService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    CacheLoader<String, String> cacheLoader = new CacheLoader<>() {
        @NonNull
        @Override
        public String load(@NonNull String key) {
            return StringUtils.defaultIfBlank(findByConfigKey(key), StringUtils.EMPTY);
        }
    };

    LoadingCache<String, String> systemCache = CacheBuilder.newBuilder()
                                                           .recordStats()
                                                           .refreshAfterWrite(5, TimeUnit.MINUTES)
                                                           .build(CacheLoader.asyncReloading(
                                                                   cacheLoader,
                                                                   Executors.newSingleThreadExecutor()
                                                           ));

    public SystemConfigService(@ReadDB NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getConfigValue(String key) {
        try {
            return systemCache.get(key);
        } catch (ExecutionException e) {
            log.log(Level.WARNING, "SystemConfigService >> getConfigValue >> ExecutionException: ", e);
            return StringUtils.EMPTY;
        }
    }


    private String findByConfigKey(String key) {
        var sql = "SELECT value FROM system_settings WHERE name = :name";
        var params = new MapSqlParameterSource().addValue("name", key);
        return jdbcTemplate.query(sql, params, (rs, i) -> rs.getString("value"))
                           .stream()
                           .findFirst()
                           .orElse(StringUtils.EMPTY);
    }
}
