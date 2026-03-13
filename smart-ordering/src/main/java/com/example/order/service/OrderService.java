package com.example.order.service;

import cn.gydev.lib.bean.ResultBean;
import com.example.order.bean.order.*;
import com.example.order.config.ProductConfig;
import com.example.order.dao.OrderDao;
import com.example.order.dao.ProductDao;
import com.example.order.dao.ShopDao;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单服务
 * 包含商品查询和下单功能，以及企业微信 WebHook 通知
 *
 * @author leeway
 * @since 2026/02/01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductConfig productConfig;
    private final RestTemplate restTemplate;
    private final Gson gson;
    private final OrderDao orderDao;
    private final ShopDao shopDao;
    private final ProductDao productDao;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取所有店铺及其可用商品列表（从数据库加载）
     *
     * @return 店铺列表
     */
    public List<ShopInfo> getShops() {
        log.info("从数据库加载所有店铺及其商品信息");
        List<ShopInfo> shops = shopDao.getAllShops();
        for (ShopInfo shop : shops) {
            shop.setProducts(productDao.getProductsByShopId(shop.getId()));
        }
        return shops;
    }

    /**
     * 获取所有可用商品列表（从数据库加载）
     *
     * @return 商品列表
     */
    public List<ProductInfo> getAvailableProducts() {
        return productDao.getAllAvailableProducts();
    }

    /**
     * 根据ID获取商品信息（从数据库加载）
     *
     * @param productId 商品ID
     * @return 商品信息
     */
    public ProductInfo getProductById(String productId) {
        return productDao.getProductById(productId);
    }

    /**
     * 处理下单请求（包含库存扣减和数据库落库）
     *
     * @param orderRequest 下单请求
     * @return 处理结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultBean<String> placeOrder(OrderRequest orderRequest) {
        log.info("开始处理订单请求: {}", gson.toJson(orderRequest));

        // 1. 检查订单功能是否启用
        if (!Boolean.TRUE.equals(productConfig.getEnabled())) {
            log.warn("订单功能已禁用");
            return ResultBean.statusError("禀阁下，本坊今日内务整顿，灵炉暂熄。此时无缘呈赠珍馐，待阵法重开，恭候灵气流转！🏮");
        }

        // 2. 预校验商品信息并计算总价
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : orderRequest.getItems()) {
            ProductInfo product = getProductById(item.getProductId());
            if (product == null || product.getStock() <= 0) {
                log.warn("商品不存在: {}", item.getProductId());
                return ResultBean.statusError("哎呀，这件珍宝（" + product.getName() + "）莫非已被哪位微服私访的大人悄悄买断？本坊库房竟查无此货。✨");
            }
            if (!Boolean.TRUE.equals(product.getAvailable())) {
                log.warn("商品已下架: {}", product.getName());
                return ResultBean.statusError("憾甚！这道‘" + product.getName() + "’目前灵力尚在凝聚中，今日暂不待客。仙友不妨另选它珍？🏮");
            }
            // 补充商品信息
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            totalPrice = totalPrice
                    .add(new BigDecimal(product.getPrice().toString()).multiply(new BigDecimal(item.getQuantity())));
        }

        // 3. 库存检查与预扣
        for (OrderItem item : orderRequest.getItems()) {
            log.info("预扣库存: 商品={}, 数量={}", item.getProductName(), item.getQuantity());
            int rows = productDao.reduceStock(item.getProductId(), item.getQuantity());
            if (rows <= 0) {
                log.warn("库存不足: {}", item.getProductName());
                ProductInfo currentProduct = productDao.getProductById(item.getProductId());
                int stockLeft = (currentProduct != null && currentProduct.getStock() != null)
                        ? currentProduct.getStock()
                        : 0;
                throw new RuntimeException(
                        "哎呀，慢了一步！‘" + item.getProductName() + "’刚被抢购一空，目前仅余 " + stockLeft + " 份，望仙友海涵。🏮");
            }
        }

        // 4. 生成订单号并写入数据库
        String orderNo = generateOrderNo();
        log.info("生成订单号并持久化: {}", orderNo);

        // 写入主表
        OrderMainEntity orderMain = new OrderMainEntity();
        orderMain.setOrderNo(orderNo);
        orderMain.setCustomerName(orderRequest.getCustomerName());
        orderMain.setWorkstationAddress(orderRequest.getWorkstationAddress());
        orderMain.setPhoneNumber(orderRequest.getPhoneNumber());
        orderMain.setRemark(orderRequest.getRemark());
        orderMain.setTotalPrice(totalPrice);
        orderDao.insertOrderMain(orderMain);

        // 写入明细表
        for (OrderItem item : orderRequest.getItems()) {
            OrderDetailEntity orderDetail = new OrderDetailEntity();
            orderDetail.setOrderNo(orderNo);
            orderDetail.setProductId(item.getProductId());
            orderDetail.setProductName(item.getProductName());
            orderDetail.setPrice(new BigDecimal(item.getPrice().toString()));
            orderDetail.setQuantity(item.getQuantity());
            orderDao.insertOrderDetail(orderDetail);
        }

        // 5. 发送企业微信通知
        boolean webhookResult = sendWechatWebhook(orderNo, orderRequest, totalPrice.doubleValue());
        if (!webhookResult) {
            log.error("企业微信通知发送失败，订单号: {}", orderNo);
            throw new RuntimeException("糟糕，兴许是京城人潮拥挤，快马驿站（接口）受阻了。为了稳妥起见，订单暂未入录，请仙友重试一番。✨");
        }

        log.info("订单持久化并通知成功，订单号: {}", orderNo);
        return ResultBean.success("锦书已达！您的订单已由快马收纳。请仙友在工位稍候，珍馐正快马加鞭赶来！🚴‍♂️", orderNo);
    }

    /**
     * 获取最近订单（用于轮播展示）
     *
     * @return 最近订单列表
     */
    public List<RecentOrderDto> getRecentOrders() {
        List<OrderMainEntity> orders = orderDao.selectRecentOrders();
        java.util.List<RecentOrderDto> recentOrders = new java.util.ArrayList<>();

        long now = System.currentTimeMillis();

        for (OrderMainEntity order : orders) {
            RecentOrderDto dto = new RecentOrderDto();
            // 姓名脱敏
            String name = order.getCustomerName();
            if (name != null) {
                if (name.length() <= 1) {
                    dto.setCustomerName("*");
                } else if (name.length() == 2) {
                    dto.setCustomerName(name.charAt(0) + "*");
                } else {
                    dto.setCustomerName(name.charAt(0) + "**" + name.charAt(name.length() - 1));
                }
            } else {
                dto.setCustomerName("神秘食客");
            }

            // 获取商品名列表
            List<OrderDetailEntity> details = orderDao.selectOrderDetailsByOrderNo(order.getOrderNo());
            java.util.List<String> productNames = new java.util.ArrayList<>();
            for (OrderDetailEntity detail : details) {
                productNames.add(detail.getProductName());
            }
            dto.setProducts(productNames);

            // 计算时间距今多久
            long diff = now - order.getCreateTime().getTime();
            if (diff < 60000) {
                dto.setTimeAgo("刚刚");
            } else if (diff < 3600000) {
                dto.setTimeAgo(diff / 60000 + "分钟前");
            } else if (diff < 86400000) {
                dto.setTimeAgo(diff / 3600000 + "小时前");
            } else {
                dto.setTimeAgo(diff / 86400000 + "天前");
            }

            recentOrders.add(dto);
        }
        return recentOrders;
    }

    /**
     * 生成订单号
     *
     * @return 订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "ORD" + timestamp + uuid;
    }

    /**
     * 发送企业微信 WebHook 通知
     *
     * @param orderNo      订单号
     * @param orderRequest 订单请求
     * @param totalPrice   总价
     * @return 是否发送成功
     */
    private boolean sendWechatWebhook(String orderNo, OrderRequest orderRequest, double totalPrice) {
        try {
            // 1. 获取基础映射数据
            Map<String, String> productIdToShopId = new HashMap<>();
            Map<String, String> productIdToShopName = new HashMap<>();
            List<ShopInfo> shops = shopDao.getAllShops();
            if (shops != null) {
                for (ShopInfo shop : shops) {
                    List<ProductInfo> pList = productDao.getProductsByShopId(shop.getId());
                    if (pList != null) {
                        for (ProductInfo p : pList) {
                            productIdToShopId.put(p.getId(), shop.getId());
                            productIdToShopName.put(p.getId(), shop.getName());
                        }
                    }
                }
            }

            // 2. 按 WebHook 目标拆分订单项
            List<OrderItem> shop004Items = new java.util.ArrayList<>();
            List<OrderItem> otherItems = new java.util.ArrayList<>();

            for (OrderItem item : orderRequest.getItems()) {
                if ("shop004".equals(productIdToShopId.get(item.getProductId()))) {
                    shop004Items.add(item);
                } else {
                    otherItems.add(item);
                }
            }

            boolean allSuccess = true;

            // 3. 发送辣糕坊群通知 (shop004)
            if (!shop004Items.isEmpty()) {
                String webhook004 = "";
                allSuccess &= doSendWechatWebhook(webhook004, " \n", orderNo, shop004Items, orderRequest, productIdToShopName, totalPrice);
            }

            // 4. 发送默认群通知
            if (!otherItems.isEmpty()) {
                String defaultWebhook = productConfig.getWebhookUrl();
                if (StringUtils.hasText(defaultWebhook)) {
                    allSuccess &= doSendWechatWebhook(defaultWebhook, "<@所有人>\n", orderNo, otherItems, orderRequest, productIdToShopName, totalPrice);
                } else {
                    log.error("未配置默认 WebHook URL，跳过发送普通通知");
                    // 如果只有普通商品但配置缺失，这里视具体需求决定是否报错，此处为严谨起见判定为 false
                    if (shop004Items.isEmpty()) allSuccess = false;
                }
            }

            return allSuccess;
        } catch (Exception e) {
            log.error("拆分 WebHook 通知逻辑异常", e);
            return false;
        }
    }

    /**
     * 执行底层的 WebHook 发送
     */
    private boolean doSendWechatWebhook(String url, String mention, String orderNo, List<OrderItem> items, 
                                        OrderRequest request, Map<String, String> idToNameMap, double grandTotal) {
        try {
            StringBuilder content = new StringBuilder();
            content.append(mention);
            content.append("## 📦 新订单通知 (分单)\n\n");
            content.append("**订单号：** ").append(orderNo).append("\n");
            content.append("**下单时间：** ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
            content.append("**工位地址：** ").append(request.getWorkstationAddress()).append("\n");

            if (StringUtils.hasText(request.getCustomerName())) {
                content.append("**下单人：** ").append(request.getCustomerName()).append("\n");
            }
            if (StringUtils.hasText(request.getPhoneNumber())) {
                content.append("**联系电话：** ").append(request.getPhoneNumber()).append("\n");
            }

            content.append("\n### 本单所属商品\n");
            
            // 数据再次按店铺分组以防一个推送里包含多个子店
            Map<String, List<OrderItem>> group = new HashMap<>();
            BigDecimal subTotal = BigDecimal.ZERO;
            for (OrderItem item : items) {
                String shopName = idToNameMap.getOrDefault(item.getProductId(), "未知摊位");
                group.computeIfAbsent(shopName, k -> new java.util.ArrayList<>()).add(item);
                subTotal = subTotal.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            }

            for (Map.Entry<String, List<OrderItem>> entry : group.entrySet()) {
                content.append("#### 🏮 ").append(entry.getKey()).append("\n");
                for (OrderItem item : entry.getValue()) {
                    content.append("- ").append(item.getProductName())
                            .append(" × ").append(item.getQuantity()).append("\n");
                }
            }

            content.append("\n**本次分单金额：** ¥").append(subTotal.setScale(2, java.math.RoundingMode.HALF_UP).toString()).append("\n");
            content.append("**全单总额：** ¥").append(String.format("%.2f", grandTotal)).append("\n");

            if (StringUtils.hasText(request.getRemark())) {
                content.append("\n**备注：** ").append(request.getRemark()).append("\n");
            }

            // 构建 JSON
            Map<String, Object> body = new HashMap<>();
            body.put("msgtype", "markdown");
            Map<String, String> markdown = new HashMap<>();
            markdown.put("content", content.toString());
            body.put("markdown", markdown);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(gson.toJson(body), headers);

            restTemplate.postForObject(url, entity, String.class);
            return true;
        } catch (Exception e) {
            log.error("发送 WebHook 失败, URL: {}, Error: {}", url, e.getMessage());
            return false;
        }
    }
}
