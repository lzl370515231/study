### \<uses-permission-sdk-23>

#### 语法

```xml
<uses-permission-sdk-23 android:name="string"
        android:maxSdkVersion="integer" />
```

#### 包含于

\<manifest>

#### 说明

指明应用需要特定权限，但仅当应用在 Android 6.0（API 级别 23）

当您更新应用以包含需要其他权限的新功能时，此元素很有用。如果用户在运行 API 级别 22 或更低版本的设备更新应用，系统在安装时会提示用户授予在该更新中声明的所有新权限。如果某个新功能无关紧要，您可能想同时在这些设备上停用该功能，以便用户不需要授予额外权限即可更新应用。如果使用 `<uses-permission-sdk-23>` 元素而非 [``](https://developer.android.com/guide/topics/manifest/uses-permission-element.html)，则*仅*当应用在支持[运行时权限](https://developer.android.com/training/permissions/requesting.html)模式（用户在应用运行时向其授予权限）的平台上运行时才可请求权限。

#### 版本要求

API >= 23

