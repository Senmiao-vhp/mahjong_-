package com.example.mahjong.service.impl;

import com.example.mahjong.entity.*;
import com.example.mahjong.service.AIService;
import com.example.mahjong.service.GameLogicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AIServiceImplTest {

    @InjectMocks
    private AIServiceImpl aiService;

    @Mock
    private GameLogicService gameLogicService;

    private Game game;
    private AIPlayer aiPlayer;
    private List<Tile> handTiles;
    private List<Meld> melds;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // 创建游戏
        game = new Game();
        game.setId(1L);
        game.setCurrentPosition(1);
        game.setRoundWind(Wind.EAST);
        game.setDealerPosition(0);
        
        // 创建玩家
        List<PlayerGame> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            PlayerGame player = new PlayerGame();
            player.setId((long) (i + 1));
            player.setUserId((long) (i + 100));
            player.setPosition(i);
            player.setIsDealer(i == 0);
            player.setScore(25000);
            player.setDiscardTiles(new ArrayList<>());
            players.add(player);
        }
        game.setPlayers(players);
        
        // 设置牌山和宝牌
        game.initializeWall();
        
        // 创建AI玩家
        aiPlayer = new AIPlayer();
        aiPlayer.setId(2L);
        aiPlayer.setGameId(1L);
        aiPlayer.setPosition(1);
        aiPlayer.setSeatWind(Wind.SOUTH);
        aiPlayer.setIsDealer(false);
        aiPlayer.setDifficultyLevel(2); // 中等难度
        aiPlayer.setIsMenzen(true);
        aiPlayer.setIsRiichi(false);
        
        // 设置手牌
        handTiles = new ArrayList<>();
        // 创建一个接近听牌的手牌
        handTiles.add(new Tile(TileType.MANZU, 1));
        handTiles.add(new Tile(TileType.MANZU, 2));
        handTiles.add(new Tile(TileType.MANZU, 3));
        handTiles.add(new Tile(TileType.PINZU, 3));
        handTiles.add(new Tile(TileType.PINZU, 4));
        handTiles.add(new Tile(TileType.PINZU, 5));
        handTiles.add(new Tile(TileType.SOUZU, 7));
        handTiles.add(new Tile(TileType.SOUZU, 8));
        handTiles.add(new Tile(TileType.SOUZU, 9));
        handTiles.add(new Tile(TileType.WIND, 1)); // 东风
        handTiles.add(new Tile(TileType.WIND, 1)); // 东风
        handTiles.add(new Tile(TileType.DRAGON, 1)); // 白
        handTiles.add(new Tile(TileType.DRAGON, 2)); // 发
        
        aiPlayer.setHandTiles(handTiles);
        
        // 设置副露
        melds = new ArrayList<>();
        aiPlayer.setMelds(melds);
    }

    @Test
    public void testInitializeAIPlayer() {
        // 创建一个新的AI玩家进行初始化测试
        AIPlayer testAI = new AIPlayer();
        
        // 初始化
        aiService.initializeAIPlayer(testAI, game);
        
        // 验证初始化结果
        assertEquals(game.getId(), testAI.getGameId());
        assertEquals(game.getCurrentPosition(), testAI.getPosition());
        assertEquals(Wind.values()[game.getCurrentPosition()], testAI.getSeatWind());
        assertEquals(game.getCurrentPosition() == 0, testAI.getIsDealer());
        assertEquals(25000, testAI.getScore());
        assertTrue(testAI.getIsMenzen());
    }

    @Test
    public void testDecideDiscard() {
        // 模拟游戏逻辑服务的行为
        when(gameLogicService.isTenpai(anyList(), anyList())).thenReturn(false);
        when(gameLogicService.getPossibleWaitingTiles(any(PlayerGame.class))).thenReturn(new ArrayList<>());
        
        // 测试打牌决策
        Tile discardTile = aiService.decideDiscard(aiPlayer, game);
        
        // 验证结果
        assertNotNull(discardTile);
        assertTrue(aiPlayer.getHandTiles().contains(discardTile));
    }

    @Test
    public void testDecideRiichi() {
        // 设置听牌状态
        List<Tile> waitingTiles = Arrays.asList(
            new Tile(TileType.SOUZU, 6),
            new Tile(TileType.PINZU, 6)
        );
        
        // 模拟游戏逻辑服务的行为
        when(gameLogicService.isTenpai(anyList(), anyList())).thenReturn(true);
        when(gameLogicService.getPossibleWaitingTiles(any(PlayerGame.class))).thenReturn(waitingTiles);
        
        // 测试立直决策
        boolean shouldRiichi = aiService.decideRiichi(aiPlayer, game);
        
        // 验证结果 - 中等难度下有一定概率立直
        // 由于立直决策涉及概率，这里我们只验证方法调用
        verify(gameLogicService, atLeastOnce()).getPossibleWaitingTiles(any(PlayerGame.class));
    }

    @Test
    public void testDecideMeld() {
        // 被打出的牌
        Tile discardedTile = new Tile(TileType.MANZU, 4);
        
        // 可能的副露
        Meld pon = new Meld(MeldType.PON, Arrays.asList(
            new Tile(TileType.MANZU, 4),
            new Tile(TileType.MANZU, 4),
            discardedTile
        ), true, 0L);
        
        List<Meld> possibleMelds = Arrays.asList(pon);
        
        // 模拟游戏逻辑服务的行为
        when(gameLogicService.getPossibleMelds(anyList(), any(Tile.class))).thenReturn(possibleMelds);
        when(gameLogicService.calculateYaku(any(PlayerGame.class), any(Game.class))).thenReturn(new ArrayList<>());
        
        // 添加配对牌到手牌中
        aiPlayer.getHandTiles().add(new Tile(TileType.MANZU, 4));
        aiPlayer.getHandTiles().add(new Tile(TileType.MANZU, 4));
        
        // 测试副露决策
        Meld decidedMeld = aiService.decideMeld(aiPlayer, game, discardedTile);
        
        // 验证结果
        assertNotNull(decidedMeld);
        assertEquals(MeldType.PON, decidedMeld.getType());
    }

    @Test
    public void testDecideWin() {
        // 获胜牌
        Tile winningTile = new Tile(TileType.SOUZU, 6);
        
        // 模拟听牌状态
        List<Tile> waitingTiles = Arrays.asList(winningTile);
        
        // 模拟游戏逻辑服务的行为
        when(gameLogicService.isTenpai(anyList(), anyList())).thenReturn(true);
        when(gameLogicService.getPossibleWaitingTiles(any(PlayerGame.class))).thenReturn(waitingTiles);
        when(gameLogicService.canWin(anyList(), anyList())).thenReturn(true);
        
        // 测试和牌决策
        boolean shouldWin = aiService.decideWin(aiPlayer, game, winningTile);
        
        // 验证结果
        assertTrue(shouldWin);
    }

    @Test
    public void testCalculateWaitingTiles() {
        // 设置听牌状态
        List<Tile> expectedWaitingTiles = Arrays.asList(
            new Tile(TileType.SOUZU, 6),
            new Tile(TileType.PINZU, 6)
        );
        
        // 模拟游戏逻辑服务的行为
        when(gameLogicService.canWin(anyList(), anyList())).thenAnswer(invocation -> {
            List<Tile> tiles = invocation.getArgument(0);
            return tiles.stream().anyMatch(tile -> 
                (tile.getType() == TileType.SOUZU && tile.getNumber() == 6) ||
                (tile.getType() == TileType.PINZU && tile.getNumber() == 6)
            );
        });
        
        // 计算等待牌
        List<Tile> waitingTiles = aiService.calculateWaitingTiles(aiPlayer, game);
        
        // 验证结果
        assertNotNull(waitingTiles);
        assertFalse(waitingTiles.isEmpty());
        // 验证等待牌中包含期望的牌
        boolean containsExpectedTiles = waitingTiles.stream().anyMatch(tile -> 
            (tile.getType() == TileType.SOUZU && tile.getNumber() == 6) ||
            (tile.getType() == TileType.PINZU && tile.getNumber() == 6)
        );
        assertTrue(containsExpectedTiles);
    }

    @Test
    public void testCalculateWinProbability() {
        // 设置听牌状态
        List<Tile> waitingTiles = Arrays.asList(
            new Tile(TileType.SOUZU, 6),
            new Tile(TileType.PINZU, 6)
        );
        
        // 模拟游戏逻辑服务的行为
        when(gameLogicService.canWin(anyList(), anyList())).thenAnswer(invocation -> {
            List<Tile> tiles = invocation.getArgument(0);
            return tiles.stream().anyMatch(tile -> 
                (tile.getType() == TileType.SOUZU && tile.getNumber() == 6) ||
                (tile.getType() == TileType.PINZU && tile.getNumber() == 6)
            );
        });
        
        // 计算和牌概率
        double winProbability = aiService.calculateWinProbability(aiPlayer, game);
        
        // 验证结果
        assertTrue(winProbability >= 0.0);
        assertTrue(winProbability <= 1.0);
    }

    @Test
    public void testEvaluateHand() {
        // 模拟游戏逻辑服务的行为
        when(gameLogicService.calculateYaku(any(PlayerGame.class), any(Game.class))).thenReturn(
            Arrays.asList(new Yaku("立直", 1), new Yaku("平和", 1))
        );
        
        List<Tile> waitingTiles = Arrays.asList(
            new Tile(TileType.SOUZU, 6),
            new Tile(TileType.PINZU, 6)
        );
        
        when(gameLogicService.getPossibleWaitingTiles(any(PlayerGame.class))).thenReturn(waitingTiles);
        
        // 评估手牌
        double handScore = aiService.evaluateHand(aiPlayer, game);
        
        // 验证结果
        assertTrue(handScore > 0.0);
    }

    @Test
    public void testDifferentDifficultyLevels() {
        // 测试不同难度等级
        for (int level = 1; level <= 3; level++) {
            aiPlayer.setDifficultyLevel(level);
            
            // 模拟游戏逻辑服务
            List<Tile> waitingTiles = Arrays.asList(
                new Tile(TileType.SOUZU, 6),
                new Tile(TileType.PINZU, 6)
            );
            
            when(gameLogicService.getPossibleWaitingTiles(any(PlayerGame.class))).thenReturn(waitingTiles);
            when(gameLogicService.isTenpai(anyList(), anyList())).thenReturn(true);
            
            // 测试立直决策
            boolean shouldRiichi = aiService.decideRiichi(aiPlayer, game);
            
            // 验证结果 - 不同难度下的行为应该有所不同，但这里我们只验证方法调用
            verify(gameLogicService, atLeastOnce()).getPossibleWaitingTiles(any(PlayerGame.class));
        }
    }
} 