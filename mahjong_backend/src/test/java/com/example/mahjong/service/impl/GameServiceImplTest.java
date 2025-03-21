package com.example.mahjong.service.impl;

import com.example.mahjong.entity.*;
import com.example.mahjong.mapper.GameMapper;
import com.example.mahjong.mapper.PlayerGameMapper;
import com.example.mahjong.service.AIService;
import com.example.mahjong.service.GameLogicService;
import com.example.mahjong.service.GameWebSocketService;
import com.example.mahjong.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
public class GameServiceImplTest {

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private PlayerGameMapper playerGameMapper;

    @Mock
    private GameWebSocketService webSocketService;

    @Mock
    private GameLogicService gameLogicService;

    @Mock
    private AIService aiService;

    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    private Game game;
    private List<PlayerGame> players;
    private Long roomId = 1L;
    private List<Long> userIds = Arrays.asList(101L, 102L, 103L, 104L);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // 初始化游戏
        game = new Game();
        game.setId(1L);
        game.setRoomId(roomId);
        game.setStatus(GameState.PLAYING);
        game.setRoundWind(Wind.EAST);
        game.setDealerPosition(0);
        game.setCurrentPosition(0);
        game.setWallPosition(0);
        game.setHonbaCount(0);
        game.setRiichiSticks(0);
        game.setNormalEnd(false);
        game.setIsTsumo(false);
        
