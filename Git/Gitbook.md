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
