# Drawable

### 分类

- 位图文件

  .png、.jpg、.gif（不建议）

- 九空格文件

- 图层列表

- 状态列表

- 级别列表

- 转换可绘制对象

- 插入可绘制对象

- 裁剪可绘制对象

- 缩放可绘制对象

- 形状可绘制对象

### 位图

#### 位图文件

.png、.jpg、.gif

##### 文件位置

res/drawable/filename.png

##### 资源引用

- java	R.drawable.filename
- xml     [package:]drawable/filename

##### 示例

```xml
<ImageView
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:src="@drawable/myimage" />
```

```java
Resources res = getResources();
Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.myimage, null);
```

#### XML 位图

##### 文件位置

res/drawable/filename.xml

##### 资源引用

- Java	R.drawable.filename
- XML    [package:]drawable/filename

##### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<bitmap
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:src="@[package:]drawable/drawable_resource"
    android:antialias=["true" | "false"]
    android:dither=["true" | "false"]
    android:filter=["true" | "false"]
    android:gravity=["top" | "bottom" | "left" | "right" | "center_vertical" |
                      "fill_vertical" | "center_horizontal" | "fill_horizontal" |
                      "center" | "fill" | "clip_vertical" | "clip_horizontal"]
    android:mipMap=["true" | "false"]
    android:tileMode=["disabled" | "clamp" | "repeat" | "mirror"] />
```

##### 属性

| 属性              | 描述                                                         | 是否必须 | 常见值                                       |
| ----------------- | ------------------------------------------------------------ | -------- | -------------------------------------------- |
| xmlns:android     | 定义XML命名空间                                              | 是       | "http://schemas.android.com/apk/res/android" |
| android:src       | 可绘制对象资源                                               | 是       |                                              |
| android:antialias | 启用或停用抗锯齿                                             |          |                                              |
| android:dither    | 当位图的像素配置与屏幕不同时，启用或停用位图抖动。           |          |                                              |
| android:filter    | 启用或停用位图过滤。当位图收缩或拉伸以使其外观平滑时使用过滤。 |          |                                              |
| android:gravity   | 定义位图的重力。重力指示当位图小于容器时，可绘制对象在其容器中放置的位置。 |          | 值见下表 gravityMode                         |
| android:mipMap    | 启用或停用 mipmap 提示。                                     |          | 默认值为 false                               |
| android:tileMode  | 定义平铺模式。当平铺模式启用时，位图会重复。重力在平铺模式启用时将被忽略。 |          | 值见下表 tileMode                            |

###### GravityMode

| 值                | 描述                                                         |
| ----------------- | ------------------------------------------------------------ |
| top               | 将对象放在其容器顶部，不改变其大小。                         |
| bottom            | 将对象放在其容器底部，不改变其大小。                         |
| left              | 将对象放在其容器左边缘，不改变其大小。                       |
| right             | 将对象放在其容器右边缘，不改变其大小。                       |
| center_vertical   | 将对象放在其容器的垂直中心，不改变其大小。                   |
| fill_vertical     | 按需要扩展对象的垂直大小，使其完全适应其容器。               |
| center_horizontal | 将对象放在其容器的水平中心，不改变其大小。                   |
| fill_horizontal   | 按需要扩展对象的水平大小，使其完全适应其容器。               |
| center            | 将对象放在其容器的水平和垂直轴中心，不改变其大小。           |
| fill              | 按需要扩展对象的垂直大小，使其完全适应其容器。这是默认值。   |
| clip_vertical     | 可设置为让子元素的上边缘和/或下边缘裁剪至其容器边界的附加选项。裁剪基于垂直重力：顶部重力裁剪上边缘，底部重力裁剪下边缘，任一重力不会同时裁剪两边。 |
| clip_horizontal   | 可设置为让子元素的左边和/或右边裁剪至其容器边界的附加选项。裁剪基于水平重力：左边重力裁剪右边缘，右边重力裁剪左边缘，任一重力不会同时裁剪两边。 |



###### titleMode

| 值       | 说明                                                         |
| -------- | ------------------------------------------------------------ |
| disabled | 不平铺位图。这是默认值。                                     |
| clamp    | 当着色器绘制范围超出其原边界时复制边缘颜色                   |
| repeat   | 水平和垂直重复着色器的图像。                                 |
| mirror   | 水平和垂直重复着色器的图像，交替镜像图像以使相邻图像始终相接。 |

##### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<bitmap xmlns:android="http://schemas.android.com/apk/res/android"
    android:src="@drawable/icon"
    android:tileMode="repeat" />
```

