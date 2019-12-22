### \<uses-permission>



#### 说明

要控制过滤，务必在 `<uses-feature>` 元素中显式声明硬件功能，而不是依赖 Google Play“发现” `<uses-permission>` 元素中的要求。然后，如果要对特定功能停用过滤，可将 `android:required="false"` 属性添加到 `<uses-feature>` 声明中。



#### 语法

```xml
<uses-permission android:name="string"
        android:maxSdkVersion="integer" />
```

#### 包含于

\<manifest>

#### 属性

| 属性                  | 描述                                                         | 是否必须 | 常用值 |
| --------------------- | ------------------------------------------------------------ | -------- | ------ |
| android:maxSdkVersion | 此权限应授予应用的最高 API 级别。如果从某个 API 级别开始不再需要应用所需的权限，则设置此属性非常有用。 |          |        |
|                       |                                                              |          |        |
|                       |                                                              |          |        |

##### maxSdkVersion

例如，从 Android 4.4（API 级别 19）开始，应用在外部存储空间写入其特定目录（`getExternalFilesDir()` 提供的目录）时不再需要请求 `WRITE_EXTERNAL_STORAGE` 权限。但 API 级别 18 和更低版本*需要*此权限。因此，您可以使用如下声明，声明只有 API 级别 18 及以前版本才需要此权限：

```xml
<uses-permission
     android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     android:maxSdkVersion="18" />
```

