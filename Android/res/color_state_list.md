# Color State List

颜色状态列表资源

#### 文件位置

res/color/filename.xml

#### 资源引用

- java	R.color.filename
- xml     [package:]color/filename

#### 属性语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android" >
    <item
        android:color="hex_color"
        android:state_pressed=["true" | "false"]
        android:state_focused=["true" | "false"]
        android:state_selected=["true" | "false"]
        android:state_checkable=["true" | "false"]
        android:state_checked=["true" | "false"]
        android:state_enabled=["true" | "false"]
        android:state_window_focused=["true" | "false"] />
</selector>
```

### 属性

#### \<selector>

**必须的**，是根元素，包含一个或多个 <item> 元素。

| 属性          | 描述                                                         | 是否必须 | 常见值 |
| ------------- | ------------------------------------------------------------ | -------- | ------ |
| xmlns:android | 定义xml 的命名空间，必须是 “http://schemas.android.com/apk/res/android” | 是       |        |

##### \<item>

<selector> 的子节点。

| 属性                         | 描述                               | 是否必须 | 常见值                              |
| ---------------------------- | ---------------------------------- | -------- | ----------------------------------- |
| android:color                | 颜色值。必须是Alpha-Red-Green-Blue | 是       |                                     |
| android:state_pressed        | 按压的状态                         |          | true：表示按下<br>false：表示非按下 |
| android:state_focused        | 是否获取焦点                       |          |                                     |
| android:state_selected       | 是否被选中                         |          |                                     |
| android:state_checkable      |                                    |          |                                     |
| android:state_checked        |                                    |          |                                     |
| android:state_enabled        | 是否可点击                         |          |                                     |
| android:state_window_focused |                                    |          |                                     |

##### 注意

将应用状态列表中与对象当前状态匹配的第一个项。 因此，如果列表中的第一个条目不包含上面提到的任何状态属性，那么每次都会应用它，这就是为什么默认值应该总是最后一个，如下面的示例所示。



##### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true"
          android:color="#ffff0000"/> <!-- pressed -->
    <item android:state_focused="true"
          android:color="#ff0000ff"/> <!-- focused -->
    <item android:color="#ff000000"/> <!-- default -->
</selector>
```

```xml
<Button
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:text="@string/button_text"
    android:textColor="@color/button_text" />
```

