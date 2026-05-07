-- =====================================================
-- 酒店管理系统数据库初始化脚本
-- 版本: 1.2.0
-- 创建时间: 2024-01-15
-- 更新时间: 2026-04-01
-- =====================================================

-- 1. 创建数据库
DROP DATABASE IF EXISTS hotel_management;
CREATE DATABASE hotel_management
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hotel_management;

-- =====================================================
-- 2. 创建表结构
-- =====================================================

-- 2.1 用户表 (sys_user)
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
                          id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                          username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                          password VARCHAR(255) NOT NULL COMMENT '密码',
                          name VARCHAR(100) NOT NULL COMMENT '姓名',
                          role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin-管理员，user-普通用户',
                          status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                          avatar VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
                          create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
                          KEY idx_username (username),
                          KEY idx_role (role),
                          KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2.2 房型表 (room_type)
DROP TABLE IF EXISTS room_type;
CREATE TABLE room_type (
                           id INT PRIMARY KEY AUTO_INCREMENT COMMENT '房型ID',
                           name VARCHAR(50) NOT NULL COMMENT '房型名称',
                           capacity INT NOT NULL COMMENT '可住人数',
                           beds VARCHAR(100) NOT NULL COMMENT '床位描述',
                           price DECIMAL(10,2) NOT NULL COMMENT '参考价格',
                           status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active-启用，inactive-禁用',
                           image VARCHAR(500) DEFAULT NULL COMMENT '房型封面图片URL',
                           description TEXT DEFAULT NULL COMMENT '房型描述',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           UNIQUE KEY uk_name (name),
                           KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房型表';

-- 2.3 房间表 (room)
DROP TABLE IF EXISTS room;
CREATE TABLE room (
                      id INT PRIMARY KEY AUTO_INCREMENT COMMENT '房间ID',
                      room_number VARCHAR(20) NOT NULL COMMENT '房间编号',
                      room_type_id INT NOT NULL COMMENT '所属房型ID（逻辑外键）',
                      floor INT NOT NULL COMMENT '所在楼层',
                      status VARCHAR(20) NOT NULL DEFAULT 'available' COMMENT '房间状态：available-空闲，occupied-已入住，cleaning-清洁中，maintenance-维修中，waiting_clean-等待清洁',
                      clean_status VARCHAR(20) DEFAULT 'clean' COMMENT '清洁状态：clean-已清洁，dirty-脏，cleaning-清洁中',
                      do_not_disturb BOOLEAN DEFAULT FALSE COMMENT '勿扰模式：true-开启，false-关闭',
                      price DECIMAL(10,2) NOT NULL COMMENT '房间价格',
                      create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                      UNIQUE KEY uk_room_number (room_number),
                      KEY idx_room_type (room_type_id),
                      KEY idx_floor (floor),
                      KEY idx_status (status),
                      KEY idx_clean_status (clean_status),
                      KEY idx_do_not_disturb (do_not_disturb)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- 2.4 订单表 (orders)
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
                        id INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
                        order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
                        guest_name VARCHAR(100) NOT NULL COMMENT '客人姓名',
                        guest_phone VARCHAR(20) NOT NULL COMMENT '客人电话',
                        guest_id_card VARCHAR(20) DEFAULT NULL COMMENT '客人身份证号',
                        guest_count INT NOT NULL DEFAULT 1 COMMENT '入住人数',
                        room_id INT NOT NULL COMMENT '预订房间ID（逻辑外键）',
                        room_type_id INT NOT NULL COMMENT '预订房型ID（逻辑外键）',
                        check_in_date DATE NOT NULL COMMENT '入住日期',
                        check_out_date DATE NOT NULL COMMENT '退房日期',
                        nights INT NOT NULL DEFAULT 0 COMMENT '入住天数',
                        status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '订单状态：pending-待入住，active-入住中，completed-已完成，canceled-已取消',
                        payment_status VARCHAR(20) NOT NULL DEFAULT 'unpaid' COMMENT '支付状态：unpaid-未支付，paid-已支付，refunded-已退款',
                        total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
                        remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
                        create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        UNIQUE KEY uk_order_no (order_no),
                        KEY idx_room_id (room_id),
                        KEY idx_room_type_id (room_type_id),
                        KEY idx_guest_phone (guest_phone),
                        KEY idx_guest_id_card (guest_id_card),
                        KEY idx_status (status),
                        KEY idx_payment_status (payment_status),
                        KEY idx_check_in_date (check_in_date),
                        KEY idx_check_out_date (check_out_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 2.5 操作日志表 (operation_log)
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
                               id INT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
                               user_id INT NOT NULL COMMENT '操作人ID（逻辑外键）',
                               operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/LOGIN/LOGOUT',
                               operation_module VARCHAR(50) NOT NULL COMMENT '操作模块',
                               operation_desc VARCHAR(500) NOT NULL COMMENT '操作描述',
                               ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
                               create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                               KEY idx_user_id (user_id),
                               KEY idx_operation_type (operation_type),
                               KEY idx_operation_module (operation_module),
                               KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =====================================================
-- 3. 插入示例数据
-- =====================================================

-- 3.1 初始化管理员账号 admin / 123456
INSERT INTO sys_user (username, password, name, role) VALUES
    ('admin', '$2a$10$jFHpybOMqSSTpYGWBW0Ci.PsxE30Em4b1MYfWkdtQZ.JyuqxnwdS.', '系统管理员', 'admin');

-- 3.2 初始化房型数据
INSERT INTO room_type (name, capacity, beds, price, status, image, description) VALUES
                                                                                    ('豪华大床房', 2, '1张特大床', 888.00, 'active', 'https://example.com/images/room_luxury.jpg', '豪华装修，配备高档家具和设施，享受尊贵入住体验'),
                                                                                    ('标准双床房', 2, '2张单人床', 688.00, 'active', 'https://example.com/images/room_standard.jpg', '标准配置，舒适整洁，适合商务出行'),
                                                                                    ('商务套房', 2, '1张king size大床', 1288.00, 'active', 'https://example.com/images/room_business.jpg', '独立办公区域，高速网络，专为商务人士设计'),
                                                                                    ('家庭房', 4, '2张大床', 1588.00, 'active', 'https://example.com/images/room_family.jpg', '宽敞空间，适合家庭出游，儿童友好'),
                                                                                    ('标准间', 2, '2张单人床', 399.00, 'active', 'https://example.com/images/room_basic.jpg', '经济实惠，性价比高'),
                                                                                    ('行政套房', 3, '1张特大床+1张单人床', 1888.00, 'active', 'https://example.com/images/room_executive.jpg', '顶级配置，行政楼层，专属服务'),
                                                                                    ('蜜月套房', 2, '1张圆形大床', 1688.00, 'active', 'https://example.com/images/room_honeymoon.jpg', '浪漫主题，适合蜜月旅行');

-- 3.3 初始化房间数据
INSERT INTO room (room_number, room_type_id, floor, status, clean_status, do_not_disturb, price) VALUES
                                                                                     ('101', 5, 1, 'available', 'clean', false, 399.00),
                                                                                     ('102', 5, 1, 'available', 'clean', false, 399.00),
                                                                                     ('103', 2, 1, 'occupied', 'clean', false, 688.00),
                                                                                     ('104', 2, 1, 'available', 'clean', false, 688.00),
                                                                                     ('105', 1, 1, 'available', 'clean', false, 888.00),
                                                                                     ('106', 1, 1, 'maintenance', 'clean', false, 888.00),
                                                                                     ('107', 3, 1, 'available', 'clean', false, 1288.00),
                                                                                     ('108', 3, 1, 'occupied', 'clean', false, 1288.00),
                                                                                     ('109', 4, 1, 'available', 'clean', false, 1588.00),
                                                                                     ('110', 4, 1, 'available', 'clean', false, 1588.00),
                                                                                     ('201', 5, 2, 'available', 'clean', false, 399.00),
                                                                                     ('202', 5, 2, 'cleaning', 'cleaning', false, 399.00),
                                                                                     ('203', 2, 2, 'available', 'clean', false, 688.00),
                                                                                     ('204', 2, 2, 'occupied', 'clean', false, 688.00),
                                                                                     ('205', 1, 2, 'available', 'clean', false, 888.00),
                                                                                     ('206', 1, 2, 'available', 'clean', false, 888.00),
                                                                                     ('207', 3, 2, 'occupied', 'clean', false, 1288.00),
                                                                                     ('208', 3, 2, 'available', 'clean', false, 1288.00),
                                                                                     ('209', 4, 2, 'available', 'clean', false, 1588.00),
                                                                                     ('210', 4, 2, 'available', 'clean', false, 1588.00),
                                                                                     ('301', 5, 3, 'available', 'clean', false, 399.00),
                                                                                     ('302', 5, 3, 'available', 'clean', false, 399.00),
                                                                                     ('303', 2, 3, 'available', 'clean', false, 688.00),
                                                                                     ('304', 2, 3, 'available', 'clean', false, 688.00),
                                                                                     ('305', 1, 3, 'occupied', 'clean', false, 888.00),
                                                                                     ('306', 1, 3, 'available', 'clean', false, 888.00),
                                                                                     ('307', 3, 3, 'available', 'clean', false, 1288.00),
                                                                                     ('308', 3, 3, 'available', 'clean', false, 1288.00),
                                                                                     ('309', 4, 3, 'available', 'clean', false, 1588.00),
                                                                                     ('310', 4, 3, 'available', 'clean', false, 1588.00),
                                                                                     ('401', 5, 4, 'available', 'clean', false, 399.00),
                                                                                     ('402', 5, 4, 'available', 'clean', false, 399.00),
                                                                                     ('403', 2, 4, 'occupied', 'clean', false, 688.00),
                                                                                     ('404', 2, 4, 'available', 'clean', false, 688.00),
                                                                                     ('405', 1, 4, 'available', 'clean', false, 888.00),
                                                                                     ('406', 1, 4, 'available', 'clean', false, 888.00),
                                                                                     ('407', 3, 4, 'available', 'clean', false, 1288.00),
                                                                                     ('408', 3, 4, 'maintenance', 'clean', false, 1288.00),
                                                                                     ('409', 4, 4, 'available', 'clean', false, 1588.00),
                                                                                     ('410', 4, 4, 'available', 'clean', false, 1588.00),
                                                                                     ('501', 6, 5, 'available', 'clean', false, 1888.00),
                                                                                     ('502', 6, 5, 'available', 'clean', false, 1888.00),
                                                                                     ('503', 6, 5, 'occupied', 'clean', false, 1888.00),
                                                                                     ('504', 6, 5, 'available', 'clean', false, 1888.00),
                                                                                     ('505', 7, 5, 'available', 'clean', false, 1688.00),
                                                                                     ('506', 7, 5, 'occupied', 'clean', false, 1688.00),
                                                                                     ('507', 7, 5, 'available', 'clean', false, 1688.00),
                                                                                     ('508', 7, 5, 'available', 'clean', false, 1688.00),
                                                                                     ('509', 3, 5, 'available', 'clean', false, 1288.00),
                                                                                     ('510', 3, 5, 'available', 'clean', false, 1288.00),
                                                                                     ('601', 5, 6, 'available', 'clean', false, 399.00),
                                                                                     ('602', 5, 6, 'available', 'clean', false, 399.00),
                                                                                     ('603', 2, 6, 'available', 'clean', false, 688.00),
                                                                                     ('604', 2, 6, 'available', 'clean', false, 688.00),
                                                                                     ('605', 1, 6, 'available', 'clean', false, 888.00),
                                                                                     ('606', 1, 6, 'cleaning', 'cleaning', false, 888.00),
                                                                                     ('607', 3, 6, 'available', 'clean', false, 1288.00),
                                                                                     ('608', 3, 6, 'available', 'clean', false, 1288.00),
                                                                                     ('609', 4, 6, 'occupied', 'clean', false, 1588.00),
                                                                                     ('610', 4, 6, 'available', 'clean', false, 1588.00),
                                                                                     ('701', 5, 7, 'available', 'clean', false, 399.00),
                                                                                     ('702', 5, 7, 'available', 'clean', false, 399.00),
                                                                                     ('703', 2, 7, 'available', 'clean', false, 688.00),
                                                                                     ('704', 2, 7, 'available', 'clean', false, 688.00),
                                                                                     ('705', 1, 7, 'available', 'clean', false, 888.00),
                                                                                     ('706', 1, 7, 'occupied', 'clean', false, 888.00),
                                                                                     ('707', 3, 7, 'available', 'clean', false, 1288.00),
                                                                                     ('708', 3, 7, 'available', 'clean', false, 1288.00),
                                                                                     ('709', 4, 7, 'available', 'clean', false, 1588.00),
                                                                                     ('710', 4, 7, 'available', 'clean', false, 1588.00),
                                                                                     ('801', 5, 8, 'available', 'clean', false, 399.00),
                                                                                     ('802', 5, 8, 'available', 'clean', false, 399.00),
                                                                                     ('803', 2, 8, 'occupied', 'clean', false, 688.00),
                                                                                     ('804', 2, 8, 'available', 'clean', false, 688.00),
                                                                                     ('805', 1, 8, 'available', 'clean', false, 888.00),
                                                                                     ('806', 1, 8, 'available', 'clean', false, 888.00),
                                                                                     ('807', 3, 8, 'available', 'clean', false, 1288.00),
                                                                                     ('808', 3, 8, 'available', 'clean', false, 1288.00),
                                                                                     ('809', 4, 8, 'available', 'clean', false, 1588.00),
                                                                                     ('810', 4, 8, 'maintenance', 'clean', false, 1588.00);

-- 3.4 初始化订单数据
INSERT INTO orders (order_no, guest_name, guest_phone, guest_id_card, guest_count, room_id, room_type_id, check_in_date, check_out_date, nights, status, payment_status, total_amount, remark, create_time) VALUES
                                                                                                                                                                                                   ('ORD20260417001', '张三', '13800138000', '110101199001011234', 2, 3, 2, '2026-04-17', '2026-04-20', 3, 'active', 'paid', 2064.00, '商务出行', '2026-04-17 10:00:00'),
                                                                                                                                                                                                   ('ORD20260417002', '李四', '13900139000', '110101199002021234', 2, 5, 1, '2026-04-17', '2026-04-19', 2, 'active', 'paid', 1776.00, NULL, '2026-04-17 10:30:00'),
                                                                                                                                                                                                   ('ORD20260417003', '王五', '13700137000', '110101199003031234', 3, 8, 3, '2026-04-17', '2026-04-20', 3, 'active', 'paid', 3864.00, '家庭旅行', '2026-04-17 11:00:00'),
                                                                                                                                                                                                   ('ORD20260416001', '赵六', '13600136000', '110101199004041234', 2, 13, 2, '2026-04-16', '2026-04-18', 2, 'active', 'paid', 1376.00, NULL, '2026-04-16 14:00:00'),
                                                                                                                                                                                                   ('ORD20260416002', '钱七', '13500135000', '110101199005051234', 2, 20, 3, '2026-04-16', '2026-04-19', 3, 'active', 'paid', 3864.00, '商务会议', '2026-04-16 15:00:00'),
                                                                                                                                                                                                   ('ORD20260415001', '孙八', '13400134000', '110101199006061234', 4, 23, 4, '2026-04-15', '2026-04-19', 4, 'active', 'paid', 6352.00, '家庭出游', '2026-04-15 09:00:00'),
                                                                                                                                                                                                   ('ORD20260414001', '周九', '13300133000', '110101199007071234', 2, 27, 1, '2026-04-14', '2026-04-16', 2, 'active', 'paid', 1776.00, NULL, '2026-04-14 16:00:00'),
                                                                                                                                                                                                   ('ORD20260413001', '吴十', '13200132000', '110101199008081234', 2, 30, 2, '2026-04-13', '2026-04-15', 2, 'active', 'paid', 1376.00, '蜜月旅行', '2026-04-13 11:00:00'),
                                                                                                                                                                                                   ('ORD20260412001', '郑十一', '13100131000', '110101199009091234', 3, 43, 4, '2026-04-12', '2026-04-16', 4, 'active', 'paid', 6352.00, '家庭旅行', '2026-04-12 10:00:00'),
                                                                                                                                                                                                   ('ORD20260411001', '王十二', '13000130000', '110101199010101234', 2, 53, 6, '2026-04-11', '2026-04-13', 2, 'active', 'paid', 3776.00, '商务出行', '2026-04-11 14:00:00'),
                                                                                                                                                                                                   ('ORD20260410001', '刘十三', '15900159000', '110101199011111234', 2, 56, 7, '2026-04-10', '2026-04-12', 2, 'active', 'paid', 3376.00, '蜜月旅行', '2026-04-10 09:00:00'),
                                                                                                                                                                                                   ('ORD20260409001', '陈十四', '15800158000', '110101199012121234', 2, 63, 1, '2026-04-09', '2026-04-12', 3, 'active', 'paid', 2664.00, NULL, '2026-04-09 15:00:00'),
                                                                                                                                                                                                   ('ORD20260408001', '杨十五', '15700157000', '110101199101131234', 2, 68, 3, '2026-04-08', '2026-04-11', 3, 'active', 'paid', 3864.00, '商务出行', '2026-04-08 10:00:00'),
                                                                                                                                                                                                   ('ORD20260407001', '黄十六', '15600156000', '110101199102141234', 2, 73, 2, '2026-04-07', '2026-04-10', 3, 'active', 'paid', 2064.00, NULL, '2026-04-07 14:00:00'),
                                                                                                                                                                                                   ('ORD20260406001', '朱十七', '15500155000', '110101199103151234', 2, 78, 3, '2026-04-06', '2026-04-08', 2, 'active', 'paid', 2576.00, '商务会议', '2026-04-06 11:00:00'),
                                                                                                                                                                                                   ('ORD20260418001', '马十八', '15400154000', '110101199104161234', 2, 1, 5, '2026-04-18', '2026-04-20', 2, 'pending', 'paid', 798.00, NULL, '2026-04-17 09:00:00'),
                                                                                                                                                                                                   ('ORD20260419001', '林十九', '15300153000', '110101199105171234', 2, 4, 5, '2026-04-19', '2026-04-22', 3, 'pending', 'unpaid', 1197.00, '需要延迟入住', '2026-04-17 10:00:00'),
                                                                                                                                                                                                   ('ORD20260420001', '徐二十', '15200152000', '110101199106181234', 2, 14, 2, '2026-04-20', '2026-04-22', 2, 'pending', 'paid', 1376.00, NULL, '2026-04-17 11:00:00'),
                                                                                                                                                                                                   ('ORD20260421001', '何二一', '15100151000', '110101199107191234', 3, 15, 1, '2026-04-21', '2026-04-24', 3, 'pending', 'unpaid', 2664.00, '家庭旅行', '2026-04-17 12:00:00'),
                                                                                                                                                                                                   ('ORD20260422001', '高二二', '15000150000', '110101199108201234', 2, 16, 1, '2026-04-22', '2026-04-23', 1, 'pending', 'paid', 888.00, NULL, '2026-04-17 13:00:00'),
                                                                                                                                                                                                   ('ORD20260415002', '梁二三', '14900149000', '110101199109211234', 2, 3, 2, '2026-04-15', '2026-04-17', 2, 'completed', 'paid', 1376.00, NULL, '2026-04-15 09:00:00'),
                                                                                                                                                                                                   ('ORD20260414002', '郭二四', '14800148000', '110101199110221234', 2, 8, 3, '2026-04-14', '2026-04-17', 3, 'completed', 'paid', 3864.00, '商务出行', '2026-04-14 10:00:00'),
                                                                                                                                                                                                   ('ORD20260413002', '宋二五', '14700147000', '110101199111231234', 4, 23, 4, '2026-04-13', '2026-04-17', 4, 'completed', 'paid', 6352.00, '家庭出游', '2026-04-13 09:00:00'),
                                                                                                                                                                                                   ('ORD20260412002', '唐二六', '14600146000', '110101199112241234', 2, 27, 1, '2026-04-12', '2026-04-14', 2, 'completed', 'paid', 1776.00, NULL, '2026-04-12 10:00:00'),
                                                                                                                                                                                                   ('ORD20260401001', '罗二七', '14500145000', '110101199201251234', 2, 50, 6, '2026-04-01', '2026-04-04', 3, 'completed', 'paid', 5664.00, '假期旅行', '2026-03-31 15:00:00'),
                                                                                                                                                                                                   ('ORD20260416003', '韩二八', '14400144000', '110101199202261234', 2, 1, 5, '2026-04-16', '2026-04-18', 2, 'canceled', 'refunded', 798.00, '临时取消', '2026-04-16 09:00:00'),
                                                                                                                                                                                                   ('ORD20260415003', '冯二九', '14300143000', '110101199203271234', 2, 4, 5, '2026-04-15', '2026-04-17', 2, 'canceled', 'refunded', 798.00, NULL, '2026-04-15 10:00:00');

-- 3.5 初始化操作日志数据
INSERT INTO operation_log (user_id, operation_type, operation_module, operation_desc, ip_address) VALUES
                                                                                                      (1, 'LOGIN', '用户认证', '管理员登录系统', '192.168.1.100'),
                                                                                                      (1, 'CREATE', '房型管理', '新增房型：豪华大床房', '192.168.1.100'),
                                                                                                      (1, 'CREATE', '房型管理', '新增房型：标准双床房', '192.168.1.100'),
                                                                                                      (1, 'CREATE', '房型管理', '新增房型：商务套房', '192.168.1.100'),
                                                                                                      (1, 'CREATE', '房型管理', '新增房型：家庭房', '192.168.1.100'),
                                                                                                      (1, 'CREATE', '房型管理', '新增房型：标准间', '192.168.1.100'),
                                                                                                      (1, 'CREATE', '房型管理', '新增房型：行政套房', '192.168.1.100'),
                                                                                                      (1, 'CREATE', '房型管理', '新增房型：蜜月套房', '192.168.1.100'),
                                                                                                      (1, 'CREATE', '房间管理', '批量创建房间 1-8楼 共80间', '192.168.1.100'),
                                                                                                      (1, 'CREATE', '订单管理', '创建订单：ORD20240325001', '192.168.1.100'),
                                                                                                      (1, 'CREATE', '订单管理', '创建订单：ORD20240325002', '192.168.1.100'),
                                                                                                      (1, 'UPDATE', '订单管理', '更新订单状态：ORD20240325001 -> active', '192.168.1.100'),
                                                                                                      (1, 'UPDATE', '房间管理', '更新房间状态：103 -> occupied', '192.168.1.100'),
                                                                                                      (1, 'UPDATE', '房间管理', '更新房间状态：108 -> occupied', '192.168.1.100'),
                                                                                                      (1, 'LOGOUT', '用户认证', '管理员退出系统', '192.168.1.100');

-- =====================================================
-- 4. 验证数据
-- =====================================================
SELECT '用户表数据量:' as description, COUNT(*) as count FROM sys_user
UNION ALL
SELECT '房型表数据量:', COUNT(*) FROM room_type
UNION ALL
SELECT '房间表数据量:', COUNT(*) FROM room
UNION ALL
SELECT '订单表数据量:', COUNT(*) FROM orders
UNION ALL
SELECT '操作日志表数据量:', COUNT(*) FROM operation_log;

-- =====================================================
-- 数据库初始化完成 ✅
-- =====================================================