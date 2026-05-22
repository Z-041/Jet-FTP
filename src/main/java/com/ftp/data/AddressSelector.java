package com.ftp.data;

import com.ftp.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

class AddressSelector {
    private static final Logger logger = LoggerFactory.getLogger(AddressSelector.class);

    InetAddress selectAddressForClient(InetAddress clientAddress, Config config) throws IOException {
        InetAddress configuredAddr = getConfiguredExternalAddress(clientAddress, config);
        if (configuredAddr != null) {
            logger.info("Using configured external IP: " + formatAddress(configuredAddr));
            return configuredAddr;
        }

        if (clientAddress != null) {
            logger.debug("Client address: " + formatAddress(clientAddress) + 
                         " (SiteLocal: " + clientAddress.isSiteLocalAddress() + 
                         ", LinkLocal: " + clientAddress.isLinkLocalAddress() + ")");
            
            InetAddress subnetMatch = findAddressInSameSubnet(clientAddress);
            if (subnetMatch != null) {
                logger.info("Found matching subnet address: " + formatAddress(subnetMatch));
                return subnetMatch;
            }
            
            logger.debug("No matching subnet found, selecting best address");
        }

        InetAddress bestAddr = findBestAddressForClient(clientAddress, config);
        logger.info("Using best available address: " + formatAddress(bestAddr) + 
                   " (Client was: " + (clientAddress != null ? formatAddress(clientAddress) : "unknown") + ")");
        return bestAddr;
    }

    private String formatAddress(InetAddress addr) {
        if (addr instanceof Inet6Address) {
            return compressIPv6(addr.getHostAddress());
        }
        return addr.getHostAddress();
    }

