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
        // 复制手牌列表，避免修改原始数据
        List<Tile> tiles = new ArrayList<>(handTiles);
        
        // 添加副露的牌
        for (Meld meld : melds) {
            tiles.addAll(meld.getTiles());
        }
        
        // 检查特殊和牌型
        if (isSevenPairs(tiles)) {
            return true;
        }
        
        if (isThirteenOrphans(tiles)) {
            return true;
        }
        
        // 检查普通和牌型
        return checkNormalWin(tiles);
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
        for (int i = 1; i <= 7; i++) {
            requiredTiles.add(new Tile(TileType.HONOR, i));
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
        if (first.getType() == TileType.HONOR) {
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
    public List<Yaku> calculateYaku(PlayerGame player, Game game, Tile winningTile) {
        List<Yaku> yakus = new ArrayList<>();
        
        // 检查基本役种
        if (isPinfu(player, winningTile)) {
            yakus.add(new Yaku("平和", 1));
        }
        if (isTanyao(player)) {
            yakus.add(new Yaku("断幺九", 1));
        }
        if (isSanshoku(player)) {
            yakus.add(new Yaku("三色同顺", 2));
        }
        if (isIttsu(player)) {
            yakus.add(new Yaku("一气通贯", 2));
        }
        if (isToitoi(player)) {
            yakus.add(new Yaku("对对和", 2));
        }
        if (isSanankou(player)) {
            yakus.add(new Yaku("三暗刻", 2));
        }
        if (isChinitsu(player)) {
            yakus.add(new Yaku("清一色", 6));
        }
        if (isTsuuiisou(player)) {
            yakus.add(new Yaku("字一色", 13));
        }
        if (isTsumo(player, game)) {
            yakus.add(new Yaku("自摸", 1));
        }
        
        // 检查特殊役种
        if (isSanshokudoukou(player)) {
            yakus.add(new Yaku("三色同刻", 2));
        }
        if (isSankantsu(player)) {
            yakus.add(new Yaku("三杠子", 2));
        }
        if (isShousangen(player)) {
            yakus.add(new Yaku("小三元", 2));
        }
        if (isDaisangen(player)) {
            yakus.add(new Yaku("大三元", 13));
        }
        if (isShousuushi(player)) {
            yakus.add(new Yaku("小四喜", 13));
        }
        if (isDaisuushi(player)) {
            yakus.add(new Yaku("大四喜", 26));
        }
        if (isRyuuiisou(player)) {
            yakus.add(new Yaku("绿一色", 13));
        }
        if (isChinroutou(player)) {
            yakus.add(new Yaku("清老头", 13));
        }
        if (isChuurenpoutou(player)) {
            yakus.add(new Yaku("九莲宝灯", 13));
        }
        if (isHonroutou(player)) {
            yakus.add(new Yaku("混老头", 2));
        }
        if (isSuuankou(player)) {
            yakus.add(new Yaku("四暗刻", 13));
        }
        if (isTenhou(player)) {
            yakus.add(new Yaku("天和", 13));
        }
        
        // 检查立直相关役种
        if (player.getIsRiichi()) {
            yakus.add(new Yaku("立直", 1));
            if (player.getIsIppatsu()) {
                yakus.add(new Yaku("一发", 1));
            }
        }
        if (player.getIsDoubleRiichi()) {
            yakus.add(new Yaku("双立直", 2));
        }
        
        return yakus;
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
    private boolean isPinfu(PlayerGame player, Tile winningTile) {
        // 平和的条件：
        // 1. 没有副露
        if (!player.getMelds().isEmpty()) {
            return false;
        }
        
        // 2. 所有面子都是顺子
        List<Tile> handTiles = new ArrayList<>(player.getHandTiles());
        handTiles.add(winningTile);
        
        // 排序手牌
        Collections.sort(handTiles, (a, b) -> {
            if (a.getType() != b.getType()) {
                return a.getType().ordinal() - b.getType().ordinal();
            }
            return a.getNumber() - b.getNumber();
        });
        
        // 检查是否都是顺子
        for (int i = 0; i < handTiles.size(); i += 3) {
            Tile first = handTiles.get(i);
            Tile second = handTiles.get(i + 1);
            Tile third = handTiles.get(i + 2);
            
            // 检查是否是顺子
            if (!isSequence(first, second, third)) {
                return false;
            }
        }
        
        // 3. 和牌是两面或双碰
        return isTwoSidedWait(handTiles, winningTile) || isDoublePairWait(handTiles, winningTile);
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
            if (tile.getType() == TileType.HONOR || 
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
    private boolean isSanshoku(PlayerGame player) {
        List<Tile> handTiles = new ArrayList<>(player.getHandTiles());
        
        // 检查手牌中的顺子
        for (int i = 0; i < handTiles.size() - 2; i++) {
            for (int j = i + 1; j < handTiles.size() - 1; j++) {
                for (int k = j + 1; k < handTiles.size(); k++) {
                    Tile first = handTiles.get(i);
                    Tile second = handTiles.get(j);
                    Tile third = handTiles.get(k);
                    
                    if (isSequence(first, second, third)) {
                        // 检查是否有相同数字的顺子在其他花色中
                        if (hasSameNumberSequenceInOtherSuits(first.getNumber(), handTiles)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * 检查一气通贯
     */
    private boolean isIttsu(PlayerGame player) {
        List<Tile> handTiles = new ArrayList<>(player.getHandTiles());
        
        // 检查每种花色是否有123、456、789的顺子
        for (TileType type : new TileType[]{TileType.MANZU, TileType.PINZU, TileType.SOUZU}) {
            if (hasCompleteStraight(type, handTiles)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查对对和
     */
    private boolean isToitoi(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查所有面子是否都是刻子
        for (int i = 0; i < allTiles.size(); i += 3) {
            Tile first = allTiles.get(i);
            Tile second = allTiles.get(i + 1);
            Tile third = allTiles.get(i + 2);
            
            if (!isTriplet(first, second, third)) {
                return false;
            }
        }
        
        return true;
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
    private boolean isChinitsu(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查是否都是同一种花色
        TileType firstType = allTiles.get(0).getType();
        for (Tile tile : allTiles) {
            if (tile.getType() != firstType) {
                return false;
            }
        }
        
        return true;
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
            if (tile.getType() != TileType.HONOR) {
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
     * 检查三色同刻
     */
    private boolean isSanshokudoukou(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查每种花色是否有相同数字的刻子
        for (int number = 1; number <= 9; number++) {
            boolean hasManzu = false;
            boolean hasPinzu = false;
            boolean hasSouzu = false;
            
            for (int i = 0; i < allTiles.size() - 2; i++) {
                Tile first = allTiles.get(i);
                Tile second = allTiles.get(i + 1);
                Tile third = allTiles.get(i + 2);
                
                if (isTriplet(first, second, third) && first.getNumber() == number) {
                    switch (first.getType()) {
                        case MANZU:
                            hasManzu = true;
                            break;
                        case PINZU:
                            hasPinzu = true;
                            break;
                        case SOUZU:
                            hasSouzu = true;
                            break;
                    }
                }
            }
            
            if (hasManzu && hasPinzu && hasSouzu) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查三杠子
     */
    private boolean isSankantsu(PlayerGame player) {
        int kantsuCount = 0;
        
        // 检查手牌中的杠子
        for (Meld meld : player.getMelds()) {
            if (meld.getType() == MeldType.KAN) {
                kantsuCount++;
            }
        }
        
        return kantsuCount >= 3;
    }
    
    /**
     * 检查小三元
     */
    private boolean isShousangen(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查白、发、中的刻子
        boolean hasHaku = false;
        boolean hasHatsu = false;
        boolean hasChun = false;
        
        for (int i = 0; i < allTiles.size() - 2; i++) {
            Tile first = allTiles.get(i);
            Tile second = allTiles.get(i + 1);
            Tile third = allTiles.get(i + 2);
            
            if (isTriplet(first, second, third)) {
                if (first.getType() == TileType.HONOR) {
                    switch (first.getNumber()) {
                        case 5: // 白
                            hasHaku = true;
                            break;
                        case 6: // 发
                            hasHatsu = true;
                            break;
                        case 7: // 中
                            hasChun = true;
                            break;
                    }
                }
            }
        }
        
        // 小三元需要两个刻子加一个对子
        return (hasHaku && hasHatsu && hasChun) || 
               (hasHaku && hasHatsu && hasChunPair(allTiles)) ||
               (hasHaku && hasHatsuPair(allTiles) && hasChun) ||
               (hasHakuPair(allTiles) && hasHatsu && hasChun);
    }
    
    /**
     * 检查大三元
     */
    private boolean isDaisangen(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查白、发、中的刻子
        boolean hasHaku = false;
        boolean hasHatsu = false;
        boolean hasChun = false;
        
        for (int i = 0; i < allTiles.size() - 2; i++) {
            Tile first = allTiles.get(i);
            Tile second = allTiles.get(i + 1);
            Tile third = allTiles.get(i + 2);
            
            if (isTriplet(first, second, third)) {
                if (first.getType() == TileType.HONOR) {
                    switch (first.getNumber()) {
                        case 5: // 白
                            hasHaku = true;
                            break;
                        case 6: // 发
                            hasHatsu = true;
                            break;
                        case 7: // 中
                            hasChun = true;
                            break;
                    }
                }
            }
        }
        
        return hasHaku && hasHatsu && hasChun;
    }
    
    /**
     * 检查小四喜
     */
    private boolean isShousuushi(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查东南西北的刻子
        boolean hasTon = false;
        boolean hasNan = false;
        boolean hasShaa = false;
        boolean hasPei = false;
        
        for (int i = 0; i < allTiles.size() - 2; i++) {
            Tile first = allTiles.get(i);
            Tile second = allTiles.get(i + 1);
            Tile third = allTiles.get(i + 2);
            
            if (isTriplet(first, second, third)) {
                if (first.getType() == TileType.HONOR) {
                    switch (first.getNumber()) {
                        case 1: // 东
                            hasTon = true;
                            break;
                        case 2: // 南
                            hasNan = true;
                            break;
                        case 3: // 西
                            hasShaa = true;
                            break;
                        case 4: // 北
                            hasPei = true;
                            break;
                    }
                }
            }
        }
        
        // 小四喜需要三个刻子加一个对子
        return (hasTon && hasNan && hasShaa && hasPeiPair(allTiles)) ||
               (hasTon && hasNan && hasShaaPair(allTiles) && hasPei) ||
               (hasTon && hasNanPair(allTiles) && hasShaa && hasPei) ||
               (hasTonPair(allTiles) && hasNan && hasShaa && hasPei);
    }
    
    /**
     * 检查大四喜
     */
    private boolean isDaisuushi(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查东南西北的刻子
        boolean hasTon = false;
        boolean hasNan = false;
        boolean hasShaa = false;
        boolean hasPei = false;
        
        for (int i = 0; i < allTiles.size() - 2; i++) {
            Tile first = allTiles.get(i);
            Tile second = allTiles.get(i + 1);
            Tile third = allTiles.get(i + 2);
            
            if (isTriplet(first, second, third)) {
                if (first.getType() == TileType.HONOR) {
                    switch (first.getNumber()) {
                        case 1: // 东
                            hasTon = true;
                            break;
                        case 2: // 南
                            hasNan = true;
                            break;
                        case 3: // 西
                            hasShaa = true;
                            break;
                        case 4: // 北
                            hasPei = true;
                            break;
                    }
                }
            }
        }
        
        return hasTon && hasNan && hasShaa && hasPei;
    }
    
    /**
     * 检查绿一色
     */
    private boolean isRyuuiisou(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查是否都是绿牌（2,3,4,6,8索和发）
        for (Tile tile : allTiles) {
            if (tile.getType() == TileType.SOUZU) {
                if (tile.getNumber() != 2 && tile.getNumber() != 3 && 
                    tile.getNumber() != 4 && tile.getNumber() != 6 && 
                    tile.getNumber() != 8) {
                    return false;
                }
            } else if (tile.getType() == TileType.HONOR && tile.getNumber() != 6) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查清老头
     */
    private boolean isChinroutou(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查是否都是老头牌（1,9万、1,9筒、1,9索）
        for (Tile tile : allTiles) {
            if (tile.getType().isNumberTile()) {
                if (tile.getNumber() != 1 && tile.getNumber() != 9) {
                    return false;
                }
            } else {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查九莲宝灯
     */
    private boolean isChuurenpoutou(PlayerGame player) {
        // 九莲宝灯必须是清一色
        if (!isChinitsu(player)) {
            return false;
        }
        
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查是否符合九莲宝灯的要求
        TileType type = allTiles.get(0).getType();
        int[] counts = new int[9];
        
        // 统计每种牌的数量
        for (Tile tile : allTiles) {
            counts[tile.getNumber() - 1]++;
        }
        
        // 检查是否符合九莲宝灯的要求
        return counts[0] >= 3 && counts[8] >= 3 && // 1和9至少3张
               counts[1] >= 1 && counts[2] >= 1 && counts[3] >= 1 && // 2-8至少1张
               counts[4] >= 1 && counts[5] >= 1 && counts[6] >= 1 && counts[7] >= 1;
    }
    
    /**
     * 检查混老头
     */
    private boolean isHonroutou(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 检查是否都是老头牌（1,9万、1,9筒、1,9索）和字牌
        for (Tile tile : allTiles) {
            if (tile.getType().isNumberTile()) {
                if (tile.getNumber() != 1 && tile.getNumber() != 9) {
                    return false;
                }
            } else if (tile.getType() != TileType.HONOR) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查四暗刻
     */
    private boolean isSuuankou(PlayerGame player) {
        List<Tile> allTiles = new ArrayList<>(player.getHandTiles());
        
        // 添加副露的牌
        for (Meld meld : player.getMelds()) {
            allTiles.addAll(meld.getTiles());
        }
        
        // 统计刻子数量
        int koutsuCount = 0;
        
        // 检查手牌中的刻子
        for (int i = 0; i < allTiles.size() - 2; i++) {
            Tile first = allTiles.get(i);
            Tile second = allTiles.get(i + 1);
            Tile third = allTiles.get(i + 2);
            
            if (isTriplet(first, second, third)) {
                koutsuCount++;
                i += 2; // 跳过已检查的牌
            }
        }
        
        return koutsuCount >= 4;
    }
    
    /**
     * 检查天和
     */
    private boolean isTenhou(PlayerGame player) {
        // 天和必须是庄家
        if (!player.getIsDealer()) {
            return false;
        }
        
        // 天和必须是第一巡自摸
        return player.getIsFirstTurn() && isTsumo(player, null);
    }
    
    // 辅助方法
    private boolean hasChunPair(List<Tile> tiles) {
        return hasPair(tiles, TileType.HONOR, 7);
    }
    
    private boolean hasHatsuPair(List<Tile> tiles) {
        return hasPair(tiles, TileType.HONOR, 6);
    }
    
    private boolean hasHakuPair(List<Tile> tiles) {
        return hasPair(tiles, TileType.HONOR, 5);
    }
    
    private boolean hasPeiPair(List<Tile> tiles) {
        return hasPair(tiles, TileType.HONOR, 4);
    }
    
    private boolean hasShaaPair(List<Tile> tiles) {
        return hasPair(tiles, TileType.HONOR, 3);
    }
    
    private boolean hasNanPair(List<Tile> tiles) {
        return hasPair(tiles, TileType.HONOR, 2);
    }
    
    private boolean hasTonPair(List<Tile> tiles) {
        return hasPair(tiles, TileType.HONOR, 1);
    }
    
    private boolean hasPair(List<Tile> tiles, TileType type, int number) {
        for (int i = 0; i < tiles.size() - 1; i++) {
            Tile first = tiles.get(i);
            Tile second = tiles.get(i + 1);
            if (first.getType() == type && second.getType() == type &&
                first.getNumber() == number && second.getNumber() == number) {
                return true;
            }
        }
        return false;
    }
} 