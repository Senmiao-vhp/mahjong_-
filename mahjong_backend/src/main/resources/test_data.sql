-- 测试数据插入脚本
USE mahjong;

-- 插入房间数据
INSERT INTO `room` (`room_name`, `room_type`, `game_type`, `has_red_dora`, `has_open_tanyao`, 
                   `status`, `max_player_count`, `current_player_count`, `creator_id`, `create_time`, `update_time`)
VALUES ('雀神之间', 0, 1, 1, 1, 0, 4, 1, 10000001, NOW(), NOW()),
       ('新手练习室', 0, 0, 1, 1, 0, 4, 2, 10000002, NOW(), NOW()),
       ('高手对决', 1, 1, 1, 0, 1, 4, 4, 10000003, NOW(), NOW()),
       ('休闲娱乐', 0, 0, 1, 1, 2, 4, 0, 10000004, NOW(), NOW());

-- 插入对局数据
INSERT INTO `game` (`room_id`, `round_wind`, `dealer_position`, `current_position`, `honba_count`, 
                   `riichi_stick_count`, `wall_tiles`, `dora_indicators`, `ura_dora_indicators`, 
                   `kan_dora_indicators`, `wall_position`, `status`, `start_time`, `create_time`, `update_time`)
VALUES (3, 0, 0, 1, 0, 1, 
       '1m,2m,3m,4m,5m,6m,7m,8m,9m,1p,2p,3p,4p,5p,6p,7p,8p,9p,1s,2s,3s,4s,5s,6s,7s,8s,9s,1z,2z,3z,4z,5z,6z,7z', 
       '5m', '3p', '', 14, 1, NOW(), NOW(), NOW()),
       (4, 0, 0, 0, 0, 0, 
       '1m,2m,3m,4m,5m,6m,7m,8m,9m,1p,2p,3p,4p,5p,6p,7p,8p,9p,1s,2s,3s,4s,5s,6s,7s,8s,9s,1z,2z,3z,4z,5z,6z,7z', 
       '7p', '2s', '', 14, 2, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW());

-- 插入玩家对局数据
INSERT INTO `player_game` (`game_id`, `user_id`, `position`, `is_dealer`, `score`, `is_riichi`, 
                          `is_double_riichi`, `is_ippatsu`, `is_furiten`, `is_temporary_furiten`, 
                          `is_menzen`, `create_time`, `update_time`)
VALUES (1, 10000001, 0, 1, 25000, 0, 0, 0, 0, 0, 1, NOW(), NOW()),
       (1, 10000002, 1, 0, 25000, 0, 0, 0, 0, 0, 1, NOW(), NOW()),
       (1, 10000003, 2, 0, 25000, 0, 0, 0, 0, 0, 1, NOW(), NOW()),
       (1, 10000004, 3, 0, 25000, 0, 0, 0, 0, 0, 1, NOW(), NOW()),
       (2, 10000001, 0, 1, 17000, 0, 0, 0, 0, 0, 1, NOW(), NOW()),
       (2, 10000002, 1, 0, 33000, 1, 0, 0, 0, 0, 1, NOW(), NOW()),
       (2, 10000003, 2, 0, 25000, 0, 0, 0, 0, 0, 1, NOW(), NOW()),
       (2, 10000004, 3, 0, 25000, 0, 0, 0, 0, 0, 1, NOW(), NOW());

