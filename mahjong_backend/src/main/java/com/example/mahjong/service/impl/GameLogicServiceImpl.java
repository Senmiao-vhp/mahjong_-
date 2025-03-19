package com.example.mahjong.service.impl;

import com.example.mahjong.entity.*;
import com.example.mahjong.service.GameLogicService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * 游戏逻辑服务实现类
 * 负责处理麻将游戏的核心逻辑，如和牌判断、牌型分析、得分计算等
 */
@Service
public class GameLogicServiceImpl implements GameLogicService {

    private static final Logger logger = Logger.getLogger(GameLogicServiceImpl.class.getName());

    @Override
    public boolean canWin(List<Tile> handTiles, List<Meld> melds) {
        // 检查副露数量，如果超过4个则不能和牌
        if (melds != null && melds.size() > 4) {
            return false;
        }
        
        // 复制手牌列表，避免修改原始数据
        List<Tile> tiles = new ArrayList<>(handTiles);
        
        // 如果手牌数量加上副露数量乘以3不等于14，则不能和牌
        int meldTilesCount = (melds != null) ? melds.size() * 3 : 0;
        if (tiles.size() + meldTilesCount != 14) {
            return false;
        }
        
        // 处理七对子情况
        if (melds == null || melds.isEmpty()) {
            if (isSevenPairs(tiles)) {
                return true;
            }
        }
        
        // 处理十三幺情况
        if (melds == null || melds.isEmpty()) {
            if (isThirteenOrphans(tiles)) {
                return true;
            }
        }
        
        // 处理一般型和牌情况
        return isNormalWin(tiles, melds);
    }

    @Override
    public boolean isTenpai(List<Tile> handTiles, List<Meld> melds) {
        // 检查副露数量
        if (melds != null && melds.size() > 4) {
            return false;
        }
        
        // 复制手牌列表
        List<Tile> tiles = new ArrayList<>(handTiles);
        
        // 如果已经和牌，则不是听牌
        if (canWin(tiles, melds)) {
            return false;
        }
        
        // 检查是否有待牌
        for (TileType type : TileType.values()) {
            for (int number = 1; number <= type.getMaxNumber(); number++) {
                Tile testTile = new Tile(type, number);
                tiles.add(testTile);
                if (canWin(tiles, melds)) {
                    return true;
                }
                tiles.remove(testTile);
            }
        }
        
        return false;
    }

    @Override
    public List<Tile> getPossibleWaitingTiles(PlayerGame playerGame) {
        List<Tile> waitingTiles = new ArrayList<>();
        List<Tile> handTiles = new ArrayList<>(playerGame.getHandTiles());
        
        // 检查每种牌是否能够组成和牌
        for (TileType type : TileType.values()) {
            for (int number = 1; number <= type.getMaxNumber(); number++) {
                Tile testTile = new Tile(type, number);
                handTiles.add(testTile);
                if (canWin(handTiles, playerGame.getMelds())) {
                    waitingTiles.add(testTile);
                }
                handTiles.remove(testTile);
            }
        }
        
        return waitingTiles;
    }

    @Override
    public List<Meld> getPossibleMelds(List<Tile> handTiles, Tile discardedTile) {
        List<Meld> possibleMelds = new ArrayList<>();
        
        // 检查是否可以碰
        if (canPon(handTiles, discardedTile)) {
            List<Tile> ponTiles = new ArrayList<>();
            // 找出手牌中与弃牌相同的两张牌
            for (Tile tile : handTiles) {
                if (tile.getType() == discardedTile.getType() && tile.getNumber() == discardedTile.getNumber()) {
                    ponTiles.add(tile);
                    if (ponTiles.size() == 2) {
                        break;
                    }
                }
            }
            ponTiles.add(discardedTile);
            Meld ponMeld = new Meld(MeldType.PON, ponTiles, false, (Integer)null);
            possibleMelds.add(ponMeld);
        }
        
        // 检查是否可以吃
        if (canChi(handTiles, discardedTile)) {
            // 获取所有可能的吃组合
            List<List<Tile>> chiCombinations = getPossibleChiCombinations(handTiles, discardedTile);
            for (List<Tile> chiTiles : chiCombinations) {
                chiTiles.add(discardedTile);
                Meld chiMeld = new Meld(MeldType.CHI, chiTiles, false, (Integer)null);
                possibleMelds.add(chiMeld);
            }
        }
        
        // 检查是否可以杠
        if (canKan(handTiles, discardedTile)) {
            List<Tile> kanTiles = new ArrayList<>();
            // 找出手牌中与弃牌相同的三张牌
            for (Tile tile : handTiles) {
                if (tile.getType() == discardedTile.getType() && tile.getNumber() == discardedTile.getNumber()) {
                    kanTiles.add(tile);
                    if (kanTiles.size() == 3) {
                        break;
                    }
                }
            }
            kanTiles.add(discardedTile);
            Meld kanMeld = new Meld(MeldType.KAN, kanTiles, false, (Integer)null);
            possibleMelds.add(kanMeld);
        }
        
        return possibleMelds;
    }

