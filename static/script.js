// 猜数字小游戏 —— 前端交互（JavaScript 部分）
//   ① const 声明不变的量
//   ② 函数 function
//   ③ if 条件判断
//   ④ JSON.stringify：JS对象 → JSON字符串
//   ⑤ fetch 调用后端接口
//   ⑥ document.getElementById：通过 id 找到页面元素
//   ⑦ 模板字符串 `...`

// ① 用 const 声明"不会重新赋值"的量
// ⑥ getElementById("guessInput") 的意思是：在页面里找到 id 为 guessInput 的元素
const input      = document.getElementById('guessInput');    // 输入框
const message    = document.getElementById('message');       // 提示文字
const guessBtn   = document.getElementById('guessBtn');      // "猜！"按钮
const restartBtn = document.getElementById('restartBtn');    // "再来一局"按钮

// ② 定义一个函数：HTML 里 <button onclick="guess()"> 点击时就会执行它
function guess() {
    // Number() 把输入框里的字符串变成数字（输入框拿到的一律是字符串）
    const value = Number(input.value);

    //    isNaN(x) 判断 x 是不是"不是数字"；|| 表示"或者"
    if (isNaN(value) || value < 1 || value > 100) {
        message.textContent = '请输入 1~100 之间的整数哦！';  
        return;  
    }

    // ⑤ fetch：向后端发送 HTTP 请求
    //    method 是请求方法 POST
    //    headers 是请求头：告诉服务器"我发的是 JSON"
    //    body 是请求体：真正要发过去的数据
    //    ④ JSON.stringify({guess: value})：把对象 {guess:42} 变成字符串 '{"guess":42}'
    fetch('/api/guess', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ guess: value })
    })
    //第一次then拿到HTTP响应对象，调用response.js（）并转换成JS对象，第二次转换接受数据data，data.result是后端返回的结果，data.attempts是猜的次数
    .then(response => response.json())   // 服务器返回的 JSON 字符串 → JS 对象
    .then(data => {
        // ③ 根据后端返回的 result 字段，用 if/else if/else 显示不同提示
        if (data.result === 'big') {
            message.textContent = '猜大了！已经猜了 ' + data.attempts + ' 次';
        } else if (data.result === 'small') {
            message.textContent = '猜小了！已经猜了 ' + data.attempts + ' 次';
        } else if (data.result === 'correct') {
            message.textContent = '🎉 恭喜你猜对了！一共猜了 ' + data.attempts + ' 次';
            guessBtn.hidden = true;       // 猜对了：把"猜！"按钮藏起来。
            restartBtn.hidden = false;    // 把"再来一局"按钮显示出来。
        }
    })
    .catch(error => {
        // catch：请求失败时执行（比如后端没启动、端口被占用）
        message.textContent = '请求失败了，检查后端启动没有？';
        console.log(error);   // 控制台打印错误详情。
    });
}

// ② 重新开始函数：HTML 里 <button onclick="restart()"> 点击时执行
function restart() {
    // ⑤ 请求后端的 /api/restart 接口，让服务器重新想一个数字
    fetch('/api/restart', { method: 'POST' })
    .then(response => response.json())
    .then(data => {
        message.textContent = '新的一局开始啦！继续猜吧';
        guessBtn.hidden = false;      // 恢复显示"猜！"按钮
        restartBtn.hidden = true;     // 藏起"再来一局"按钮
        input.value = '';             // 清空输入框
    });
}
