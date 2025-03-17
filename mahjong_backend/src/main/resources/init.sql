use mahjong;

CREATE TABLE `room` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `room_name` varchar(50) NOT NULL COMMENT '房间名称',
  `room_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '房间类型：0-普通房间，1-比赛房间',
  `game_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '游戏类型：0-东风战，1-半庄战',
  `has_red_dora` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有赤宝牌：0-无，1-有',
  `has_open_tanyao` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有食断：0-无，1-有',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '房间状态：0-等待中，1-游戏中，2-已结束',
  `max_player_count` tinyint(4) NOT NULL DEFAULT 4 COMMENT '最大玩家数',
  `current_player_count` tinyint(4) NOT NULL DEFAULT 0 COMMENT '当前玩家数',
  `creator_id` bigint(20) NOT NULL COMMENT '创建者ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

CREATE TABLE `game` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '对局ID',
  `room_id` bigint(20) NOT NULL COMMENT '房间ID',
  `round_wind` tinyint(4) NOT NULL DEFAULT 0 COMMENT '场风：0-东，1-南，2-西，3-北',
  `dealer_position` tinyint(4) NOT NULL COMMENT '庄家位置：0-东，1-南，2-西，3-北',
  `current_position` tinyint(4) NOT NULL COMMENT '当前行动位置：0-东，1-南，2-西，3-北',
  `honba_count` int(11) NOT NULL DEFAULT 0 COMMENT '本场数',
  `riichi_stick_count` int(11) NOT NULL DEFAULT 0 COMMENT '立直棒数',
  `wall_tiles` text COMMENT '牌山序列（JSON格式）',
  `dora_indicators` varchar(255) COMMENT '宝牌指示牌序列（JSON格式）',
  `ura_dora_indicators` varchar(255) COMMENT '里宝牌指示牌序列（JSON格式）',
  `kan_dora_indicators` varchar(255) COMMENT '杠宝牌指示牌序列（JSON格式）',
  `wall_position` int(11) NOT NULL DEFAULT 0 COMMENT '当前摸牌位置',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '对局状态：0-准备中，1-进行中，2-已结束',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_room_id` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对局表';

CREATE TABLE `player_game` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '玩家对局ID',
  `game_id` bigint(20) NOT NULL COMMENT '对局ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `position` tinyint(4) NOT NULL COMMENT '座位位置：0-东，1-南，2-西，3-北',
  `is_dealer` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否庄家：0-否，1-是',
  `score` int(11) NOT NULL DEFAULT 25000 COMMENT '当前点数',
  `is_riichi` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否立直：0-否，1-是',
  `is_double_riichi` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否双立直：0-否，1-是',
  `is_ippatsu` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否一发：0-否，1-是',
  `is_furiten` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否振听：0-否，1-是',
  `is_temporary_furiten` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否临时振听：0-否，1-是',
  `is_menzen` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否门前清：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_game_user` (`game_id`, `user_id`),
  INDEX `idx_game_id` (`game_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家对局表';

CREATE TABLE `hand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '手牌ID',
  `player_game_id` bigint(20) NOT NULL COMMENT '玩家对局ID',
  `tiles` varchar(255) NOT NULL COMMENT '手牌序列（JSON格式）',
  `drawn_tile` varchar(10) DEFAULT NULL COMMENT '摸到的牌',
  `tenpai_tiles` varchar(255) DEFAULT NULL COMMENT '听牌列表（JSON格式）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_player_game_id` (`player_game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手牌表';

CREATE TABLE `discard_pile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '牌河ID',
  `player_game_id` bigint(20) NOT NULL COMMENT '玩家对局ID',
  `tiles` text NOT NULL COMMENT '牌河序列（JSON格式，包含状态信息）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_player_game_id` (`player_game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牌河表';

CREATE TABLE `meld` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '副露ID',
  `player_game_id` bigint(20) NOT NULL COMMENT '玩家对局ID',
  `meld_type` tinyint(4) NOT NULL COMMENT '副露类型：0-吃，1-碰，2-明杠，3-暗杠，4-加杠',
  `tiles` varchar(255) NOT NULL COMMENT '副露牌组（JSON格式）',
  `from_position` tinyint(4) DEFAULT NULL COMMENT '来源玩家位置：0-东，1-南，2-西，3-北',
  `called_tile` varchar(10) DEFAULT NULL COMMENT '副露的牌',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_player_game_id` (`player_game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='副露表';

CREATE TABLE `game_action_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `game_id` bigint(20) NOT NULL COMMENT '对局ID',
  `player_game_id` bigint(20) DEFAULT NULL COMMENT '玩家对局ID',
  `action_type` tinyint(4) NOT NULL COMMENT '操作类型：0-摸牌，1-打牌，2-吃，3-碰，4-杠，5-立直，6-和牌，7-流局等',
  `action_data` text COMMENT '操作数据（JSON格式）',
  `action_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  INDEX `idx_game_id` (`game_id`),
  INDEX `idx_player_game_id` (`player_game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对局操作日志表';

CREATE TABLE `game_current_state` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '状态ID',
  `game_id` bigint(20) NOT NULL COMMENT '对局ID',
  `current_position` tinyint(4) NOT NULL COMMENT '当前行动位置：0-东，1-南，2-西，3-北',
  `current_action` tinyint(4) NOT NULL COMMENT '当前动作：0-摸牌，1-打牌，2-等待响应',
  `available_actions` varchar(255) DEFAULT NULL COMMENT '可用操作列表（JSON格式）',
  `last_discard_tile` varchar(10) DEFAULT NULL COMMENT '最后打出的牌',
  `last_discard_position` tinyint(4) DEFAULT NULL COMMENT '最后打牌的位置',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_game_id` (`game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对局当前状态表';

CREATE TABLE `settlement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '结算ID',
  `game_id` bigint(20) NOT NULL COMMENT '对局ID',
  `settlement_type` tinyint(4) NOT NULL COMMENT '结算类型：0-和牌，1-流局',
  `settlement_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结算时间',
  `honba_count` int(11) NOT NULL DEFAULT 0 COMMENT '本场数',
  `riichi_stick_count` int(11) NOT NULL DEFAULT 0 COMMENT '立直棒数',
  `details` text COMMENT '结算详情（JSON格式）',
  PRIMARY KEY (`id`),
  INDEX `idx_game_id` (`game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结算表';

CREATE TABLE `winning_hand_analysis` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '和牌分析ID',
  `settlement_id` bigint(20) NOT NULL COMMENT '结算ID',
  `player_game_id` bigint(20) NOT NULL COMMENT '玩家对局ID',
  `win_type` tinyint(4) NOT NULL COMMENT '和牌类型：0-自摸，1-荣和',
  `from_position` tinyint(4) DEFAULT NULL COMMENT '放铳位置：0-东，1-南，2-西，3-北',
  `win_tile` varchar(10) NOT NULL COMMENT '和牌',
  `hand_tiles` varchar(255) NOT NULL COMMENT '手牌（JSON格式）',
  `melds` varchar(255) DEFAULT NULL COMMENT '副露（JSON格式）',
  `dora_count` int(11) NOT NULL DEFAULT 0 COMMENT '宝牌数',
  `ura_dora_count` int(11) NOT NULL DEFAULT 0 COMMENT '里宝牌数',
  `aka_dora_count` int(11) NOT NULL DEFAULT 0 COMMENT '赤宝牌数',
  `fu` int(11) NOT NULL DEFAULT 0 COMMENT '符数',
  `han` int(11) NOT NULL DEFAULT 0 COMMENT '番数',
  `score` int(11) NOT NULL DEFAULT 0 COMMENT '得分',
  `yakuman_count` int(11) NOT NULL DEFAULT 0 COMMENT '役满倍数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_settlement_id` (`settlement_id`),
  INDEX `idx_player_game_id` (`player_game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='和牌分析表';

CREATE TABLE `yaku` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '役种ID',
  `name_zh` varchar(50) NOT NULL COMMENT '中文名称',
  `name_jp` varchar(50) NOT NULL COMMENT '日文名称',
  `han_closed` int(11) NOT NULL COMMENT '门清番数',
  `han_open` int(11) NOT NULL COMMENT '副露番数',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='役种表';

CREATE TABLE `yakuman` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '役满ID',
  `name_zh` varchar(50) NOT NULL COMMENT '中文名称',
  `name_jp` varchar(50) NOT NULL COMMENT '日文名称',
  `yakuman_times` int(11) NOT NULL DEFAULT 1 COMMENT '役满倍数',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='役满表';

CREATE TABLE `fu_calculation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `fu_type` varchar(50) NOT NULL COMMENT '符数类型',
  `fu_value` int(11) NOT NULL COMMENT '符数值',
  `description` varchar(255) NOT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='符数计算表';

CREATE TABLE `winning_hand_yaku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `winning_hand_id` bigint(20) NOT NULL COMMENT '和牌分析ID',
  `yaku_id` int(11) NOT NULL COMMENT '役种ID',
  `han` int(11) NOT NULL COMMENT '实际番数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_winning_hand_id` (`winning_hand_id`),
  INDEX `idx_yaku_id` (`yaku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='和牌役种关联表';

CREATE TABLE `winning_hand_yakuman` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `winning_hand_id` bigint(20) NOT NULL COMMENT '和牌分析ID',
  `yakuman_id` int(11) NOT NULL COMMENT '役满ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_winning_hand_id` (`winning_hand_id`),
  INDEX `idx_yakuman_id` (`yakuman_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='和牌役满关联表';

CREATE TABLE `user`
(
    `id`              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '用户ID，8位数字',
    `nickname`        VARCHAR(24) NOT NULL COMMENT '用户昵称，最多8个字符，应用层控制',
    `is_guest`        TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '是否为游客：0-注册用户，1-游客',
    `create_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `last_login_time` DATETIME    NULL COMMENT '最后登录时间',
    `status`          TINYINT     NOT NULL DEFAULT 1 COMMENT '用户状态：0-禁用，1-正常',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_nickname` (`nickname`)
) ENGINE = InnoDB COMMENT ='用户信息表';

-- 设置自增起始值为10000000（8位数字的最小值）
ALTER TABLE `user`
    AUTO_INCREMENT = 10000000;