    @Override
    public List<Yaku> calculateYaku(PlayerGame playerGame, Game game) {
        List<Yaku> yakus = new ArrayList<>();
        List<Tile> handTiles = playerGame.getHandTiles();
        List<Meld> melds = playerGame.getMelds();
        
        // 检查立直
        if (playerGame.getIsRiichi()) {
            yakus.add(new Yaku("立直", 1));
        }
        
        // 检查门前清
        if (playerGame.getIsMenzen()) {
            yakus.add(new Yaku("门前清", 1));
        }
        
        // 检查平和
        if (isPinfu(handTiles, melds)) {
            yakus.add(new Yaku("平和", 1));
        }
        
        // 检查断幺九
        if (isTanyao(handTiles, melds)) {
            yakus.add(new Yaku("断幺九", 1));
        }
        
        // 检查一杯口
        if (isIipeikou(handTiles, melds)) {
            yakus.add(new Yaku("一杯口", 1));
        }
        
        // 检查自风
        Wind seatWind = playerGame.getSeatWind();
        if (hasWindTiles(handTiles, melds, seatWind)) {
            yakus.add(new Yaku("自风", 1));
        }
        
        // 检查场风
        Wind roundWind = game.getRoundWind();
        if (hasWindTiles(handTiles, melds, roundWind)) {
            yakus.add(new Yaku("场风", 1));
        }
        
        // 检查三元牌
        if (hasDragonTiles(handTiles, melds)) {
            yakus.add(new Yaku("三元牌", 1));
        }
        
        // 检查海底摸月
        if (game.getWallTiles().isEmpty() && game.getIsTsumo()) {
            yakus.add(new Yaku("海底摸月", 1));
        }
        
        // 检查河底捞鱼
        if (game.getWallTiles().isEmpty() && !game.getIsTsumo()) {
            yakus.add(new Yaku("河底捞鱼", 1));
        }
        
        return yakus;
    }

    @Override
    public int calculateScore(List<Yaku> yakus, int basePoints, boolean isDealer, boolean isTsumo) {
        int totalHan = yakus.stream().mapToInt(Yaku::getHan).sum();
        
        // 根据翻数计算底分
        int score;
        if (totalHan >= 13) {
            // 役满
            score = isDealer ? 48000 : 32000;
        } else if (totalHan >= 11) {
            // 三倍满
            score = isDealer ? 36000 : 24000;
        } else if (totalHan >= 8) {
            // 倍满
            score = isDealer ? 24000 : 16000;
        } else if (totalHan >= 6) {
            // 跳满
            score = isDealer ? 18000 : 12000;
        } else if (totalHan >= 5) {
            // 满贯
            score = isDealer ? 12000 : 8000;
        } else {
            // 计算符数和翻数
            int points = basePoints * (int) Math.pow(2, totalHan + 2);
            if (isDealer) {
                points = points * 6 / 4; // 庄家得分为通常的1.5倍
            }
            score = (points + 99) / 100 * 100; // 向上取整到百位
        }
        
        // 自摸时得分减半，所有玩家均需要支付
        if (isTsumo) {
            score = score / 2;
        }
        
        return score;
    }

    @Override
    public boolean canChi(List<Tile> handTiles, Tile discardedTile) {
        // 字牌不能吃
        if (discardedTile.getType() == TileType.WIND || discardedTile.getType() == TileType.DRAGON) {
            return false;
        }
        
        TileType type = discardedTile.getType();
        int number = discardedTile.getNumber();
        
        // 检查是否有连续的两张牌可以组成顺子
        boolean hasLower2 = containsTile(handTiles, type, number - 2) && containsTile(handTiles, type, number - 1);
        boolean hasLower1Upper1 = containsTile(handTiles, type, number - 1) && containsTile(handTiles, type, number + 1);
        boolean hasUpper2 = containsTile(handTiles, type, number + 1) && containsTile(handTiles, type, number + 2);
        
        return hasLower2 || hasLower1Upper1 || hasUpper2;
    }

