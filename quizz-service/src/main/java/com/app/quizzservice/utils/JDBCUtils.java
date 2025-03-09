package com.app.quizzservice.utils;

import lombok.experimental.UtilityClass;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@UtilityClass
public class JDBCUtils {
    public static <T> T getValueResultSet(ResultSet rs, Class<T> tClass, T defaultValue, String key) {
        try {
            rs.findColumn(key);
            if (Objects.isNull(rs.getObject(key))) {
                return defaultValue;
            }
            return rs.getObject(key, tClass);
        } catch (SQLException e) {
            return defaultValue;
        }
    }
}
