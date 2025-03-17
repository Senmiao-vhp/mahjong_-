package com.example.mahjong.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * 通用JSON类型处理器
 * @param <T> 要处理的类型
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({List.class})
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {
    
    private static final Logger logger = Logger.getLogger(JsonTypeHandler.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> clazz;
    private final JavaType javaType;
    
    public JsonTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
        this.javaType = null;
    }
    
    public JsonTypeHandler(JavaType javaType) {
        if (javaType == null) {
            throw new IllegalArgumentException("JavaType argument cannot be null");
        }
        this.clazz = null;
        this.javaType = javaType;
    }
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = objectMapper.writeValueAsString(parameter);
            ps.setString(i, json);
        } catch (Exception e) {
            logger.severe("Error converting object to JSON: " + e.getMessage());
            throw new SQLException("Error converting object to JSON", e);
        }
    }
    
    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }
    
    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }
    
    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }
    
    private T parseJson(String json) throws SQLException {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            if (clazz != null) {
                return objectMapper.readValue(json, clazz);
            } else {
                return objectMapper.readValue(json, javaType);
            }
        } catch (Exception e) {
            logger.severe("Error converting JSON to object: " + e.getMessage() + ", JSON: " + json);
            // 返回null而不是抛出异常，以便应用程序可以继续运行
            return null;
        }
    }
    
    /**
     * 创建处理List类型的TypeHandler
     */
    public static <E> JsonTypeHandler<List<E>> createListTypeHandler(Class<E> elementType) {
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
        return new JsonTypeHandler<>(javaType);
    }
} 