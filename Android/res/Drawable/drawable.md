# Drawable



## 概念

1. Drawable本身表示的只是一种**图像**的概念，因此Drawable不仅仅是图片，也可以是颜色构造出来的图像效果（后面会说明）。
2. Drawable本身是一个**抽象类**，因此具体的实现都是由子类完成的，比如ShapeDrawable，BitmapDrawable等。
3. Drawable的内部宽高可以分别通过getIntrinsicWidth()和getIntrinsicHeight()获取，但**并不是所有的Drawable都有内部宽高的属性**，比如一个颜色形成的Drawable并没有宽高的概念。在大多数情况下，Drawable并没有大小的概念，因为当Drawable作为View的背景图时，Drawable会被拉伸至View的同等大小。



## BitmapDrawable

BitmapDrawable 是对bitmap的一种包装，可以设置它包装的bitmap在BitmapDrawable区域内的绘制方式，如平铺填充、拉伸填充或者保持图片原始大小，也可以在BitmapDrawable区域内部使用gravity指定的对齐方式。



### 属性

**android:src**

​	Drawable resource

**android:antialias**

​	 类型：Boolean。是否开启抗锯齿。开启后图片会变得更平滑些，因此一般建议开启，设置为true即可。

**android:dither**

 类型：Boolean。是否允许抖动，如果位图与屏幕的像素配置不同时，开启这个选项可以让高质量的图片在低质量的屏幕上保持较好的显示效果（例如：一个位图的像素设置是 ARGB 8888，但屏幕的设置是RGB 565，开启这个选项可以是图片不过于失真）一般建议开启，为true即可。 

**android:filter**

​	类型：Boolean。是否允许对位图进行滤波。当图片被压缩或者拉伸时，使用滤波可以获得平滑的外观效果。一般建议开启，为true即可。

**android:gravity**

​	当图片小于容器尺寸时，设置此选项可以对图片经典定位，这个属性比较多，不同选项可以使用"|"来组合使用。

|      可选项       | 含义                                               |
| :---------------: | :------------------------------------------------- |
|        top        | 将图片放在容器顶部，不改变图片大小                 |
|      bottom       | 将图片放在容器底部，不改变图片大小                 |
|       left        | 将图片放在容器左侧，不改变图片大小                 |
|       right       | 将图片放在容器右侧，不改变图片大小                 |
|  center_vertical  | 图片竖直居中，不改变图片大小                       |
|   fill_vertical   | 图片竖直方向填充容器                               |
| center_horizontal | 图片水平居中，不改变图片大小                       |
|  fill_horizontal  | 图片水平方向填充容器                               |
|      center       | 使图片在水平方向和竖直方向同时居中，不改变图片大小 |
|       fill        | 图片填充容器，默认值                               |
|   clip_vertical   | 竖直方向剪切，很少使用                             |
|  clip_horizontal  | 水平方向剪切，很少使用                             |



**android:mipMap**

​	纹理映射处理技术

**android:tileMode**

​	平铺模式。有以下几个值:

​		disabled：默认值，表示不平铺

​		clamp：复制边缘色彩

​		repeat: X、Y轴进行重复图片显示

​		mirror:在水平和垂直方向上使用交替镜像的方式重复图片的绘制

**clamp 的效果**

原图

![](.\other\drawable_bitmap_tilemode.png)

处理之后的效果图

![](.\other\drawable_bitmap_tilemode_clamp.png)



## NinePatchDrawable

NinePatchDrawable表示的是我们熟悉的.9格式的图片，.9图片可以在保证图片不失真的情况下任意进行缩放，在实际的使用中我们也是通过Xml来实现即可。

**特别注意**：

​	点九图只能适用于拉伸的情况，对于压缩的情况并不适用，如果需要适配很多分辨率的屏幕时需要把点九图做的小一点。



## ShapeDrawable

ShapeDrawable对于Xml的shape标签，在实际开发中我们经常将其作为背景图片使用，因为ShapeDrawable可以帮助我们通过颜色来构造图片，也可以构造渐变效果的图片。

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
        android:centerX="integer"
        android:centerY="integer"
        android:centerColor="integer"
        android:endColor="color"
        android:gradientRadius="integer"
        android:startColor="color"
        android:type=["linear" | "radial" | "sweep"]
        android:usesLevel=["true" | "false"] />
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