        // 初始化玩家
        players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            PlayerGame player = new PlayerGame();
            player.setId((long) (i + 1));
            player.setUserId(userIds.get(i));
            player.setPosition(i);
            player.setIsDealer(i == 0);
            player.setScore(25000);
            player.setIsMenzen(true);
            player.setIsRiichi(false);
            player.setSeatWind(Wind.values()[i]);
            player.setHandTiles(new ArrayList<>());
            player.setDiscardTiles(new ArrayList<>());
            player.setMelds(new ArrayList<>());
            players.add(player);
        }
        game.setPlayers(players);
        
        // 初始化牌山
        game.initializeWall();
        
        // 模拟GameMapper的行为
        when(gameMapper.selectById(anyLong())).thenReturn(game);
        when(playerGameMapper.selectByGameId(anyLong())).thenReturn(players);
    }

    @Test
    public void testCreateGame() {
        // 配置游戏
        GameConfig config = new GameConfig();
        config.setAiPlayerCount(0);
        config.setAiDifficultyLevel(2);
        
        // 模拟游戏创建
        doReturn(1).when(gameMapper).insert(any(Game.class));
        doReturn(1).when(playerGameMapper).insert(any(PlayerGame.class));
        
        // 执行创建游戏
        Game createdGame = gameService.createGame(roomId, userIds.get(0), config);
        
        // 验证结果
        assertNotNull(createdGame);
        assertEquals(roomId, createdGame.getRoomId());
        assertEquals(GameState.WAITING, createdGame.getStatus());
        assertEquals(Wind.EAST, createdGame.getRoundWind());
        assertEquals(0, createdGame.getDealerPosition());
    }

    @Test
    public void testGetGameDetails() {
        // 模拟数据
        when(gameLogicService.getPossibleWaitingTiles(any(PlayerGame.class))).thenReturn(new ArrayList<>());
        
        // 执行获取游戏详情
        Map<String, Object> details = gameService.getGameDetails(game.getId());
        
        // 验证结果
        assertNotNull(details);
        assertEquals(game.getId(), details.get("id"));
        assertEquals(game.getStatus().name(), details.get("status"));
        assertEquals(game.getCurrentPosition(), details.get("current_position"));
        assertNotNull(details.get("players"));
    }

    @Test
    public void testDiscardTile() {
        // 准备测试数据
        Long userId = userIds.get(0);
        String tileStr = "1万";
        Tile tile = new Tile(TileType.MANZU, 1);
        
        // 模拟玩家手牌
        players.get(0).setHandTiles(Arrays.asList(
            new Tile(TileType.MANZU, 1),
            new Tile(TileType.MANZU, 2),
            new Tile(TileType.MANZU, 3)
        ));
        
        // 模拟游戏服务行为
        when(gameLogicService.canWin(anyList(), anyList())).thenReturn(false);
        
        // 执行打牌操作
        Map<String, Object> result = gameService.discardTile(game.getId(), userId, tileStr, false);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("discard", result.get("action"));
        verify(webSocketService, times(1)).broadcastGameState(eq(game.getId()), any(Map.class));
    }

    @Test
    public void testRiichi() {
        // 准备测试数据
        Long userId = userIds.get(0);
        String tileStr = "1万";
        Tile tile = new Tile(TileType.MANZU, 1);
        
        // 模拟玩家手牌
        players.get(0).setHandTiles(Arrays.asList(
            new Tile(TileType.MANZU, 1),
            new Tile(TileType.MANZU, 2),
            new Tile(TileType.MANZU, 3)
        ));
        
        // 模拟游戏服务行为
        when(gameLogicService.canRiichi(any(PlayerGame.class))).thenReturn(true);
        
        // 执行立直操作
        Map<String, Object> result = gameService.riichi(game.getId(), userId, tileStr);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("riichi", result.get("action"));
        verify(webSocketService, times(1)).broadcastGameState(eq(game.getId()), any(Map.class));
    }

    @Test
    public void testGetGameSettlement() {
        // 模拟游戏结算数据
        game.setStatus(GameState.FINISHED);
        game.setNormalEnd(true);
        game.setWinner(userIds.get(0));
        game.setIsTsumo(true);
        
        // 模拟游戏服务行为
        when(gameLogicService.calculateYaku(any(PlayerGame.class), any(Game.class)))
            .thenReturn(Arrays.asList(new Yaku("立直", 1), new Yaku("自摸", 1)));
        when(gameLogicService.calculateScore(anyList(), anyInt(), anyBoolean(), anyBoolean()))
            .thenReturn(3000);
        
        // 执行获取游戏结算
        Map<String, Object> settlement = gameService.getGameSettlement(game.getId());
        
        // 验证结果
        assertNotNull(settlement);
        assertEquals("end", settlement.get("type"));
        assertNotNull(settlement.get("players"));
        verify(webSocketService, times(1)).broadcastGameState(eq(game.getId()), any(Map.class));
    }

    @Test
    public void testPerformAction() {
        // 准备测试数据
        Long userId = userIds.get(0);
        String actionType = "chi";
        List<String> tiles = Arrays.asList("1万", "2万", "3万");
        
        // 模拟游戏服务行为
        when(gameLogicService.canChi(anyList(), any(Tile.class))).thenReturn(true);
        
        // 执行玩家操作
        Map<String, Object> result = gameService.performAction(game.getId(), userId, actionType, tiles);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(actionType, result.get("action"));
        verify(webSocketService, times(1)).broadcastGameState(eq(game.getId()), any(Map.class));
    }

    @Test
    public void testSimulateFullGameRound() {
        // 配置游戏
        GameConfig config = new GameConfig();
        config.setAiPlayerCount(0);
        config.setAiDifficultyLevel(2);
        
        // 模拟游戏创建和开始
        doReturn(1).when(gameMapper).insert(any(Game.class));
        doReturn(1).when(playerGameMapper).insert(any(PlayerGame.class));
        
        // 1. 创建游戏
        Game testGame = gameService.createGame(roomId, userIds.get(0), config);
        assertNotNull(testGame);
        
        // 2. 开始游戏
        testGame.setStatus(GameState.PLAYING);
        testGame.start();
        assertEquals(GameState.PLAYING, testGame.getStatus());
        
        // 3. 玩家1打出一张牌
        String tileStr = "1万";
        when(gameLogicService.canWin(anyList(), anyList())).thenReturn(false);
        Map<String, Object> discardResult = gameService.discardTile(testGame.getId(), userIds.get(0), tileStr, false);
        assertNotNull(discardResult);
        
        // 4. 玩家2进行碰操作
        List<String> ponTiles = Arrays.asList("1万", "1万", "1万");
        when(gameLogicService.canPon(anyList(), any(Tile.class))).thenReturn(true);
        Map<String, Object> ponResult = gameService.performAction(testGame.getId(), userIds.get(1), "pon", ponTiles);
        assertNotNull(ponResult);
        
        // 5. 玩家2打出一张牌
        String discardAfterPon = "2万";
        Map<String, Object> discardAfterPonResult = gameService.discardTile(testGame.getId(), userIds.get(1), discardAfterPon, false);
        assertNotNull(discardAfterPonResult);
        
        // 6. 玩家3立直
        String riichiTile = "3万";
        when(gameLogicService.canRiichi(any(PlayerGame.class))).thenReturn(true);
        Map<String, Object> riichiResult = gameService.riichi(testGame.getId(), userIds.get(2), riichiTile);
        assertNotNull(riichiResult);
        
        // 7. 玩家4打出和牌
        String winningTile = "4万";
        testGame.setWinner(userIds.get(2));
        testGame.setIsTsumo(false);
        testGame.setStatus(GameState.FINISHED);
        testGame.setNormalEnd(true);
        when(gameLogicService.canWin(anyList(), anyList())).thenReturn(true);
        when(gameLogicService.calculateYaku(any(PlayerGame.class), any(Game.class)))
            .thenReturn(Arrays.asList(new Yaku("立直", 1), new Yaku("平和", 1)));
        when(gameLogicService.calculateScore(anyList(), anyInt(), anyBoolean(), anyBoolean()))
            .thenReturn(3000);
        
        // 8. 获取游戏结算
        Map<String, Object> settlement = gameService.getGameSettlement(testGame.getId());
        assertNotNull(settlement);
        assertEquals("end", settlement.get("type"));
        
        // 验证整个对局流程
        verify(webSocketService, atLeastOnce()).broadcastGameState(eq(testGame.getId()), any(Map.class));
    }
} 