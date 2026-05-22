package com.ftp.server;

import com.ftp.config.Config;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

class ServerSocketFactory {

    ServerSocket createServerSocket(int port, InetAddress bindAddr) throws IOException {
        ServerSocket ss = new ServerSocket(port, 50, bindAddr);
        ss.setReuseAddress(true);
        ss.setSoTimeout(1000);
        return ss;
    }

    List<InetAddress> getListenAddresses(Config config) throws IOException {
        List<InetAddress> addresses = new ArrayList<>();
        
        String bindAddressStr = config.getBindAddress();
        
        // 如果用户明确指定了绑定地址，就只绑定该地址
        if (!"::".equals(bindAddressStr) && !"0.0.0.0".equals(bindAddressStr)) {
            try {
                InetAddress bindAddr = InetAddress.getByName(bindAddressStr);
                addresses.add(bindAddr);
                return addresses;
            } catch (Exception e) {
                // 如果指定的地址无效，回退到通配符
            }
        }
        
        // 在现代操作系统上，监听IPv6通配符(::)就足够了
        // 它会同时接受IPv4和IPv6连接
        try {
            InetAddress ipv6Wildcard = InetAddress.getByName("::");
            addresses.add(ipv6Wildcard);
            return addresses;
        } catch (Exception e) {
            // 如果IPv6通配符不可用，就回退到IPv4通配符
        }
        
        try {
            InetAddress ipv4Wildcard = InetAddress.getByName("0.0.0.0");
            addresses.add(ipv4Wildcard);
        } catch (Exception e) {
            throw new IOException("Failed to get any wildcard addresses for listening", e);
        }
        
        return addresses;
    }

    private boolean shouldSkipInterface(NetworkInterface ni, String listenInterface) {
        if (listenInterface == null || "auto".equalsIgnoreCase(listenInterface)) {
            return false;
        }
        return !ni.getName().equalsIgnoreCase(listenInterface);
    }

    private boolean isExcludedAddress(InetAddress addr) {
        if (addr instanceof Inet6Address && addr.isLinkLocalAddress()) {
            return true;
        }

        return false;
    }

    String getInterfaceName(InetAddress addr) {
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(addr);
            return ni != null ? ni.getName() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
