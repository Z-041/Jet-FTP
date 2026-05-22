package com.ftp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IPFilter {
    private static final Logger logger = LoggerFactory.getLogger(IPFilter.class);
    
    private final Set<String> whitelist;
    private final Set<String> blacklist;
    private final Set<String> subnetWhitelist;
    private final Set<String> subnetBlacklist;
    private volatile boolean whitelistEnabled = false;
    private volatile boolean blacklistEnabled = true;

    private static final IPFilter INSTANCE = new IPFilter();

    private IPFilter() {
        this.whitelist = ConcurrentHashMap.newKeySet();
        this.blacklist = ConcurrentHashMap.newKeySet();
        this.subnetWhitelist = ConcurrentHashMap.newKeySet();
        this.subnetBlacklist = ConcurrentHashMap.newKeySet();
    }

    public static IPFilter getInstance() {
        return INSTANCE;
    }

    public boolean isAllowed(InetAddress address) {
        String ip = address.getHostAddress();
        
        if (blacklistEnabled && isInBlacklist(ip)) {
            logger.warn("IP blocked by blacklist: " + ip);
            return false;
        }
        
        if (whitelistEnabled && !isInWhitelist(ip)) {
            logger.warn("IP blocked by whitelist: " + ip);
            return false;
        }
        
        return true;
    }

    private boolean isInBlacklist(String ip) {
        if (blacklist.contains(ip)) {
            return true;
        }
        for (String subnet : subnetBlacklist) {
            if (isInSubnet(ip, subnet)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInWhitelist(String ip) {
        if (whitelist.contains(ip)) {
            return true;
        }
        for (String subnet : subnetWhitelist) {
            if (isInSubnet(ip, subnet)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查 IP 地址是否在指定子网内
     * 
     * <p>算法原理：将 IP 地址和子网地址转换为二进制，然后根据前缀长度进行掩码运算，
     * 如果两者的高位前缀相同，则说明 IP 在该子网内。</p>
     * 
     * @param ip IP 地址
     * @param subnet CIDR 格式的子网（如 192.168.1.0/24）
     * @return 如果 IP 在子网内返回 true，否则返回 false
     */
    private boolean isInSubnet(String ip, String subnet) {
        try {
            String[] parts = subnet.split("/");
            if (parts.length != 2) {
                return false;
            }
            
            String subnetIP = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);
            
            // 检测是否为 IPv6
            if (subnetIP.contains(":")) {
                return isInSubnetV6(ip, subnetIP, prefixLength);
            } else {
                return isInSubnetV4(ip, subnetIP, prefixLength);
            }
        } catch (Exception e) {
            logger.debug("Invalid subnet format: " + subnet, e);
            return false;
        }
    }

    /**
     * 检查 IPv4 地址是否在子网内
     * 
     * <p>算法：将 IPv4 地址转换为 long 类型，然后使用位掩码比较网络前缀</p>
     * 
     * @param ip IPv4 地址
     * @param subnetIP 子网地址
     * @param prefixLength 前缀长度（如 24 表示/24）
     * @return 如果 IP 在子网内返回 true
     */
    private boolean isInSubnetV4(String ip, String subnetIP, int prefixLength) {
        try {
            long subnetAddr = ipv4ToLong(subnetIP);
            long ipAddr = ipv4ToLong(ip);
            long mask = -1L << (32 - prefixLength);
            
            return (subnetAddr & mask) == (ipAddr & mask);
        } catch (Exception e) {
            logger.debug("Error checking IPv4 subnet", e);
            return false;
        }
    }

    /**
     * 检查 IPv6 地址是否在子网内
     * 
     * <p>算法：将 IPv6 地址转换为字节数组，然后逐字节比较前缀。对于不完整字节，
     * 使用位掩码比较高位比特。</p>
     * 
     * @param ip IPv6 地址
     * @param subnetIP 子网地址
     * @param prefixLength 前缀长度（0-128）
     * @return 如果 IP 在子网内返回 true
     */
    private boolean isInSubnetV6(String ip, String subnetIP, int prefixLength) {
        try {
            byte[] ipBytes = InetAddress.getByName(ip).getAddress();
            byte[] subnetBytes = InetAddress.getByName(subnetIP).getAddress();
            
            if (ipBytes.length != 16 || subnetBytes.length != 16) {
                return false;
            }
            
            int fullBytes = prefixLength / 8;
            int remainingBits = prefixLength % 8;
            
            for (int i = 0; i < fullBytes && i < 16; i++) {
                if ((ipBytes[i] & 0xFF) != (subnetBytes[i] & 0xFF)) {
                    return false;
                }
            }
            
            if (remainingBits > 0 && fullBytes < 16) {
                int mask = 0xFF << (8 - remainingBits);
                if ((ipBytes[fullBytes] & mask) != (subnetBytes[fullBytes] & mask)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.debug("Error checking IPv6 subnet", e);
            return false;
        }
    }

    private long ipv4ToLong(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ip);
        }
        
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) + Long.parseLong(parts[i]);
        }
        return result;
    }

    public void addToWhitelist(String ip) {
        if (ip.contains("/")) {
            subnetWhitelist.add(ip);
            logger.info("Added subnet to whitelist: " + ip);
        } else {
            whitelist.add(ip);
            logger.info("Added IP to whitelist: " + ip);
        }
    }

    public void addToBlacklist(String ip) {
        if (ip.contains("/")) {
            subnetBlacklist.add(ip);
            logger.info("Added subnet to blacklist: " + ip);
        } else {
            blacklist.add(ip);
            logger.info("Added IP to blacklist: " + ip);
        }
    }

    public void removeFromWhitelist(String ip) {
        whitelist.remove(ip);
        subnetWhitelist.remove(ip);
        logger.info("Removed from whitelist: " + ip);
    }

    public void removeFromBlacklist(String ip) {
        blacklist.remove(ip);
        subnetBlacklist.remove(ip);
        logger.info("Removed from blacklist: " + ip);
    }

    public void enableWhitelist(boolean enable) {
        this.whitelistEnabled = enable;
        logger.info("Whitelist " + (enable ? "enabled" : "disabled"));
    }

    public void enableBlacklist(boolean enable) {
        this.blacklistEnabled = enable;
        logger.info("Blacklist " + (enable ? "enabled" : "disabled"));
    }

    public Set<String> getWhitelist() {
        Set<String> result = new HashSet<>(whitelist);
        result.addAll(subnetWhitelist);
        return result;
    }

    public Set<String> getBlacklist() {
        Set<String> result = new HashSet<>(blacklist);
        result.addAll(subnetBlacklist);
        return result;
    }

    public void clear() {
        whitelist.clear();
        blacklist.clear();
        subnetWhitelist.clear();
        subnetBlacklist.clear();
    }
}
