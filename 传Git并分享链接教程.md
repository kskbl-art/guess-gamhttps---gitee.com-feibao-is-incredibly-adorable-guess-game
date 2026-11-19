# 把项目传到 Gitee、把链接发给别人（考核要求必须有 Git）

考核要求里写了：**必须有 git 部分**，可以用图形界面操作，但流程要跑通。

## 第 1 步：在 Gitee 上新建一个仓库

1. 浏览器登录 gitee.com（你的账号：`feibao-is-incredibly-adorable`）；
2. 右上角 **+ 号 → 新建仓库**；
3. 仓库名填 `guess-game`，简介随便写，**可见性选「私有」**（考核要求说可以设私有）；
4. **不要**勾选「使用 Readme 初始化仓库」那几项，直接点「创建」。

## 第 2 步：把本地项目推上去

### 方式 A：VS Code 图形界面（最简单，考核也认可）

1. 用 VS Code 打开 `guess-game` 文件夹；
2. 点左侧 **源代码管理** 图标（树杈形状）；
3. 点 **「初始化仓库」**；
4. 上方消息框输入 `first commit 猜数字小游戏`，点 **✓ 提交**；
5. （第一次会让你配置用户名和邮箱，直接点允许，用默认的就行；想改可以在终端执行 `git config --global user.name "你的名字"` 和 `git config --global user.email "你的gitee邮箱"`）
6. 点 **「发布分支」** 或 菜单 `...` → 远程 → 添加远程，地址填：

   ```
   https://gitee.com/feibao-is-incredibly-adorable/guess-game.git
   ```

7. 点 **推送 ↑**。会弹出 **Git Credential Manager** 登录框：
   - 用户名：你的 gitee 用户名或邮箱
   - 密码：**私人令牌**（不是登录密码！）——生成方法：gitee 网页 → 头像 → 设置 → 安全设置 → 私人令牌 → 生成新令牌（勾选 projects）→ 复制
8. 推送成功后，刷新 gitee 网页，仓库里就能看到代码了。

### 方式 B：命令行（VS Code 里 Ctrl+` 打开终端）

```bat
cd /d 你的项目路径\guess-game
git init
git add .
git commit -m "first commit 猜数字小游戏"
git remote add origin https://gitee.com/feibao-is-incredibly-adorable/guess-game.git
git push -u origin master
```

> 第一次 push 会弹 GCM 登录框，同样：密码填**私人令牌**。
> 如果之前把 origin 指向过别的仓库，先执行 `git remote remove origin` 再重新 add。

## 第 3 步：拿到链接发给别人

- 网页打开你的仓库，**地址栏那一串就是项目链接**：

  ```
  https://gitee.com/feibao-is-incredibly-adorable/guess-game
  ```

- 直接把这条链接发给收作业的人 / 学长学姐即可。

> ⚠️ **关键**：私有仓库，别人打开会显示 404 或"没有权限"。两种解决：
> 1. 把仓库改成**公开**：仓库页 → 管理 → 基本信息 → 可见性改为「公开」；
> 2. 保持私有，在 仓库 → 管理 → 成员管理 → 添加成员，把收件人的 gitee 账号加进去。
>
> 交作业前先问清楚：是交**链接**还是交**压缩包**；交链接的话记得确认对方能打开。

## 以后改了代码，怎么再传？

```bat
git add .
git commit -m "改了什么写什么"
git push
```

（VS Code 里就是：改完文件 → 源代码管理里写消息 → ✓ → 推送 ↑）

## 附：你电脑上的环境（已帮你确认过）

| 工具 | 位置 |
|---|---|
| JDK 21 | `D:\IDEA\JAVA` |
| IDEA 2026.2.3 | `D:\IDEA\IDEA\IDEA\IntelliJ IDEA 2026.2.3` |
| VS Code | `D:\Program Files\Microsoft VS Code` |
| Git | `D:\git\Git\cmd\git.exe`（版本 2.56.1，全局用户名 feibao 已配置） |