    @Override
    public boolean canPon(List<Tile> handTiles, Tile discardedTile) {
        // 计算手牌中与弃牌相同的牌的数量
        int count = 0;
        for (Tile tile : handTiles) {
            if (tile.getType() == discardedTile.getType() && tile.getNumber() == discardedTile.getNumber()) {
                count++;
            }
        }
        
        return count >= 2;
    }

    @Override
    public boolean canKan(List<Tile> handTiles, Tile discardedTile) {
        // 计算手牌中与弃牌相同的牌的数量
        int count = 0;
        for (Tile tile : handTiles) {
            if (tile.getType() == discardedTile.getType() && tile.getNumber() == discardedTile.getNumber()) {
                count++;
            }
        }
        
        return count >= 3;
    }

    @Override
    public boolean canAnkan(List<Tile> handTiles) {
        // 统计手牌中相同牌的数量
        Map<String, Integer> tileCounts = new HashMap<>();
        for (Tile tile : handTiles) {
            String key = tile.getType().toString() + "-" + tile.getNumber();
            tileCounts.put(key, tileCounts.getOrDefault(key, 0) + 1);
        }
        
        // 检查是否有四张相同的牌
        for (int count : tileCounts.values()) {
            if (count >= 4) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean canKakan(List<Tile> handTiles, List<Meld> melds) {
        if (melds == null || melds.isEmpty()) {
            return false;
        }
        
        // 检查是否有可以加杠的碰
        for (Meld meld : melds) {
            if (meld.getType() == MeldType.PON) {
                // 获取碰的牌
                Tile ponTile = meld.getTiles().get(0);
                
                // 检查手牌中是否有相同的牌
                for (Tile tile : handTiles) {
                    if (tile.getType() == ponTile.getType() && tile.getNumber() == ponTile.getNumber()) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean canRiichi(PlayerGame playerGame) {
        // 检查是否已经立直
        if (playerGame.getIsRiichi()) {
            return false;
        }
        
        // 检查是否门前清
        if (!playerGame.getIsMenzen()) {
            return false;
        }
        
        // 检查是否听牌
        return isTenpai(playerGame.getHandTiles(), playerGame.getMelds());
    }

    @Override
    public boolean isFuriten(PlayerGame playerGame, List<Tile> discardTiles) {
        // 获取待牌列表
        List<Tile> waitingTiles = getPossibleWaitingTiles(playerGame);
        
        // 检查是否有待牌出现在弃牌中
        for (Tile waitingTile : waitingTiles) {
            for (Tile discardTile : discardTiles) {
                if (waitingTile.getType() == discardTile.getType() && waitingTile.getNumber() == discardTile.getNumber()) {
                    return true;
                }
            }
        }
        
        return false;
    }

    // 私有辅助方法

    /**
     * 检查是否为七对子和牌形式
     */
    private boolean isSevenPairs(List<Tile> tiles) {
        if (tiles.size() != 14) {
            return false;
        }
        
        // 统计每种牌的数量
        Map<String, Integer> tileCounts = new HashMap<>();
        for (Tile tile : tiles) {
            String key = tile.getType().toString() + "-" + tile.getNumber();
            tileCounts.put(key, tileCounts.getOrDefault(key, 0) + 1);
        }
        
        // 检查是否全部为对子
        for (int count : tileCounts.values()) {
            if (count != 2) {
                return false;
            }
        }
        
        return tileCounts.size() == 7;
    }

    /**
     * 检查是否为十三幺和牌形式
     */
    private boolean isThirteenOrphans(List<Tile> tiles) {
        if (tiles.size() != 14) {
            return false;
        }
        
        // 统计每种幺九牌的数量
        boolean has1Manzu = false, has9Manzu = false;
        boolean has1Pinzu = false, has9Pinzu = false;
        boolean has1Souzu = false, has9Souzu = false;
        boolean hasEast = false, hasSouth = false, hasWest = false, hasNorth = false;
        boolean hasWhite = false, hasGreen = false, hasRed = false;
        
        for (Tile tile : tiles) {
            TileType type = tile.getType();
            int number = tile.getNumber();
            
            if (type == TileType.MANZU) {
                if (number == 1) has1Manzu = true;
                if (number == 9) has9Manzu = true;
            } else if (type == TileType.PINZU) {
                if (number == 1) has1Pinzu = true;
                if (number == 9) has9Pinzu = true;
            } else if (type == TileType.SOUZU) {
                if (number == 1) has1Souzu = true;
                if (number == 9) has9Souzu = true;
            } else if (type == TileType.WIND) {
                if (number == 1) hasEast = true;
                if (number == 2) hasSouth = true;
                if (number == 3) hasWest = true;
                if (number == 4) hasNorth = true;
            } else if (type == TileType.DRAGON) {
                if (number == 1) hasWhite = true;
                if (number == 2) hasGreen = true;
                if (number == 3) hasRed = true;
            }
        }
        
        return has1Manzu && has9Manzu && has1Pinzu && has9Pinzu && has1Souzu && has9Souzu
                && hasEast && hasSouth && hasWest && hasNorth && hasWhite && hasGreen && hasRed;
    }

    /**
     * 检查是否为一般型和牌形式
     */
    private boolean isNormalWin(List<Tile> tiles, List<Meld> melds) {
        // 复制手牌，避免修改原始数据
        List<Tile> handTiles = new ArrayList<>(tiles);
        
        // 计算已经组成的面子数量
        int meldCount = (melds != null) ? melds.size() : 0;
        
        // 需要找到的面子数量
        int neededSets = 4 - meldCount;
        
        // 查找可能的雀头
        for (int i = 0; i < handTiles.size() - 1; i++) {
            Tile t1 = handTiles.get(i);
            
            for (int j = i + 1; j < handTiles.size(); j++) {
                Tile t2 = handTiles.get(j);
                
                // 判断是否可以作为雀头
                if (t1.getType() == t2.getType() && t1.getNumber() == t2.getNumber()) {
                    // 创建去掉雀头的手牌副本
                    List<Tile> remainingTiles = new ArrayList<>(handTiles);
                    remainingTiles.remove(t1);
                    remainingTiles.remove(t2);
                    
                    // 检查剩余牌是否可以组成指定数量的面子
                    if (canFormSets(remainingTiles, neededSets)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * 检查是否可以组成指定数量的面子
     */
    private boolean canFormSets(List<Tile> tiles, int neededSets) {
        if (neededSets == 0) {
            return tiles.isEmpty();
        }
        
        if (tiles.isEmpty()) {
            return false;
        }
        
        // 尝试组成顺子
        if (tiles.size() >= 3) {
            Tile t1 = tiles.get(0);
            
            // 只对数牌尝试组成顺子
            if (t1.getType().isNumberTile()) {
                // 查找连续的三张牌
                Tile t2 = findTile(tiles, t1.getType(), t1.getNumber() + 1);
                Tile t3 = findTile(tiles, t1.getType(), t1.getNumber() + 2);
                
                if (t2 != null && t3 != null) {
                    // 创建去掉这三张牌的副本
                    List<Tile> remainingTiles = new ArrayList<>(tiles);
                    remainingTiles.remove(t1);
                    remainingTiles.remove(t2);
                    remainingTiles.remove(t3);
                    
                    // 递归检查剩余牌
                    if (canFormSets(remainingTiles, neededSets - 1)) {
                        return true;
                    }
                }
            }
        }
        
        // 尝试组成刻子
        if (tiles.size() >= 3) {
            Tile t1 = tiles.get(0);
            Tile t2 = findTile(tiles, t1.getType(), t1.getNumber(), t1);
            Tile t3 = findTile(tiles, t1.getType(), t1.getNumber(), t1, t2);
            
            if (t2 != null && t3 != null) {
                // 创建去掉这三张牌的副本
                List<Tile> remainingTiles = new ArrayList<>(tiles);
                remainingTiles.remove(t1);
                remainingTiles.remove(t2);
                remainingTiles.remove(t3);
                
                // 递归检查剩余牌
                if (canFormSets(remainingTiles, neededSets - 1)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * 在牌列表中查找特定类型和数字的牌
     */
    private Tile findTile(List<Tile> tiles, TileType type, int number, Tile... excludes) {
        for (Tile tile : tiles) {
            if (tile.getType() == type && tile.getNumber() == number) {
                boolean excluded = false;
                for (Tile exclude : excludes) {
                    if (exclude == tile) {
                        excluded = true;
                        break;
                    }
                }
                if (!excluded) {
                    return tile;
                }
            }
        }
        return null;
    }

    /**
     * 检查牌列表中是否包含特定类型和数字的牌
     */
    private boolean containsTile(List<Tile> tiles, TileType type, int number) {
        for (Tile tile : tiles) {
            if (tile.getType() == type && tile.getNumber() == number) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有可能的吃组合
     */
    private List<List<Tile>> getPossibleChiCombinations(List<Tile> handTiles, Tile discardedTile) {
        List<List<Tile>> combinations = new ArrayList<>();
        TileType type = discardedTile.getType();
        int number = discardedTile.getNumber();
        
        // 字牌不能吃
        if (type == TileType.WIND || type == TileType.DRAGON) {
            return combinations;
        }
        
        // 检查下2、下1的组合
        if (number >= 3 && containsTile(handTiles, type, number - 2) && containsTile(handTiles, type, number - 1)) {
            List<Tile> combination = new ArrayList<>();
            combination.add(findTile(handTiles, type, number - 2));
            combination.add(findTile(handTiles, type, number - 1));
            combinations.add(combination);
        }
        
        // 检查下1、上1的组合
        if (number >= 2 && number <= 8 && containsTile(handTiles, type, number - 1) && containsTile(handTiles, type, number + 1)) {
            List<Tile> combination = new ArrayList<>();
            combination.add(findTile(handTiles, type, number - 1));
            combination.add(findTile(handTiles, type, number + 1));
            combinations.add(combination);
        }
        
        // 检查上1、上2的组合
        if (number <= 7 && containsTile(handTiles, type, number + 1) && containsTile(handTiles, type, number + 2)) {
            List<Tile> combination = new ArrayList<>();
            combination.add(findTile(handTiles, type, number + 1));
            combination.add(findTile(handTiles, type, number + 2));
            combinations.add(combination);
        }
        
        return combinations;
    }

    /**
     * 检查是否是平和
     */
    private boolean isPinfu(List<Tile> handTiles, List<Meld> melds) {
        // 平和要求门前清
        if (melds != null && !melds.isEmpty()) {
            return false;
        }
        
        // 重新组织牌，查找雀头和顺子
        // 简化实现，只检查基本条件
        return true;
    }

    /**
     * 检查是否是断幺九
     */
    private boolean isTanyao(List<Tile> handTiles, List<Meld> melds) {
        // 检查手牌中是否有幺九牌
        for (Tile tile : handTiles) {
            if (isYaochuuTile(tile)) {
                return false;
            }
        }
        
        // 检查副露中是否有幺九牌
        if (melds != null) {
            for (Meld meld : melds) {
                for (Tile tile : meld.getTiles()) {
                    if (isYaochuuTile(tile)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }

    /**
     * 检查是否是幺九牌
     */
    private boolean isYaochuuTile(Tile tile) {
        if (tile.getType() == TileType.WIND || tile.getType() == TileType.DRAGON) {
            return true;
        }
        
        if (tile.getType().isNumberTile() && (tile.getNumber() == 1 || tile.getNumber() == 9)) {
            return true;
        }
        
        return false;
    }

    /**
     * 检查是否是一杯口
     */
    private boolean isIipeikou(List<Tile> handTiles, List<Meld> melds) {
        // 一杯口要求门前清
        if (melds != null && !melds.isEmpty()) {
            return false;
        }
        
        // 简化实现，只检查基本条件
        return true;
    }

    /**
     * 检查是否有特定风牌
     */
    private boolean hasWindTiles(List<Tile> handTiles, List<Meld> melds, Wind wind) {
        int windNumber = wind.ordinal() + 1;
        
        // 检查手牌中是否有该风牌
        for (int i = 0; i < handTiles.size() - 2; i++) {
            Tile t1 = handTiles.get(i);
            if (t1.getType() == TileType.WIND && t1.getNumber() == windNumber) {
                for (int j = i + 1; j < handTiles.size() - 1; j++) {
                    Tile t2 = handTiles.get(j);
                    if (t2.getType() == TileType.WIND && t2.getNumber() == windNumber) {
                        for (int k = j + 1; k < handTiles.size(); k++) {
                            Tile t3 = handTiles.get(k);
                            if (t3.getType() == TileType.WIND && t3.getNumber() == windNumber) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        
        // 检查副露中是否有该风牌的刻子
        if (melds != null) {
            for (Meld meld : melds) {
                if (meld.getType() == MeldType.PON || meld.getType() == MeldType.KAN) {
                    Tile tile = meld.getTiles().get(0);
                    if (tile.getType() == TileType.WIND && tile.getNumber() == windNumber) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * 检查是否有三元牌
     */
    private boolean hasDragonTiles(List<Tile> handTiles, List<Meld> melds) {
        // 检查手牌中是否有三元牌的刻子
        for (TileType type : new TileType[]{TileType.DRAGON}) {
            for (int number = 1; number <= 3; number++) {
                int count = 0;
                for (Tile tile : handTiles) {
                    if (tile.getType() == type && tile.getNumber() == number) {
                        count++;
                    }
                }
                
                if (count >= 3) {
                    return true;
                }
            }
        }
        
        // 检查副露中是否有三元牌的刻子
        if (melds != null) {
            for (Meld meld : melds) {
                if (meld.getType() == MeldType.PON || meld.getType() == MeldType.KAN) {
                    Tile tile = meld.getTiles().get(0);
                    if (tile.getType() == TileType.DRAGON) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}
