# AndroidManifest

| 属性                      | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| \<action>                 | 向 Intent 过滤器添加操作。                                   |
| \<activity>               | 声明 Activity 组件                                           |
| \<activity-alias>         | 声明 Activity 的别名                                         |
| \<application>            | 应用的声明                                                   |
| \<category>               | 向 Intent 过滤器添加类别名称                                 |
| \<compatible-screens>     | 指定与应用兼容的每个屏幕配置                                 |
| \<data>                   | 向 Intent 过滤器添加数据规范                                 |
| \<grant-uri-permission>   | 指定父级内容提供程序有权访问的应用数据的子集                 |
| \<instrumentation>        | 声明支持您监控应用与系统进行交互的 `Instrumentation` 类      |
| \<intent-filter>          | 指定 Activity、服务或广播接收器可以响应的 Intent 类型        |
| \<manifest>               | AndroidManifest.xml 文件的根元素                             |
| \<meta-data>              | 可以提供给父级组件的其他任意数据项的名称-值对                |
| \<path-permission>        | 定义内容提供程序中特定数据子集的路径和所需权限               |
| \<permission>             | 声明安全权限，可用于限制对此应用或其他应用的特定组件或功能的访问 |
| \<permission-group>       | 为相关权限的逻辑分组声明名称                                 |
| \<permission-tree>        | 声明权限树的基本名称                                         |
| \<provider>               | 声明内容提供程序组件                                         |
| \<receiver>               | 声明广播接收器组件                                           |
| \<service>                | 声明服务组件                                                 |
| \<support-gl-texture>     | 声明应用支持的一种 GL 纹理压缩格式                           |
| \<supports-screen>        | 声明应用支持的屏幕尺寸，并为大于此尺寸的屏幕启用屏幕兼容模式 |
| \<uses-configuration>     | 指明应用要求的特定输入功能                                   |
| \<uses-feature>           | 声明应用使用的单个硬件或软件功能                             |
| \<uses-library>           | 指定应用必须链接到的共享库                                   |
| \<uses-permission>        | 指定为使应用正常运行，用户必须授予的系统权限                 |
| \<uses-permission-sdk-23> | 指明应用需要特定权限，但仅当应用在运行 Android 6.0（API 级别 23）或更高版本的设备上安装时才需要 |
| \<uses-sdk>               | 您可以通过整数形式的 API 级别，表示应用与一个或多个版本的 Android 平台的兼容性 |

### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:versionCode="1"
    android:versionName="1.0"
    package="com.example.myapp">

    <!-- Beware that these values are overridden by the build.gradle file -->
    <uses-sdk android:minSdkVersion="15" android:targetSdkVersion="26" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        <!-- This name is resolved to com.example.myapp.MainActivity
             based upon the package attribute -->
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".DisplayMessageActivity"
            android:parentActivityName=".MainActivity" />
    </application>
</manifest>
```

