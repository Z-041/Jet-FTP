# FTP 服务器管理系统

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A36.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

一个基于 Java 17+ 开发的高性能 FTP 服务器，完全兼容 RFC 959 协议标准，提供现代化的 Swing GUI 管理界面。

## ✨ 特性

- **📡 完整协议支持**：实现 RFC 959 标准，支持所有标准 FTP 命令
- **🎨 现代化界面**：基于 Swing 的精美 GUI，支持配置管理、连接监控、日志查看
- **🔒 安全可靠**：支持用户认证、IP 过滤、路径遍历防护、密码加密存储
- **⚡ 高性能**：多线程架构，连接池管理，支持带宽限制
- **📊 实时监控**：实时显示连接状态、传输速率、运行时间
- **🔧 灵活配置**：配置文件驱动，支持热加载（部分配置）
- **📝 完善日志**：分级日志系统，支持日志轮转

## 🚀 快速开始

### 环境要求

- Java 17 或更高版本
- Maven 3.6 或更高版本

### 安装运行

```bash
# 克隆仓库
git clone https://github.com/yourusername/ftp-server.git
cd ftp-server

# 编译打包
mvn clean package

# 运行程序
java -jar target/ftp-server.jar
```

### 默认配置

- **监听端口**：21
- **根目录**：ftp-root/
- **最大连接数**：10
- **超时时间**：300 秒
- **日志级别**：INFO

## 📖 使用指南

### 启动服务器

1. 运行程序后，点击标题栏的"启动服务"按钮
2. 状态指示灯变为绿色表示启动成功
3. 左侧面板显示监听端口和当前连接数

### 配置管理

1. 点击左侧面板的"编辑配置"按钮
2. 修改端口号、根目录、最大连接数等参数
3. 点击"保存"按钮保存配置
4. **注意**：部分配置需要重启服务器才能生效

### 用户管理

用户数据存储在 `users.dat` 文件中，格式为：
```
username|password_hash|root_directory|is_anonymous
```

默认用户：
- 用户名：`admin`
- 密码：`admin`

### 连接监控

- 实时显示当前连接的客户端信息
- 显示客户端 IP 地址、当前目录、传输状态
- 支持断开指定连接

### 日志查看

- 实时显示服务器运行日志
- 支持日志级别筛选
- 支持日志导出

## 🏗️ 项目结构

```
ftp-server/
├── src/
│   ├── main/
│   │   ├── java/com/ftp/
│   │   │   ├── Main.java                 # 程序入口
│   │   │   ├── auth/                     # 认证模块
│   │   │   │   ├── User.java
│   │   │   │   └── UserManager.java
│   │   │   ├── command/                  # FTP 命令实现
│   │   │   │   ├── Command.java
│   │   │   │   ├── CommandFactory.java
│   │   │   │   └── impl/                 # 39 个 FTP 命令实现
│   │   │   ├── config/                   # 配置管理
│   │   │   │   ├── Config.java
│   │   │   │   ├── ConfigManager.java
│   │   │   │   └── ConfigValidator.java
│   │   │   ├── logging/                  # 日志系统
│   │   │   │   └── Logger.java
│   │   │   ├── server/                   # 服务器核心
│   │   │   │   ├── FtpServer.java
│   │   │   │   └── ClientHandler.java
│   │   │   ├── transfer/                 # 数据传输
│   │   │   │   ├── DataTransfer.java
│   │   │   │   └── TransferMode.java
│   │   │   ├── ui/                       # GUI 界面
│   │   │   │   ├── FtpServerUI.java
│   │   │   │   ├── LeftControlPanel.java
│   │   │   │   ├── ConnectionMonitorPanel.java
│   │   │   │   ├── LogPanel.java
│   │   │   │   ├── StatusBar.java
│   │   │   │   └── UIStyles.java
│   │   │   └── util/                     # 工具类
│   │   │       ├── BandwidthLimiter.java
│   │   │       ├── PathValidator.java
│   │   │       └── IpFilter.java
│   │   └── resources/
│   │       └── config.properties         # 默认配置文件
│   └── test/                             # 单元测试
├── target/                               # 编译输出
├── ftp-root/                             # FTP 根目录（自动创建）
├── logs/                                 # 日志目录（自动创建）
├── pom.xml                               # Maven 配置
├── .gitignore                            # Git 排除文件
└── README.md                             # 项目文档
```

## 🔧 配置说明

### 配置文件

配置文件位于 `src/main/resources/config.properties`，包含以下选项：

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `server.port` | FTP 服务器监听端口 | 21 |
| `server.rootDirectory` | FTP 文件根目录 | ftp-root |
| `server.maxConnections` | 最大并发连接数 | 10 |
| `server.timeoutSeconds` | 连接超时时间（秒） | 300 |
| `log.level` | 日志级别（DEBUG/INFO/WARN/ERROR） | INFO |
| `log.filePath` | 日志文件路径 | logs/ftp-server.log |

### 系统属性

- `java.net.preferIPv4Stack=true`：优先使用 IPv4 地址

## 🧪 测试

```bash
# 运行所有测试
mvn test

# 运行特定测试
mvn test -Dtest=ConfigTest

# 生成测试报告
mvn surefire-report:report
```

## 📚 技术栈

- **Java 17+**：核心编程语言
- **Swing**：GUI 框架
- **Maven**：项目构建工具
- **JUnit 5**：单元测试框架
- **BCrypt**：密码加密
- **RFC 959**：FTP 协议标准

## 🔐 安全特性

- **密码加密**：使用 BCrypt 算法存储用户密码
- **路径遍历防护**：严格验证文件路径，防止目录遍历攻击
- **IP 过滤**：支持白名单/黑名单模式
- **连接限制**：防止 DoS 攻击
- **带宽限制**：支持上传/下载速度限制

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目基于 MIT 许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- [RFC 959](https://tools.ietf.org/html/rfc959) - File Transfer Protocol (FTP)
- [Apache Commons Net](https://commons.apache.org/proper/commons-net/) - FTP 客户端参考实现
- [jBCrypt](https://github.com/jeremyh/jBCrypt) - 密码哈希库

## 📞 联系方式

如有问题或建议，欢迎提交 Issue 或联系维护者。

---

**注意**：本项目仅供学习和研究使用，生产环境使用前请进行充分测试。
