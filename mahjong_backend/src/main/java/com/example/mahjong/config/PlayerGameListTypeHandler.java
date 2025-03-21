package com.example.mahjong.config;

import com.example.mahjong.entity.PlayerGame;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@MappedTypes(List.class)
public class PlayerGameListTypeHandler extends BaseTypeHandler<List<PlayerGame>> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<PlayerGame> parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = objectMapper.writeValueAsString(parameter);
            ps.setString(i, json);
        } catch (Exception e) {
            throw new SQLException("Error converting List<PlayerGame> to JSON", e);
        }
    }
    
    @Override
    public List<PlayerGame> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }
    
    @Override
    public List<PlayerGame> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }
    
    @Override
    public List<PlayerGame> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }
    
    private List<PlayerGame> parseJson(String json) throws SQLException {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<PlayerGame>>() {});
        } catch (Exception e) {
            throw new SQLException("Error converting JSON to List<PlayerGame>", e);
        }
    }
} 