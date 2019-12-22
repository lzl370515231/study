### \<uses-sdk>

#### 语法

```xml
<uses-sdk android:minSdkVersion="integer"
          android:targetSdkVersion="integer"
          android:maxSdkVersion="integer" />
```

#### 包含在

\<manifest>

#### 属性

| 属性                     | 描述                                                         | 是否必须 | 常用值 | 说明                                                   |
| ------------------------ | ------------------------------------------------------------ | -------- | ------ | ------------------------------------------------------ |
| android:minSdkVersion    | 一个用于指定应用运行所需最低 API 级别的整数。如果系统的 API 级别低于该属性中指定的值，Android 系统将阻止用户安装应用。您应始终声明该属性。 |          |        |                                                        |
| android:targetSdkVersion | 一个用于指定应用的目标 API 级别的整数。如果未设置，其默认值与为 `minSdkVersion` 指定的值相等。 |          |        | API>=4                                                 |
| android:maxSdkVersion    | 一个指定作为应用设计运行目标的最高 API 级别的整数。          |          |        | API>=4<br><br>不建议声明该属性。新版本平台完全向后兼容 |

