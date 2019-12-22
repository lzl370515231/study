### \<data>

#### 语法

```xml
<data android:scheme="string"
      android:host="string"
      android:port="string"
      android:path="string"
      android:pathPattern="string"
      android:pathPrefix="string"
      android:mimeType="string" />
```

#### 包含于

\<intent-filter>

#### 属性

规范可以只是一个数据类型**mimetype**，只有一个URI，或者同时有一个数据类型和一个 URI。Uri 由每个部分的单独属性指定：

```xml
<scheme>://<host>:<port>[<path>|<pathPrefix>|<pathPattern>]
```