android:shape

​	表示图像的形状，有以下值：

​		rectangle	矩形		（**默认**）

​		oval			 椭圆

​		line			  横线

​		ring			 圆环

​	ring 值的几个相关属性：

|           属性           | 含义                                                         |
| :----------------------: | :----------------------------------------------------------- |
|   android:innerRadius    | 圆环的半径与android:innerRadiusRatio 同时存在时，以android:innerRadius 为准 |
| android:innerRadiusRatio | 内半径占整个Drawable宽度的比例，默认值为9.如果为n，那么半径=宽度/n |
|    android:thickness     | 圆环的厚度，即外半径减去内半径的大小与android：thicknessRatio同时存在时以android:thickness为准 |
| android：thicknessRatio  | 厚度占整个Drawable宽度比例，默认值为3，如果为n，那么厚度=宽度/n |
|     android:useLevel     | 一般都应该使用false，否则可能无法达到预期显示效果，除非它被当做LevelListDrawable来使用。 |

​	

#### 子元素

**\<corners\>**

​	指定边角的半径，数值越大角越圆，数值越小越趋近于直角，参数为

```xml
<corners
android:radius="integer"
android:topLeftRadius="integer"
android:topRightRadius="integer"
android:bottomLeftRadius="integer"
android:bottomRightRadius="integer" />
```

Android:radius 直接指定4个角的半径。

**\<gradient\>**

​	设置颜色渐变与 \<solid\> 为互斥标签，因为 solid 表示纯色填充，而 gradient 表示渐变填充

|          属性          | 含义                                                         |
| :--------------------: | :----------------------------------------------------------- |
|     android:angle      | 渐变的角度，默认为0，其值务必为45°的倍数，0表示从左到右，90表示从下到上 |
|    android:centerX     | 渐变中心点的横坐标                                           |
|    android:centerY     | 渐变的中心点的纵坐标，渐变中心点会影响渐变的具体效果。       |
|   android:startColor   | 渐变的开始颜色                                               |
|  android:centerColor   | 渐变的中间颜色                                               |
|    android:endColor    | 渐变的结束颜色                                               |
| android:gradientRadius | 渐变的半径，当android:type=”radial”有效                      |
|    android:useLevel    | 一般为false                                                  |
|      android:type      | 渐变类别，linear(线性)为默认值，radial（径内渐变），sweep（扫描渐变） |

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:innerRadius="20dp"
    android:shape="ring"
    android:thickness="8dp"
    android:useLevel="false" >

    <gradient android:angle="0"
        android:startColor="@color/normal"
        android:centerColor="#5027844F"
        android:endColor="#fff"
        android:useLevel="false"
        android:type="sweep"
        />
</shape>
```

效果如下：

![](.\other\drawable_shape_ring_sweep.png)

将自定义环形圈设置给一个旋转动画，并利用该旋转动画自定义成一个环形进度圈的style，最后将该自定义的style赋值给Progress组件。

自定义旋转动画 progress_rotate.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<rotate xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/shape_drawable"
    android:pivotX="50%"
    android:pivotY="50%"
    android:fromDegrees="0"
    android:toDegrees="360"
    >
</rotate>
```

自定义Progress的style

```xml
<style name="CustomProgressStyle" >
    <item name="android:indeterminateDrawable">@drawable/progress_rotate</item>
    <item name="android:minWidth">72dp</item>
    <item name="android:maxWidth">72dp</item>
    <item name="android:minHeight">72dp</item>
    <item name="android:maxHeight">72dp</item>
</style>
```

应用到Progress组件

```xml
<ProgressBar
     android:layout_width="100dp"
     android:layout_height="100dp"
     android:layout_centerInParent="true"
     style="@style/CustomProgressStyle"
     android:indeterminateDuration="700"
     />
```

效果如下

![](.\other\drawable_shape_rotate_progress.gif)

**\<solid\>**

​	表示纯色填充，通过android:color设置颜色即可。

**\<stroke\>**

​	描述边框，属性如下

