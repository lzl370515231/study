### 其他数据

**引用与具体的文件名称无关，只与节点和属性名称有关。**

### Bool

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <bool
        name="bool_name"
        >[true | false]</bool>
</resources>
```

#### 示例

res/values/bools.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <bool name="screen_small">true</bool>
    <bool name="adjust_view_bounds">true</bool>
</resources>
```



```java
Resources res = getResources();
boolean screenIsSmall = res.getBoolean(R.bool.screen_small);
```

### Color

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color
        name="color_name"
        >hex_color</color>
</resources>
```

#### 示例

res/values/colors.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
   <color name="opaque_red">#f00</color>
   <color name="translucent_red">#80ff0000</color>
</resources>
```



```java
Resources res = getResources();
int color = res.getColor(R.color.opaque_red);
```



```xml
<TextView
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:textColor="@color/translucent_red"
    android:text="Hello"/>
```



### Dimension

##### dp

密度无关像素-基于屏幕物理密度的抽象单位。这些单位是相对于一个160dpi (每英寸点数)的屏幕，其中1dp 大约等于1px。

##### sp

字体大小，将根据屏幕密度和用户的偏好进行调整

##### pt



##### px

实际像素

##### mm

毫米

##### in

英寸

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen
        name="dimension_name"
        >dimension</dimen>
</resources>
```

#### 示例

res/values/dimens.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="textview_height">25dp</dimen>
    <dimen name="textview_width">150dp</dimen>
    <dimen name="ball_radius">30dp</dimen>
    <dimen name="font_size">16sp</dimen>
</resources>
```



```java
Resources res = getResources();
float fontSize = res.getDimension(R.dimen.font_size);
```



```xml
<TextView
    android:layout_height="@dimen/textview_height"
    android:layout_width="@dimen/textview_width"
    android:textSize="@dimen/font_size"/>
```



### ID

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <item
        type="id"
        name="id_name" />
</resources>
```

#### 示例

res/values/ids.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <item type="id" name="button_ok" />
    <item type="id" name="dialog_exit" />
</resources>
```



```xml
<Button android:id="@id/button_ok"
    style="@style/button_style" />
```



```java
showDialog(R.id.dialog_exit);
```



### Integer

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <integer
        name="integer_name"
        >integer</integer>
</resources>
```

#### 示例

res/values/integers.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <integer name="max_speed">75</integer>
    <integer name="min_speed">5</integer>
</resources>
```



```java
Resources res = getResources();
int maxSpeed = res.getInteger(R.integer.max_speed);
```

## Integer array

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <integer-array
        name="integer_array_name">
        <item
            >integer</item>
    </integer-array>
</resources>
```

#### 示例

res/values/integers.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <integer-array name="bits">
        <item>4</item>
        <item>8</item>
        <item>16</item>
        <item>32</item>
    </integer-array>
</resources>
```



```xml
Resources res = getResources();
int[] bits = res.getIntArray(R.array.bits);
```



### Typed array

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <array
        name="integer_array_name">
        <item>resource</item>
    </array>
</resources>
```

#### 示例

res/values/arrays.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <array name="icons">
        <item>@drawable/home</item>
        <item>@drawable/settings</item>
        <item>@drawable/logout</item>
    </array>
    <array name="colors">
        <item>#FFFF0000</item>
        <item>#FF00FF00</item>
        <item>#FF0000FF</item>
    </array>
</resources>
```



```
Resources res = getResources();
TypedArray icons = res.obtainTypedArray(R.array.icons);
Drawable drawable = icons.getDrawable(0);

TypedArray colors = res.obtainTypedArray(R.array.colors);
int color = colors.getColor(0,0);
```

