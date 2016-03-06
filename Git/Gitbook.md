## 安装 npm & gitbooks
1. 安装 node.js
```
https://nodejs.org/en/download/
```
最新的 nodejs 已经集成了 npm, 安装完成之后,
使用 `node --version` 和 `npm -v` 测试 node 和 npm 是否安装成功

2. 安装 gitbooks
```
npm install -g gitbook-cli
```

## gitbooks 提交
```
$ touch README.md
$ git init .
$ git add README.md
$ git commit -m "init"
$ git remote add gitbook "https://{USER_NAME}:{API_TOKEN or PASSWORD}@git.gitbook.com/{USER_NAME}/{repo}"
$ git push --set-upstream origin master
```
在 `git push` 时如果遇到 `error: RPC failed; result=35, HTTP code = 0` 错误,
目前的解决方案有两种(但都不一定能解决)
1. `$ GIT_CURLOPT_SSLVERSION=3  git  ...`

2. `$ git push --set-upstream origin master`


##  [git] warning: LF will be replaced by CRLF | fatal: CRLF would be replaced by LF

##### core.safecrlf

Git提供了一个换行符检查功能（core.safecrlf），可以在提交时检查文件是否混用了不同风格的换行符。这个功能的选项如下：

- false - 不做任何检查
- warn - 在提交时检查并警告
- true - 在提交时检查，如果发现混用则拒绝提交

建议使用最严格的 true 选项。

##### core.autocrlf
假如你正在Windows上写程序，又或者你正在和其他人合作，他们在Windows上编程，而你却在其他系统上，在这些情况下，你可能会遇到行尾结束符问题。这是因为Windows使用回车和换行两个字符来结束一行，而Mac和Linux只使用换行一个字符。虽然这是小问题，但它会极大地扰乱跨平台协作。

Git可以在你提交时自动地把行结束符CRLF转换成LF，而在签出代码时把LF转换成CRLF。用core.autocrlf来打开此项功能，如果是在Windows系统上，把它设置成true，这样当签出代码时，LF会被转换成CRLF：
```
$ git config --global core.autocrlf true
```
Linux或Mac系统使用LF作为行结束符，因此你不想 Git 在签出文件时进行自动的转换；当一个以CRLF为行结束符的文件不小心被引入时你肯定想进行修正，把core.autocrlf设置成input来告诉 Git 在提交时把CRLF转换成LF，签出时不转换：
```
$ git config --global core.autocrlf input
```
这样会在Windows系统上的签出文件中保留CRLF，会在Mac和Linux系统上，包括仓库中保留LF。

如果你是Windows程序员，且正在开发仅运行在Windows上的项目，可以设置false取消此功能，把回车符记录在库中：
```
$ git config --global core.autocrlf false
```
