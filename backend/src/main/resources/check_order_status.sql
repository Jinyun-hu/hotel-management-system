-- 订单状态检查脚本
-- 检查订单状态和支付状态的逻辑关系

USE hotel_management;

-- 1. 查看所有订单状态分布
SELECT 
    status AS '订单状态',
    payment_status AS '支付状态',
    COUNT(*) AS '订单数量',
    GROUP_CONCAT(id ORDER BY id) AS '订单ID列表'
FROM orders
GROUP BY status, payment_status
ORDER BY status, payment_status;

-- 2. 检查订单状态逻辑问题
-- 2.1 检查状态为 pending 但支付状态为 paid 的订单（应该为 active）
SELECT 
    '状态逻辑问题: pending订单但已支付' AS 问题描述,
    COUNT(*) AS 问题订单数,
    GROUP_CONCAT(id) AS 订单ID列表
FROM orders
WHERE status = 'pending' AND payment_status = 'paid';

-- 2.2 检查状态为 completed 但支付状态为 unpaid 的订单（应该已支付）
SELECT 
    '状态逻辑问题: 已完成订单但未支付' AS 问题描述,
    COUNT(*) AS 问题订单数,
    GROUP_CONCAT(id) AS 订单ID列表
FROM orders
WHERE status = 'completed' AND payment_status = 'unpaid';

-- 2.3 检查状态为 active 但支付状态为 unpaid 的订单（应该已支付）
SELECT 
    '状态逻辑问题: 入住中订单但未支付' AS 问题描述,
    COUNT(*) AS 问题订单数,
    GROUP_CONCAT(id) AS 订单ID列表
FROM orders
WHERE status = 'active' AND payment_status = 'unpaid';

-- 2.4 检查退房日期已过但状态不是 completed 的订单
SELECT 
    '状态逻辑问题: 退房日期已过但订单未完成' AS 问题描述,
    COUNT(*) AS 问题订单数,
    GROUP_CONCAT(id) AS 订单ID列表
FROM orders
WHERE check_out_date < CURDATE() 
  AND status NOT IN ('completed', 'canceled');

-- 3. 检查订单和房间状态的一致性
SELECT 
    o.id AS 订单ID,
    o.order_no AS 订单号,
    o.status AS 订单状态,
    o.check_out_date AS 退房日期,
    r.id AS 房间ID,
    r.room_number AS 房间号,
    r.status AS 房间状态
FROM orders o
LEFT JOIN room r ON o.room_id = r.id
WHERE o.status IN ('active', 'pending') 
  AND r.status != 'occupied'
ORDER BY o.id;

-- 4. 修复建议查询
-- 4.1 建议将 pending+paid 的订单状态更新为 active
SELECT 
    '修复建议: 将pending+paid订单状态更新为active' AS 建议,
    GROUP_CONCAT(id) AS 需要修复的订单ID
FROM orders
WHERE status = 'pending' AND payment_status = 'paid';

-- 4.2 建议将退房日期已过的订单状态更新为 completed
SELECT 
    '修复建议: 将退房日期已过的订单状态更新为completed' AS 建议,
    GROUP_CONCAT(id) AS 需要修复的订单ID
FROM orders
WHERE check_out_date < CURDATE() 
  AND status NOT IN ('completed', 'canceled');

-- 5. 执行修复（取消注释以执行）
/*
-- 5.1 修复 pending+paid 的订单状态
UPDATE orders 
SET status = 'active'
WHERE status = 'pending' AND payment_status = 'paid';

-- 5.2 修复退房日期已过的订单状态
UPDATE orders o
LEFT JOIN room r ON o.room_id = r.id
SET o.status = 'completed',
    r.status = 'available'
WHERE o.check_out_date < CURDATE() 
  AND o.status NOT IN ('completed', 'canceled');
*/