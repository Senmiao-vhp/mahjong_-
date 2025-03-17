package com.example.mahjong.service;

import com.example.mahjong.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameLogicService {

    /**
     * 判断是否可以和牌
     */
    public boolean canWin(List<Tile> handTiles, List<Meld> melds) {
        if (handTiles == null || handTiles.isEmpty()) {
            return false;
        }

        // 检查特殊和牌
        if (isSevenPairs(handTiles) || isThirteenOrphans(handTiles)) {
            return true;
        }

        // 检查标准和牌
        return isStandardWin(handTiles, melds);
    }
    
    /**
     * 检查七对子
     */
    private boolean isSevenPairs(List<Tile> tiles) {
        if (tiles.size() != 14) {
            return false;
        }
        
        // 统计每种牌的数量
        Map<Tile, Integer> tileCounts = new HashMap<>();
        for (Tile tile : tiles) {
            tileCounts.put(tile, tileCounts.getOrDefault(tile, 0) + 1);
        }
        
        // 检查是否都是对子
        int pairCount = 0;
        for (Integer count : tileCounts.values()) {
            if (count == 2) {
                pairCount++;
            } else {
                return false;
            }
        }
        
        return pairCount == 7;
    }
    
    /**
     * 检查国士无双
     */
    private boolean isThirteenOrphans(List<Tile> tiles) {
        if (tiles.size() != 14) {
            return false;
        }
        
        // 需要的牌
        Set<Tile> requiredTiles = new HashSet<>();
        
        // 添加老头牌（1,9万、1,9筒、1,9索）
        for (TileType type : Arrays.asList(TileType.MANZU, TileType.PINZU, TileType.SOUZU)) {
            requiredTiles.add(new Tile(type, 1));
            requiredTiles.add(new Tile(type, 9));
        }
        
        // 添加字牌
        for (int i = 1; i <= 4; i++) {
            requiredTiles.add(new Tile(TileType.WIND, i));
        }
        for (int i = 1; i <= 3; i++) {
            requiredTiles.add(new Tile(TileType.DRAGON, i));
        }
        
        // 检查是否包含所有需要的牌
        for (Tile tile : tiles) {
            requiredTiles.remove(tile);
        }
        
        // 如果剩余一张牌，且这张牌在需要的牌中，则和牌
        return requiredTiles.size() == 1 && tiles.stream()
                .anyMatch(tile -> requiredTiles.contains(tile));
    }
    
    /**
     * 检查普通和牌型
     */
    private boolean checkNormalWin(List<Tile> tiles) {
        if (tiles.size() != 14) {
            return false;
        }
        
        // 复制牌列表
        List<Tile> remainingTiles = new ArrayList<>(tiles);
        
        // 尝试所有可能的和牌组合
        return tryWinCombination(remainingTiles);
    }
    
    /**
     * 尝试和牌组合
     */
    private boolean tryWinCombination(List<Tile> tiles) {
        // 如果牌数为0，说明已经成功组合
        if (tiles.isEmpty()) {
            return true;
        }
        
        // 尝试组成对子
        if (tiles.size() >= 2) {
            Tile first = tiles.get(0);
            Tile second = tiles.get(1);
            
            if (first.equals(second)) {
                // 移除对子
                tiles.remove(0);
                tiles.remove(0);
                
                if (tryWinCombination(tiles)) {
                    return true;
                }
                
                // 恢复牌
                tiles.add(0, second);
                tiles.add(0, first);
            }
        }
        
        // 尝试组成顺子
        if (tiles.size() >= 3) {
            Tile first = tiles.get(0);
            Tile second = tiles.get(1);
            Tile third = tiles.get(2);
            
            if (isSequence(first, second, third)) {
                // 移除顺子
                tiles.remove(0);
                tiles.remove(0);
                tiles.remove(0);
                
                if (tryWinCombination(tiles)) {
                    return true;
                }
                
                // 恢复牌
                tiles.add(0, third);
                tiles.add(0, second);
                tiles.add(0, first);
            }
        }
        
        // 尝试组成刻子
        if (tiles.size() >= 3) {
            Tile first = tiles.get(0);
            Tile second = tiles.get(1);
            Tile third = tiles.get(2);
            
            if (isTriplet(first, second, third)) {
                // 移除刻子
                tiles.remove(0);
                tiles.remove(0);
                tiles.remove(0);
                
                if (tryWinCombination(tiles)) {
                    return true;
                }
                
                // 恢复牌
                tiles.add(0, third);
                tiles.add(0, second);
                tiles.add(0, first);
            }
        }
        
        return false;
    }
    
    /**
     * 判断是否为顺子
     */
    private boolean isSequence(Tile first, Tile second, Tile third) {
        // 字牌不能组成顺子
        if (first.getType() == TileType.WIND || first.getType() == TileType.DRAGON) {
            return false;
        }
        
        // 检查是否是连续的三张牌
        return first.getType() == second.getType() &&
               second.getType() == third.getType() &&
               first.getNumber() + 1 == second.getNumber() &&
               second.getNumber() + 1 == third.getNumber();
    }
    
    /**
     * 判断是否为刻子
     */
    private boolean isTriplet(Tile first, Tile second, Tile third) {
        return first.equals(second) && second.equals(third);
    }
    
    /**
     * 计算役种
     */
    public List<Yaku> calculateYaku(PlayerGame player, Game game) {
        if (player == null || game == null) {
            return Collections.emptyList();
        }

        List<Yaku> yakuList = new ArrayList<>();
        List<Tile> handTiles = player.getHandTiles();
        List<Meld> melds = player.getMelds();
        Tile winningTile = game.getLastDiscardedTile(); // 获取最后一张打出的牌作为和牌

        // 检查立直
        if (player.getIsRiichi()) {
            yakuList.add(new Yaku("立直", 1));
            if (player.getIsIppatsu()) {
                yakuList.add(new Yaku("一发", 1));
            }
        }

        // 检查门清
        if (player.getIsMenzen()) {
            yakuList.add(new Yaku("门清", 1));
        }

        // 检查平和
        if (isPinfu(handTiles, melds, game.getRoundWind(), player.getSeatWind(), winningTile)) {
            yakuList.add(new Yaku("平和", 1));
        }

        // 检查一气通贯
        if (isIttsu(handTiles, melds)) {
            yakuList.add(new Yaku("一气通贯", player.getIsMenzen() ? 2 : 1));
        }

        // 检查三色同顺
        if (isSanshokuDoujun(handTiles, melds)) {
            yakuList.add(new Yaku("三色同顺", player.getIsMenzen() ? 2 : 1));
        }

        // 检查七对子
        if (isSevenPairs(handTiles)) {
            yakuList.add(new Yaku("七对子", 2));
        }

        // 检查对对和
        if (isToitoi(handTiles, melds)) {
            yakuList.add(new Yaku("对对和", 2));
        }

        // 检查混一色
        if (isHonitsu(handTiles, melds)) {
            yakuList.add(new Yaku("混一色", player.getIsMenzen() ? 3 : 2));
        }

        // 检查清一色
        if (isChinitsu(handTiles, melds)) {
            yakuList.add(new Yaku("清一色", player.getIsMenzen() ? 6 : 5));
        }

        return yakuList;
    }
    
    /**
     * 计算分数
     */
    public int calculateScore(List<Yaku> yakus, int basePoints, boolean isDealer, boolean isTsumo) {
        // 计算总番数
        int totalHan = yakus.stream().mapToInt(Yaku::getHan).sum();
        
        // 计算符数
        int fu = calculateFu(yakus, basePoints);
        
        // 计算基本点数
        int baseScore = calculateBaseScore(totalHan, fu);
        
        // 根据庄家和自摸调整点数
        if (isDealer) {
            if (isTsumo) {
                // 庄家自摸，其他玩家各付2倍
                return baseScore * 2;
            } else {
                // 庄家荣和，放铳者付6倍
                return baseScore * 6;
            }
        } else {
            if (isTsumo) {
                // 闲家自摸，庄家付2倍，其他闲家各付1倍
                return baseScore;
            } else {
                // 闲家荣和，放铳者付4倍
                return baseScore * 4;
            }
        }
    }
    
    /**
     * 计算符数
     */
    private int calculateFu(List<Yaku> yakus, int basePoints) {
        int fu = basePoints;
        
        // 检查特殊役种对符数的影响
        for (Yaku yaku : yakus) {
            switch (yaku.getName()) {
                case "平和":
                    fu = Math.max(fu, 30);
                    break;
                case "断幺九":
                    fu = Math.max(fu, 30);
                    break;
                case "三色同顺":
                    fu = Math.max(fu, 40);
                    break;
                case "一气通贯":
                    fu = Math.max(fu, 40);
                    break;
                case "对对和":
                    fu = Math.max(fu, 40);
                    break;
                case "三暗刻":
                    fu = Math.max(fu, 40);
                    break;
                case "清一色":
                    fu = Math.max(fu, 60);
                    break;
                case "字一色":
                    fu = Math.max(fu, 80);
                    break;
            }
        }
        
        // 向上取整到10的倍数
        return (fu + 9) / 10 * 10;
    }
    
    /**
     * 计算基本点数
     */
    private int calculateBaseScore(int han, int fu) {
        // 满贯及以上直接返回
        if (han >= 13) {
            return 8000; // 役满
        } else if (han >= 11) {
            return 6000; // 三倍满
        } else if (han >= 8) {
            return 4000; // 倍满
        } else if (han >= 6) {
            return 3000; // 跳满
        } else if (han >= 5) {
            return 2000; // 满贯
        }
        
        // 计算基本点数
        int baseScore = fu * (int) Math.pow(2, han + 2);
        
        // 满贯限制
        return Math.min(baseScore, 2000);
    }
    
    /**
     * 检查振听
     */
    public boolean isFuriten(PlayerGame player, Game game) {
        // 检查永久振听
        if (player.getIsFuriten()) {
            return true;
        }
        
        // 检查临时振听
        if (player.getIsTemporaryFuriten()) {
            return true;
        }
        
        // 检查同巡振听
        return isSameTurnFuriten(player, game);
    }
    
    /**
     * 检查同巡振听
     */
    private boolean isSameTurnFuriten(PlayerGame player, Game game) {
        // 获取当前回合的弃牌
        List<Tile> currentTurnDiscards = getCurrentTurnDiscards(game);
        
        // 检查是否有可以和的牌被弃掉
        for (Tile tile : currentTurnDiscards) {
            if (canWinWithTile(player, tile)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查是否可以和某张牌
     */
    private boolean canWinWithTile(PlayerGame player, Tile tile) {
        // 复制手牌
        List<Tile> handTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加要检查的牌
        handTiles.add(tile);
        
        // 检查是否可以和牌
        return canWin(handTiles, player.getMelds());
    }
    
    /**
     * 获取当前回合的弃牌
     */
    private List<Tile> getCurrentTurnDiscards(Game game) {
        List<Tile> discards = new ArrayList<>();
        int currentPosition = game.getCurrentPosition();
        
        // 获取所有玩家的弃牌
        for (PlayerGame player : game.getPlayers()) {
            if (player.getPosition() != currentPosition) {
                discards.addAll(player.getDiscardTiles());
            }
        }
        
        return discards;
    }
    
    /**
     * 设置振听状态
     */
    public void setFuriten(PlayerGame player, boolean isFuriten) {
        player.setIsFuriten(isFuriten);
    }
    
    /**
     * 设置临时振听状态
     */
    public void setTemporaryFuriten(PlayerGame player, boolean isTemporaryFuriten) {
        player.setIsTemporaryFuriten(isTemporaryFuriten);
    }
    
    /**
     * 检查是否可以荣和
     */
    public boolean canRon(PlayerGame player, Game game) {
        // 振听状态下不能荣和
        if (isFuriten(player, game)) {
            return false;
        }
        
        // 检查是否有可以和的牌
        List<Tile> currentTurnDiscards = getCurrentTurnDiscards(game);
        for (Tile tile : currentTurnDiscards) {
            if (canWinWithTile(player, tile)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查平和
     */
    private boolean isPinfu(List<Tile> handTiles, List<Meld> melds, Wind roundWind, Wind seatWind, Tile winningTile) {
        // 如果有副露，不能是平和
        if (melds != null && !melds.isEmpty()) {
            return false;
        }

        // 检查所有面子组合
        List<Tile> remainingTiles = new ArrayList<>(handTiles);
        if (winningTile != null) {
            remainingTiles.add(winningTile);
        }

        // 检查是否有刻子
        for (int i = 0; i < remainingTiles.size() - 2; i++) {
            if (isTriplet(remainingTiles.get(i), remainingTiles.get(i + 1), remainingTiles.get(i + 2))) {
                return false;
            }
        }

        // 检查对子是否为场风或自风
        for (int i = 0; i < remainingTiles.size() - 1; i++) {
            if (remainingTiles.get(i).getType() == remainingTiles.get(i + 1).getType() &&
                remainingTiles.get(i).getNumber() == remainingTiles.get(i + 1).getNumber()) {
                // 检查是否为场风或自风
                if (remainingTiles.get(i).getType() == TileType.WIND) {
                    int windNumber = remainingTiles.get(i).getNumber();
                    if (windNumber == roundWind.ordinal() + 1 || windNumber == seatWind.ordinal() + 1) {
                        return false;
                    }
                }
                break;
            }
        }

        // 检查是否有边张或嵌张
        for (int i = 0; i < remainingTiles.size() - 2; i++) {
            if (isSequence(remainingTiles.get(i), remainingTiles.get(i + 1), remainingTiles.get(i + 2))) {
                // 检查是否为边张或嵌张
                if (remainingTiles.get(i).getNumber() == 1 || remainingTiles.get(i + 2).getNumber() == 9) {
                    return false;
                }
            }
        }

        return true;
    }
    
    /**
     * 检查断幺九
     */
    private boolean isTanyao(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查所有牌是否都是2-8的数牌
        for (Tile tile : allTiles) {
            if (tile.getType() == TileType.WIND || 
                tile.getType() == TileType.DRAGON || 
                tile.getNumber() == 1 || 
                tile.getNumber() == 9) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查三色同顺
     */
    private boolean isSanshokuDoujun(List<Tile> handTiles, List<Meld> melds) {
        // 合并手牌和副露的牌
        List<Tile> allTiles = new ArrayList<>(handTiles);
        if (melds != null) {
            for (Meld meld : melds) {
                allTiles.addAll(meld.getTiles());
            }
        }

        // 按数字分组
        Map<Integer, List<Tile>> tilesByNumber = new HashMap<>();
        for (Tile tile : allTiles) {
            if (tile.getType() != TileType.WIND && tile.getType() != TileType.DRAGON) {
                tilesByNumber.computeIfAbsent(tile.getNumber(), k -> new ArrayList<>()).add(tile);
            }
        }

        // 检查每个数字是否有三色同顺
        for (List<Tile> tiles : tilesByNumber.values()) {
            if (tiles.size() >= 3) {
                Set<TileType> types = new HashSet<>();
                for (Tile tile : tiles) {
                    types.add(tile.getType());
                }
                if (types.size() == 3) {
                    return true;
                }
            }
        }

        return false;
    }
    
    /**
     * 检查一气通贯
     */
    private boolean isIttsu(List<Tile> handTiles, List<Meld> melds) {
        // 合并手牌和副露的牌
        List<Tile> allTiles = new ArrayList<>(handTiles);
        if (melds != null) {
            for (Meld meld : melds) {
                allTiles.addAll(meld.getTiles());
            }
        }

        // 按类型分组
        Map<TileType, List<Tile>> tilesByType = new HashMap<>();
        for (Tile tile : allTiles) {
            if (tile.getType() != TileType.WIND && tile.getType() != TileType.DRAGON) {
                tilesByType.computeIfAbsent(tile.getType(), k -> new ArrayList<>()).add(tile);
            }
        }

        // 检查每种类型是否有一气通贯
        for (List<Tile> tiles : tilesByType.values()) {
            if (hasCompleteStraight(tiles)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasCompleteStraight(List<Tile> tiles) {
        // 排序
        Collections.sort(tiles, (a, b) -> a.getNumber() - b.getNumber());

        // 检查是否有123、456、789的顺子
        boolean has123 = false;
        boolean has456 = false;
        boolean has789 = false;

        for (int i = 0; i < tiles.size() - 2; i++) {
            if (isSequence(tiles.get(i), tiles.get(i + 1), tiles.get(i + 2))) {
                if (tiles.get(i).getNumber() == 1) {
                    has123 = true;
                } else if (tiles.get(i).getNumber() == 4) {
                    has456 = true;
                } else if (tiles.get(i).getNumber() == 7) {
                    has789 = true;
                }
            }
        }

        return has123 && has456 && has789;
    }
    
    /**
     * 检查对对和
     */
    private boolean isToitoi(List<Tile> handTiles, List<Meld> melds) {
        // 合并手牌和副露的牌
        List<Tile> allTiles = new ArrayList<>(handTiles);
        if (melds != null) {
            for (Meld meld : melds) {
                allTiles.addAll(meld.getTiles());
            }
        }

        // 检查所有面子组合
        List<Tile> remainingTiles = new ArrayList<>(allTiles);
        int tripletCount = 0;

        // 检查刻子
        for (int i = 0; i < remainingTiles.size() - 2; i++) {
            if (isTriplet(remainingTiles.get(i), remainingTiles.get(i + 1), remainingTiles.get(i + 2))) {
                tripletCount++;
                i += 2; // 跳过已检查的牌
            }
        }

        // 检查对子
        for (int i = 0; i < remainingTiles.size() - 1; i++) {
            if (remainingTiles.get(i).getType() == remainingTiles.get(i + 1).getType() &&
                remainingTiles.get(i).getNumber() == remainingTiles.get(i + 1).getNumber()) {
                tripletCount++;
                i++; // 跳过已检查的牌
            }
        }

        // 必须有4个刻子或对子
        return tripletCount == 4;
    }
    
    /**
     * 检查三暗刻
     */
    private boolean isSanankou(PlayerGame player) {
        List<Tile> handTiles = new ArrayList<>(player.getHandTiles());
        int ankouCount = 0;
        
        // 检查手牌中的暗刻
        for (int i = 0; i < handTiles.size() - 2; i++) {
            if (isTriplet(handTiles.get(i), handTiles.get(i + 1), handTiles.get(i + 2))) {
                ankouCount++;
                i += 2; // 跳过已检查的牌
            }
        }
        
        return ankouCount >= 3;
    }
    
    /**
     * 检查清一色
     */
    private boolean isChinitsu(List<Tile> handTiles, List<Meld> melds) {
        Set<TileType> types = new HashSet<>();

        // 检查手牌
        for (Tile tile : handTiles) {
            if (tile.getType() == TileType.WIND || tile.getType() == TileType.DRAGON) {
                return false;
            }
            types.add(tile.getType());
        }

        // 检查副露
        if (melds != null) {
            for (Meld meld : melds) {
                for (Tile tile : meld.getTiles()) {
                    if (tile.getType() == TileType.WIND || tile.getType() == TileType.DRAGON) {
                        return false;
                    }
                    types.add(tile.getType());
                }
            }
        }

        return types.size() == 1;
    }
    
    /**
     * 检查字一色
     */
    private boolean isTsuuiisou(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查是否都是字牌
        for (Tile tile : allTiles) {
            if (tile.getType() != TileType.WIND && tile.getType() != TileType.DRAGON) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查自摸
     */
    private boolean isTsumo(PlayerGame player, Game game) {
        return player.getPosition() == game.getCurrentPosition();
    }
    
    // 辅助方法
    private boolean isTwoSidedWait(List<Tile> handTiles, Tile winningTile) {
        // 检查两面
        if (winningTile.getType().isNumberTile()) {
            int number = winningTile.getNumber();
            if (number > 1 && number < 9) {
                Tile lower = new Tile(winningTile.getType(), number - 1);
                Tile upper = new Tile(winningTile.getType(), number + 1);
                return handTiles.contains(lower) && handTiles.contains(upper);
            }
        }
        return false;
    }
    
    private boolean isDoublePairWait(List<Tile> handTiles, Tile winningTile) {
        // 检查双碰
        int count = 0;
        for (Tile tile : handTiles) {
            if (tile.equals(winningTile)) {
                count++;
            }
        }
        return count >= 2;
    }
    
    private boolean hasSameNumberSequenceInOtherSuits(int number, List<Tile> handTiles) {
        // 检查其他花色是否有相同数字的顺子
        for (TileType type : new TileType[]{TileType.MANZU, TileType.PINZU, TileType.SOUZU}) {
            boolean found = false;
            for (int i = 0; i < handTiles.size() - 2; i++) {
                Tile first = handTiles.get(i);
                Tile second = handTiles.get(i + 1);
                Tile third = handTiles.get(i + 2);
                
                if (first.getType() == type && 
                    second.getType() == type && 
                    third.getType() == type &&
                    first.getNumber() == number &&
                    second.getNumber() == number + 1 &&
                    third.getNumber() == number + 2) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
    
    private boolean hasCompleteStraight(TileType type, List<Tile> handTiles) {
        // 检查是否有123、456、789的顺子
        boolean has123 = false;
        boolean has456 = false;
        boolean has789 = false;
        
        for (int i = 0; i < handTiles.size() - 2; i++) {
            Tile first = handTiles.get(i);
            Tile second = handTiles.get(i + 1);
            Tile third = handTiles.get(i + 2);
            
            if (first.getType() == type && 
                second.getType() == type && 
                third.getType() == type) {
                if (first.getNumber() == 1 && second.getNumber() == 2 && third.getNumber() == 3) {
                    has123 = true;
                } else if (first.getNumber() == 4 && second.getNumber() == 5 && third.getNumber() == 6) {
                    has456 = true;
                } else if (first.getNumber() == 7 && second.getNumber() == 8 && third.getNumber() == 9) {
                    has789 = true;
                }
            }
        }
        
        return has123 && has456 && has789;
    }
    
    /**
     * 获取可能的副露
     */
    public List<Meld> getPossibleMelds(List<Tile> handTiles, Tile discardedTile) {
        List<Meld> possibleMelds = new ArrayList<>();
        
        // 检查是否可以碰
        if (canPon(handTiles, discardedTile)) {
            possibleMelds.add(createPonMeld(handTiles, discardedTile));
        }
        
        // 检查是否可以吃
        possibleMelds.addAll(getPossibleChiMelds(handTiles, discardedTile));
        
        // 检查是否可以杠
        if (canKan(handTiles, discardedTile)) {
            possibleMelds.add(createKanMeld(handTiles, discardedTile));
        }
        
        return possibleMelds;
    }
    
    /**
     * 检查是否可以碰
     */
    private boolean canPon(List<Tile> handTiles, Tile discardedTile) {
        int count = 0;
        for (Tile tile : handTiles) {
            if (tile.getType() == discardedTile.getType() && 
                tile.getNumber() == discardedTile.getNumber()) {
                count++;
            }
        }
        return count >= 2;
    }
    
    /**
     * 创建碰副露
     */
    private Meld createPonMeld(List<Tile> handTiles, Tile discardedTile) {
        List<Tile> meldTiles = new ArrayList<>();
        meldTiles.add(discardedTile);
        
        int found = 0;
        for (Tile tile : handTiles) {
            if (tile.getType() == discardedTile.getType() && 
                tile.getNumber() == discardedTile.getNumber() && 
                found < 2) {
                meldTiles.add(tile);
                found++;
            }
        }
        
        return new Meld(MeldType.PON, meldTiles, true, (Long)null);
    }
    
    /**
     * 获取可能的吃副露
     */
    private List<Meld> getPossibleChiMelds(List<Tile> handTiles, Tile discardedTile) {
        List<Meld> chiMelds = new ArrayList<>();
        
        // 荣牌不能是字牌
        if (discardedTile.getType() == TileType.WIND || 
            discardedTile.getType() == TileType.DRAGON) {
            return chiMelds;
        }
        
        int number = discardedTile.getNumber();
        TileType type = discardedTile.getType();
        
        // 检查三种可能的顺子
        if (number <= 7) { // n, n+1, n+2
            if (hasTile(handTiles, type, number + 1) && 
                hasTile(handTiles, type, number + 2)) {
                List<Tile> meldTiles = new ArrayList<>();
                meldTiles.add(discardedTile);
                meldTiles.add(findTile(handTiles, type, number + 1));
                meldTiles.add(findTile(handTiles, type, number + 2));
                chiMelds.add(new Meld(MeldType.CHI, meldTiles, true, (Long)null));
            }
        }
        
        if (number >= 2 && number <= 8) { // n-1, n, n+1
            if (hasTile(handTiles, type, number - 1) && 
                hasTile(handTiles, type, number + 1)) {
                List<Tile> meldTiles = new ArrayList<>();
                meldTiles.add(findTile(handTiles, type, number - 1));
                meldTiles.add(discardedTile);
                meldTiles.add(findTile(handTiles, type, number + 1));
                chiMelds.add(new Meld(MeldType.CHI, meldTiles, true, (Long)null));
            }
        }
        
        if (number >= 3) { // n-2, n-1, n
            if (hasTile(handTiles, type, number - 2) && 
                hasTile(handTiles, type, number - 1)) {
                List<Tile> meldTiles = new ArrayList<>();
                meldTiles.add(findTile(handTiles, type, number - 2));
                meldTiles.add(findTile(handTiles, type, number - 1));
                meldTiles.add(discardedTile);
                chiMelds.add(new Meld(MeldType.CHI, meldTiles, true, (Long)null));
            }
        }
        
        return chiMelds;
    }
    
    /**
     * 检查是否可以杠
     */
    private boolean canKan(List<Tile> handTiles, Tile discardedTile) {
        int count = 0;
        for (Tile tile : handTiles) {
            if (tile.getType() == discardedTile.getType() && 
                tile.getNumber() == discardedTile.getNumber()) {
                count++;
            }
        }
        return count >= 3;
    }
    
    /**
     * 创建杠副露
     */
    private Meld createKanMeld(List<Tile> handTiles, Tile discardedTile) {
        List<Tile> meldTiles = new ArrayList<>();
        meldTiles.add(discardedTile);
        
        int found = 0;
        for (Tile tile : handTiles) {
            if (tile.getType() == discardedTile.getType() && 
                tile.getNumber() == discardedTile.getNumber() && 
                found < 3) {
                meldTiles.add(tile);
                found++;
            }
        }
        
        return new Meld(MeldType.KAN, meldTiles, true, (Long)null);
    }
    
    /**
     * 检查手牌中是否存在指定牌
     */
    private boolean hasTile(List<Tile> handTiles, TileType type, int number) {
        return handTiles.stream().anyMatch(t -> 
            t.getType() == type && t.getNumber() == number);
    }
    
    /**
     * 在手牌中找到指定牌
     */
    private Tile findTile(List<Tile> handTiles, TileType type, int number) {
        return handTiles.stream()
            .filter(t -> t.getType() == type && t.getNumber() == number)
            .findFirst()
            .orElse(null);
    }

    private boolean isStandardWin(List<Tile> handTiles, List<Meld> melds) {
        // 复制手牌列表
        List<Tile> tiles = new ArrayList<>(handTiles);
        
        // 添加副露的牌
        if (melds != null) {
            for (Meld meld : melds) {
                tiles.addAll(meld.getTiles());
            }
        }
        
        // 排序手牌
        Collections.sort(tiles, (a, b) -> {
            if (a.getType() != b.getType()) {
                return a.getType().ordinal() - b.getType().ordinal();
            }
            return a.getNumber() - b.getNumber();
        });
        
        // 检查是否有对子
        boolean hasPair = false;
        for (int i = 0; i < tiles.size() - 1; i++) {
            if (tiles.get(i).getType() == tiles.get(i + 1).getType() &&
                tiles.get(i).getNumber() == tiles.get(i + 1).getNumber()) {
                hasPair = true;
                break;
            }
        }
        
        if (!hasPair) {
            return false;
        }
        
        // 检查剩余牌是否都是面子（顺子或刻子）
        int i = 0;
        while (i < tiles.size() - 2) {
            Tile first = tiles.get(i);
            Tile second = tiles.get(i + 1);
            Tile third = tiles.get(i + 2);
            
            // 检查是否是刻子
            if (first.getType() == second.getType() && second.getType() == third.getType() &&
                first.getNumber() == second.getNumber() && second.getNumber() == third.getNumber()) {
                i += 3;
                continue;
            }
            
            // 检查是否是顺子
            if (first.getType() == second.getType() && second.getType() == third.getType() &&
                first.getNumber() + 1 == second.getNumber() && second.getNumber() + 1 == third.getNumber()) {
                i += 3;
                continue;
            }
            
            return false;
        }
        
        return true;
    }

    private boolean isHonitsu(List<Tile> handTiles, List<Meld> melds) {
        Set<TileType> types = new HashSet<>();
        boolean hasHonorTile = false;

        // 检查手牌
        for (Tile tile : handTiles) {
            if (tile.getType() == TileType.WIND || tile.getType() == TileType.DRAGON) {
                hasHonorTile = true;
            } else {
                types.add(tile.getType());
            }
        }

        // 检查副露
        if (melds != null) {
            for (Meld meld : melds) {
                for (Tile tile : meld.getTiles()) {
                    if (tile.getType() == TileType.WIND || tile.getType() == TileType.DRAGON) {
                        hasHonorTile = true;
                    } else {
                        types.add(tile.getType());
                    }
                }
            }
        }

        return types.size() == 1 && hasHonorTile;
    }
} 