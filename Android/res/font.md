### font

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<font-family>
  <font
    android:font="@[package:]font/font_to_include"
    android:fontStyle=["normal" | "italic"]
    android:fontWeight="weight_value" />
</font-family>
```

#### 属性

##### \<font>

| 属性               | 描述         | 是否必须 | 常用值                     | 说明   |
| ------------------ | ------------ | -------- | -------------------------- | ------ |
| android:fontStyle  | 定义字体样式 |          | normal 或 italic           | 关键词 |
| android:fontWeight | 字体的权重   |          | 400是常规字体<br>700是黑体 |        |
|                    |              |          |                            |        |

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<font-family xmlns:android="http://schemas.android.com/apk/res/android">
    <font
        android:fontStyle="normal"
        android:fontWeight="400"
        android:font="@font/lobster_regular" />
    <font
        android:fontStyle="italic"
        android:fontWeight="400"
        android:font="@font/lobster_italic" />
</font-family>
```



```xml
<?xml version="1.0" encoding="utf-8"?>
<EditText
    android:fontFamily="@font/lobster"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:text="Hello, World!" />
```

### Downloadable font(可下载的字体)

可下载的字体资源定义了可以在应用程序中使用的自定义字体。 这种字体在应用程序本身中不可用，而是从字体提供程序中检索字体。

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<font-family
    android:fontProviderAuthority="authority"
    android:fontProviderPackage="package"
    android:fontProviderQuery="query"
    android:fontProviderCerts="@[package:]array/array_resource" />
```

#### 属性

##### \<font-family>

| 属性                          | 描述                                         | 是否必须 | 常用值 |
| ----------------------------- | -------------------------------------------- | -------- | ------ |
| android:fontProviderAuthority | 定义字体请求的字体提供程序的权限             | 是       |        |
| android:fontProviderPackage   | 用于验证提供程序的标识                       | 是       |        |
| android:fontProviderQuery     | 字体的字符串查询                             | 是       |        |
| android:fontProviderCerts     | 定义用于对此提供程序进行签名的证书的散列集。 | 是       |        |

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<font-family xmlns:android="http://schemas.android.com/apk/res/android"
    android:fontProviderAuthority="com.example.fontprovider.authority"
    android:fontProviderPackage="com.example.fontprovider"
    android:fontProviderQuery="Lobster"
    android:fontProviderCerts="@array/certs">
</font-family>
```

##### 定义 cert 数组的XML文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string-array name="certs">
      <item>MIIEqDCCA5CgAwIBAgIJA071MA0GCSqGSIb3DQEBBAUAMIGUMQsww...</item>
    </string-array>
</resources>
```



```xml
<?xml version="1.0" encoding="utf-8"?>
<EditText
    android:fontFamily="@font/lobster"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:text="Hello, World!" />
```