-- 插入手牌数据
INSERT INTO `hand` (`player_game_id`, `tiles`, `drawn_tile`, `tenpai_tiles`, `create_time`, `update_time`)
VALUES (1, '["1m","1m","2m","3m","4m","5m","6p","7p","8p","1s","2s","3s"]', '4s', '["5m","9p"]', NOW(), NOW()),
       (2, '["2m","2m","3m","4m","5m","6m","7p","8p","9p","2s","3s","4s"]', '5s', '["1m","6s"]', NOW(), NOW()),
       (3, '["3m","3m","4m","5m","6m","7m","8p","9p","1p","3s","4s","5s"]', '6s', '["2m","7s"]', NOW(), NOW()),
       (4, '["4m","4m","5m","6m","7m","8m","9p","1p","2p","4s","5s","6s"]', '7s', '["3m","8s"]', NOW(), NOW()),
       (5, '["1m","1m","2m","3m","4m","5m","6p","7p","8p","1s","2s","3s"]', null, '["5m","9p"]', NOW(), NOW()),
       (6, '["2m","2m","3m","4m","5m","6m","7p","8p","9p","2s","3s","4s"]', null, '["1m","6s"]', NOW(), NOW()),
       (7, '["3m","3m","4m","5m","6m","7m","8p","9p","1p","3s","4s","5s"]', null, '["2m","7s"]', NOW(), NOW()),
       (8, '["4m","4m","5m","6m","7m","8m","9p","1p","2p","4s","5s","6s"]', null, '["3m","8s"]', NOW(), NOW());

-- 插入牌河数据
INSERT INTO `discard_pile` (`player_game_id`, `tiles`, `create_time`, `update_time`)
VALUES (1, '[{"tile":"1p","is_riichi":false,"is_tsumogiri":false,"is_furiten":false},{"tile":"9s","is_riichi":false,"is_tsumogiri":true,"is_furiten":false}]', NOW(), NOW()),
       (2, '[{"tile":"3m","is_riichi":false,"is_tsumogiri":false,"is_furiten":false}]', NOW(), NOW()),
       (3, '[]', NOW(), NOW()),
       (4, '[{"tile":"5p","is_riichi":false,"is_tsumogiri":true,"is_furiten":false}]', NOW(), NOW()),
       (5, '[{"tile":"1p","is_riichi":false,"is_tsumogiri":false,"is_furiten":false},{"tile":"9s","is_riichi":true,"is_tsumogiri":true,"is_furiten":false}]', NOW(), NOW()),
       (6, '[{"tile":"3m","is_riichi":false,"is_tsumogiri":false,"is_furiten":false},{"tile":"5m","is_riichi":true,"is_tsumogiri":false,"is_furiten":false}]', NOW(), NOW()),
       (7, '[{"tile":"7p","is_riichi":false,"is_tsumogiri":false,"is_furiten":false}]', NOW(), NOW()),
       (8, '[{"tile":"5p","is_riichi":false,"is_tsumogiri":true,"is_furiten":false}]', NOW(), NOW());

-- 插入副露数据
INSERT INTO `meld` (`player_game_id`, `meld_type`, `tiles`, `from_position`, `called_tile`, `create_time`)
VALUES (3, 1, '["7s","7s","7s"]', 2, '7s', NOW()),
       (7, 2, '["6p","6p","6p","6p"]', null, '6p', NOW());

