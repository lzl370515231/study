### \<uses-library>

#### 语法

```xml
<uses-library
  android:name="string"
  android:required=["true" | "false"] />
```

#### 包含于

- \<application>

#### 属性

| 属性             | 描述 | 是否必须 | 常用值                                                       | 说明   |
| ---------------- | ---- | -------- | ------------------------------------------------------------ | ------ |
| android:name     |      |          |                                                              |        |
| android:required |      |          | true：没有此库，应用程序将无法运行。<br>false：该应用程序可以使用该库（如果存在），但是在没有必要的情况下被设计为可以运行。即使该库不存在，系统也会允许安装该应用程序。如果使用`"false"`，则负责在运行时检查库是否可用。<br><br>要检查库，可以使用反射来确定特定的类是否可用 | API>=7 |

