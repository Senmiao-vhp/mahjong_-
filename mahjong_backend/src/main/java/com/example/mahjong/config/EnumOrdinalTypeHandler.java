package com.example.mahjong.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * 通用枚举类型处理器
 * 根据枚举的ordinal值进行处理
 * @param <E> 枚举类型
 */
public class EnumOrdinalTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    
    private static final Logger logger = Logger.getLogger(EnumOrdinalTypeHandler.class.getName());
    private final Class<E> type;
    private final E[] enums;
    
    /**
     * 构造函数
     * @param type 枚举类型
     */
    public EnumOrdinalTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        this.enums = type.getEnumConstants();
        if (this.enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
    }
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.ordinal());
    }
    
    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int ordinal = rs.getInt(columnName);
        return getEnumForOrdinal(ordinal, rs.wasNull());
    }
    
    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int ordinal = rs.getInt(columnIndex);
        return getEnumForOrdinal(ordinal, rs.wasNull());
    }
    
    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int ordinal = cs.getInt(columnIndex);
        return getEnumForOrdinal(ordinal, cs.wasNull());
    }
    
    private E getEnumForOrdinal(int ordinal, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        
        try {
            if (ordinal >= 0 && ordinal < enums.length) {
                return enums[ordinal];
            } else {
                logger.warning("无效的枚举ordinal值: " + ordinal + " 对于枚举类型: " + type.getSimpleName());
                return null;
            }
        } catch (Exception e) {
            logger.severe("处理枚举类型时出错: " + e.getMessage());
            return null;
        }
    }
} 