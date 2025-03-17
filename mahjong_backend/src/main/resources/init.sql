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