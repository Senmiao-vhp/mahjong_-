package com.example.mahjong.game;

import com.example.mahjong.entity.*;
import com.example.mahjong.service.AIService;
import com.example.mahjong.service.GameLogicService;
import com.example.mahjong.service.impl.AIServiceImpl;
import com.example.mahjong.service.impl.GameLogicServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AI对局测试类
 * 用于模拟带有AI玩家的麻将对局，测试AI玩家的决策逻辑
 */
@SpringBootTest
public class AIGameTest {

    private AIService aiService;
    
    @Mock
    private GameLogicService gameLogicService;
    
    private Game game;
    private AIPlayer aiPlayer;
    private List<PlayerGame> humanPlayers;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // 初始化AI服务
        aiService = new AIServiceImpl();
        // 通过反射设置gameLogicService
        try {
            java.lang.reflect.Field field = AIServiceImpl.class.getDeclaredField("gameLogicService");
            field.setAccessible(true);
            field.set(aiService, gameLogicService);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 初始化游戏
        game = new Game();
        game.setId(1L);
        game.setRoundWind(Wind.EAST);
        game.setDealerPosition(0);
        game.setCurrentPosition(1); // AI玩家的位置
        game.setWallPosition(0);
        game.setHonbaCount(0);
        game.setRiichiSticks(0);
        game.setStatus(GameState.PLAYING);
        
        // 初始化人类玩家
        humanPlayers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PlayerGame player = new PlayerGame();
            player.setId((long) (i + 1));
            player.setUserId((long) (i + 100));
            player.setPosition(i == 0 ? 0 : i + 1); // 位置0、2、3
            player.setIsDealer(i == 0);
            player.setScore(25000);
            player.setIsMenzen(true);
            player.setIsRiichi(false);
            player.setSeatWind(Wind.values()[i == 0 ? 0 : i+1]);
            player.setHandTiles(new ArrayList<>());
            player.setDiscardTiles(new ArrayList<>());
            player.setMelds(new ArrayList<>());
            humanPlayers.add(player);
        }
        
        // 初始化AI玩家
        aiPlayer = new AIPlayer();
        aiPlayer.setId(2L);
        aiPlayer.setGameId(1L);
        aiPlayer.setPosition(1); // 位置1
        aiPlayer.setSeatWind(Wind.SOUTH);
        aiPlayer.setIsDealer(false);
        aiPlayer.setDifficultyLevel(2); // 中等难度
        aiPlayer.setIsMenzen(true);
        aiPlayer.setIsRiichi(false);
        aiPlayer.setScore(25000);
        
        // 设置AI玩家手牌
        List<Tile> handTiles = new ArrayList<>();
        handTiles.add(new Tile(TileType.MANZU, 1));
        handTiles.add(new Tile(TileType.MANZU, 2));
        handTiles.add(new Tile(TileType.MANZU, 3));
        handTiles.add(new Tile(TileType.PINZU, 2));
        handTiles.add(new Tile(TileType.PINZU, 3));
        handTiles.add(new Tile(TileType.PINZU, 4));
        handTiles.add(new Tile(TileType.SOUZU, 3));
        handTiles.add(new Tile(TileType.SOUZU, 4));
        handTiles.add(new Tile(TileType.SOUZU, 5));
        handTiles.add(new Tile(TileType.WIND, 1)); // 东风
        handTiles.add(new Tile(TileType.WIND, 1)); // 东风
        handTiles.add(new Tile(TileType.WIND, 1)); // 东风
        handTiles.add(new Tile(TileType.DRAGON, 1)); // 白
        aiPlayer.setHandTiles(handTiles);
        
        // 设置副露
        aiPlayer.setMelds(new ArrayList<>());
        
        // 添加玩家到游戏
        List<PlayerGame> allPlayers = new ArrayList<>(humanPlayers);
        // 转换AI玩家为PlayerGame并插入到正确位置
        PlayerGame aiPlayerGame = new PlayerGame();
        aiPlayerGame.setId(aiPlayer.getId());
        aiPlayerGame.setUserId(aiPlayer.getId() + 1000); // 假设AI的userId是id+1000
        aiPlayerGame.setPosition(aiPlayer.getPosition());
        aiPlayerGame.setIsDealer(aiPlayer.getIsDealer());
        aiPlayerGame.setScore(aiPlayer.getScore());
        aiPlayerGame.setIsMenzen(aiPlayer.getIsMenzen());
        aiPlayerGame.setIsRiichi(aiPlayer.getIsRiichi());
        aiPlayerGame.setSeatWind(aiPlayer.getSeatWind());
        aiPlayerGame.setHandTiles(new ArrayList<>(aiPlayer.getHandTiles()));
        aiPlayerGame.setDiscardTiles(new ArrayList<>());
        aiPlayerGame.setMelds(new ArrayList<>(aiPlayer.getMelds()));
        
        // 将AI玩家插入到玩家列表的正确位置
        allPlayers.add(1, aiPlayerGame); // 位置1是AI玩家
        
