**git** **操作记录文档**

**切换到指定远程分支**：

​       git checkout -b 本地分支名 远程分支名

​       示例：git checkout -b dev origin/dev

**删除远程分支**：

​       git push --delete origin 分支名

​       示例：git push --delete origin dev

删除本地分支

​       git branch –d <分支名>

**重命名分支**：

​       git branch -m dev newdev

**重命名远程分支**：

​       切换到指定远程分支      dev

​       删除远程分支    origin/dev

​       切换到其他分支       master

​       重命名本地分支名   dev ->newdev

​       切换到重命名分支   newdev

​       将本分支推到远程服务器 git push origin newdev

​       更新分支记录的远程分支

​              git branch --unset-upstream

删除不跟踪的代码和目录：

​       git clean –fd       删除文件和目录

​       git clean –f         只删除文件，保留目录

**合并代码**：

​       git checkout master

​       git merge dev    //将 dev 分支合并到当前分支

​       说明：

将 dev 分支合并到 master，分三种情况说明：

\1.    fast forward

合并时出现了“Fast forward”的提示。由于当前 master 分支所在的提交对象是要并入的 hotfix 分支的直接上游，Git 只需把 master 分支指针直接右移。换句话说，如果顺着一个分支走下去可以到达另一个分支的话，那么 Git 在合并两者时，只会简单地把指针右移，因为这种单线的历史分支不存在任何需要解决的分歧，所以这种合并过程可以称为**快进**（Fast forward）。

\2.    Auto-merging README

 

**查看节点**

\1.    以文件的方式显示

git show --name-only <commit id>



#### git 修改远程仓库地址

1. 修改命令

   ```shell
   git remote set-url origin [url]
   ```

2. 先删后加

   ```shell
   git remote rm origin
   git remote add origin [url]
   ```

3. 直接修改config文件

   ```shell
   [core]
   	repositoryformatversion = 0
   	filemode = false
   	bare = false
   	logallrefupdates = true
   	symlinks = false
   	ignorecase = true
   [remote "origin"]
   	url = https://git.vankeservice.com/IOTP/ruilian_android_1.x.git
   	fetch = +refs/heads/*:refs/remotes/origin/*
   [branch "master"]
   	remote = origin
   	merge = refs/heads/master
   [branch "patrolTrack"]
   	remote = origin
   	merge = refs/heads/patrolTrack
   ```

   替换上文中的 url

#### git 清楚远程仓库 HTTPS 认证信息的方法

修改远程认证的凭证

目前Windows下Git的用户凭据已不用明文密码保存了，用户目录下已找不到保存验证信息的文件，而是采用Windows凭据管理的方式，查看方法：Win键 ->搜索credential ->管理Windows 凭据 -> 普通凭据(or 控制面板 -> 用户账户 -> 管理Windows 凭据 -> 普通凭据）。