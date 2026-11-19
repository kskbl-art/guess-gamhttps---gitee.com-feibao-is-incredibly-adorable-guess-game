# 🎯 猜数字小游戏

一个**前后端分离的极简小项目**：浏览器里猜数字，Java 后端判断对错。
只用「前端三件套 + 纯 Java（JDK 自带功能，无任何框架）」，专为考核而写，每一段代码都有注释。

## 项目长什么样

- 打开网页，后端已经想好了一个 1~100 的秘密数字；
- 你在输入框里猜，点「猜！」按钮；
- 后端告诉你：猜大了 / 猜小了 / 猜对了，并统计次数；
- 猜对后可以点「再来一局」。

## 技术栈（对应培训内容）

| 部分 | 技术 | 对应课件 |
|---|---|---|
| 前端结构 | HTML（标签骨架、属性、表单、按钮） | 前端1.pptx |
| 前端样式 | CSS（选择器、盒子模型、Flex布局） | 前端2.pptx |
| 前端交互 | JavaScript（变量、函数、if、JSON、fetch） | 前端3.pptx |
| 后端 | 纯 Java（HttpServer，无 Spring 框架） | 后端1.pptx、后端5.pptx |
| 网络知识 | HTTP 方法、端口号 8080、JSON、状态码 | 后端5.pptx、fastapi和常见认证.pptx |

## 文件结构

```
guess-game/
├── GuessGameServer.java   # 后端：一个文件搞定整个服务器
├── static/                # 前端：三个文件
│   ├── index.html         #   页面结构
│   ├── style.css          #   样式
│   └── script.js          #   交互逻辑
├── README.md              # 本文件
├── 知识点来源.md           # 每段代码对应哪份课件
├── 考核话术.md             # 现场展示讲稿
└── 传Git并分享链接教程.md   # 怎么把项目传到 Gitee 并发给别人
```

## 怎么运行（任选一种）

你电脑上的环境：
- **JDK 21**：`D:\IDEA\JAVA`
- **IDEA**：`D:\IDEA\IDEA\IDEA\IntelliJ IDEA 2026.2.3`
- **VS Code**：`D:\Program Files\Microsoft VS Code`

### 方式一：IDEA（推荐）

1. 打开 IDEA → `Open` 选择 `guess-game` 文件夹；
2. 首次打开如果提示选 JDK，选 `D:\IDEA\JAVA`；
3. 右键 `GuessGameServer.java` → **Run 'GuessGameServer.main()'**；
4. 看到控制台输出「服务器启动成功」后，浏览器打开 <http://127.0.0.1:8080> 玩。

### 方式二：VS Code / 命令行

在 `guess-game` 文件夹里打开终端（VS Code 里 Ctrl+` ），执行：

```bat
:: 编译（-encoding UTF-8 是为了让中文注释不乱码）
"D:\IDEA\JAVA\bin\javac.exe" -encoding UTF-8 GuessGameServer.java

:: 运行
"D:\IDEA\JAVA\bin\java.exe" GuessGameServer
```

然后浏览器打开 <http://127.0.0.1:8080>。

> ⚠️ 两个注意点：
> 1. 一定要在 `guess-game` 文件夹里运行（程序要靠相对路径找 `static` 文件夹）；
> 2. 8080 端口一次只能跑一个，如果提示端口被占用，把上一个程序关掉再试。

## 整条链路怎么走（考核必讲）

```
浏览器输入数字、点按钮
        │  script.js 用 fetch 发 POST /api/guess（请求体是 JSON）
        ▼
Java 后端 HttpServer 收到请求
        │  GuessHandler 解析 JSON、比较大小
        ▼
返回 JSON {result: "big"/"small"/"correct", attempts: 次数}
        │  script.js 收到后更新页面文字
        ▼
浏览器显示：猜大了 / 猜小了 / 🎉猜对了
```