|       属性        | 含义                                                     |
| :---------------: | -------------------------------------------------------- |
|   android:width   | 描述边框的宽度，数值越大，越边框越厚                     |
|   android:color   | 边框的颜色                                               |
| android:dashWidth | 组成虚线的线段宽度                                       |
|  android:dashGap  | 组成虚线的线段之间的间隔，间隔越大，虚线看起的间隙就越大 |

有点要明白的是**android:dashWidth和android:dashGap有任意一个为0，则虚线无法预期显示**。

**\<padding\>**

​	表示内容或子标签边距，4个属性top、bottom、left、right，需要注意的是这个标签的作用是为内容设置与当前应用此shape的View的边距，而不是设置当前View与父元素的边距。

**\<size\>**

​	设置背景大小，width和height俩属性。一般来说这个值不是shape的最终显示大小，因为shape作为背景时会根据View的大小而填充其背景，因此Shape的大小很多时候是View的大小决定的。



## LayDrawable

一个LayerDrawable是一个可以管理一组drawable对象的drawable。在LayerDrawable的drawable资源按照列表的顺序绘制，列表的最后一个drawable绘制在最上层。LayerDrawable对于xml的标签是**\<layer-list\>**其语法如下：

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

**android:id**

​	资源ID，一个为这个item定义的唯一的资源ID。

**android:top**

​	Drawable相对于View的顶部的偏移量

**android:right** 

​	Integer，Drawable相对于View的右边的偏移量，单位像素 
**android:bottom** 

​	Integer，Drawable相对于View的底部的偏移量，单位像素 
**android:left** 

​	Integer，Drawable相对于View的左边的偏移量，单位像素 

**android:drawable** 

​	Drawable资源，可以引用已有的drawable资源，也可在item中自定义Drawable。

示例：（带阴影的圆角矩形）

```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 灰色阴影 内容距离左边2dp，距离顶部4dp-->
    <item
        android:left="2dp"
        android:top="4dp">
        <shape>
            <solid android:color="@android:color/darker_gray" />
            <corners android:radius="10dp" />
        </shape>
    </item>
    <!-- 白色前景 内容距离底部4dp 右边2dp-->
    <item
        android:bottom="4dp"
        android:right="2dp">
        <shape>
            <solid android:color="#FFFFFF" />
            <corners android:radius="10dp" />
        </shape>
    </item>
</layer-list>
```

效果如下：

![](.\other\drawable_layer.png)



## StateListDrawable

StateListDrawable对于xml的**\<selector\>**标签，这个标签可以说是我们最常用的标签了，在开发中，有时候我们需要一个View在点击前显示某种状态，而在点击后又切换到另外一种状态，这时我们就需要利用\<selector\>标签来实现啦。

StateListDrawable本身也是表示Drawable的集合，每个Drawable就对于View的一种状态，因此我们经常使用StateListDrawable来设置View的背景，以便在不同状态下显示不同的效果，从而获得更优的用户体验。其主要语法如下：

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

|             属性             | 含义                                             |
| :--------------------------: | ------------------------------------------------ |
|       android:drawable       | 该状态下要显示的图像，可以是Drawable也可以是图片 |
|    android:state_pressed     | 表示是否处于被按下状态                           |
|    android:state_focused     | 表示是否已得到焦点状态                           |
|    android:state_hovered     | 表示光标是否停留在View的自身大小范围内的状态     |
|    android:state_selected    | 表示是否处于被选中状态                           |
|   android:state_checkable    | 表示是否处于可勾选状态                           |
|    android:state_checked     | 表示是否处于已勾选状态，一般用于CheckBox         |
|    android:state_enabled     | 表示是否处于可用状态                             |
|     android:state_active     | 表示是否处于激活状态                             |
| android:state_window_focused | 表示是否窗口已得到焦点状态                       |

**\<selector\>**标签的属性含义：

**android:constantSize**

​	StateListDrawable的固有大小是否随着其状态改变而改变，因为在状态改变后，StateListDrawable会切换到别的Drawable，而不同的Drawable其大小可能不一样。true表示大小不变，这时其固有大小是内容所有Drawable的固有大小的最大值。false则会随着状态改变而改变，默认值为false 

