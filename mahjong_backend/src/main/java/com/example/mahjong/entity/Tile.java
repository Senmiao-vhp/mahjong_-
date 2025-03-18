package com.example.mahjong.entity;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 麻将牌实体类
 */
@Schema(description = "麻将牌信息")
public class Tile implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 牌类型
     */
    @Schema(description = "牌类型", example = "MANZU", allowableValues = {"MANZU", "PINZU", "SOUZU", "WIND", "DRAGON"})
    private TileType type;
    
    /**
     * 牌数字
     */
    @Schema(description = "牌数字", example = "1")
    private Integer number;
    
    /**
     * 是否为赤宝牌
     */
    @Schema(description = "是否为赤宝牌", example = "false")
    private Boolean isRedDora;
    
    /**
     * 默认构造函数
     */
    public Tile() {
    }
    
    /**
     * 带参数的构造函数
     * 
     * @param type 牌类型
     * @param number 牌数字
     */
    public Tile(TileType type, Integer number) {
        this.type = type;
        this.number = number;
        this.isRedDora = false;
    }
    
    /**
     * 带参数的构造函数
     * 
     * @param type 牌类型
     * @param number 牌数字
     * @param isRedDora 是否为赤宝牌
     */
    public Tile(TileType type, Integer number, Boolean isRedDora) {
        this.type = type;
        this.number = number;
        this.isRedDora = isRedDora;
    }
    
    /**
     * 获取牌类型
     */
    public TileType getType() {
        return type;
    }
    
    /**
     * 设置牌类型
     */
    public void setType(TileType type) {
        this.type = type;
    }
    
    /**
     * 获取牌数字
     */
    public Integer getNumber() {
        return number;
    }
    
    /**
     * 设置牌数字
     */
    public void setNumber(Integer number) {
        this.number = number;
    }
    
    /**
     * 获取是否为赤宝牌
     */
    public Boolean getIsRedDora() {
        return isRedDora;
    }
    
    /**
     * 设置是否为赤宝牌
     */
    public void setIsRedDora(Boolean isRedDora) {
        this.isRedDora = isRedDora;
    }
    
    /**
     * 获取牌名称
     */
    public String getName() {
        if (type.isHonorTile()) {
            switch (number) {
                case 1:
                    return "东";
                case 2:
                    return "南";
                case 3:
                    return "西";
                case 4:
                    return "北";
                case 5:
                    return "白";
                case 6:
                    return "发";
                case 7:
                    return "中";
                default:
                    return "";
            }
        } else {
            return number + type.getName();
        }
    }
    
    /**
     * 判断是否为同一张牌
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Tile other = (Tile) obj;
        return type == other.type && number.equals(other.number);
    }
    
    /**
     * 获取哈希值
     */
    @Override
    public int hashCode() {
        return 31 * type.hashCode() + number.hashCode();
    }
    
    /**
     * 获取字符串表示
     */
    @Override
    public String toString() {
        return getName();
    }
    
    public static Tile fromString(String tileStr) {
        if (tileStr == null || tileStr.length() < 2) {
            throw new IllegalArgumentException("Invalid tile string: " + tileStr);
        }

        char typeChar = tileStr.charAt(tileStr.length() - 1);
        String numberStr = tileStr.substring(0, tileStr.length() - 1);
        int number = Integer.parseInt(numberStr);

        TileType type;
        switch (typeChar) {
            case 'm':
                type = TileType.MANZU;
                break;
            case 'p':
                type = TileType.PINZU;
                break;
            case 's':
                type = TileType.SOUZU;
                break;
            case 'w':
                type = TileType.WIND;
                break;
            case 'd':
                type = TileType.DRAGON;
                break;
            default:
                throw new IllegalArgumentException("Invalid tile type: " + typeChar);
        }

        return new Tile(type, number);
    }
} 