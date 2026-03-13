package com.example.order.controller;

import cn.gydev.lib.bean.ResultBean;
import com.example.order.bean.order.*;
import com.example.news.feign.OaCenterClient;
import com.example.order.service.OrderService;
import com.example.order.util.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 订单控制器
 * 提供商品查询和下单接口
 *
 * @author leeway
 * @since 2026/02/01
 */
@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OaCenterClient oaCenterClient;

    /**
     * 获取所有店铺及其可用商品列表
     *
     * @return 店铺列表
     */
    @GetMapping("/shops")
    public ResultBean<List<ShopInfo>> getShops() {
        log.info("获取店铺列表请求");
        try {
            List<ShopInfo> shops = orderService.getShops();
            return ResultBean.success("获取成功", shops);
        } catch (Exception e) {
            log.error("获取店铺列表失败", e);
            return ResultBean.statusError("哎呀，坊间名录（店铺信息）似乎在一阵乱风中迷了路。本坊正设法找回，请仙友稍事歇息。🏮");
        }
    }

    /**
     * 获取最近订单列表（用于轮播展示）
     *
     * @return 最近订单列表
     */
    @GetMapping("/recent")
    public ResultBean<List<RecentOrderDto>> getRecentOrders() {
        log.info("获取最近订单列表请求");
        try {
            return ResultBean.success("获取成功", orderService.getRecentOrders());
        } catch (Exception e) {
            log.error("获取最近订单失败", e);
            return ResultBean.statusError("获取最近订单失败");
        }
    }

    /**
     * 获取所有可用商品列表
     *
     * @return 商品列表
     */
    @GetMapping("/products")
    public ResultBean<List<ProductInfo>> getProducts() {
        log.info("获取商品列表请求");
        try {
            List<ProductInfo> products = orderService.getAvailableProducts();
            return ResultBean.success("获取成功", products);
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return ResultBean.statusError("糟了！库房司工报告说商品卷宗暂时被封在了灵力阵中。请仙友莫急，本坊正全力破解。✨");
        }
    }

    /**
     * 获取单个商品信息
     *
     * @param productId 商品ID
     * @return 商品信息
     */
    @GetMapping("/product/{productId}")
    public ResultBean<ProductInfo> getProduct(@PathVariable String productId) {
        log.info("获取商品信息请求, productId: {}", productId);
        try {
            ProductInfo product = orderService.getProductById(productId);
            if (product == null) {
                return ResultBean.statusError("奇怪，这枚令牌（ID:" + productId + "）对应的珍馐似乎从未在《大唐百草味》中登记过呢。🏮");
            }
            return ResultBean.success("获取成功", product);
        } catch (Exception e) {
            log.error("获取商品信息失败", e);
            return ResultBean.statusError("禀阁下，内务府正在紧急查阅此物的来历，目前灵脉不畅。请稍后再探！✨");
        }
    }

    /**
     * 允许下单的IP或CIDR范围
     */
    private static final List<String> ALLOWED_IP_RANGES = Arrays.asList(
            "127.0.0.1",);

    /**
     * 提交订单
     *
     * @param orderRequest 订单请求
     * @param request      HttpServletRequest
     * @return 订单号
     */
    @PostMapping("/submit")
    public ResultBean<String> submitOrder(@Validated @RequestBody OrderRequest orderRequest,
            HttpServletRequest request) {
        /*
         * Enumeration<String> headers = request.getHeaderNames();
         * while (headers.hasMoreElements()) {
         * String name = headers.nextElement();
         * log.info("HEADER {} = {}", name, request.getHeader(name));
         * }
         */
        // String clientIp = IPUtils.getIpFromHeader(request);
        String clientIp = getClientIp(request);
        log.info("收到下单请求, clientIp: {}, 工位地址: {}, 商品数量: {}",
                clientIp,
                orderRequest.getWorkstationAddress(),
                orderRequest.getItems().size());

        // IP 校验
        boolean allowed = false;
        for (String range : ALLOWED_IP_RANGES) {
            if (IpUtils.isInRange(clientIp, range)) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            log.warn("非法IP下单尝试: {}", clientIp);
            String funnyMessage = String.format(
                    "哎呀，检测到阁下的灵气波动（IP: %s）似乎不在本坊的派送灵阵之内。🏮\n" +
                            "莫非是从外界误入蓬莱的仙友？此间珍馐仅供盈创动力大厦领地的缘人。\n" +
                            "若想品鉴，还请阁下速速归位后再来呀！✨",
                    clientIp);
            return ResultBean.statusError(funnyMessage);
        }

        // 收货人（员工姓名）校验
        String customerName = orderRequest.getCustomerName();
        if (!org.springframework.util.StringUtils.hasText(customerName)) {
            return ResultBean.statusError("好汉请留步！🏮\n呈递御旨怎能不留下姓名？还请在“姓名”一栏写下阁下的尊姓大名。");
        }

        try {
            OaResultBean<OaUserInfoDto> oaResult = oaCenterClient.getByStaffName(customerName);
            if (oaResult == null || !oaResult.isSuccess() || oaResult.getData() == null) {
                log.warn("OA中心未找到员工信息: {}", customerName);
                String funnyStaffMessage = String.format(
                        "哎呀，本坊寻遍《盛唐名录》，竟未寻得“%s”这位仙友。🏮\n" +
                                "莫非是阁下名字太响亮，在户部登记了别号？\n" +
                                "（温馨提示：若有重名，请尝试在名字后加数字，如：‘%s02’）✨",
                        customerName, customerName);
                return ResultBean.statusError(funnyStaffMessage);
            }
        } catch (Exception e) {
            log.error("调用OA中心校验员工信息失败: {}", customerName, e);
            return ResultBean.statusError("糟糕，通往内务府的传音符（OA接口）似乎失效了。🏮\n请阁下稍后再试，或联络工部仙官（技术支持）查验。");
        }

        try {
            return orderService.placeOrder(orderRequest);
        } catch (Exception e) {
            log.error("订单提交失败: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.contains("java.lang") || errorMessage.length() > 200) {
                errorMessage = "糟糕，兴许是京城人潮拥挤，快马驿站（接口）受阻了。还请仙友稍事歇息，待灵力平复后再行呈递。✨";
            }
            return ResultBean.statusError(errorMessage);
        }
    }

    public static String getClientIp(HttpServletRequest request) {

        String ip = getHeader(request, "X-Original-Forwarded-For");

        if (!isValidIp(ip)) {
            ip = getHeader(request, "X-Forwarded-For");
        }

        if (!isValidIp(ip)) {
            ip = getHeader(request, "GY-X-Forwarded-For");
        }

        if (!isValidIp(ip)) {
            ip = getHeader(request, "X-Real-IP");
        }

        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            // 第一个才是真实客户端 IP
            ip = ip.split(",")[0].trim();
        }

        return ip == null ? "0.0.0.0" : ip;
    }

    private static String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        return (value == null || value.isEmpty() || "unknown".equalsIgnoreCase(value))
                ? null
                : value;
    }

    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }
}
