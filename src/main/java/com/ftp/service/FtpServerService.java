package com.ftp.service;

import com.ftp.config.Config;
import com.ftp.ui.ConnectionListener;

import java.io.IOException;

/**
 * FTP服务器服务接口，提供服务器管理的核心功能
 * 分离UI与业务逻辑，提高可测试性和可维护性
 */
public interface FtpServerService {
    
    /**
     * 启动FTP服务器
     * @throws IOException 启动失败时抛出
     */
    void start() throws IOException;
    
    /**
     * 停止FTP服务器
     */
    void stop();
    
    /**
     * 检查服务器是否运行中
     * @return 是否运行中
     */
    boolean isRunning();
    
    /**
     * 获取当前活动连接数
     * @return 活动连接数
     */
    int getActiveConnections();
    
    /**
     * 获取服务器端口
     * @return 端口号
     */
    int getPort();
    
    /**
     * 获取最大连接数
     * @return 最大连接数
     */
    int getMaxConnections();
    
    /**
     * 重新加载配置
     */
    void reloadConfig();
    
    /**
     * 添加连接监听器
     * @param listener 连接监听器
     */
    void addConnectionListener(ConnectionListener listener);
    
    /**
     * 移除连接监听器
     * @param listener 连接监听器
     */
    void removeConnectionListener(ConnectionListener listener);
    
    /**
     * 获取当前配置
     * @return 配置对象
     */
    Config getConfig();
}
