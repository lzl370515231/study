## 编码GBK的不可映射字符

#### 问题描述

java-library  出现错误: 编码GBK的不可映射字符

#### 解决办法

```groovy
tasks.withType(JavaCompile) {    
    options.encoding = "UTF-8"
}
```

#### 原因

编码格式的兼容性问题



## 额外添加属性定义

#### 问题描述

额外添加属性定义

#### 解决办法

##### 定义 config.gradle

在根目录添加文件 config.gradle

```groovy
//ext : 准确的说是 Gradle 领域对象的一个属性，可以将自定义的属性添加到 ext 对象上
ext{
    isModule = true

    android =[
        compileSdkVersion :28,
        minSdkVersion :14,
        targetSdkVersion :28,
        versionCode :1,
        versionName :"1.0"
    ]

    appId=[
        "app":"com.lzl.builder.routerdemo",
        "module1":"com.lzl.builder.module1",
        "module2":"com.lzl.builder.module2",
    ]

    supportLibrary = "25.4.0"

    dependencies=[
        "appcompat-v7":"com.android.support:appcompat-v7:${supportLibrary}"
    ]
}
```



##### 添加引用

在根目录的 build.gradle 文件头添加

```groovy
apply from: "config.gradle"
```

##### 使用

```groovy
def cfg = rootProject.ext.android
def appId = rootProject.ext.appId

minSdkVersion cfg.minSdkVersion
```



## 定义编译生成的值

#### 问题描述

定义编译生成的值

#### 解决办法

```groovy
buildConfigField("boolean","isModule",String.valueOf(isModule))
```



## 定义引用其他路劲资源文件

#### 问题描述

定义引用其他路劲资源文件

#### 解决办法

```groovy
android{
    sourceSets{
        main{
            if(isModule){
                manifest.srcFile 'src/main/AndroidManifest.xml'
            }else{
                manifest.srcFile 'src/main/module/AndroidManifest.xml'
                java.srcDirs 'src/main/module/java','src/main/java'
            }
        }
    }
}
```



## 注解

### 定义Options

```groovy
javaCompileOptions {
            annotationProcessorOptions {
                arguments = [moduleName: project.getName()]
            }
        }
```

### 指定注解处理工程

```groovy
annotationProcessor project(':easy_compiler')
```



##  Gradle 与AS 不匹配

#### 问题描述

```shell
Cause: org.jetbrains.plugins.gradle.tooling.util.ModuleComponentIdentifierImpl.getModuleIdentifier()Lorg/gradle/api/artifacts/ModuleIdentifier
```

#### 解决办法

修改 gradle 的版本，gradle-->wrapper-->gradle-wrapper.properties。如：

```groovy
distributionUrl=https\://services.gradle.org/distributions/gradle-4.6-all.zip
```

## implements 与 api 的区别

### implements

使用了该命令编译的依赖，它仅仅对当前的`Module`提供接口。

### api

使用该方式依赖的库将会**参与编译和打包**。compile 被弃用

### 其他依赖方式

#### provided(compileOnly)

只在编译时有效，不会参与打包

#### apk(runtimeOnly)

只在生成`apk`的时候参与打包，编译时不会参与，很少用。

#### testCompile（testImplementation）

`testCompile` 只在单元测试代码的编译以及最终打包测试apk时有效。

#### debugCompile（debugImplementation）

`debugCompile` 只在 **debug** 模式的编译和最终的 **debug apk** 打包时有效

#### releaseCompile（releaseImplementation）

`Release compile`仅仅针对 **Release** 模式的编译和最终的 **Release apk** 打包。

### 依赖冲突解决

#### 问题描述

```shell
All com.android.support libraries must use the exact same version specification (mixing versions can lead to runtime crashes
```



#### 解决办法

1. 修改自己项目中的com.android.support的版本号，与所依赖的库版本号一致，但是当我们依赖的库中的com.android.support版本号有好几个版本就不行了。**（不推荐）**
2. 依赖第三方库时候排除掉对com.android.support包的依赖，这样自己的项目随便依赖什么版本都可以，但是这种方法需要你先找到哪些库存在冲突
3. 通过groovy脚本强制修改冲突的依赖库版本号 **（推荐）**
4. 将项目迁移至AndroidX（推荐）

##### 排除依赖法

###### 查看哪些库存在着冲突

- 点击 Terminal 输入 gradlew -q app:dependencies 回车即可将app中所依赖的库展示出来

- **exclude group:表示只要包含com.android.support的都排除**

  ```groovy
  api("com.afollestad.material-dialogs:core:0.9.5.0") {
  	exclude group: 'com.android.support'
  }
  ```

  

- **module：删排除group中的指定module**

  ```groovy
  api("com.afollestad.material-dialogs:core:0.9.5.0") {
  	exclude group: 'com.android.support', module: 'support-v13'
  	exclude group: 'com.android.support', module: 'support-vector-drawable'
  }
  ```

- 在我们自己创建library给别人使用时，如果需要依赖com.android.support的话，**建议用provided的方式依赖（android studio3.0中更改为compileOnly）**，这样只会在编译时有效，不会参与打包。

  ```groovy
  provided 'com.android.support:appcompat-v7:26.1.0'
  provided 'com.android.support:design:26.1.0'
  provided 'com.android.support:support-vector-drawable:26.1.0'
  ```

##### 通过Grovvy脚本修改版本号解决冲突

在其存在冲突的module中的build.gradle文件中加入下面代码，原理就是通过遍历所有依赖，并修改指定库的版本号。其中 requested.group == 'com.android.support' com.android.support表示要修改的依赖库 details.useVersion '28.0.0'	28.0.0表示要修改的版本号。

```groovy
configurations.all {
    resolutionStrategy.eachDependency { DependencyResolveDetails details ->
        def requested = details.requested
        if (requested.group == 'com.android.support') {
            if (!requested.name.startsWith("multidex")) {
                details.useVersion '28.0.0'
            }
        }
    }
}
```

##### 将项目迁移至AndroidX

## FrameMetricsAggregator

#### 问题描述

将开源项目中的 targetSdkVersion 和 compileSdkVersion 手动更改为 28，则出现了该问题

```shell
Program type already present: android.support.v4.app.FrameMetricsAggregator$FrameMetricsApi24Impl$1
```

#### 解决办法

将对应的  support 包替换为对应的最新版本