### 九空格（NinePatch）

可在其中定义可伸缩区域，以便 Android 在视图中的内容超出正常图像边界时进行缩放。此类图像通常指定为至少有一个尺寸设置为 `"wrap_content"` 的视图背景，而且当视图通过扩展来适应内容时，九宫格图像也会通过扩展来匹配视图的大小。

#### 使用

- Java	R.drawable.filename
- xml     [package:]drawable/filename

```xml
<Button
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:background="@drawable/myninepatch" />
```



#### xml 九空格

##### 文件位置

res/drawable/*filename*.xml

##### 资源引用

- Java	R.drawable.filename
- xml     [package:]drawable/filename

##### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<nine-patch
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:src="@[package:]drawable/drawable_resource"
    android:dither=["true" | "false"] />
```

##### 属性

| 属性           | 描述                                                         | 是否必须 | 常见值                                       |
| -------------- | ------------------------------------------------------------ | -------- | -------------------------------------------- |
| xmlns:android  | 定义 XML 命名空间                                            | 是       | "http://schemas.android.com/apk/res/android" |
| android:src    | 可绘制对象资源                                               | 是       |                                              |
| android:dither | 当位图的像素配置与屏幕不同时（例如：ARGB 8888 位图和 RGB 565 屏幕），启用或停用位图抖动。 |          |                                              |

##### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<nine-patch xmlns:android="http://schemas.android.com/apk/res/android"
    android:src="@drawable/myninepatch"
    android:dither="false" />
```

### 图层列表

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list
    xmlns:android="http://schemas.android.com/apk/res/android" >
    <item
        android:drawable="@[package:]drawable/drawable_resource"
        android:id="@[+][package:]id/resource_name"
        android:top="dimension"
        android:right="dimension"
        android:bottom="dimension"
        android:left="dimension" />
</layer-list>
```

#### 属性

##### \<layer-list>

| 属性          | 描述            | 是否必须 | 常用值                                       |
| ------------- | --------------- | -------- | -------------------------------------------- |
| xmlns:android | 定义XML命名空间 | 是       | "http://schemas.android.com/apk/res/android" |

##### \<item>

| 属性             | 描述             | 是否必须 | 常用值 |
| ---------------- | ---------------- | -------- | ------ |
| android:drawable | 可绘制对象资源   | 是       |        |
| android:id       |                  |          |        |
| android:top      | 顶部偏移（像素） |          |        |
| android:right    | 右边偏移（像素） |          |        |
| android:bottom   | 底部偏移（像素） |          |        |
| android:left     | 左边偏移（像素） |          |        |

默认情况下，所有可绘制项都会缩放以适应包含视图的大小。因此，将图像放在图层列表中的不同位置可能会增大视图的大小，并且有些图像会相应地缩放。为避免缩放列表中的项目，请在 `<item>` 元素内使用 `<bitmap>` 元素指定可绘制对象，并且对某些不缩放的项目（例如 `"center"`）定义重力。

缩放以适应其容器视图的项目：

```xml
<item android:drawable="@drawable/image">
```

为避免缩放，使用重力居中的\<bitmap>元素：

```xml
<item>
  <bitmap android:src="@drawable/image"
          android:gravity="center" />
</item>
```

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
      <bitmap android:src="@drawable/android_red"
        android:gravity="center" />
    </item>
    <item android:top="10dp" android:left="10dp">
      <bitmap android:src="@drawable/android_green"
        android:gravity="center" />
    </item>
    <item android:top="20dp" android:left="20dp">
      <bitmap android:src="@drawable/android_blue"
        android:gravity="center" />
    </item>
</layer-list>
```

### 状态列表 Status List

`StateListDrawable` 是在 XML 文件中定义的可绘制对象，它会根据对象状态，使用多个不同的图像来表示同一个图形。

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android"
    android:constantSize=["true" | "false"]
    android:dither=["true" | "false"]
    android:variablePadding=["true" | "false"] >
    <item
        android:drawable="@[package:]drawable/drawable_resource"
        android:state_pressed=["true" | "false"]
        android:state_focused=["true" | "false"]
        android:state_hovered=["true" | "false"]
        android:state_selected=["true" | "false"]
        android:state_checkable=["true" | "false"]
        android:state_checked=["true" | "false"]
        android:state_enabled=["true" | "false"]
        android:state_activated=["true" | "false"]
        android:state_window_focused=["true" | "false"] />
</selector>
```

#### 属性

##### \<selector>

| 属性                    | 描述                                                         | 是否必须 | 常用值                                       |
| ----------------------- | ------------------------------------------------------------ | -------- | -------------------------------------------- |
| xmlns:android           | 定义 XML 命名空间                                            | 是       | "http://schemas.android.com/apk/res/android" |
| android:constantSize    | 如果可绘制对象报告的内部大小在状态变更时保持不变，则值为“true”（大小是所有状态的最大值）；如果大小根据当前状态而变化，则值为“false”。默认值为 false。 |          |                                              |
| android:dither          | 值为“true”时，将在位图的像素配置与屏幕不同时（例如：ARGB 8888 位图和 RGB 565 屏幕）启用位图的抖动；值为“false”时则停用抖动。 |          |                                              |
| android:variablePadding | 如果可绘制对象的内边距应根据选择的当前状态而变化，则值为“true”；如果内边距应保持不变（基于所有状态的最大内边距），则值为“false”。启用此功能要求您在状态变更时处理执行布局，这通常不受支持。默认值为 false。 |          |                                              |

##### \<item>

| 属性                         | 描述                                                         | 是否必须 | 常用值 | 说明         |
| ---------------------------- | ------------------------------------------------------------ | -------- | ------ | ------------ |
| android:drawable             | 可绘制对象资源                                               | 是       |        |              |
| android:state_pressed        |                                                              |          |        |              |
| android:state_focused        |                                                              |          |        |              |
| android:state_hovered        | 如果当光标悬停在对象上时应使用此项目，则值为“true”；如果在默认的非悬停状态时应使用此项目，则值为“false”。通常，这个可绘制对象可能与用于“聚焦”状态的可绘制对象相同。 |          |        | API 14新引入 |
| android:state_selected       | 如果在使用定向控件浏览（例如使用方向键浏览列表）的情况下对象为当前用户选择时应使用此项目，则值为“true”；如果在未选择对象时应使用此项目，则值为“false”。 |          |        |              |
| android:state_checkable      | 如果当对象可选中时应使用此项目，则值为“true”；如果当对象不可选中时应使用此项目，则值为“false”。 |          |        |              |
| android:state_checked        | 如果在对象已选中时应使用此项目，则值为“true”；如果在对象未选中时应使用此项目，则值为“false”。 |          |        |              |
| android:state_enabled        | 如果在对象启用（能够接收触摸/点击事件）时应使用此项目，则值为“true”；如果在对象停用时应使用此项目，则值为“false”。 |          |        |              |
| android:state_activated      | 如果在对象激活作为持续选择（例如，在持续导航视图中“突出显示”之前选中的列表项）时应使用此项目，则值为“true”；如果在对象未激活时应使用此项目，则值为“false”。 |          |        | API 11才引入 |
| android:state_window_focused | 如果当应用窗口有焦点（应用在前台）时应使用此项目，则值为“true”；如果当应用窗口没有焦点（例如，通知栏下拉或对话框出现）时应使用此项目，则值为“false”。 |          |        |              |

##### 注意

请记住，Android 将应用状态列表中第一个与对象当前状态匹配的项目。因此，如果列表中的第一个项目不含上述任何状态属性，则每次都会应用它，这就是默认值应始终放在最后的原因（如以下示例所示）。

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true"
          android:drawable="@drawable/button_pressed" /> <!-- pressed -->
    <item android:state_focused="true"
          android:drawable="@drawable/button_focused" /> <!-- focused -->
    <item android:state_hovered="true"
          android:drawable="@drawable/button_focused" /> <!-- hovered -->
    <item android:drawable="@drawable/button_normal" /> <!-- default -->
</selector>
```

### 级别列表 Level list

管理大量备选可绘制对象的可绘制对象，每个可绘制对象都配有最大备选数量。若使用 `setLevel()` 设置可绘制对象的级别值，则会加载级别列表中 `android:maxLevel` 值大于或等于传递至方法的值的可绘制对象资源。

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<level-list
    xmlns:android="http://schemas.android.com/apk/res/android" >
    <item
        android:drawable="@drawable/drawable_resource"
        android:maxLevel="integer"
        android:minLevel="integer" />
</level-list>
```

#### 属性

##### \<level-list>

| 属性          | 描述              | 是否必须 | 常用值                                         |
| ------------- | ----------------- | -------- | ---------------------------------------------- |
| xmlns:android | 定义 XML 命名空间 | 是       | `"http://schemas.android.com/apk/res/android"` |

##### \<item>

| 属性             | 描述                       | 是否必须 | 常用值 |
| ---------------- | -------------------------- | -------- | ------ |
| android:drawable | *可绘制对象资源*           | 是       |        |
| android:maxLevel | 此项目允许的最高级别，整型 |          |        |
| android:minLevel | 此项目允许的最低级别，整型 |          |        |

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<level-list xmlns:android="http://schemas.android.com/apk/res/android" >
    <item
        android:drawable="@drawable/status_off"
        android:maxLevel="0" />
    <item
        android:drawable="@drawable/status_on"
        android:maxLevel="1" />
</level-list>
```

在将此项目应用到 `View` 后，您便可通过 `setLevel()` 或 `setImageLevel()` 更改级别。

### 转换可绘制对象

`TransitionDrawable` 是可在两种可绘制对象资源之间交错淡出的可绘制对象。

每个可绘制对象由单个 `<transition>` 元素内的 `<item>` 元素表示。不支持超过两个项目。如要向前转换，请调用 `startTransition()`。如要向后转换，则调用 `reverseTransition()`。

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<transition
xmlns:android="http://schemas.android.com/apk/res/android" >
    <item
        android:drawable="@[package:]drawable/drawable_resource"
        android:id="@[+][package:]id/resource_name"
        android:top="dimension"
        android:right="dimension"
        android:bottom="dimension"
        android:left="dimension" />
</transition>
```

#### 属性

##### \<transition>

| 属性          | 描述              | 是否必须 | 常用值                                       |
| ------------- | ----------------- | -------- | -------------------------------------------- |
| xmlns:android | 定义 XML 命名空间 | 是       | "http://schemas.android.com/apk/res/android" |

##### \<item>

| 属性             | 描述             | 是否必须 | 常用值 |
| ---------------- | ---------------- | -------- | ------ |
| android:drawable | *可绘制对象资源* | 是       |        |
| android:id       |                  |          |        |
| android:top      | 顶部偏移（像素） |          |        |
| android:right    | 右边偏移（像素） |          |        |
| android:bottom   | 底部偏移（像素） |          |        |
| android:left     | 左边偏移（像素） |          |        |

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<transition xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@drawable/on" />
    <item android:drawable="@drawable/off" />
</transition>
```

```xml
<ImageButton
    android:id="@+id/button"
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:src="@drawable/transition" />
```

```java
ImageButton button = (ImageButton) findViewById(R.id.button);
Drawable drawable = button.getDrawable();
if (drawable instanceof TransitionDrawable) {
    ((TransitionDrawable) drawable).startTransition(500);
}
```

### 插入可绘制对象

在 XML 文件中定义，以指定距离插入其他可绘制对象的可绘制对象。当视图需要小于视图实际边界的背景时，此类可绘制对象很有用。

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<inset
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/drawable_resource"
    android:insetTop="dimension"
    android:insetRight="dimension"
    android:insetBottom="dimension"
    android:insetLeft="dimension" />
```

#### 属性

##### \<inset>

| 属性                | 描述                               | 是否必须 | 常用值                                       |
| ------------------- | ---------------------------------- | -------- | -------------------------------------------- |
| xmlns:android       | 定义 XML 命名空间                  | 是       | "http://schemas.android.com/apk/res/android" |
| android:drawable    | *可绘制对象资源*                   | 是       |                                              |
| android:insetTop    | 顶部插入，表示为尺寸值或[尺寸资源] |          |                                              |
| android:insetRight  | 右边插入，表示为尺寸值或[尺寸资源] |          |                                              |
| android:insetBottom | 底部插入，表示为尺寸值或[尺寸资源] |          |                                              |
| android:insetLeft   | 左边插入，表示为尺寸值或[尺寸资源] |          |                                              |

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/background"
    android:insetTop="10dp"
    android:insetLeft="10dp" />
```

### 裁剪可绘制对象

在 XML 文件中定义，对其他可绘制对象进行裁剪（根据其当前级别）的可绘制对象。您可以根据级别以及用于控制其在整个容器中位置的重力，来控制子可绘制对象的裁剪宽度和高度。通常用于实现进度栏之类的项目。

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<clip
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/drawable_resource"
    android:clipOrientation=["horizontal" | "vertical"]
    android:gravity=["top" | "bottom" | "left" | "right" | "center_vertical" |
                     "fill_vertical" | "center_horizontal" | "fill_horizontal" |
                     "center" | "fill" | "clip_vertical" | "clip_horizontal"] />
```

#### 属性

##### \<clip>

| 属性                    | 描述                         | 是否必须 | 常用值                                                       | 说明   |
| ----------------------- | ---------------------------- | -------- | ------------------------------------------------------------ | ------ |
| xmlns:android           | 定义 XML 命名空间            | 是       | "http://schemas.android.com/apk/res/android"                 |        |
| android:drawable        | *可绘制对象资源*             | 是       |                                                              |        |
| android:clipOrientation | 裁剪方向                     |          | horizontal：水平裁剪可绘制对象<br>vertical：垂直裁剪可绘制对象 | 关键字 |
| android:gravity         | 指定可绘制对象中要裁剪的位置 |          | 见下表                                                       | 关键字 |

##### ClipGravity

| 值                  | 说明                                                         |
| ------------------- | ------------------------------------------------------------ |
| `top`               | 将对象放在其容器顶部，不改变其大小。当 `clipOrientation` 是 `"vertical"` 时，在可绘制对象的底部裁剪。 |
| `bottom`            | 将对象放在其容器底部，不改变其大小。当 `clipOrientation` 是 `"vertical"` 时，在可绘制对象的顶部裁剪。 |
| `left`              | 将对象放在其容器左边缘，不改变其大小。这是默认值。当 `clipOrientation` 是 `"horizontal"` 时，在可绘制对象的右边裁剪。这是默认值。 |
| `right`             | 将对象放在其容器右边缘，不改变其大小。当 `clipOrientation` 是 `"horizontal"` 时，在可绘制对象的左边裁剪。 |
| `center_vertical`   | 将对象放在其容器的垂直中心，不改变其大小。裁剪行为与重力为 `"center"` 时相同。 |
| `fill_vertical`     | 按需要扩展对象的垂直大小，使其完全适应其容器。当 `clipOrientation` 是 `"vertical"` 时，不会进行裁剪，因为可绘制对象会填充垂直空间（除非可绘制对象级别为 0，此时它不可见）。 |
| `center_horizontal` | 将对象放在其容器的水平中心，不改变其大小。裁剪行为与重力为 `"center"` 时相同。 |
| `fill_horizontal`   | 按需要扩展对象的水平大小，使其完全适应其容器。当 `clipOrientation` 是 `"horizontal"` 时，不会进行裁剪，因为可绘制对象会填充水平空间（除非可绘制对象级别为 0，此时它不可见）。 |
| `center`            | 将对象放在其容器的水平和垂直轴中心，不改变其大小。当 `clipOrientation` 是 `"horizontal"` 时，在左边和右边裁剪。当 `clipOrientation` 是 `"vertical"` 时，在顶部和底部裁剪。 |
| `fill`              | 按需要扩展对象的垂直大小，使其完全适应其容器。不会进行裁剪，因为可绘制对象会填充水平和垂直空间（除非可绘制对象级别为 0，此时它不可见） |
| `clip_vertical`     | 可设置为让子元素的上边缘和/或下边缘裁剪至其容器边界的附加选项。裁剪基于垂直重力：顶部重力裁剪上边缘，底部重力裁剪下边缘，任一重力不会同时裁剪两边。 |
| `clip_horizontal`   | 可设置为让子元素的左边和/或右边裁剪至其容器边界的附加选项。裁剪基于水平重力：左边重力裁剪右边缘，右边重力裁剪左边缘，任一重力不会同时裁剪两边。 |

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<clip xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/android"
    android:clipOrientation="horizontal"
    android:gravity="left" />
```

```xml
<ImageView
    android:id="@+id/image"
    android:background="@drawable/clip"
    android:layout_height="wrap_content"
    android:layout_width="wrap_content" />
```

```java
ImageView imageview = (ImageView) findViewById(R.id.image);
Drawable drawable = imageview.getBackground();
if (drawable instanceof CLipDrawable) {
    ((ClipDrawable)drawable).setLevel(drawable.getLevel() + 1000);
}
```

### 缩放可绘制对象

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<scale
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/drawable_resource"
    android:scaleGravity=["top" | "bottom" | "left" | "right" | "center_vertical" |
                          "fill_vertical" | "center_horizontal" | "fill_horizontal" |
                          "center" | "fill" | "clip_vertical" | "clip_horizontal"]
    android:scaleHeight="percentage"
    android:scaleWidth="percentage" />
```

#### 属性

##### \<scale>

| 属性                 | 描述                                                         | 是否必须 | 常用值                                         | 说明 |
| -------------------- | ------------------------------------------------------------ | -------- | ---------------------------------------------- | ---- |
| xmlns:android        | 定义 XML 命名空间                                            | 是       | `"http://schemas.android.com/apk/res/android"` |      |
| android:drawable     | *可绘制对象资源*                                             | 是       |                                                |      |
| android:scaleGravity | 指定缩放后的重力位置                                         |          | 见下表                                         |      |
| android:scaleHeight  | 缩放高度，表示为可绘制对象边界的百分比。值的格式为 XX%。例如：100%、12.5% 等。 |          |                                                |      |
| android:scaleWidth   | 缩放宽度，表示为可绘制对象边界的百分比。值的格式为 XX%。例如：100%、12.5% 等 |          |                                                |      |

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<scale xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/logo"
    android:scaleGravity="center_vertical|center_horizontal"
    android:scaleHeight="80%"
    android:scaleWidth="80%" />
```

### 形状可绘制对象

在 XML 文件中定义的通用形状。

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape=["rectangle" | "oval" | "line" | "ring"] >
    <corners
        android:radius="integer"
        android:topLeftRadius="integer"
        android:topRightRadius="integer"
        android:bottomLeftRadius="integer"
        android:bottomRightRadius="integer" />
    <gradient
        android:angle="integer"
        android:centerX="float"
        android:centerY="float"
        android:centerColor="integer"
        android:endColor="color"
        android:gradientRadius="integer"
        android:startColor="color"
        android:type=["linear" | "radial" | "sweep"]
        android:useLevel=["true" | "false"] />
    <padding
        android:left="integer"
        android:top="integer"
        android:right="integer"
        android:bottom="integer" />
    <size
        android:width="integer"
        android:height="integer" />
    <solid
        android:color="color" />
    <stroke
        android:width="integer"
        android:color="color"
        android:dashWidth="integer"
        android:dashGap="integer" />
</shape>
```

#### 属性

##### \<shape>

形状可绘制对象。这必须是根元素。

| 属性                     | 描述                                                         | 是否必须 | 常用值                                                      |
| ------------------------ | ------------------------------------------------------------ | -------- | ----------------------------------------------------------- |
| xmlns:android            | 定义 XML 命名空间                                            | 是       | "http://schemas.android.com/apk/res/android"                |
| android:shape            | 定义形状的类型                                               |          | 有效值见下表，当且仅当 android:shape="ring"时才使用以下属性 |
| android:innerRadius      | 环内部（中间的孔）的半径，以尺寸值或[尺寸资源]表示。         |          |                                                             |
| android:innerRadiusRatio | 环内部的半径，以环宽度的比率表示。例如，如果 `android:innerRadiusRatio="5"`，则内半径等于环宽度除以 5。此值被 `android:innerRadius` 覆盖。默认值为 9。 |          |                                                             |
| android:thickness        | 环的厚度，以尺寸值或[尺寸资源]表示                           |          |                                                             |
| android:thicknessRatio   | 环的厚度，表示为环宽度的比率。例如，如果 `android:thicknessRatio="2"`，则厚度等于环宽度除以 2。此值被 `android:innerRadius` 覆盖。默认值为 3。 |          |                                                             |
| android:useLevel         | 如果此属性用作 `LevelListDrawable`，则值为“true”。此属性的值通常应为“false”，否则无法显示形状。 |          |                                                             |

###### shapeMode

| 值            | 描述                                                         |
| ------------- | ------------------------------------------------------------ |
| `"rectangle"` | 填充包含视图的矩形。这是默认形状。                           |
| `"oval"`      | 适应包含视图尺寸的椭圆形状。                                 |
| `"line"`      | 跨越包含视图宽度的水平线。此形状需要 `<stroke>` 元素定义线宽。 |
| `"ring"`      | 环形。                                                       |



##### \<corners>

为形状产生圆角。仅当形状为**矩形**时适用。

| 属性                      | 描述         | 是否必须 | 常用值 |
| ------------------------- | ------------ | -------- | ------ |
| android:radius            | 所有角的半径 |          |        |
| android:topLeftRadius     | 左上角的半径 |          |        |
| android:topRightRadius    | 右上角的半径 |          |        |
| android:bottomLeftRadius  | 左下角的半径 |          |        |
| android:bottomRightRadius | 右下角的半径 |          |        |

##### \<gradient>

指定形状的渐变颜色。

| 属性                   | 描述                                                         | 是否必须 | 常用值 | 说明   |
| ---------------------- | ------------------------------------------------------------ | -------- | ------ | ------ |
| android:angle          | 渐变的角度（度）。0 为从左到右，90 为从上到上。必须是 45 的倍数。默认值为 0。 |          |        |        |
| android:centerX        | 渐变中心的相对 X 轴位置                                      |          |        |        |
| android:centerY        | 渐变中心的相对 Y 轴位置                                      |          |        |        |
| android:centerColor    | 起始颜色与结束颜色之间的可选颜色                             |          |        |        |
| android:endColor       | 结束颜色                                                     |          |        |        |
| android:gradientRadius | 渐变的半径                                                   |          |        |        |
| android:startColor     | 起始颜色                                                     |          |        |        |
| android:type           | 要应用的渐变图案的类型                                       |          | 见下边 | 关键字 |
| android:useLevel       | 如果此属性用作 `LevelListDrawable`，则值为“true”。           |          |        |        |

###### gradientType

| 值         | 说明                           |
| ---------- | ------------------------------ |
| `"linear"` | 线性渐变。这是默认值。         |
| `"radial"` | 径向渐变。起始颜色为中心颜色。 |
| `"sweep"`  | 流线型渐变。                   |

##### \<padding>

要应用到包含视图元素的内边距（这会填充视图内容的位置，而非形状）

| 属性           | 描述     | 是否必须 | 常用值 |
| -------------- | -------- | -------- | ------ |
| android:left   | 左内边距 |          |        |
| android:top    | 上内边距 |          |        |
| android:right  | 右内边距 |          |        |
| android:bottom | 下内边距 |          |        |

##### \<size>

形状的大小

| 属性           | 描述       | 是否必须 | 常用值 |
| -------------- | ---------- | -------- | ------ |
| android:height | 形状的高度 |          |        |
| android:width  | 形状的宽度 |          |        |

###### 注

默认情况下，形状根据此处定义的尺寸，按比例缩放至容器视图的大小。在 `ImageView` 中使用形状时，您可将 [`android:scaleType`](https://developer.android.com/reference/android/widget/ImageView.html#attr_android:scaleType) 设置为 `"center"`，从而限制缩放。

##### \<solid>

用于填充形状的纯色

| 属性          | 描述             | 是否必须 | 常用值 |
| ------------- | ---------------- | -------- | ------ |
| android:color | 应用于形状的颜色 |          |        |



##### \<stroke>

形状的笔划中线。

| 属性              | 描述                                                    | 是否必须 | 常用值 |
| ----------------- | ------------------------------------------------------- | -------- | ------ |
| android:width     | 线宽                                                    |          |        |
| android:color     | 线的颜色                                                |          |        |
| android:dashGap   | 短划线的间距                                            |          |        |
| android:dashWidth | 每个短划线的大小，仅在设置了 `android:dashGap` 时有效。 |          |        |

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:startColor="#FFFF0000"
        android:endColor="#80FF00FF"
        android:angle="45"/>
    <padding android:left="7dp"
        android:top="7dp"
        android:right="7dp"
        android:bottom="7dp" />
    <corners android:radius="8dp" />
</shape>
```

```
<TextView
    android:background="@drawable/gradient_box"
    android:layout_height="wrap_content"
    android:layout_width="wrap_content" />
```

```java
Resources res = getResources();
Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.gradient_box, getTheme());

TextView tv = (TextView)findViewById(R.id.textview);
tv.setBackground(shape);
```

