/*
 * 猜数字小游戏 —— 后端服务器（纯 Java，不用任何框架，JDK 自带功能即可）
 * 整个文件只做三件事：
 *   1. 启动一个 HTTP 服务器，监听 8080 端口；
 *   2. 把 static 文件夹里的网页（HTML/CSS/JS）发给浏览器；
 *   3. 提供两个接口（API）：猜数字、重新开始。
 *
 *   ① 后端是什么 / Java 语言
 *   ② 端口号 8080，1024 以下端口要避开
 *   ③ HTTP 请求方法 GET / POST
 *   ④ API 接口的概念
 *   ⑤ JSON 数据格式
 *   ⑥ HTTP 状态码 200 / 400 / 404 / 405（状态码的注解）
 *   ⑦ 多线程：HttpServer 每收到一个请求就会分一个线程去处理
 */

// import：导入 Java 自带（JDK 内置）的类，就像"拿出工具箱里的工具"

import com.sun.net.httpserver.HttpExchange;   // 一个"请求+响应"的封装对象
import com.sun.net.httpserver.HttpHandler;    // 处理请求的接口（接口=规定要做什么）
import com.sun.net.httpserver.HttpServer;     // HTTP 服务器本体

import java.io.IOException;                  // 处理"读写失败"的异常类型
import java.io.OutputStream;                 // 输出流：往浏览器写数据用
import java.net.InetSocketAddress;           // "IP地址+端口号"的组合
import java.nio.charset.StandardCharsets;    // UTF-8 编码（避免中文乱码）
import java.nio.file.Files;                  // 读文件用
import java.nio.file.Path;                   // 表示文件路径
import java.util.Random;                     // 生成随机数

public class GuessGameServer {
    // 游戏状态：static 表示"整个程序只存一份"，所有请求操作的都是同一个数字
    static int secretNumber;    // 服务器心里想的秘密数字（1~100）
    static int attempts;        // 玩家已经猜了几次
    static final Random r = new Random();

    public static void main(String[] args) throws IOException {
        startNewGame();
        /*
        throws；java关键字，后面加异常类型，其中IOException解决受检异常问题
        有两个方法来解决
        1，选择自己处理；用try{。。。。}catch（IOException）{。。。。]
        2.继续往上抛；方法签名里写throws IOException，把责任交给调用我的人
         */
        // ① 创建 HTTP 服务器，绑定 8080 端口
        //    InetSocketAddress(8080) = "任何IP + 8080端口"
        //    知识点：端口号像"小区大门编号"，1024 以下是系统预留端口不能占用
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // ② 注册"路由"：什么路径的请求，交给哪个 Handler 处理
        //    "/"            → 返回网页（以及 css、js 文件）
        //    "/api/guess"   → 猜数字接口
        //    "/api/restart" → 重新开始接口
        server.createContext("/", new StaticFileHandler());
        /*
        注册路由，/；将开头请求交给静态文件处理器
         */
        server.createContext("/api/guess", new GuessHandler());
        /*
        交给猜数字处理器
         */
        server.createContext("/api/restart", new RestartHandler());
        /*
        交给重新开始处理器
         */
        // ③ 启动服务器
        //    知识点（多线程）：启动后，每个进来的请求都会被分配一个线程处理，
        server.start();
        /*
        用于启动服务器，使得main方法结束后后台线程能继续跑动
         */
        System.out.println("服务器启动成功！请用浏览器打开: http://127.0.0.1:8080");
        // 127.0.0.1 是"回环地址"，只在自己电脑上访问
    }

    static void startNewGame() {
        secretNumber = r.nextInt(100) + 1;
        attempts = 0;
    }

    // 处理"猜数字"请求：前端把数字用 POST 发过来，这里判断大了/小了/对了
    static class GuessHandler implements HttpHandler {
        @Override
        /*
        处理父类接口
        说人话；当因为由于大小等微小错误出现，调用出问题时很难检查，引用@Override
        就可以在所有接口中去找下方handle这个方法，也就是说，我在告诉编辑器
        我这是去进行实行接口的handle方法，你去给我检查，我handle签名是否出错
         */
        public void handle(HttpExchange exchange) throws IOException {

            // ③ 检查请求方法：只接受 POST
            //    GET 一般用来"拿数据"，POST 用来"发送数据"
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"请使用 POST 请求\"}");
                return;
                //405服务器认识你，但不认可方法
            }

