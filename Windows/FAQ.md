# Windows 下常见问题及解决方案

## 主页被 hao123 锁定
网上说写进了 pref.js 里面， 但是我打开 about:support, 找到了配置目录，把所有包含 hao123 的的内容全部清掉了也没有用,
后来在知乎上看到了说 其实 hao123 改掉了 chrome 或者 firefox 快捷方式, 本来快捷方式里的目标是这样的写
```
"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe"
```
被 hao123 改成了
```
"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" http://hao123.com
```
这样就相当于给chrome 加了一个参数, 每次启动 chrome, 都会先打开 hao123 网址.

hao123 真是太TM贱了！