        game.setPlayers(allPlayers);
        
        // 初始化牌山和宝牌
        game.initializeWall();
        
        // 设置GameLogicService的mock行为
        when(gameLogicService.canWin(anyList(), anyList())).thenReturn(false);
        when(gameLogicService.isTenpai(anyList(), anyList())).thenReturn(false);
        when(gameLogicService.getPossibleWaitingTiles(any(PlayerGame.class))).thenReturn(new ArrayList<>());
    }
    
    @Test
    public void testAIDecideDiscard() {
        // 测试AI打牌决策
        Tile discardTile = aiService.decideDiscard(aiPlayer, game);
        
        // 验证结果
        assertNotNull(discardTile);
        assertTrue(aiPlayer.getHandTiles().contains(discardTile));
        
        // 打印决策结果
        System.out.println("AI决定打出: " + discardTile);
    }
    
    @Test
    public void testAIDecideMeld() {
        // 测试AI副露决策
        Tile discardedTile = new Tile(TileType.MANZU, 4);
        
        // 模拟可能的副露
        Meld chiMeld = new Meld(MeldType.CHI, Arrays.asList(
            new Tile(TileType.MANZU, 2),
            new Tile(TileType.MANZU, 3),
            discardedTile
        ), false, 0L);
        
        List<Meld> possibleMelds = Arrays.asList(chiMeld);
        
        // 设置mock行为
        when(gameLogicService.getPossibleMelds(anyList(), any(Tile.class))).thenReturn(possibleMelds);
        
        // 执行AI副露决策
        Meld decidedMeld = aiService.decideMeld(aiPlayer, game, discardedTile);
        
        // 验证结果
        assertNotNull(decidedMeld);
        assertEquals(MeldType.CHI, decidedMeld.getType());
    }
    
    @Test
    public void testAIDecideRiichi() {
        // 使AI处于听牌状态
        List<Tile> waitingTiles = Arrays.asList(
            new Tile(TileType.DRAGON, 2)
        );
        
        when(gameLogicService.isTenpai(anyList(), anyList())).thenReturn(true);
        when(gameLogicService.getPossibleWaitingTiles(any(PlayerGame.class))).thenReturn(waitingTiles);
        
        // 执行AI立直决策
        boolean shouldRiichi = aiService.decideRiichi(aiPlayer, game);
        
        // 验证结果是否合理
        // 注意：这取决于AI的决策逻辑，可能是true或false
        System.out.println("AI决定是否立直: " + shouldRiichi);
    }
    
    @Test
    public void testAIDecideWin() {
        // 测试AI和牌决策
        Tile winningTile = new Tile(TileType.DRAGON, 2);
        
        // 设置手牌使其能够和牌
        when(gameLogicService.canWin(anyList(), anyList())).thenReturn(true);
        
        // 执行AI和牌决策
        boolean shouldWin = aiService.decideWin(aiPlayer, game, winningTile);
        
        // 验证结果
        assertTrue(shouldWin);
    }
    
    @Test
    public void testSimulateAIGameRound() {
        // 模拟一个简单的游戏回合，让AI做出决策
        
        // 1. AI决定打牌
        Tile discardTile = aiService.decideDiscard(aiPlayer, game);
        assertNotNull(discardTile);
        
        // 从手牌中移除打出的牌
        aiPlayer.getHandTiles().remove(discardTile);
        
        // 2. 另一个玩家打出可能被AI碰的牌
        Tile otherPlayerDiscard = new Tile(TileType.DRAGON, 1);
        
        // 模拟可能的碰
        Meld ponMeld = new Meld(MeldType.PON, Arrays.asList(
            new Tile(TileType.DRAGON, 1),
            new Tile(TileType.DRAGON, 1),
            otherPlayerDiscard
        ), false, 0L);
        
        when(gameLogicService.getPossibleMelds(anyList(), any(Tile.class)))
            .thenReturn(Arrays.asList(ponMeld));
        
        // AI决定是否副露
        Meld decidedMeld = aiService.decideMeld(aiPlayer, game, otherPlayerDiscard);
        
        if (decidedMeld != null) {
            // AI选择了副露
            System.out.println("AI选择了副露: " + decidedMeld.getType());
            
            // 执行副露，修改AI玩家的状态
            aiPlayer.getMelds().add(decidedMeld);
            
            // 从手牌中移除用于副露的牌
            for (Tile tile : decidedMeld.getTiles()) {
                if (tile != otherPlayerDiscard) {
                    aiPlayer.getHandTiles().remove(tile);
                }
            }
            
            // 补充一张牌
            Tile newTile = game.draw();
            aiPlayer.getHandTiles().add(newTile);
            
            // 再次决定打牌
            Tile newDiscardTile = aiService.decideDiscard(aiPlayer, game);
            assertNotNull(newDiscardTile);
            System.out.println("副露后AI打出: " + newDiscardTile);
        } else {
            System.out.println("AI选择跳过副露");
        }
        
        // 3. 测试结束时的状态
        assertNotNull(aiPlayer.getMelds());
    }
} 