**android:variablePadding**

​	表示 StateListDrawable的padding是否随状态的改变而改变，默认值为false，一般建议设置为false就行。 

**android:dither**

​	是否开启抖动效果，开启后可使高质量的图片在低质量的屏幕上仍然有较好的显示效果，一般建议开启，设置为true。 



## LevelListDrawable

LevelListDrawable对应于<level-list>标签，也表示一个Drawable的集合，但集合中的每个Drawable都一个等级。根据不同等级，LevelListDrawable会切换到相应的Drawable。语法如下：

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

|       属性       | 含义                   |
| :--------------: | ---------------------- |
| android:drawable | 该等级下需要展示的图片 |
| android:maxLevel | 该项所允许的最大level  |
| android:minLevel | 该项所允许的最小level  |

等级是由android:maxLevel和android:minLevel所决定的，其等级范围是0-10000，最小为0，默认值，最大则为10000。

```xml
<?xml version="1.0" encoding="utf-8"?>
<level-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@drawable/image4"
        android:maxLevel="0"
        />

    <item android:drawable="@drawable/image1"
          android:maxLevel="1"
        />

    <item android:drawable="@drawable/image2"
        android:maxLevel="2"
        />

    <item android:drawable="@drawable/image3"
        android:maxLevel="3"
        />
</level-list>
```

定义了4个 item，等级分为0,1,2,3，都对应着不同的Drawable，在java代码中实现一个效果，每过2秒更好一个不同等级的图片，代码如下：

```java
package com.zejian.drawble;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static ImageView imageView;

    static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                imageView.getDrawable().setLevel(1);
            }else if(msg.what==2){
                imageView.getDrawable().setLevel(2);
            }else if(msg.what==3){
                imageView.getDrawable().setLevel(3);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.image);
        imageView.setImageResource(R.drawable.level_list_drawable);
        imageView.setImageLevel(0);

        for (int i=1;i<4;i++){
            handler.sendEmptyMessageDelayed(i,i*2000);
        }
    }
}
```

效果如下：

![](.\other\drawable_level.gif)

## TransitionDrawable

TransitionDrawable用于实现两个Drawable之间的淡入淡出的效果，它对应的是\<transition\>标签;其语法如下：

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

android:id

​	资源ID,drawable资源的唯一标识。

```xml
<?xml version="1.0" encoding="utf-8"?>
<transition xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@drawable/image1"/>
    <item android:drawable="@drawable/image2" />
</transition>
```

引用如下：

```xml
<ImageView                                           
    android:id="@+id/image"                          
    android:layout_width="wrap_content"              
    android:layout_height="wrap_content"             
    android:layout_centerInParent="true"             
    android:background="@drawable/transition_drawable"      
/> 
```

通过 startTransition 和 reverseTransition 方法实现淡入淡出效果，代码如下：

```java
TransitionDrawable drawable= (TransitionDrawable) imageView.getBackground();
drawable.startTransition(4000);   
```

如果在imageView设置的src属性

```xml
<ImageView                                           
    android:id="@+id/image"                          
    android:layout_width="wrap_content"              
    android:layout_height="wrap_content"             
    android:layout_centerInParent="true"             
    android:src="@drawable/transition_drawable"      
/> 
```

代码控制为

```java
TransitionDrawable drawable= (TransitionDrawable) imageView.getDrawable();
drawable.startTransition(4000);   
```

代码实现的方式

```java
ImageView imageView= (ImageView) findViewById(R.id.tranImage);

Bitmap bitmap1= BitmapFactory.decodeResource(getResources(), R.drawable.image1);
Bitmap bitmap2= BitmapFactory.decodeResource(getResources(), R.drawable.image2);
final TransitionDrawable td = new TransitionDrawable(new Drawable[] {  new BitmapDrawable(getResources(), bitmap1),
        new BitmapDrawable(getResources(), bitmap2) });
imageView.setImageDrawable(td);
td.startTransition(4000);
```





## InsetDrawable

InsetDrawable对应<inset>标签，它可以将其他Drawable内嵌到自己当中，并可以在四周预留出一定的间距。当我们希望View的背景比实际区域小时，就可以采用InsetDrawable来实现。其语法如下：

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

