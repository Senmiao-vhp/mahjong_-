package com.example.mahjong.config;

import com.example.mahjong.entity.*;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.logging.Logger;

/**
 * MyBatis类型处理器配置
 */
@Configuration
public class TypeHandlerConfig {
    
    private static final Logger logger = Logger.getLogger(TypeHandlerConfig.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 自定义MyBatis配置
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
            
            // 注册List<Tile>类型处理器
            registerListTypeHandler(registry, "List<Tile>", Tile.class);
            
            // 注册List<Meld>类型处理器
            registerListTypeHandler(registry, "List<Meld>", Meld.class);
            
            // 注册List<PlayerGame>类型处理器
            registerListTypeHandler(registry, "List<PlayerGame>", PlayerGame.class);
            
            // 注册List<AIPlayer>类型处理器
            registerListTypeHandler(registry, "List<AIPlayer>", AIPlayer.class);
            
            // 注册通用的List<Object>类型处理器
            JsonTypeHandler<List<Object>> listHandler = new JsonTypeHandler<>(
                objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
            registry.register(List.class, JdbcType.VARCHAR, listHandler);
            
            // 注册枚举类型处理器
            registerEnumTypeHandlers(registry);
            
            logger.info("已注册所有自定义TypeHandler");
        };
    }
    
    /**
     * 注册枚举类型处理器
     */
    private void registerEnumTypeHandlers(TypeHandlerRegistry registry) {
        try {
            // 注册Wind枚举处理器
            EnumOrdinalTypeHandler<Wind> windTypeHandler = new EnumOrdinalTypeHandler<>(Wind.class);
            registry.register(Wind.class, windTypeHandler);
            
            // 注册GameState枚举处理器
            EnumOrdinalTypeHandler<GameState> gameStateTypeHandler = new EnumOrdinalTypeHandler<>(GameState.class);
            registry.register(GameState.class, gameStateTypeHandler);
            
            // 注册TileType枚举处理器
            EnumOrdinalTypeHandler<TileType> tileTypeTypeHandler = new EnumOrdinalTypeHandler<>(TileType.class);
            registry.register(TileType.class, tileTypeTypeHandler);
            
            // 注册MeldType枚举处理器
            EnumOrdinalTypeHandler<MeldType> meldTypeTypeHandler = new EnumOrdinalTypeHandler<>(MeldType.class);
            registry.register(MeldType.class, meldTypeTypeHandler);
            
            // 注册ActionType枚举处理器
            EnumOrdinalTypeHandler<ActionType> actionTypeTypeHandler = new EnumOrdinalTypeHandler<>(ActionType.class);
            registry.register(ActionType.class, actionTypeTypeHandler);
            
            logger.info("已注册所有枚举类型处理器");
        } catch (Exception e) {
            logger.severe("注册枚举类型处理器失败: " + e.getMessage());
        }
    }
    
    /**
     * 注册List类型的TypeHandler
     */
    private <T> void registerListTypeHandler(TypeHandlerRegistry registry, String typeName, Class<T> elementType) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
            JsonTypeHandler<List<T>> typeHandler = new JsonTypeHandler<>(javaType);
            
            // 注册特定类型的List处理器
            registry.register(typeHandler);
            
            // 为特定类型的List和VARCHAR类型注册处理器
            registry.register(List.class, JdbcType.VARCHAR, typeHandler);
            
            logger.info("已注册" + typeName + "类型处理器");
        } catch (Exception e) {
            logger.severe("注册" + typeName + "类型处理器失败: " + e.getMessage());
        }
    }
    
    /**
     * 为List<Tile>类型创建TypeHandler
     */
    @Bean
    public JsonTypeHandler<List<Tile>> tileListTypeHandler() {
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, Tile.class);
        return new JsonTypeHandler<>(javaType);
    }
    
    /**
     * 为List<Meld>类型创建TypeHandler
     */
    @Bean
    public JsonTypeHandler<List<Meld>> meldListTypeHandler() {
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, Meld.class);
        return new JsonTypeHandler<>(javaType);
    }
    
    /**
     * 为Wind枚举类型创建TypeHandler
     */
    @Bean
    public EnumOrdinalTypeHandler<Wind> windTypeHandler() {
        return new EnumOrdinalTypeHandler<>(Wind.class);
    }
    
    /**
     * 为GameState枚举类型创建TypeHandler
     */
    @Bean
    public EnumOrdinalTypeHandler<GameState> gameStateTypeHandler() {
        return new EnumOrdinalTypeHandler<>(GameState.class);
    }
} 