-- ============================================
-- 订单管理系统 - 数据库初始化脚本 (SQL Server)
-- ============================================

-- 创建数据库 (请手动执行或确保有权限)
-- CREATE DATABASE shop_db;
-- GO

-- USE shop_db;
-- GO

-- ============================================
-- 1. 店铺信息表
-- ============================================
IF OBJECT_ID('shop_info', 'U') IS NOT NULL DROP TABLE shop_info;
CREATE TABLE shop_info (
    id VARCHAR(50) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(MAX),
    avatar_url VARCHAR(255),
    background_url VARCHAR(255),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT GETDATE(),
    update_time DATETIME DEFAULT GETDATE()
);

-- ============================================
-- 2. 商品信息表
-- ============================================
IF OBJECT_ID('product_info', 'U') IS NOT NULL DROP TABLE product_info;
CREATE TABLE product_info (
    id VARCHAR(50) PRIMARY KEY,
    shop_id VARCHAR(50) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(255),
    description NVARCHAR(MAX),
    available TINYINT DEFAULT 1,
    stock INT DEFAULT 0,
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT GETDATE(),
    update_time DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_product_shop FOREIGN KEY (shop_id) REFERENCES shop_info(id) ON DELETE CASCADE
);
CREATE INDEX idx_shop_id ON product_info(shop_id);

-- ============================================
-- 3. 订单主表
-- ============================================
IF OBJECT_ID('order_main', 'U') IS NOT NULL DROP TABLE order_main;
CREATE TABLE order_main (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_no VARCHAR(50) NOT NULL UNIQUE,
    customer_name NVARCHAR(100) NOT NULL,
    workstation_address NVARCHAR(255),
    phone_number VARCHAR(20),
    remark NVARCHAR(MAX),
    total_price DECIMAL(10,2) NOT NULL,
    status TINYINT DEFAULT 1,
    ip_address VARCHAR(50),
    create_time DATETIME DEFAULT GETDATE(),
    update_time DATETIME DEFAULT GETDATE()
);
CREATE INDEX idx_order_no ON order_main(order_no);
CREATE INDEX idx_customer_name ON order_main(customer_name);
CREATE INDEX idx_create_time ON order_main(create_time);

-- ============================================
-- 4. 订单明细表
-- ============================================
IF OBJECT_ID('order_detail', 'U') IS NOT NULL DROP TABLE order_detail;
CREATE TABLE order_detail (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_no VARCHAR(50) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    product_name NVARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    create_time DATETIME DEFAULT GETDATE()
);
CREATE INDEX idx_detail_order_no ON order_detail(order_no);
CREATE INDEX idx_detail_product_id ON order_detail(product_id);

-- ============================================
-- 初始化示例数据
-- ============================================

-- 插入示例店铺数据
INSERT INTO shop_info (id, name, description, avatar_url, background_url, sort_order, status) VALUES
('shop001', N'御馔海焙坊', N'唐风雅韵，品味纯正，精选御用烘焙', 'tang_lady.png', 'tang_bg.png', 1, 1),
('shop002', N'华京冰果肆', N'主营水果捞，冰激凌，双皮奶，《古》法酿造', 'bingguo_lady.png', 'bingguo_bg.png', 2, 1),
('shop003', N'长安甜馥肆', N'长安甜馥肆，主营精致甜点与雅致饮品', 'sweet_lady.png', 'sweet_bg.png', 3, 1),
('shop004', N'唐肆辣糕坊', N'主营唐风辣味糕点，火热出炉，麻辣鲜香', 'img/tangsi_avatar.png', 'img/tangsi_bg.jpg', 4, 1);