|        属性         | 含义               |
| :-----------------: | ------------------ |
|  android:insetTop   | 图像距离上边的距离 |
| android:insetRight  | 图像距离右边的距离 |
| android:insetBottom | 图像距离底边的距离 |
|  android:insetLeft  | 图像距离左边的距离 |

示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:insetBottom="10dp"
    android:drawable="@drawable/transition_bg_1"
    android:insetTop="10dp"
    android:insetLeft="10dp"
    android:insetRight="10dp"
    >
</inset>                                           
```

作为根布局的背景

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/inset_drawable"
    tools:context="com.zejian.drawble.MainActivity">

</RelativeLayout>
```

效果如下

![](.\other\drawable_inset.png)

## ScaleDrawable

ScaleDrawable对应**\<scale\>**标签，主要基于当前的level，对指定的Drawable进行缩放操作。有点需要特别注意的是我们如果定义好了ScaleDrawable，**要将其显示出来的话，必须给ScaleDrawable设置一个大于0小于10000的等级**（级别越大Drawable显示得越大，等级为10000时就没有缩放效果了），否则将无法正常显示。其语法为：

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

**android:scaleGravity**

​	当图片小于容器尺寸时，设置此选项可以对图片经典定位。

|      可选项       | 含义                                               |
| :---------------: | -------------------------------------------------- |
|        top        | 将图片放在容器顶部，不改变图片大小                 |
|      bottom       | 将图片放在容器底部，不改变图片大小                 |
|       left        | 将图片放在容器左侧，不改变图片大小                 |
|       right       | 将图片放在容器右侧，不改变图片大小                 |
|  center_vertical  | 图片竖直居中，不改变图片大小                       |
|   fill_vertical   | 图片竖直方向填充容器                               |
| center_horizontal | 图片水平居中，不改变图片大小                       |
|  fill_horizontal  | 图片水平方向填充容器                               |
|      center       | 使图片在水平方向和竖直方向同时居中，不改变图片大小 |
|       fill        | 图片填充容器，默认值                               |
|   clip_vertical   | 竖直方向剪切，很少使用                             |
|  clip_horizontal  | 水平方向剪切，很少使用                             |

**android:scaleHeight**

​	表示Drawable的高的缩放比例，值越大，内部Drawable的高度显示得越小。

**android:scaleWidth** 

​	表示Drawable的宽的缩放比例，值越大，内部Drawable的宽显示得越小。

示例：

```xml
<?xml version="1.0" encoding="utf-8"?>
<scale xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/image1"
    android:scaleHeight="70%"
    android:scaleWidth="70%"
    android:scaleGravity="center"
    >
</scale>
```

注意

​	在 java 调用设置 level（默认为0）的代码才可以正常显示

```java
ImageView scaleImage= (ImageView) findViewById(R.id.scaleImage);
ScaleDrawable scale= (ScaleDrawable) scaleImage.getBackground();
scale.setLevel(1);
```

效果如下

![](.\other\drawable_scale.png)



## ClipDrawable

ClipDrawable是通过设置一个Drawable的当前显示比例来裁剪出另一张Drawable，我们可以通过调节这个比例来控制裁剪的宽高，以及裁剪内容占整个View的权重，通过ClipDrawable的setLevel()方法控制显示比例，ClipDrawable的level值范围在[0,10000]，level的值越大裁剪的内容越少，当level为10000时则完全显示，而0表示完全裁剪，不可见。需要注意的是在给clip元素中android:drawable属性设置背景图片时，图片不能是9图，因为这涉及到裁剪这张图片，如果设置为9图，裁剪的实际情况会与想要的效果不一样。ClipDrawable对应xml的\<clip\>标签，其语法如下：

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

android:clipOrientation和android:gravity属性共同控制Drawable被裁剪的方向，其中clipOrientation表示裁剪的方向（水平和垂直两种），gravity比较复杂必须和clipOrientation一起才能起作用，同样的我们可以通过“|”来组合使用gravity的属性值。gravity属性值说明如下：

