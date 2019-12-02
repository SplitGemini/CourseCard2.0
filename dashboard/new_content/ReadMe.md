## 新内容更新详情

### 新内容：

1. 修复了很多问题，比如修复自动滚动到当前星期几的课的问题，学期保存问题等
1. 数据库课程和笔记增加删除和更新函数，笔记数据添加对课程的外键
1. 网页获取增加稳定性，简化了读取json的内容，解决了之前读取复杂json数据库崩溃的问题
1. 主界面点击进入课程库列表的按钮改为一个下拉选项菜单，可选择进入课程库列表、添加课程，删除cookie三个选项
1. 添加自定义新课程功能
1. 课程库列表长按课程item可选删除该课程库
1. 添加修改课程功能，课程主界面长按item进入修改界面，可选删除该门课程
1. 添加点击通知可直接跳转进app功能
1. 修改了一些兼容性问题，比如rxjava1 -> rxjava2 ,修复已弃用，已过期的函数
1. 部分结构和逻辑已改变，比如读写notes将基于课程id而不是课程名等
1. 新增了通过Github读取json数据类，之前的通过教务系统读取json的类暂时失效，在此做保留处理

### 展示

新建课程库

![](img/newCoursePage.gif)

删除课程库

![](img/deleteCoursePage.gif)

新建课程

![](img/newCourse.gif)

修改课程

![](img/editCourse.gif)

删除课程

![](img/deleteCourse.gif)

点击通知跳转

![](img/touchNotification.gif)

### 贡献

贡献，SplitGemini

![](img/contribute.png)

对比原分支,16 commits, 84 files changed

![](img/change.png)

3182 additions

![](img/diff.png)

### 问题

因为访问课程表的网络api在2019年12月1日已弃用，在没找到新的api前该课程表app功能性废了，目前获取的课程表皆为从GitHub上获取测试用课程表
[在这](https://github.com/SplitGemini/Coursecard2.0/tree/master/dashboard/new_content/sample)
就算不能自动获取课程表也能手动添加与删除，仍保留一定功能，而之前的版本就没有这个功能

如果想修改api，修改CASActivity->loadUrl来访问主页，CASActivity->setWebViewClient到MyWebViewClient，然后在MyWebViewClient->GithubService and MyWebViewClient->OnClick4Data里对应api