            // 读"请求体"：POST 发送的数据放在请求体里
            //   请求体就像快递箱里的货物
            String body = new String(exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8);
            // 前端发来的 JSON 长这样：{"guess":42}
            // 这里用正则 replaceAll 把"非数字"的字符全部删掉，剩下 "42"
            String numberStr = body.replaceAll("[^0-9]", "");

            // ⑥ 状态码 400：客户端发来的数据不对
            //    400 系列 = "客户端出错了"
            if (numberStr.isEmpty()) {
                sendJson(exchange, 400, "{\"error\":\"请输入一个整数\"}");
                return;
            }
            int guess = Integer.parseInt(numberStr);
            /*
            将字符串转换为整数的强制转换
             */
            if (guess < 1 || guess > 100) {             // 再检查范围
                sendJson(exchange, 400, "{\"error\":\"数字要在 1~100 之间\"}");
                return;
            }

            attempts++;

            String result;
            if (guess > secretNumber) {
                result = "big";       // 猜大了
            } else if (guess < secretNumber) {
                result = "small";     // 猜小了
            } else {
                result = "correct";   // 猜对了！
            }

            // ⑤ 把结果拼成 JSON 字符串返回给前端，200 表示成功
            String json = "{\"result\":\"" + result + "\",\"attempts\":" + attempts + "}";
            sendJson(exchange, 200, json);
        }
    }

    // 处理"重新开始"请求：重新生成数字、次数清零
    static class RestartHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"请使用 POST 请求\"}");
                return;
            }
            startNewGame();   // 复用开局的逻辑：重新想一个数字
            sendJson(exchange, 200, "{\"result\":\"restarted\"}");
        }
    }

    // 处理静态文件：浏览器访问 / 就返回 index.html，
    // 访问 /style.css 就返回样式文件，/script.js 返回脚本
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 浏览器打开网页用的是 GET 请求
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"请使用 GET 请求\"}");
                return;
            }

            // 从请求里拿到路径："/" 表示主页，"/style.css" 表示样式文件
            String path = exchange.getRequestURI().getPath();
            // 把 "/" 换成 index.html；"/style.css" 去掉开头斜杠变成 "style.css"
            String fileName = path.equals("/") ? "index.html" : path.substring(1);
            /*
            path.substring(n)表示从n索引往后打印，就是说把n索引之前的给删去了
            这两行就是为了把文件路径和文件名的“/”给删去
             */
            // 所有前端文件都放在 static 文件夹里（Path.of 拼出完整路径）
            Path file = Path.of("static", fileName);
            /*
            path.of作用；把static /style.css拼成static/style.css
             */

            // ⑥ 状态码 404：文件不存在（fastapi和常见认证.pptx：状态码简介）
            //    404 = "查无此件"
            if (!Files.exists(file)) {
                sendJson(exchange, 404, "{\"error\":\"文件不存在: " + fileName + "\"}");
                return;
            }

            // 读文件内容，准备发给浏览器
            byte[] data = Files.readAllBytes(file);

            // Content-Type 响应头：告诉浏览器"我发给你的是什么类型"
            // 知识点：响应头就像快递面单上的信息
            String contentType;
            if (fileName.endsWith(".html")) {
                contentType = "text/html; charset=utf-8";
            } else if (fileName.endsWith(".css")) {
                contentType = "text/css; charset=utf-8";
            } else if (fileName.endsWith(".js")) {
                contentType = "application/javascript; charset=utf-8";
            } else {
                contentType = "application/octet-stream";
            }
       /*
    此处判断文件类型，判断文件是否是html css js 若三者都不满足，如zip，png用
    application/octet-stream二进制流，触发浏览器默认下载
        */
            // 发送响应：200 成功 + 文件内容
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, data.length);
            //data。length告诉浏览器文件有多大
            OutputStream os = exchange.getResponseBody();
            os.write(data);
            os.close();
        }
    }

    // 工具方法：统一的"返回 JSON"函数，三个接口都用它，避免重复代码
    // statusCode：HTTP 返回的状态码
    //   200 = 成功；400 = 请求错误；404 = 找不到；405 = 方法不对
    //   口诀：2xx 好事，3xx 去别处，4xx 怪自己，5xx 怪服务器
    static void sendJson(HttpExchange exchange, int statusCode, String json)
            throws IOException {
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, data.length); 
        //1，获取通道（传送带）
        OutputStream os = exchange.getResponseBody();
        //2，把数据放到管道上（把字节数据写进去）
        os.write(data);
        //3，关闭通道（结束发货）
        os.close();
    }
}