|      属性值       | 描述                                                         |
| :---------------: | ------------------------------------------------------------ |
|        top        | 将这个对象放在容器的顶部，不改变其大小。当clipOrientation 是”vertical”，裁剪从底部开始 |
|      bottom       | 将这个对象放在容器的底部，不改变其大小。当clipOrientation 是 “vertical”，裁剪从顶部（top）开始 |
|       left        | 将这个对象放在容器的左部，不改变其大小。当clipOrientation 是 “horizontal”，裁剪从drawable的右边（right）开始，默认值 |
|       right       | 将这个对象放在容器的右部，不改变其大小。当clipOrientation 是 “horizontal”，裁剪从drawable的左边（left）开始 |
|  enter_vertical   | 将对象放在垂直中间，不改变其大小，如果clipOrientation 是 “vertical”，那么从上下同时开始裁剪 |
|   fill_vertical   | 垂直方向上不发生裁剪。（除非drawable的level是 0，才会不可见，表示全部裁剪完） |
| center_horizontal | 将对象放在水平中间，不改变其大小，clipOrientation 是 “horizontal”，那么从左右两边开始裁剪 |
|  fill_horizontal  | 水平方向上不发生裁剪。（除非drawable的level是 0，才会不可见，表示全部裁剪完） |
|      center       | 将这个对象放在水平垂直坐标的中间，不改变其大小。当clipOrientation 是 “horizontal”裁剪发生在左右。当clipOrientation是”vertical”,裁剪发生在上下。 |
|       fill        | 填充整个容器，不会发生裁剪。(除非drawable的level是 0，才会不可见，表示全部裁剪完)。 |
|   clip_vertical   | 附加选项，表示竖直方向的裁剪，很少使用                       |
|  clip_horizontal  | 附加选项，表示水平方向的裁剪，很少使用                       |

示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<clip xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/image1"
    android:clipOrientation="horizontal"
    android:gravity="right"
    >
</clip>
```

应用到ImageView上代码如下：

```xml
<ImageView                                             
    android:id="@+id/clipImage"                        
    android:layout_width="wrap_content"                
    android:layout_height="wrap_content"               
    android:layout_centerInParent="true"               
    android:background="@drawable/clip_drawable"       
    />                                                 
```

接着代码中设置ClipDrawable的等级，代码如下：

```xml
ImageView clipImage= (ImageView) findViewById(R.id.clipImage);
ClipDrawable clip= (ClipDrawable) clipImage.getBackground();
clip.setLevel(6000);
```

在xml引用时设置的是背景图所以使用`clipImage.getBackground()`，如果在xml引用时使用的是src，那么就使用`clipImage.getDrawable()`即可。

效果如下

![](.\other\drawable_clip.png)



## ColorDrawable

ColorDrawable 是最简单的Drawable，它实际上是代表了单色可绘制区域，它包装了一种固定的颜色，当ColorDrawable被绘制到画布的时候会使用颜色填充Paint，在画布上绘制一块单色的区域。 在xml文件中对应**\<color\>**标签，它只有一个android:color属性，通过它来决定ColorDrawable的颜色。 实现如下：

```xml
<?xmlversion="1.0" encoding="utf-8"?>
<color xmlns:android="http://schemas.android.com/apk/res/android"
  android:color="@color/normal"
/>
```

也可以使用代码实现，注意传入的颜色值为 16进制数字

```java
ColorDrawable cd = new ColorDrawable(0xff000000);
ImageView iv = (ImageView)findViewById(...);
iv.setImageDrawable(cd);
```



## GradientDrawable

GradientDrawable 表示一个渐变区域，可以实现线性渐变、发散渐变和平铺渐变效果。

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle"
    >

    <gradient android:angle="90"
        android:startColor="@color/colorPrimary"
        android:centerColor="#fff"
        android:endColor="@color/color_state"
        android:type="linear"
        />
</shape>
```

GradientDrawable也可以作为 View的背景图，代码实现如下：

```java
//分别为开始颜色，中间夜色，结束颜色
int colors[] = { 0xff255779 , 0xff3e7492, 0xffa6c0cd };
GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
```

最后设置给View的背景图即可

```java
setBackgroundDrawable(gd);
```

