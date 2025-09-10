//index.js
const readline = require("readline");

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

rl.question("안녕하세요(y/n)?", (answer) => {
  if (answer === "y") {
    console.log("감사합니다");
  } else if (answer === "n") {
    console.log("ㅠㅠ");
  } else {
    console.log("y 또는 n을 입력해주세요");
  }
  rl.close();
});
