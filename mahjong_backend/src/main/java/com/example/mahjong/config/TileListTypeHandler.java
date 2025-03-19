package com.example.mahjong.config;

import com.example.mahjong.entity.Tile;
import com.fasterxml.jackson.core.type.TypeReference;
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
 * 专门处理List<Tile>类型的处理器
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({List.class})
public class TileListTypeHandler extends BaseTypeHandler<List<Tile>> {
    
    private static final Logger logger = Logger.getLogger(TileListTypeHandler.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Tile> parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = objectMapper.writeValueAsString(parameter);
            ps.setString(i, json);
            logger.fine("转换List<Tile>为JSON: " + json);
        } catch (Exception e) {
            logger.severe("转换List<Tile>为JSON失败: " + e.getMessage());
            throw new SQLException("转换List<Tile>为JSON失败", e);
        }
    }
    
    @Override
    public List<Tile> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }
    
    @Override
    public List<Tile> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }
    
    @Override
    public List<Tile> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }
    
    private List<Tile> parseJson(String json) throws SQLException {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            List<Tile> tiles = objectMapper.readValue(json, new TypeReference<List<Tile>>() {});
            logger.fine("解析JSON为List<Tile>成功，共" + tiles.size() + "个元素");
            return tiles;
        } catch (Exception e) {
            logger.severe("解析JSON为List<Tile>失败: " + e.getMessage() + ", JSON: " + json);
            // 返回null而不是抛出异常，以便应用程序可以继续运行
            return null;
        }
    }
} 