-- 插入示例商品数据（店铺 1）
INSERT INTO product_info (id, shop_id, name, price, image_url, description, available, stock, sort_order) VALUES
('prod001', 'shop001', N'锦鲤玉卷', 5.00, 'https://via.placeholder.com/150/A61C00/ffffff?text=锦鲤玉卷', N'锦鲤伴红袖，玉卷带余香', 1, 100, 1),
('prod002', 'shop001', N'瑞鲑锦宴', 5.00, 'https://via.placeholder.com/150/A61C00/ffffff?text=瑞鲑锦宴', N'深海珍鲑，锦绣佳肴', 1, 100, 2),
('prod003', 'shop001', N'玉蔬彩堂', 4.00, 'https://via.placeholder.com/150/A61C00/ffffff?text=玉蔬彩堂', N'时令鲜蔬，五彩缤纷', 1, 100, 3),
('prod004', 'shop001', N'金果瑞派', 3.00, 'https://via.placeholder.com/150/A61C00/ffffff?text=金果瑞派', N'金秋硕果，香颂雅派', 1, 100, 4),
('prod005', 'shop001', N'麻薯包', 2.00, 'img/mashu.jpg', N'软糯香甜，Q 弹十足', 1, 100, 5);

-- 插入示例商品数据（店铺 2）
INSERT INTO product_info (id, shop_id, name, price, image_url, description, available, stock, sort_order) VALUES
('prod101', 'shop002', N'杨梅荔枝饮', 8.00, 'https://via.placeholder.com/150/D4AF37/000000?text=杨梅荔枝饮', N'酸甜清爽，消暑良品', 1, 100, 1),
('prod102', 'shop002', N'凝霜琼酪（双皮奶)', 6.00, 'https://via.placeholder.com/150/D4AF37/000000?text=双皮奶', N'奶香浓郁，嫩滑如丝', 1, 100, 2),
('prod103', 'shop002', N'鲜果冷泉碗（水果捞）', 12.00, 'https://via.placeholder.com/150/D4AF37/000000?text=水果捞', N'精选时令鲜果，满口留香', 1, 100, 3),
('prod104', 'shop002', N'酥酪冰团（冰淇淋）', 5.00, 'https://via.placeholder.com/150/D4AF37/000000?text=冰激凌', N'入口即化，甜而不腻', 1, 100, 4);

-- 插入示例商品数据（店铺 3）
INSERT INTO product_info (id, shop_id, name, price, image_url, description, available, stock, sort_order) VALUES
('prod201', 'shop003', N'玉乳金酥挞', 6.00, 'https://via.placeholder.com/150/f2d57e/000000?text=蛋挞', N'外酥里嫩，奶香浓郁', 1, 100, 1),
('prod202', 'shop003', N'咖啡 (香黑玉饮)', 15.00, 'https://via.placeholder.com/150/2F1B1B/ffffff?text=咖啡', N'香浓醇厚，回味悠长', 1, 100, 2),
('prod203', 'shop003', N'花边蛋挞小蛋糕', 8.00, 'https://via.placeholder.com/150/f2d57e/000000?text=小蛋糕', N'精致花边，甜美诱人', 1, 100, 3),
('prod204', 'shop003', N'特色小吃 (盛唐小馔)', 12.00, 'https://via.placeholder.com/150/D4AF37/000000?text=小吃', N'盛唐风味，精致可口', 1, 100, 4),
('prod205', 'shop003', N'冰粉 (甘露冻玉饮)', 5.00, 'https://via.placeholder.com/150/F8F4E3/000000?text=冰粉', N'清凉解暑，甘甜如露', 1, 100, 5);

-- 插入示例商品数据（店铺 4）
INSERT INTO product_info (id, shop_id, name, price, image_url, description, available, stock, sort_order) VALUES
('prod301', 'shop004', N'火凤辣糕', 6.00, 'https://via.placeholder.com/150/FF4500/ffffff?text=火凤辣糕', N'色泽红亮，麻辣诱人', 1, 100, 1),
('prod302', 'shop004', N'椒香酥饼', 4.00, 'https://via.placeholder.com/150/FF4500/ffffff?text=椒香酥饼', N'椒香四溢，酥脆可口', 1, 100, 2),
('prod303', 'shop004', N'红油玉糕', 5.00, 'https://via.placeholder.com/150/FF4500/ffffff?text=红油玉糕', N'温润如玉，红油点睛', 1, 100, 3),
('prod304', 'shop004', N'麻辣团子', 3.00, 'https://via.placeholder.com/150/FF4500/ffffff?text=麻辣团子', N'软糯麻辣，层次分明', 1, 100, 4);
