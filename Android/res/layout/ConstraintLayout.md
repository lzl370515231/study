`ConstraintLayout` 是一种布局，它根据同级视图和父布局的约束条件来定义每个视图的位置。这样一来，使用扁平视图层次结构既可以创建简单布局，又可以创建复杂布局。也就是说，不再需要嵌套布局（即布局内的布局，如图 2 中所示），也就不会增加绘制界面所需的时间了。

![](.\png\ConstraintLayout_1.png)

可以声明：

- 视图 A 距离父布局顶部 16dp
- 视图 A 距离父布局左侧 16dp
- 视图 B 距离视图 A 右侧 16dp
- 视图 B 与视图 A 顶部对齐



按照到文本框右侧的距离以及文本框基线来约束按钮

1. 在 **Palette** 中，点击 **Buttons**
2. 将 **Button** 拖动到设计编辑器中，并将其放在靠近右侧的位置
3. 建一个从按钮左侧到文本框右侧的约束条件
4. 要按水平对齐约束视图，您需要创建一个文本基线之间的约束条件。因此，请点击按钮，然后点击 **Edit Baseline** 图标![](.\png\ConstraintLayout_2.png)，该图标显示在设计编辑器中选定视图的正下方。基线锚点显示在按钮内部。点击并按住此锚点，然后将其拖动到文本框中显示的基线锚点。

结果如下图所示：

![](.\png\ConstraintLayout_3.png)





### 详细操作

[Android新特性介绍，ConstraintLayout完全解析](https://blog.csdn.net/guolin_blog/article/details/53122387)



### 属性

```xml
<ImageView
        android:id="@+id/menu_logo"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:layout_centerInParent="true"
        android:src="@mipmap/pic_1"
        app:layout_constraintBottom_toTopOf="@+id/guideline9"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@+id/guideline8"/>

<android.support.constraint.Guideline
        android:id="@+id/guideline"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        app:layout_constraintGuide_percent="0.5"/>
```

#### 相对相位

在ConstraintLayout中，可以相对于一个其它的组件，而定位当前组件。约束一个组件的横轴和纵轴，相关的属性有：

- 横轴： Left, Right, Start and End
- 纵轴：Top, Bottom

##### 属性规则

layout_constraint *[source]* _to *[target]* Of = "A"

source 和 target 都表示某个方向

= 后面接的就是当前控件 B 引做参考的控件 A

**当前控件B 的 source 方向 相对于引用控件 A 的 target 方向，进行约束**

##### 示例

**居中**

```xml
app:layout_constraintBottom_toBottomOf="parent"
app:layout_constraintLeft_toLeftOf="parent"
app:layout_constraintRight_toRightOf="parent"
app:layout_constraintTop_toTopOf="parent"
```

> 上面定位的含义是：
> 当前控件的 Bottom ，相对于 parent 的Bottom进行约束；
> 当前控件的 Left ，相对于 parent 的Left进行约束；
> 当前控件的 Right ，相对于 parent 的Right进行约束；
> 当前控件的 Top ，相对于 parent 的Top进行约束；
> **所以，当前控件就相对parent 进行居中了**

#### 边距

##### gone margins

在约束布局中除了之前的常规边距margins外，还新加了gone margins 。
gone margins 的意义在于 ，当位置约束的相对控件的可见性为：View.GONE时，设置的边距依旧可用。

```xml
layout_goneMarginStart
layout_goneMarginEnd
layout_goneMarginLeft
layout_goneMarginTop
layout_goneMarginRight
layout_goneMarginBottom
```

###### 具体区别：（在C中设置边距属性，相对的控件是B ，B的相邻控件是A）


>**android:layout_margin**
>当控件B ,View.visible 或者 View.invisible时，设置的数值 相对于控件B有效；
>当控件B ,View.Gone时，设置的数值，就相对于控件A有效了。

>**layout_goneMargin**
>当控件B ,View.visible 或者 View.invisible时，设置的数值，看不出任何效果 ，即边距无效；
>当控件B ,View.Gone时，设置的数值，就相对于控件A有效了

**区别在于，goneMargin，只有当目标控件View.GONE时才会触发**

#### 偏移率(bias)

```xml
app:layout_constraintHorizontal_bias="0.1"
app:layout_constraintVertical_bias="0.8"
```

>Horizontal_bias (横向偏移) 的取值范围[0.0, 1.0]，方向从左向右
>Vertical_bias (纵向偏移) 的取值范围[0.0, 1.0]，方向从上向下

#### Guideline，引导线

使用Guideline为相对组件时，以帮助定位组件。
Guideline主要属性有：

```xml
android:orientation    //方向
layout_constraintGuide_begin  //引导线距顶部或左边框的距离
layout_constraintGuide_end   //引导线距底部或右边框的距离
layout_constraintGuide_percent   //所占百分比
```

