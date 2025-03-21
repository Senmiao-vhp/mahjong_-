package com.example.mahjong.entity;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 麻将面子组合实体类
 */
@Data
public class Meld implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 面子类型
     */
    private MeldType type;
    
    /**
     * 组成面子的牌
     */
    private List<Tile> tiles;
    
    /**
     * 是否为暗杠
     */
    private boolean isConcealed;
    
    /**
     * 来源玩家位置（吃、碰、明杠的情况下）
     */
    private Integer fromPlayer;
    
    /**
     * 是否为明杠
     */
    private boolean isOpen;
    
    /**
     * 来源玩家ID
     */
    private Long fromPlayerId;
    
    /**
     * 默认构造函数
     */
    public Meld() {
    }
    
    /**
     * 带参数的构造函数
     * 
     * @param type 面子类型
     * @param tiles 组成面子的牌
     * @param isConcealed 是否为暗杠
     * @param fromPlayer 来源玩家位置
     */
    public Meld(MeldType type, List<Tile> tiles, boolean isConcealed, Integer fromPlayer) {
        this.type = type;
        this.tiles = tiles;
        this.isConcealed = isConcealed;
        this.fromPlayer = fromPlayer;
    }
    
    /**
     * 带参数的构造函数
     * 
     * @param type 面子类型
     * @param tiles 组成面子的牌
     * @param isOpen 是否为明杠
     * @param fromPlayerId 来源玩家ID
     */
    public Meld(MeldType type, List<Tile> tiles, boolean isOpen, Long fromPlayerId) {
        this.type = type;
        this.tiles = tiles;
        this.isOpen = isOpen;
        this.fromPlayerId = fromPlayerId;
    }
    
    /**
     * 获取面子类型
     * 
     * @return 面子类型
     */
    public MeldType getType() {
        return type;
    }
    
    /**
     * 设置面子类型
     * 
     * @param type 面子类型
     */
    public void setType(MeldType type) {
        this.type = type;
    }
    
    /**
     * 获取组成面子的牌
     * 
     * @return 组成面子的牌
     */
    public List<Tile> getTiles() {
        return tiles;
    }
    
    /**
     * 设置组成面子的牌
     * 
     * @param tiles 组成面子的牌
     */
    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }
    
    /**
     * 判断是否为暗杠
     * 
     * @return 是否为暗杠
     */
    public boolean isConcealed() {
        return isConcealed;
    }
    
    /**
     * 设置是否为暗杠
     * 
     * @param concealed 是否为暗杠
     */
    public void setConcealed(boolean concealed) {
        isConcealed = concealed;
    }
    
    /**
     * 获取来源玩家位置
     * 
     * @return 来源玩家位置
     */
    public Integer getFromPlayer() {
        return fromPlayer;
    }
    
    /**
     * 设置来源玩家位置
     * 
     * @param fromPlayer 来源玩家位置
     */
    public void setFromPlayer(Integer fromPlayer) {
        this.fromPlayer = fromPlayer;
    }
    
    /**
     * 获取面子的第一张牌
     * 
     * @return 面子的第一张牌
     */
    public Tile getFirstTile() {
        return tiles != null && !tiles.isEmpty() ? tiles.get(0) : null;
    }
    
    /**
     * 判断是否是有效的面子
     * 
     * @return 是否是有效的面子
     */
    public boolean isValid() {
        if (type == null || tiles == null || tiles.isEmpty()) {
            return false;
        }

        switch (type) {
            case CHI:
                return isValidChi();
            case PON:
                return isValidPon();
            case KAN:
            case ANKAN:
                return isValidKan();
            default:
                return false;
        }
    }
    
    /**
     * 判断是否是有效的顺子
     * 
     * @return 是否是有效的顺子
     */
    private boolean isValidChi() {
        if (tiles.size() != 3) {
            return false;
        }

        // 排序牌
        tiles.sort((a, b) -> a.getNumber() - b.getNumber());

        // 检查是否是同一种花色
        TileType type = tiles.get(0).getType();
        if (type == TileType.WIND || type == TileType.DRAGON) {
            return false;
        }

        // 检查是否是连续的数字
        return tiles.get(0).getType() == type &&
               tiles.get(1).getType() == type &&
               tiles.get(2).getType() == type &&
               tiles.get(0).getNumber() + 1 == tiles.get(1).getNumber() &&
               tiles.get(1).getNumber() + 1 == tiles.get(2).getNumber();
    }
    
    /**
     * 判断是否是有效的刻子
     * 
     * @return 是否是有效的刻子
     */
    private boolean isValidPon() {
        if (tiles.size() != 3) {
            return false;
        }

        // 检查是否是相同的牌
        return tiles.get(0).getType() == tiles.get(1).getType() &&
               tiles.get(1).getType() == tiles.get(2).getType() &&
               tiles.get(0).getNumber() == tiles.get(1).getNumber() &&
               tiles.get(1).getNumber() == tiles.get(2).getNumber();
    }
    
    /**
     * 判断是否是有效的杠子
     * 
     * @return 是否是有效的杠子
     */
    private boolean isValidKan() {
        if (tiles.size() != 4) {
            return false;
        }

        // 检查是否是相同的牌
        return tiles.get(0).getType() == tiles.get(1).getType() &&
               tiles.get(1).getType() == tiles.get(2).getType() &&
               tiles.get(2).getType() == tiles.get(3).getType() &&
               tiles.get(0).getNumber() == tiles.get(1).getNumber() &&
               tiles.get(1).getNumber() == tiles.get(2).getNumber() &&
               tiles.get(2).getNumber() == tiles.get(3).getNumber();
    }
} 