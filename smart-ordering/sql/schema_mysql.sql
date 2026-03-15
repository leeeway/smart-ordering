-- ============================================
-- 订单管理系统 - 数据库初始化脚本
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS shop_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shop_db;

-- ============================================
-- 1. 店铺信息表
-- ============================================
DROP TABLE IF EXISTS `shop_info`;
CREATE TABLE `shop_info` (
    `id` VARCHAR(50) PRIMARY KEY COMMENT '店铺 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
    `description` TEXT COMMENT '店铺描述',
    `avatar_url` VARCHAR(255) COMMENT '头像 URL',
    `background_url` VARCHAR(255) COMMENT '背景图 URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺信息表';

-- ============================================
-- 2. 商品信息表
-- ============================================
DROP TABLE IF EXISTS `product_info`;
CREATE TABLE `product_info` (
    `id` VARCHAR(50) PRIMARY KEY COMMENT '商品 ID',
    `shop_id` VARCHAR(50) NOT NULL COMMENT '所属店铺 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `image_url` VARCHAR(255) COMMENT '商品图片 URL',
    `description` TEXT COMMENT '商品描述',
    `available` TINYINT DEFAULT 1 COMMENT '是否可用：1-是，0-否',
    `stock` INT DEFAULT 0 COMMENT '库存数量',
    `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_shop_id` (`shop_id`),
    CONSTRAINT `fk_product_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop_info`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品信息表';

-- ============================================
-- 3. 订单主表
-- ============================================
DROP TABLE IF EXISTS `order_main`;
CREATE TABLE `order_main` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    `customer_name` VARCHAR(100) NOT NULL COMMENT '下单人姓名',
    `workstation_address` VARCHAR(255) COMMENT '工位地址',
    `phone_number` VARCHAR(20) COMMENT '联系电话',
    `remark` TEXT COMMENT '备注',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `status` TINYINT DEFAULT 1 COMMENT '订单状态：1-待处理，2-处理中，3-已完成，4-已取消',
    `ip_address` VARCHAR(50) COMMENT '下单 IP 地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_customer_name` (`customer_name`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- ============================================
-- 4. 订单明细表
-- ============================================
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `product_id` VARCHAR(50) NOT NULL COMMENT '商品 ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ============================================
-- 初始化示例数据
-- ============================================

-- 插入示例店铺数据
INSERT INTO `shop_info` (`id`, `name`, `description`, `avatar_url`, `background_url`, `sort_order`, `status`) VALUES
('shop001', '御馔海焙坊', '唐风雅韵，品味纯正，精选御用烘焙', 'tang_lady.png', 'tang_bg.png', 1, 1),
('shop002', '华京冰果肆', '主营水果捞，冰激凌，双皮奶，《古》法酿造', 'bingguo_lady.png', 'bingguo_bg.png', 2, 1),
('shop003', '长安甜馥肆', '长安甜馥肆，主营精致甜点与雅致饮品', 'sweet_lady.png', 'sweet_bg.png', 3, 1),
('shop004', '唐肆辣糕坊', '主营唐风辣味糕点，火热出炉，麻辣鲜香', 'img/tangsi_avatar.png', 'img/tangsi_bg.jpg', 4, 1);

-- 插入示例商品数据（店铺 1）
INSERT INTO `product_info` (`id`, `shop_id`, `name`, `price`, `image_url`, `description`, `available`, `stock`, `sort_order`) VALUES
('prod001', 'shop001', '锦鲤玉卷', 5.00, 'https://via.placeholder.com/150/A61C00/ffffff?text=锦鲤玉卷', '锦鲤伴红袖，玉卷带余香', 1, 100, 1),
('prod002', 'shop001', '瑞鲑锦宴', 5.00, 'https://via.placeholder.com/150/A61C00/ffffff?text=瑞鲑锦宴', '深海珍鲑，锦绣佳肴', 1, 100, 2),
('prod003', 'shop001', '玉蔬彩堂', 4.00, 'https://via.placeholder.com/150/A61C00/ffffff?text=玉蔬彩堂', '时令鲜蔬，五彩缤纷', 1, 100, 3),
('prod004', 'shop001', '金果瑞派', 3.00, 'https://via.placeholder.com/150/A61C00/ffffff?text=金果瑞派', '金秋硕果，香颂雅派', 1, 100, 4),
('prod005', 'shop001', '麻薯包', 2.00, 'img/mashu.jpg', '软糯香甜，Q 弹十足', 1, 100, 5);

-- 插入示例商品数据（店铺 2）
INSERT INTO `product_info` (`id`, `shop_id`, `name`, `price`, `image_url`, `description`, `available`, `stock`, `sort_order`) VALUES
('prod101', 'shop002', '杨梅荔枝饮', 8.00, 'https://via.placeholder.com/150/D4AF37/000000?text=杨梅荔枝饮', '酸甜清爽，消暑良品', 1, 100, 1),
('prod102', 'shop002', '凝霜琼酪（双皮奶)', 6.00, 'https://via.placeholder.com/150/D4AF37/000000?text=双皮奶', '奶香浓郁，嫩滑如丝', 1, 100, 2),
('prod103', 'shop002', '鲜果冷泉碗（水果捞）', 12.00, 'https://via.placeholder.com/150/D4AF37/000000?text=水果捞', '精选时令鲜果，满口留香', 1, 100, 3),
('prod104', 'shop002', '酥酪冰团（冰淇淋）', 5.00, 'https://via.placeholder.com/150/D4AF37/000000?text=冰激凌', '入口即化，甜而不腻', 1, 100, 4);

-- 插入示例商品数据（店铺 3）
INSERT INTO `product_info` (`id`, `shop_id`, `name`, `price`, `image_url`, `description`, `available`, `stock`, `sort_order`) VALUES
('prod201', 'shop003', '玉乳金酥挞', 6.00, 'https://via.placeholder.com/150/f2d57e/000000?text=蛋挞', '外酥里嫩，奶香浓郁', 1, 100, 1),
('prod202', 'shop003', '咖啡 (香黑玉饮)', 15.00, 'https://via.placeholder.com/150/2F1B1B/ffffff?text=咖啡', '香浓醇厚，回味悠长', 1, 100, 2),
('prod203', 'shop003', '花边蛋挞小蛋糕', 8.00, 'https://via.placeholder.com/150/f2d57e/000000?text=小蛋糕', '精致花边，甜美诱人', 1, 100, 3),
('prod204', 'shop003', '特色小吃 (盛唐小馔)', 12.00, 'https://via.placeholder.com/150/D4AF37/000000?text=小吃', '盛唐风味，精致可口', 1, 100, 4),
('prod205', 'shop003', '冰粉 (甘露冻玉饮)', 5.00, 'https://via.placeholder.com/150/F8F4E3/000000?text=冰粉', '清凉解暑，甘甜如露', 1, 100, 5);

-- 插入示例商品数据（店铺 4）
INSERT INTO `product_info` (`id`, `shop_id`, `name`, `price`, `image_url`, `description`, `available`, `stock`, `sort_order`) VALUES
('prod301', 'shop004', '火凤辣糕', 6.00, 'https://via.placeholder.com/150/FF4500/ffffff?text=火凤辣糕', '色泽红亮，麻辣诱人', 1, 100, 1),
('prod302', 'shop004', '椒香酥饼', 4.00, 'https://via.placeholder.com/150/FF4500/ffffff?text=椒香酥饼', '椒香四溢，酥脆可口', 1, 100, 2),
('prod303', 'shop004', '红油玉糕', 5.00, 'https://via.placeholder.com/150/FF4500/ffffff?text=红油玉糕', '温润如玉，红油点睛', 1, 100, 3),
('prod304', 'shop004', '麻辣团子', 3.00, 'https://via.placeholder.com/150/FF4500/ffffff?text=麻辣团子', '软糯麻辣，层次分明', 1, 100, 4);

-- ============================================
-- 说明
-- ============================================
-- 1. 执行此脚本前请确保已安装 MySQL 或 MariaDB
-- 2. 根据实际情况修改数据库名称和连接参数
-- 3. 生产环境请删除或修改示例数据
-- 4. 建议为各表添加适当的索引以优化查询性能
-- 5. 定期清理日志表数据，避免表过大
