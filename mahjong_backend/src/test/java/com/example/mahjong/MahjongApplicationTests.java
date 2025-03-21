package com.example.mahjong;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.example.mahjong.service.impl.AIServiceImplTest;
import com.example.mahjong.service.impl.GameLogicServiceImplTest;
import com.example.mahjong.service.impl.GameServiceImplTest;
import com.example.mahjong.game.AIGameTest;

/**
 * 雀鬼麻将游戏测试套件
 * 
 * 包含所有测试类：
 * - AIServiceImplTest：AI决策服务测试
 * - GameLogicServiceImplTest：麻将核心规则测试
 * - GameServiceImplTest：游戏服务测试
 * - AIGameTest：AI游戏流程测试
 */
@Suite
@SelectClasses({
    AIServiceImplTest.class,
    GameLogicServiceImplTest.class,
    GameServiceImplTest.class,
    AIGameTest.class
})
public class MahjongApplicationTests {
    // 测试套件不需要任何代码
} 