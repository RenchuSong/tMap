tMap
====

Indoor digital map project

### 开发流程
1、大家首先建立自己的分支

2、每次开发前pull一下master分支，解决冲突

3、开发过程中的所有的改动全部push到自己的分支上

4、子任务开发完毕后将代码merge到develop分支

5、经阶段测试完成后将develop分支改动merge回master分支

6、每次Scrum完成时，将master分支merge到release分支

#### release、master、develop分支的关系：

release分支保持为上一Scrum阶段完成的可运行版本（阶段性可交付产品）

master分支保持为开发过程中经测试确认的可运行版本（允许存在做了一部分的功能），一旦发现致命错误立即回滚

develop分支为开发分支，允许存在bug，致命错误可不会滚进行修复

其他分支为开发者自行管理，在遵守代码规范的前提下不进行约束