-- 插入对局操作日志
INSERT INTO `game_action_log` (`game_id`, `player_game_id`, `action_type`, `action_data`, `action_time`)
VALUES (1, 1, 0, '{"tile":"4m"}', DATE_SUB(NOW(), INTERVAL 50 SECOND)),
       (1, 1, 1, '{"tile":"1p","is_tsumogiri":false}', DATE_SUB(NOW(), INTERVAL 45 SECOND)),
       (1, 2, 0, '{"tile":"7s"}', DATE_SUB(NOW(), INTERVAL 40 SECOND)),
       (2, 5, 0, '{"tile":"4m"}', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
       (2, 5, 1, '{"tile":"1p","is_tsumogiri":false}', DATE_SUB(NOW(), INTERVAL 59 MINUTE)),
       (2, 6, 0, '{"tile":"7s"}', DATE_SUB(NOW(), INTERVAL 58 MINUTE)),
       (2, 6, 5, '{"tile":"5m"}', DATE_SUB(NOW(), INTERVAL 57 MINUTE)),
       (2, 6, 6, '{"win_type":1,"from_position":0,"win_tile":"5m"}', DATE_SUB(NOW(), INTERVAL 56 MINUTE));

-- 插入对局当前状态
INSERT INTO `game_current_state` (`game_id`, `current_position`, `current_action`, `available_actions`, 
                                 `last_discard_tile`, `last_discard_position`, `create_time`, `update_time`)
VALUES (1, 1, 0, '[]', '1p', 0, NOW(), NOW());

-- 插入结算数据
INSERT INTO `settlement` (`game_id`, `settlement_type`, `settlement_time`, `honba_count`, 
                         `riichi_stick_count`, `details`)
VALUES (2, 0, DATE_SUB(NOW(), INTERVAL 55 MINUTE), 0, 1, 
       '{"players":[{"position":0,"user_id":10000001,"score_change":-8000,"final_score":17000},
       {"position":1,"user_id":10000002,"score_change":8000,"final_score":33000},
       {"position":2,"user_id":10000003,"score_change":0,"final_score":25000},
       {"position":3,"user_id":10000004,"score_change":0,"final_score":25000}]}');

-- 插入和牌分析数据
INSERT INTO `winning_hand_analysis` (`settlement_id`, `player_game_id`, `win_type`, `from_position`, 
                                    `win_tile`, `hand_tiles`, `melds`, `dora_count`, `ura_dora_count`, 
                                    `aka_dora_count`, `fu`, `han`, `score`, `yakuman_count`, `create_time`)
VALUES (1, 6, 1, 0, '5m', 
       '["2m","2m","3m","4m","5m","6m","7p","8p","9p","2s","3s","4s"]', 
       '[]', 1, 0, 0, 40, 3, 8000, 0, DATE_SUB(NOW(), INTERVAL 55 MINUTE));

-- 插入役种数据
INSERT INTO `yaku` (`name_zh`, `name_jp`, `han_closed`, `han_open`, `description`, `create_time`)
VALUES ('立直', 'リーチ', 1, 0, '门前清状态下宣告听牌', NOW()),
       ('门前清自摸和', 'メンゼンツモ', 1, 0, '门前清状态下自摸和牌', NOW()),
       ('平和', 'ピンフ', 1, 0, '由四组顺子及对子组成的牌型，且最后和牌形成两面听牌', NOW()),
       ('一发', 'イッパツ', 1, 0, '立直后一巡内和牌', NOW()),
       ('自风', 'ジカゼ', 1, 1, '与自己位置相同的风牌组成刻子或杠子', NOW());

-- 插入役满数据
INSERT INTO `yakuman` (`name_zh`, `name_jp`, `yakuman_times`, `description`, `create_time`)
VALUES ('天和', 'テンホウ', 1, '庄家第一巡和牌', NOW()),
       ('地和', 'チホウ', 1, '闲家第一巡和牌', NOW()),
       ('四暗刻', 'スーアンコウ', 1, '四组暗刻', NOW()),
       ('国士无双', 'コクシムソウ', 1, '由13种幺九牌组成的特殊牌型', NOW());

-- 插入符数计算数据
INSERT INTO `fu_calculation` (`fu_type`, `fu_value`, `description`, `create_time`)
VALUES ('基本符', 20, '基本符数', NOW()),
       ('自摸', 2, '自摸和牌', NOW()),
       ('门清荣和', 10, '门前清状态下荣和', NOW()),
       ('单骑', 2, '单骑和牌', NOW());

-- 插入和牌役种关联数据
INSERT INTO `winning_hand_yaku` (`winning_hand_id`, `yaku_id`, `han`, `create_time`)
VALUES (1, 1, 1, DATE_SUB(NOW(), INTERVAL 55 MINUTE)),
       (1, 2, 1, DATE_SUB(NOW(), INTERVAL 55 MINUTE)),
       (1, 3, 1, DATE_SUB(NOW(), INTERVAL 55 MINUTE)); 