    private String compressIPv6(String address) {
        if (address == null || address.isEmpty()) {
            return address;
        }

        try {
            // Step 1: Split address into parts
            String[] parts = address.split(":");
            int nonEmptyCount = 0;
            
            // Clean up parts and count non-empty ones
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].isEmpty()) {
                    parts[i] = "";
                } else {
                    parts[i] = parts[i].toLowerCase();
                    nonEmptyCount++;
                }
            }

            // Special case for ::1
            if (address.equals("0:0:0:0:0:0:0:1")) {
                return "::1";
            }

            // Special case for ::
            if (address.equals("0:0:0:0:0:0:0:0")) {
                return "::";
            }

            // Step 2: Find the longest run of zeros to replace with ::
            int maxZeroLength = 0;
            int maxZeroStart = -1;
            int currentZeroLength = 0;
            int currentZeroStart = -1;

            for (int i = 0; i < parts.length; i++) {
                if ("0".equals(parts[i]) || parts[i].isEmpty()) {
                    if (currentZeroLength == 0) {
                        currentZeroStart = i;
                    }
                    currentZeroLength++;
                    if (currentZeroLength > maxZeroLength) {
                        maxZeroLength = currentZeroLength;
                        maxZeroStart = currentZeroStart;
                    }
                } else {
                    currentZeroLength = 0;
                    currentZeroStart = -1;
                }
            }

            // Step 3: Build the compressed address
            StringBuilder sb = new StringBuilder();
            
            // If we found a zero sequence to compress
            if (maxZeroLength >= 2 && maxZeroStart != -1) {
                boolean needDoubleColon = true;
                
                for (int i = 0; i < parts.length; i++) {
                    if (i >= maxZeroStart && i < maxZeroStart + maxZeroLength) {
                        if (needDoubleColon) {
                            sb.append("::");
                            needDoubleColon = false;
                        }
                        continue;
                    }
                    
                    if (sb.length() > 0 && !sb.toString().endsWith("::") && !sb.toString().endsWith(":")) {
                        sb.append(":");
                    }
                    
                    if (!parts[i].isEmpty()) {
                        // Remove leading zeros
                        String part = parts[i];
                        if (part.length() > 1) {
                            part = part.replaceFirst("^0+", "");
                            if (part.isEmpty()) {
                                part = "0";
                            }
                        }
                        sb.append(part);
                    }
                }
                
                // Handle case where :: is at the end
                String result = sb.toString();
                if (result.endsWith(":") && !result.endsWith("::")) {
                    result = result.substring(0, result.length() - 1);
                }
                
                return result;
            }

            // If no compression, just remove leading zeros from each part
            StringBuilder simple = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (!parts[i].isEmpty()) {
                    if (simple.length() > 0) {
                        simple.append(":");
                    }
                    String part = parts[i].replaceFirst("^0+", "");
                    if (part.isEmpty()) {
                        part = "0";
                    }
                    simple.append(part);
                }
            }
            return simple.toString();
            
        } catch (Exception e) {
            return address;
        }
    }

    private InetAddress getConfiguredExternalAddress(InetAddress clientAddress, Config config) {
        try {
            boolean clientIsIPv6 = clientAddress instanceof Inet6Address;
            
            if (clientIsIPv6) {
                String ipv6Config = config.getIpv6ExternalIp();
                if (ipv6Config != null && !ipv6Config.trim().isEmpty() && 
                    !"auto".equalsIgnoreCase(ipv6Config.trim())) {
                    InetAddress addr = InetAddress.getByName(ipv6Config.trim());
                    if (addr instanceof Inet6Address) {
                        return addr;
                    }
                }
            } else {
                String ipv4Config = config.getIpv4ExternalIp();
                if (ipv4Config != null && !ipv4Config.trim().isEmpty() &&
                    !"auto".equalsIgnoreCase(ipv4Config.trim()) &&
                    !"0.0.0.0".equals(ipv4Config.trim())) {
                    InetAddress addr = InetAddress.getByName(ipv4Config.trim());
                    if (addr instanceof Inet4Address) {
                        return addr;
                    }
                }
                
                String legacyIp = config.getPassiveModeExternalIp();
                if (legacyIp != null && !legacyIp.trim().isEmpty() &&
                    !"auto".equalsIgnoreCase(legacyIp.trim()) &&
                    !"0.0.0.0".equals(legacyIp.trim())) {
                    InetAddress addr = InetAddress.getByName(legacyIp.trim());
                    if (addr instanceof Inet4Address) {
                        return addr;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Invalid configured external IP, using auto-detection", e);
        }
        return null;
    }

    private InetAddress findAddressInSameSubnet(InetAddress clientAddr) throws IOException {
        if (clientAddr == null) {
            return null;
        }

        boolean clientIsIPv6 = clientAddr instanceof Inet6Address;
        String clientIP = clientAddr.getHostAddress();
        int clientSubnetBits = getSubnetBits(clientAddr);

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isUp() || ni.isLoopback()) {
                continue;
            }

            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                
                boolean addrIsIPv6 = addr instanceof Inet6Address;
                if (clientIsIPv6 != addrIsIPv6) {
                    continue;
                }
                
                if (addr.isLoopbackAddress()) {
                    continue;
                }

                if (isSameSubnet(clientIP, addr.getHostAddress(), clientSubnetBits)) {
                    if (isAcceptableAddress(addr)) {
                        return addr;
                    }
                }
            }
        }
        return null;
    }

    private int getSubnetBits(InetAddress clientAddr) {
        if (clientAddr.isLoopbackAddress() || clientAddr.isLinkLocalAddress()) {
            return 128;
        }
        if (clientAddr.isSiteLocalAddress()) {
            return clientAddr instanceof Inet6Address ? 64 : 24;
        }
        return clientAddr instanceof Inet6Address ? 48 : 16;
    }

    private boolean isSameSubnet(String ip1, String ip2, int bits) {
        try {
            if (ip1.contains(":") || ip2.contains(":")) {
                return isSameSubnetV6(ip1, ip2, bits);
            } else {
                return isSameSubnetV4(ip1, ip2, bits);
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSameSubnetV4(String ip1, String ip2, int bits) {
        try {
            String[] parts1 = ip1.split("\\.");
            String[] parts2 = ip2.split("\\.");
            if (parts1.length != 4 || parts2.length != 4) {
                return false;
            }

            long ip1Long = ipv4ToLong(parts1);
            long ip2Long = ipv4ToLong(parts2);
            long mask = bits >= 32 ? -1L : (-1L << (32 - bits));
            
            return (ip1Long & mask) == (ip2Long & mask);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSameSubnetV6(String ip1, String ip2, int prefixBits) {
        try {
            byte[] bytes1 = InetAddress.getByName(ip1).getAddress();
            byte[] bytes2 = InetAddress.getByName(ip2).getAddress();
            
            if (bytes1.length != 16 || bytes2.length != 16) {
                return false;
            }
            
            int fullBytes = prefixBits / 8;
            int remainingBits = prefixBits % 8;
            
            for (int i = 0; i < fullBytes && i < 16; i++) {
                if ((bytes1[i] & 0xFF) != (bytes2[i] & 0xFF)) {
                    return false;
                }
            }
            
            if (remainingBits > 0 && fullBytes < 16) {
                int mask = 0xFF << (8 - remainingBits);
                if ((bytes1[fullBytes] & mask) != (bytes2[fullBytes] & mask)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private long ipv4ToLong(String[] parts) {
        return (Long.parseLong(parts[0]) << 24) |
               (Long.parseLong(parts[1]) << 16) |
               (Long.parseLong(parts[2]) << 8) |
               Long.parseLong(parts[3]);
    }

    private InetAddress findBestAddressForClient(InetAddress clientAddr, Config config) throws IOException {
        boolean preferIPv6 = config.isPreferIPv6();
        boolean clientIsIPv6 = clientAddr instanceof Inet6Address;
        boolean clientIsLoopback = clientAddr != null && clientAddr.isLoopbackAddress();

        List<InetAddressScored> candidates = new ArrayList<>();
        List<InetAddressScored> loopbackCandidates = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isUp()) {
                continue;
            }

            boolean isLoopbackInterface = ni.isLoopback();
            if (!clientIsLoopback && isLoopbackInterface) {
                continue;
            }

            String listenInterface = config.getListenInterface();
            if (listenInterface != null && !"auto".equalsIgnoreCase(listenInterface) &&
                !ni.getName().equalsIgnoreCase(listenInterface)) {
                continue;
            }

            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                
                boolean addrIsIPv6 = addr instanceof Inet6Address;
                
                if (clientIsIPv6 && !addrIsIPv6 && !preferIPv6) {
                    continue;
                }
                if (!clientIsIPv6 && addrIsIPv6 && preferIPv6) {
                    continue;
                }
                
                if (!clientIsLoopback && addr.isLoopbackAddress()) {
                    continue;
                }

                if (!isAcceptableAddress(addr, clientIsLoopback)) {
                    continue;
                }

                int score = calculateAddressScore(addr, clientAddr, ni, preferIPv6);
                InetAddressScored scored = new InetAddressScored(addr, score);
                
                if (addr.isLoopbackAddress()) {
                    loopbackCandidates.add(scored);
                } else {
                    candidates.add(scored);
                }
            }
        }

        // If client is loopback, prefer loopback address
        if (clientIsLoopback && !loopbackCandidates.isEmpty()) {
            loopbackCandidates.sort((a, b) -> Integer.compare(b.score, a.score));
            return loopbackCandidates.get(0).address;
        }

        if (candidates.isEmpty()) {
            if (clientAddr != null && clientAddr.isLoopbackAddress()) {
                return clientAddr instanceof Inet6Address ? InetAddress.getByName("::1") : InetAddress.getByName("127.0.0.1");
            }
            
            throw new IOException("No suitable network address found for passive mode data connection. " +
                                "Please check network configuration or set passive mode external IP in config.");
        }

        candidates.sort((a, b) -> Integer.compare(b.score, a.score));
        return candidates.get(0).address;
    }

    private int calculateAddressScore(InetAddress serverAddr, InetAddress clientAddr, 
                                      NetworkInterface ni, boolean preferIPv6) {
        int score = 0;

        if (clientAddr != null) {
            int subnetBits = getSubnetBits(clientAddr);
            if (isSameSubnet(serverAddr.getHostAddress(), clientAddr.getHostAddress(), subnetBits)) {
                score += 10000;
            }
        }

        if (!ni.isVirtual()) {
            score += 500;
        }

        if (!serverAddr.isLinkLocalAddress()) {
            score += 200;
        }

        String name = ni.getName().toLowerCase();
        if (name.startsWith("eth") || name.startsWith("en") || name.startsWith("wlan") ||
            name.startsWith("wlp") || name.startsWith("ens")) {
            score += 300;
        }

        if (preferIPv6 && serverAddr instanceof Inet6Address) {
            score += 150;
        }

        String hostAddr = serverAddr.getHostAddress();
        if (hostAddr.contains("192.168.56.") || hostAddr.contains("192.168.57.")) {
            score -= 400;
        }
        if (hostAddr.contains("198.18.")) {
            score -= 300;
        }

        return score;
    }

    private boolean isAcceptableAddress(InetAddress addr, boolean allowLoopback) {
        String hostAddr = addr.getHostAddress();
        if (hostAddr == null) {
            return false;
        }
        
        if (addr.isLoopbackAddress()) {
            return allowLoopback;
        }
        
        if (addr instanceof Inet4Address) {
            if (hostAddr.startsWith("0.") ||
                hostAddr.startsWith("127.") ||
                hostAddr.startsWith("169.254.") ||
                hostAddr.startsWith("192.0.0.") ||
                hostAddr.startsWith("192.0.2.") ||
                hostAddr.startsWith("198.51.100.") ||
                hostAddr.startsWith("203.0.113.") ||
                hostAddr.equals("255.255.255.255")) {
                return false;
            }
        }
        
        if (addr instanceof Inet6Address) {
            if (hostAddr.toLowerCase().startsWith("fe80:") || 
                hostAddr.toLowerCase().startsWith("fe80::")) {
                return false;
            }
            if (hostAddr.toLowerCase().startsWith("2001:db8:") ||
                hostAddr.startsWith("2001:0DB8:")) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isAcceptableAddress(InetAddress addr) {
        return isAcceptableAddress(addr, false);
    }

    private static class InetAddressScored {
        final InetAddress address;
        final int score;

        InetAddressScored(InetAddress address, int score) {
            this.address = address;
            this.score = score;
        }
    }
}
