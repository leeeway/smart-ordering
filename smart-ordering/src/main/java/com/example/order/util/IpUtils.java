package com.example.order.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * IP工具类
 * 用于获取客户端真实IP及校验IP范围
 *
 * @author leeway
 * @since 2026/02/02
 */
@Slf4j
public class IpUtils {

    /**
     * 获取客户端真实IP
     *
     * @param request HttpServletRequest
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("True-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于通过多个代理的情况，提取第一个非unknown的有效IP
        if (ip != null && ip.indexOf(",") > 0) {
            String[] ips = ip.split(",");
            for (String subIp : ips) {
                if (subIp != null && !"unknown".equalsIgnoreCase(subIp.trim())) {
                    ip = subIp.trim();
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 校验IP是否在CIDR范围内
     *
     * @param ip   待校验IP
     * @param cidr CIDR格式范围 (例如: 192.168.1.0/24)
     * @return 是否在范围内
     */
    public static boolean isInRange(String ip, String cidr) {
        if (!StringUtils.hasText(ip) || !StringUtils.hasText(cidr)) {
            return false;
        }

        try {
            if (!cidr.contains("/")) {
                return ip.equals(cidr);
            }

            String[] parts = cidr.split("/");
            long targetIp = ipToLong(ip);
            long networkIp = ipToLong(parts[0]);
            int maskBits = Integer.parseInt(parts[1]);

            long mask = (0xFFFFFFFFL << (32 - maskBits)) & 0xFFFFFFFFL;
            return (targetIp & mask) == (networkIp & mask);
        } catch (Exception e) {
            log.error("IP范围校验异常, ip: {}, cidr: {}", ip, cidr, e);
            return false;
        }
    }

    /**
     * IP地址转Long
     *
     * @param ip IP地址
     * @return Long值
     */
    public static long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (Long.parseLong(octets[i]) << (24 - (8 * i)));
        }
        return result & 0xFFFFFFFFL;
    }
}
