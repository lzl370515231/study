Animation

## View Animation（视图动画）

视图动画(也叫补间动画)允许在一个视图容器里执行一系列简单变换, 它只能用于视图。它是以整个视图为对象, 将帧与帧"之间"的过渡给"补齐"的动画. 它通过动画的起点/终点/尺寸/旋转以及其他常见方面来计算动画. 我们可以用一系列的动画指令(anima instruction)来定义补间动画, XML或者Android代码都可以。



### 特点

​	只能用于视图，只能操作View，**不会改变View的属性**。	

### 分类

​	位移（translate）

​	缩放（scale）

​	旋转（rotate）

​	淡入淡出（alpha）



### 属性详解

|         xml属性         | java方法                      | 解释                                                         |
| :---------------------: | ----------------------------- | ------------------------------------------------------------ |
| android:detachWallpaper | setDetachWallpaper(boolean)   | 是否在壁纸上运行                                             |
|    android:duration     | setDuration(long)             | 动画持续时间，毫秒为单位                                     |
|    android:fillAfter    | setFillAfter(boolean)         | 控件动画结束时是否保持动画最后的状态                         |
|   android:fillBefore    | setFillBefore(boolean)        | 控件动画结束时是否还原到开始动画前的状态                     |
|   android:fillEnabled   | setFillEnabled(boolean)       | 与android:fillBefore效果相同                                 |
|  android:interpolator   | setInterpolator(Interpolator) | 设定插值器（指定的动画效果，譬如回弹等）                     |
|   android:repeatCount   | setRepeatCount(int)           | 重复次数                                                     |
|   android:repeatMode    | setRepeatMode(int)            | 重复类型有两个值，reverse表示倒序回放，restart表示从头播放   |
|   android:startOffset   | setStartOffset(long)          | 调用start函数之后等待开始运行的时间，单位为毫秒              |
|   android:zAdjustment   | setZAdjustment(int)           | 表示被设置动画的内容运行时在Z轴上的位置（top/bottom/normal），默认为normal |

#### Alpha属性详解

|      xml属性      | java方法                           | 解释                                                   |
| :---------------: | ---------------------------------- | ------------------------------------------------------ |
| android:fromAlpha | AlphaAnimation(float fromAlpha, …) | 动画开始的透明度（0.0到1.0，0.0是全透明，1.0是不透明） |
|  android:toAlpha  | AlphaAnimation(…, float toAlpha)   | 动画结束的透明度，同上                                 |

#### Rotate属性详解

|       xml属性       | java方法                               | 解释                                                         |
| :-----------------: | -------------------------------------- | ------------------------------------------------------------ |
| android:fromDegrees | RotateAnimation(float fromDegrees, …)  | 旋转开始角度，正代表顺时针度数，负代表逆时针度数             |
|  android:toDegrees  | RotateAnimation(…, float toDegrees, …) | 旋转结束角度，正代表顺时针度数，负代表逆时针度数             |
|   android:pivotX    | RotateAnimation(...,float pivotX,...)  | 缩放起点X坐标（数值、百分数、百分数p，譬如50表示以当前View左上角坐标加50px为初始点、50%表示以当前View的左上角加上当前View宽高的50%做为初始点、50%p表示以当前View的左上角加上父控件宽高的50%做为初始点） |
|   android:pivotY    | RotateAnimation(...,float pivotY)      | 缩放起点Y坐标，同上规律                                      |

#### Translate属性详解

|      xml属性       | java方法                                   | 解释                                                         |
| :----------------: | ------------------------------------------ | ------------------------------------------------------------ |
| android:fromXDelta | TranslateAnimation(float fromXDelta, …)    | 起始点X轴坐标（数值、百分数、百分数p，譬如50表示以当前View左上角坐标加50px为初始点、50%表示以当前View的左上角加上当前View宽高的50%做为初始点、50%p表示以当前View的左上角加上父控件宽高的50%做为初始点） |
| android:fromYDelta | TranslateAnimation(…, float fromYDelta, …) | 起始点Y轴从标，同上规律                                      |
|  android:toXDelta  | TranslateAnimation(…, float toXDelta, …)   | 结束点X轴坐标，同上规律                                      |
|  android:toYDelta  | TranslateAnimation(…, float toYDelta)      | 结束点Y轴坐标，同上规律                                      |

#### Scale属性详解

|      xml属性       | java方法                           | 解释                                                         |
| :----------------: | ---------------------------------- | ------------------------------------------------------------ |
| android:fromXScale | ScaleAnimation(float fromX, …)     | 初始X轴缩放比例，1.0表示无变化                               |
|  android:toXScale  | ScaleAnimation(…, float toX, …)    | 结束X轴缩放比例                                              |
| android:fromYScale | ScaleAnimation(…, float fromY, …)  | 初始Y轴缩放比例                                              |
|  android:toYScale  | ScaleAnimation(…, float toY, …)    | 结束Y轴缩放比例                                              |
|   android:pivotX   | ScaleAnimation(…, float pivotX, …) | 缩放起点X坐标（数值、百分数、百分数p，譬如50表示以当前View左上角坐标加50px为初始点、50%表示以当前View的左上角加上当前View宽高的50%做为初始点、50%p表示以当前View的左上角加上父控件宽高的50%做为初始点） |
|   android:pivotY   | ScaleAnimation(…, float pivotY)    | 缩放起点Y坐标，同上规律                                      |

##### xml文件中定义

xml定义动画

```xml
<!-- 以下代码用于拉伸和旋转一个View对象 -->
<set android:shareInterpolator="false">
    <scale
        android:interpolator="@android:anim/accelerate_decelerate_interpolator"
        android:fromXScale="1.0"
        android:toXScale="1.4"
        android:fromYScale="1.0"
        android:toYScale="0.6"
        android:pivotX="50%"
        android:pivotY="50%"
        android:fillAfter="false"
        android:duration="700" />
    <set android:interpolator="@android:anim/decelerate_interpolator">
        <scale
           android:fromXScale="1.4"
           android:toXScale="0.0"
           android:fromYScale="0.6"
           android:toYScale="0.0"
           android:pivotX="50%"
           android:pivotY="50%"
           android:startOffset="700"
           android:duration="400"
           android:fillBefore="false" />
        <rotate
           android:fromDegrees="0"
           android:toDegrees="-45"
           android:toYScale="0.0"
           android:pivotX="50%"
           android:pivotY="50%"
           android:startOffset="700"
           android:duration="400" />
    </set>
</set>
```

java中使用动画资源

```java
Animation animation = AnimationUtils.loadAnimation(context,R.anim.view_anin);
image.startAnimation(animation);
```

##### java文件中定义

```java
RotateAnimation rotateAnimation = new RotateAnimation(0,180);
rotateAnimation.setDuration(400);
image.startAnimation(rotateAnimation);
```

### Animation方法

|                Animation类的方法                 | 解释                        |
| :----------------------------------------------: | --------------------------- |
|                     reset()                      | 重置Animation的初始化       |
|                     cancel()                     | 取消Animation动画           |
|                     start()                      | 开始Animation动画           |
| setAnimationListener(AnimationListener listener) | 给当前Animation设置动画监听 |
|                   hasStarted()                   | 判断当前Animation是否开始   |
|                    hasEnded()                    | 判断当前Animation是否结束   |

#### View和动画相关的方法

|      View类的常用动画操作方法       | 解释                              |
| :---------------------------------: | --------------------------------- |
| startAnimation(Animation animation) | 对当前View开始设置的Animation动画 |
|          clearAnimation()           | 取消当View在执行的Animation动画   |

## Drawable Animation(帧动画)

它通过加载图片资源，将多张图片一帧一帧显示形成动画效果。

### xml定义资源文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot="false">
    <item android:drawable="@drawable/loading_1" android:duration="100"/>
    <item android:drawable="@drawable/loading_2" android:duration="100"/>
    <item android:drawable="@drawable/loading_3" android:duration="100"/>
    <item android:drawable="@drawable/loading_4" android:duration="100"/>
    <item android:drawable="@drawable/loading_5" android:duration="100"/>
    <item android:drawable="@drawable/loading_6" android:duration="100"/>
    <item android:drawable="@drawable/loading_7" android:duration="100"/>
    <item android:drawable="@drawable/loading_8" android:duration="100"/>
    <item android:drawable="@drawable/loading_9" android:duration="100"/>
    <item android:drawable="@drawable/loading_10" android:duration="100"/>
    <item android:drawable="@drawable/loading_11" android:duration="100"/>
    <item android:drawable="@drawable/loading_12" android:duration="100"/>

</animation-list>
```

代码中使用

```java
// 通过逐帧动画的资源文件获得AnimationDrawable示例
AnimationDrawable animationDrawable =(AnimationDrawable)getResources().getDrawable(R.drawable.animation_loading);
image.setImageDrawable(animationDrawable);
animationDrawable.start();
```



### java代码创建

```java
AnimationDrawable drawable = new AnimationDrawable();
drawable.addFrame(getResources().getDrawable(R.drawable.loading_1),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_2),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_3),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_4),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_5),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_6),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_7),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_8),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_9),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_10),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_11),100);
drawable.addFrame(getResources().getDrawable(R.drawable.loading_12),100);
drawable.setOneShot(true);
image.setImageDrawable(drawable);
drawable.start();
```

### 常用方法

|                    方法                    | 作用                                                         |
| :----------------------------------------: | ------------------------------------------------------------ |
|                void start()                | 开始播放逐帧动画                                             |
|                void stop()                 | 停止播放逐帧动画                                             |
| void addFrame(Drawable frame,int duration) | 为AnimationDrawable添加一帧，并设置持续时间                  |
|           int getDuration(int i)           | 得到指定index的帧的持续时间                                  |
|          Drawable getFrame(int i)          | 得到指定index 的帧Drawable                                   |
|          int getNumberOfFrames()           | 得到当前AnimationDrawable的所有帧数量                        |
|            boolean isOneShot()             | 当前AnimationDrawable是否执行一次，返回true执行一次，false循环播放。 |
|            boolean isRunning()             | 当前AnimationDrawable是否正在播放                            |
|      void setOneShot(boolean oneShot)      | 设置AnimationDrawable是否执行一次，true执行一次，false循环播放 |

## 动画Interpolator插值器

#### 插值器原理

根据时间流逝的百分比来计算出当前属性值改变的百分比。

#### 常见的插值器

|              java类              | xml id值                                         | 描述                                                 |
| :------------------------------: | ------------------------------------------------ | ---------------------------------------------------- |
| AccelerateDecelerateInterpolator | @android:anim/accelerate_decelerate_interpolator | 动画始末速率较慢，中间加速                           |
|      AccelerateInterpolator      | @android:anim/accelerate_interpolator            | 动画开始速率较慢，之后慢慢加速                       |
|      AnticipateInterpolator      | @android:anim/anticipate_interpolator            | 开始的时候从后向前甩                                 |
| AnticipateOvershootInterpolator  | @android:anim/anticipate_overshoot_interpolator  | 类似上面AnticipateInterpolator                       |
|        BounceInterpolator        | @android:anim/bounce_interpolator                | 动画结束时弹起                                       |
|        CycleInterpolator         | @android:anim/cycle_interpolator                 | 循环播放速率改变为正弦曲线                           |
|      DecelerateInterpolator      | @android:anim/decelerate_interpolator            | 动画开始快然后慢                                     |
|        LinearInterpolator        | @android:anim/linear_interpolator                | 动画匀速改变                                         |
|      OvershootInterpolator       | @android:anim/overshoot_interpolator             | 向前弹出一定值之后回到原来位置                       |
|         PathInterpolator         |                                                  | 新增，定义路径坐标后按照路径坐标来跑。               |
|         TimeInterpolator         |                                                  | 一个允许自定义Interpolator的接口，以上都实现了该接口 |

#### 插值器使用方法

```xml
<set android:interpolator="@android:anim/accelerate_interpolator">
    ...
</set>
```

#### 插值器的自定义

##### xml自定义

修改你准备自定义的插值器：

```xml
<?xml version="1.0" encoding="utf-8"?>
<InterpolatorName xmlns:android="http://schemas.android.com/apk/res/android"
    android:attribute_name="value"/>
```

在你的补间动画文件中引用该文件即可。

**有些插值器却不具备修改属性**，具体如下：

**\<accelerateDecelerateInterpolator\>**

​	无可自定义的attribute。

**\<accelerateInterpolator\>**

​	android:factor 浮点值，加速速率（默认值为1）。

**\<anticipateInterploator\>**

​	android:tension 浮点值，起始点后拉的张力数（默认值为2）。

**\<anticipateOvershootInterpolator\>**

​	android:tension 浮点值，起始点后拉的张力数（默认值为2）。 
​	android:extraTension 浮点值，拉力的倍数（默认值为1.5）。

**\<bounceInterpolator\>**

​	无可自定义的attribute。

**\<cycleInterplolator\>**

​	android:cycles 整形，循环的个数（默认为1）。

**\<decelerateInterpolator\>**

​	android:factor 浮点值，减速的速率（默认为1）。

**\<linearInterpolator\>**

​	无可自定义的attribute。

**\<overshootInterpolator\>**

​	android:tension 浮点值，超出终点后的张力（默认为2）



##### java代码实现

​	Java自定义插值器其实是xml自定义的升级，也就是说如果我们修改xml的属性还不能满足需求，那就可以选择通过Java来实现。

![](E:/lzl/CommonToolTestDemo/commonviews/src/main/res/other/animation_interpolator.png)

所有的Interpolator都实现了Interpolator接口，而Interpolator接口又继承自TimeInterpolator，TimeInterpolator接口定义了一个float **getInterpolation(float input)**;方法，这个方法是由系统调用的，其中的参数input代表动画的时间，在0和1之间，也就是开始和结束之间。

```java
float getInterpolation(float input);
```

这个方法的返回值就是**当前属性值改变的百分比**，也可以理解为前面所说的速度快慢，而参数input则表示**时间流逝的百分比**。

**示例：**

如下定义一个动画始末速率较慢、中间加速的AccelerateDecelerateInterpolator插值器

```java
public class AccelerateDecelerateInterpolator extends BaseInterpolator
        implements NativeInterpolatorFactory {
    ......
    public float getInterpolation(float input) {
        return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
    }
    ......
}
```

##### java中使用

```java
TranslateAnimation rotateAnimation = new TranslateAnimation(0,200,0,200);
rotateAnimation.setDuration(400);
//创建对于的插值器
OvershootInterpolator interpolator = new OvershootInterpolator();
//设置插值器
rotateAnimation.setInterpolator(interpolator);
image.startAnimation(rotateAnimation);
```

##### 注意

补间动画实现 Interpolator 接口

属性动画实现 TimeInterpolator 接口

## Property Animation（属性动画）

属性动画只对**Android 3.0（API 11）**以上版本的Android系统才有效，这种动画可以设置给**任何Object**，包括那些还没有渲染到屏幕上的对象。属性动画实现原理就是修改控件的属性值实现的动画。

### 属性动画原理

属性动画内部会根据我们传递的属性初始值和最终值，然后**多次去调用set方法（内部自动调用）**，每次传递给set方法的值都是变化的，也就是说随着时间的推移，所传递的值越来越接近最终值，在这个过程中也就形成了动画效果。

### 常用的View 属性

**translationX，translationY**：

​	控制View的位置，值是相对于View容器左上角坐标的偏移

**rotationX，rotationY**：

​	控制相对于轴心旋转

**x，y**：

​	控制View在容器中的位置，即左上角坐标加上translationX和translationY的值

**alpha**：

​	控制View对象的alpha透明度值

**scaleX与scaleY**:

​	控制View对象围绕它的支点进行2D缩放。

**pivotX与pivotY**:

​	两个属性控制着View对象的支点位置，包含旋转和缩放都围绕这个支点变换和处理。 默认值为View对象的中心。

### 属性动画分类

​	ValueAnimator

​	ObjectAnimator

### 类关系图

![](.\other\Animator_class.png)

|    java类名    | xml关键字                                    | 描述信息                       |
| :------------: | -------------------------------------------- | ------------------------------ |
| ValueAnimator  | `<animator>` 放置在res/animator/目录下       | 在一个特定的时间里执行一个动画 |
|  TimeAnimator  | 不支持                                       | 时序监听回调工具               |
| ObjectAnimator | `<objectAnimator>` 放置在res/animator/目录下 | 一个对象的一个属性动画         |
|  AnimatorSet   | `<set>` 放置在res/animator/目录下            | 动画集合                       |

### Android 属性动画的属性

**Duration**

​	动画的持续时间。

**TimeInterpolation**

​	定义动画变化速率的接口，所有插值器都必须实现此接口，如线性、非线性插值器。

**TypeEvaluator**

​	用于定义属性值计算方式的接口，有int、float、color类型，根据属性的起始、结束值和插值一起计算出当前时间的属性值。

**Animation sets**

​	动画集合，即可以同时对一个对象应用多个动画，这些动画可以同时播放也可以对不同动画设置不同的延迟。

**Frame refreash delay**

​	多少时间刷新一次，即每隔多少时间计算一次属性值，默认为10ms，最终刷新时间还受系统进程调度与硬件的影响。

**Repeat Country and behavoir**

​	重复次数与方式，如播放3次、5次、无限循环，可以让此动画一直重复，或播放完时向反向播放。



### 属性动画如何计算动画值

![](.\other\Animation_calculate.png)

其中的ValueAnimator是动画的执行类，跟踪了当前动画的**执行时间**和当前时间下的**属性值**；**ValueAnimator封装了动画的TimeInterpolator时间插值器和一个TypeEvaluator类型估值**，用于设置动画属性的值。如非线性动画里，TimeInterpolator使用了AccelerateDecelerateInterpolator、TypeEvaluator使用了IntEvaluator。

根据时间计算插值分数

```java
public class AccelerateDecelerateInterpolator extends BaseInterpolator
        implements NativeInterpolatorFactory {
    public AccelerateDecelerateInterpolator() {
    }
    ......
    //这是我们关注重点，可以发现如下计算公式计算后（input即为时间因子）插值大约为0.15。
    public float getInterpolation(float input) {
        return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
    }
    ......
}
```

接着ValueAnimator会根据插值分数调用合适的TypeEvaluator（IntEvaluator）去计算运动中的属性值。

```java
public class IntEvaluator implements TypeEvaluator<Integer> {
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
       	//属性值：0+0.15*（40-0）=6
        return (int)(startInt + fraction * (endValue - startInt));
    }
}
```



### 属性动画的实现

#### xml方式

​	在xml中可直接用的属性动画节点有ValueAnimator、ObjectAnimator、AnimatorSet。其中AnimatorSet对应\<set\>标签，ObjectAnimator对应\<objectAnimator\>标签，ValueAnimator则对应\<animator\>标签。属性动画的xml文件应保存在 res/animator/ 目录下。

示例

```xml
<set
  android:ordering=["together" | "sequentially"]>
    
    <objectAnimator
        android:propertyName="string"
        android:duration="int"
        android:valueFrom="float | int | color"
        android:valueTo="float | int | color"
        android:startOffset="int"
        android:repeatCount="int"
        android:repeatMode=["repeat" | "reverse"]
        android:valueType=["intType" | "floatType"]/>

    <animator
        android:duration="int"
        android:valueFrom="float | int | color"
        android:valueTo="float | int | color"
        android:startOffset="int"
        android:repeatCount="int"
        android:repeatMode=["repeat" | "reverse"]
        android:valueType=["intType" | "floatType"]/>

    <set>
        ...
    </set>
</set>
```

##### 属性解释

\<set\>

|     xml属性      | 解释                                                         |
| :--------------: | ------------------------------------------------------------ |
| android:ordering | 控制子动画启动方式是先后有序的还是同时进行。sequentially:动画按照先后顺序；together(默认):动画同时启动 |

\<objectAnimator\>

|       xml属性        | 解释                                                         |
| :------------------: | ------------------------------------------------------------ |
| android:propertyName | String类型，必须要设置的节点属性，代表要执行动画的属性（通过名字引用），辟如你可以指定了一个View的”alpha” 或者 “backgroundColor” ，这个objectAnimator元素没有对外说明target属性，所以你不能在XML中设置执行这个动画，必须通过调用loadAnimator()方法加载你的XML动画资源，然后调用setTarget()应用到具备这个属性的目标对象上（譬如TextView） |
|   android:valueTo    | float、int或者color类型，必须要设置的节点属性，表明动画结束的点；如果是颜色的话，由6位十六进制的数字表示。 |
|  android:valueFrom   | 相对应valueTo，动画的起始点，如果没有指定，系统会通过属性的get方法获取，颜色也是6位十六进制的数字表示 |
|   android:duration   | 动画的时长，int类型，以毫秒为单位，默认为300毫秒。           |
| android:startOffset  | 动画延迟的时间，从调用start方法后开始计算，int型，毫秒为单位。 |
| android:repeatCount  | 一个动画的重复次数，int型，”-1“表示无限循环，”1“表示动画在第一次执行完成后重复执行一次，也就是两次，默认为0，不重复执行 |
|  android:repeatMode  | 重复模式：int型，当一个动画执行完的时候应该如何处理。该值必须是正数或者是-1，“reverse”会使得按照动画向相反的方向执行，可实现类似钟摆效果。“repeat”会使得动画每次都从头开始循环 |
|  android:valueType   | 关键参数，如果该value是一个颜色，那么就不需要指定，因为动画框架会自动的处理颜色值。有intType和floatType（默认）两种：分别说明动画值为int和float型 |

**\<animator\>标签和\<objectAnimator\>标签相比，\<animator\>标签只少了一个 android:propertyName属性**

##### xml属性动画使用方法

```java
AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(myContext,R.animtor.property_animator);
set.setTarget(myObject);
set.start();
```

#### java方式

##### ObjectAnimator

继承自ValueAnimator，允许你指定要进行动画的对象以及该对象的一个属性。**该类会根据计算得到的新值自动更新属性**。大多数的情况使用ObjectAnimator就足够了，因为它使得目标对象动画值的处理过程变得足够简单，不用像ValueAnimator那样自己写动画更新的逻辑，但是**ObjectAnimator有一定的限制，比如它需要目标对象的属性提供指定的处理方法（譬如提供getXXX，setXXX方法）**，这时候你就需要根据自己的需求在ObjectAnimator和ValueAnimator中看哪种实现更方便了。

ObjectAnimator类提供了ofInt、ofFloat、ofObject这个三个常用的方法，这些方法都是设置动画作用的元素、属性、开始、结束等任意属性值。当属性值（上面方法的参数）只设置一个时就把通过getXXX反射获取的值作为起点，设置的值作为终点；如果设置两个（参数），那么一个是开始、另一个是结束。

###### 特别注意

​	**ObjectAnimator的动画原理是不停的调用setXXX方法更新属性值**，所有**使用ObjectAnimator更新属性时的前提是Object必须声明有getXXX和setXXX方法**。

###### 使用前提

1. 属性的get 和 set 方法。（Button 的setWidth() 操作的不是 Button 的width，**特别注意**）
2. 此属性会带来ui的改变

###### 手动重绘

我们通常使用ObjectAnimator设置View已知的属性来生成动画，而一般View已知属性变化时都会主动触发重绘图操作，所以动画会自动实现；但是也有特殊情况，譬如作用Object不是View，或者作用的属性没有触发重绘，或者我们在重绘时需要做自己的操作。可通过如下方法手动设置：

```java
ObjectAnimator mObjectAnimator= ObjectAnimator.ofInt(view, "customerDefineAnyThingName", 0,  1).setDuration(2000);
mObjectAnimator.addUpdateListener(new AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                //int value = animation.getAnimatedValue();  可以获取当前属性值
                //view.postInvalidate();  可以主动刷新
                //view.setXXX(value);
                //view.setXXX(value);
                //......可以批量修改属性
            }
        });
```

###### 示例

```java
ObjectAnimator.ofFloat(view, "rotationY", 0.0f, 360.0f).setDuration(1000).start();
```

##### PropertyValuesHolder

多属性动画同时工作管理类。有时候我们需要同时修改多个属性，那就可以用到此类。具体如下：

```java
PropertyValuesHolder a1 = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);  
PropertyValuesHolder a2 = PropertyValuesHolder.ofFloat("translationY", 0, viewWidth);  
......
ObjectAnimator.ofPropertyValuesHolder(view, a1, a2, ......).setDuration(1000).start();
```

##### ValueAnimator

属性动画中的时间驱动，管理着动画时间的开始、结束属性值，相应时间属性值计算方法等。包含所有计算动画值的核心函数以及每一个动画时间节点上的信息、一个动画是否重复、是否监听更新事件等，并且还可以设置自定义的计算类型。

###### 特别注意：

​	ValueAnimator只是动画计算管理驱动，设置了作用目标，但没有设置属性，**需要通过updateListener里设置属性才会生效**。

```java
ValueAnimator animator = ValueAnimator.ofFloat(0, mContentHeight);  //定义动画
animator.setTarget(view);   //设置作用目标
animator.setDuration(5000).start();
animator.addUpdateListener(new AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation){
        float value = (float) animation.getAnimatedValue();
        view.setXXX(value);  //必须通过这里设置属性值才有效
        view.mXXX = value;  //不需要setXXX属性方法
    }
});
```

正是由于ValueAnimator不直接操作属性值，所以要操作对象的属性可以不需要setXXX与getXXX方法，你完全可以通过当前动画的计算去修改任何属性。

##### AnimationSet

动画集合，提供把多个动画组合成一个组合的机制，并可设置动画的时序关系，如同时播放、顺序播放或延迟播放。方法有：

|               方法                | 作用                                                         |
| :-------------------------------: | ------------------------------------------------------------ |
|       after(Animator anim)        | 将现有动画插入到传入的动画之后执行                           |
|         after(long delay)         | 将现有动画延迟指定毫秒后执行                                 |
|       before(Animator anim)       | 将现有动画插入到传入的动画之前执行                           |
|        with(Animator anim)        | 将现有动画和传入的动画同时执行                               |
|   playTogether(Animator… items)   | items表示每个子动画，该方法表示同时执行集合动画中的所有动画  |
| playSequentially(Animator… items) | items表示每个子动画，该方法表示按顺序执行集合动画中的所有动画 |
|        play(Animator anim)        | 向这个方法中传入一个Animator对象(ValueAnimator或ObjectAnimator) 将会返回一个AnimatorSet.Builder的实例，并会执行这个动画 |

使用方式如下：

```java
ObjectAnimator a1 = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0f);  
ObjectAnimator a2 = ObjectAnimator.ofFloat(view, "translationY", 0f, viewWidth);  
......
AnimatorSet animSet = new AnimatorSet();  
animSet.setDuration(5000);  
animSet.setInterpolator(new LinearInterpolator());   
//animSet.playTogether(a1, a2, ...); //两个动画同时执行  
animSet.play(a1).after(a2); //先后执行
......//其他组合方式
animSet.start();  
```

##### Evaluators（估值器）

Evaluators就是属性动画系统如何去计算一个属性值。它们通过Animator提供的动画的起始和结束值去计算一个动画的属性值。

**IntEvaluator**：整数属性值

**FloatEvaluator**：浮点数属性值

**ArgbEvaluator**：十六进制color属性值

**TypeEvaluator**：用户自定义属性值接口，譬如对象属性值类型不是int、float、color类型，你必须实现这个接口去定义自己的数据类型。

```java
ValueAnimator valueAnimator = new ValueAnimator();
valueAnimator.setDuration(5000);
valueAnimator.setObjectValues(new float[2]); //设置属性值类型
valueAnimator.setInterpolator(new LinearInterpolator());
valueAnimator.setEvaluator(new TypeEvaluator<float[]>()
{
    @Override
    public float[] evaluate(float fraction, float[] startValue,
                            float[] endValue)
    {
        //实现自定义规则计算的float[]类型的属性值
        float[] temp = new float[2];
        temp[0] = fraction * 2;
        temp[1] = (float)Math.random() * 10 * fraction;
        return temp;
    }
});

valueAnimator.start();
valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
{
    @Override
    public void onAnimationUpdate(ValueAnimator animation)
    {
        float[] xyPos = (float[]) animation.getAnimatedValue();
        view.setHeight(xyPos[0]);   //通过属性值设置View属性动画
        view.setWidth(xyPos[1]);    //通过属性值设置View属性动画
    }
});
```

##### ViewPropertyAnimator

Android API 12(3.1)，View中添加了 animate方法，具体如下：

```java
public class View implements Drawable.Callback, KeyEvent.Callback,
        AccessibilityEventSource {
     ......
     /**
     * This method returns a ViewPropertyAnimator object, which can be used to animate
     * specific properties on this View.
     *
     * @return ViewPropertyAnimator The ViewPropertyAnimator associated with this View.
     */
    public ViewPropertyAnimator animate() {
        if (mAnimator == null) {
            mAnimator = new ViewPropertyAnimator(this);
        }
        return mAnimator;
    }
    ......
}
```

ViewPropertyAnimator提供了一种非常方便的方法为View的部分属性设置动画（切记，是部分属性），它可以直接使用一个Animator对象设置多个属性的动画；在多属性设置动画时，他会管理多个属性的invalidate方法统一调运触发，会有一些性能优化。

```java
myview.animate().x(0f).y(100f).start();
```

###### 优点

1. 专门针对View对象动画而操作的类
2. 提供了更简洁的链式调用设置多个属性动画，这些动画可以同时进行的
3. 拥有更好的性能，多个属性动画是一次同时变化，只执行一次UI刷新（也就是只调用一次invalidate,而n个ObjectAnimator就会进行n次属性变化，就有n次invalidate）
4. 每个属性提供两种类型方法设置
5. 该类只能通过View的animate()获取其实例对象的引用

###### 常用方法

| 方法                                                         | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| alpha(float value)                                           | 设置透明度，value表示变化到多少，1不透明，0全透明            |
| scaleY(float value)                                          | 设置Y轴方向的缩放大小，value表示缩放到多少。1表示正常规格。小于1代表缩小，大于1代表放大 |
| scaleX(float value)                                          | 设置X轴方向的缩放大小，value表示缩放到多少。1表示正常规格。小于1代表缩小，大于1代表放大 |
| translationY(float value)                                    | 设置Y轴方向的移动值，作为增量来控制View对象相对于它父容器的左上角坐标偏移的位置，即移动到哪里 |
| translationX(float value)                                    | 设置X轴方向的移动值，作为增量来控制View对象相对于它父容器的左上角坐标偏移的位置 |
| rotation(float value)                                        | 控制View对象围绕支点进行旋转， rotation针对2D旋转            |
| rotationX (float value)                                      | 控制View对象围绕X支点进行旋转， rotationX针对3D旋转          |
| rotationY(float value)                                       | 控制View对象围绕Y支点进行旋转， rotationY针对3D旋转          |
| x(float value)                                               | 控制View对象相对于它父容器的左上角坐标在X轴方向的最终位置    |
| y(float value)                                               | 控制View对象相对于它父容器的左上角坐标在Y轴方向的最终位置    |
| void cancel()                                                | 取消当前正在执行的动画                                       |
| setListener(Animator.AnimatorListener listener)              | 设置监听器，监听动画的开始，结束，取消，重复播放             |
| setUpdateListener(ValueAnimator.AnimatorUpdateListener listener) | 设置监听器，监听动画的每一帧的播放                           |
| setInterpolator(TimeInterpolator interpolator)               | 设置插值器                                                   |
| setStartDelay(long startDelay)                               | 设置动画延长开始的时间                                       |
| setDuration(long duration)                                   | 设置动画执行的时间                                           |
| withLayer()                                                  | 设置是否开启硬件加速                                         |
| withStartAction(Runnable runnable)                           | 设置用于动画监听开始（Animator.AnimatorListener）时运行的Runnable任务对象 |
| withEndAction(Runnable runnable)                             | 设置用于动画监听结束（Animator.AnimatorListener）时运行的Runnable任务对象 |

###### 两种类型方法

上面很多属性设置方法都对应着一个By结尾的方法，其变量则代表的是变化量，如下：

```java
public ViewPropertyAnimator scaleY(float value) {
	animateProperty(SCALE_Y, value);
	return this;
}

public ViewPropertyAnimator scaleYBy(float value) {
	animatePropertyBy(SCALE_Y, value);
    return this;
}
```

在看看animateProperty() 与 animatePropertyBy()

```java
private void animateProperty(int constantName, float toValue) {
	float fromValue = getValue(constantName);
	float deltaValue = toValue - fromValue;
	animatePropertyBy(constantName, fromValue, deltaValue);
}

private void animatePropertyBy(int constantName, float byValue) {
	float fromValue = getValue(constantName);
	animatePropertyBy(constantName, fromValue, byValue);
}
```

###### 时间监听器

AnimatorEventListener

ViewPropertyAnimator内部的监听器。这个类实现了Animator.AnimatorListener,ValueAnimator.AnimatorUpdateListener接口

```java
private class AnimatorEventListener
            implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationStart(Animator animation) {
            //调用了设置硬件加速的Runnable
            if (mAnimatorSetupMap != null) {
                Runnable r = mAnimatorSetupMap.get(animation);
                if (r != null) {
                    r.run();
                }
                mAnimatorSetupMap.remove(animation);
            }
            if (mAnimatorOnStartMap != null) {
                  //调用我们通过withStartAction(Runnable runnable)方法设置的runnable
                Runnable r = mAnimatorOnStartMap.get(animation);
                if (r != null) {
                    r.run();
                }
                mAnimatorOnStartMap.remove(animation);
            }
            if (mListener != null) {
                //调用我们自定义的监听器方法
                mListener.onAnimationStart(animation);
            }
        }



        @Override
        public void onAnimationCancel(Animator animation) {
            if (mListener != null) {
                //调用我们自定义的监听器方法
                mListener.onAnimationCancel(animation);
            }
            if (mAnimatorOnEndMap != null) {
                mAnimatorOnEndMap.remove(animation);
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            if (mListener != null) {
                //调用我们自定义的监听器方法
                mListener.onAnimationRepeat(animation);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mView.setHasTransientState(false);
            if (mListener != null) {
                //调用我们自定义的监听器方法
                mListener.onAnimationEnd(animation);
            }
            if (mAnimatorOnEndMap != null) {
                  //调用我们通过withEndAction(Runnable runnable)方法设置的runnable
                Runnable r = mAnimatorOnEndMap.get(animation);
                if (r != null) {
                    r.run();
                }
                mAnimatorOnEndMap.remove(animation);
            }
            if (mAnimatorCleanupMap != null) {
               //移除硬件加速
                Runnable r = mAnimatorCleanupMap.get(animation);
                if (r != null) {
                    r.run();
                }
                mAnimatorCleanupMap.remove(animation);
            }
            mAnimatorMap.remove(animation);
        }
```

由源码我们知道当监听器仅需要监听动画的开始和结束时，我们可以通过withStartAction(Runnable runnable)和withEndAction(Runnable runnable)方法来设置一些特殊的监听操作。在AnimatorEventListener中的开始事件还会判断是否开启硬件加速，当然在动画结束时也会去关闭硬件加速。

## Animator监听器

`Animator`提供了一个`addListener()`的方法，这个方法接收一个`AnimatorListener`，我们只需要去实现这个`AnimatorListener`就可以监听动画的各种事件了。

```java
anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //动画启动时调用
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //当动画结束时调用
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //当动画被取消调用,被取消的动画还会调用onAnimationEnd方法
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //当动画重演调用
            }
        });
```

## 布局动画

### 最简单的布局动画

只要给子View所在的ViewGroup的xml中添加下面的属性即可：

```xml
android:animateLayoutChanges=”true”
```

此动画为系统提供，针对于子View的增加或删除，不可自定义。

### layoutAnimation

第一次加载ListView 或者GridView的时候能有个动画的过渡效果。注意：**动画只在listView子View第一次进入时有效**，如果后面动态给ListView添加子View时，此动画无效。

#### xml实现

（**ViewGroup中添加属性**）

```xml
android:layoutAnimation=”@anim/customer_anim”
```

customer_anim.xml

```xml
<layoutAnimation xmlns:android="http://schemas.android.com/apk/res/android"
    android:animation="@anim/left_into"
    android:animationOrder="normal"
    android:delay="0.5"
    />
```

left_into.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate
        android:duration="500"
        android:fromXDelta="100%"
        android:fromYDelta="0"
        android:toXDelta="0"
        android:toYDelta="0" />
    <alpha
        android:duration="500"
        android:fromAlpha="0"
        android:toAlpha="1" />
</set>
```

**layoutAnimation标签属性**

|         属性名         | 含义                                                         |
| :--------------------: | ------------------------------------------------------------ |
|     android:delay      | delay的单位为秒，表示子View进入的延长时间                    |
| android:animationOrder | 子类的进入方式 ，其取值有，normal 0 默认 ，reverse 1 倒序 ，random 2 随机 |
|   android:animation    | 子view要执行的具体动画的文件，自定义即可                     |

#### Java实现

```java
//通过加载XML动画设置文件来创建一个Animation对象,子View进入的动画
Animation animation=AnimationUtils.loadAnimation(this, R.anim.left_into);
//得到一个LayoutAnimationController对象；
LayoutAnimationController lac=new LayoutAnimationController(animation);
//设置控件显示的顺序；
lac.setOrder(LayoutAnimationController.ORDER_REVERSE);
//设置控件显示间隔时间；
lac.setDelay(0.5f);
//为ListView设置LayoutAnimationController属性；
listView.setLayoutAnimation(lac);
```

### LayoutTransition容器布局动画

Property动画系统还提供了对ViewGroup中View添加时的动画功能，我们可以用LayoutTransition对ViewGroup中的View进行动画设置显示。LayoutTransition类用于当前布局容器中需要View添加，删除，隐藏，显示时设置布局容器子View的过渡动画。也就是说利用LayoutTransition，可以分别为需添加或删除的View对象在移动到新的位置的过程添加过渡的动画效果。

#### 五种容器转换动画

| 类型                                 | 含义                                                         |
| ------------------------------------ | ------------------------------------------------------------ |
| LayoutTransition.APPEARING           | 当View出现或者添加的时候View出现的动画                       |
| LayoutTransition.CHANGE_APPEARING    | 当添加View导致布局容器改变的时候整个布局容器的动画           |
| LayoutTransition.DISAPPEARING        | 当View消失或者隐藏的时候View消失的动画                       |
| LayoutTransition.CHANGE_DISAPPEARING | 当删除或者隐藏View导致布局容器改变的时候整个布局容器的动画   |
| LayoutTransition.CHANGE              | 当不是由于View出现或消失造成对其他View位置造成改变的时候整个布局容器的动画 |



#### JAVA实现

通过setLayoutTransition(LayoutTransition lt)方法把这些动画以一个LayoutTransition对象设置给一个ViewGroup。

使用系统提供的默认LayoutTransition动画

```java
//初始化容器动画
mTransitioner = new LayoutTransition();
mViewGroup.setLayoutTransition(mTransitioner);
```

设置更多动画

```java
mTransitioner = new LayoutTransition();
......
ObjectAnimator anim = ObjectAnimator.ofFloat(this, "scaleX", 0, 1);
......//设置更多动画
mTransition.setAnimator(LayoutTransition.APPEARING, anim);
......//设置更多类型的动画                mViewGroup.setLayoutTransition(mTransitioner);
```

##### 示例

```java
/**
* 添加View时过渡动画效果
*/
ObjectAnimator addAnimator = ObjectAnimator
	.ofFloat(null, "rotationY", 0,90,0)
    .setDuration(mTransitioner.getDuration(LayoutTransition.APPEARING));
mTransitioner.setAnimator(LayoutTransition.APPEARING, addAnimator);

/**
* 移除View时过渡动画效果
*/
ObjectAnimator removeAnimator = ObjectAnimator
	.ofFloat(null, "rotationX", 0, -90, 0)
    .setDuration(mTransitioner.getDuration(LayoutTransition.DISAPPEARING));
mTransitioner.setAnimator(LayoutTransition.DISAPPEARING, removeAnimator);

/**
* view 动画改变时，布局中的每个子view动画的时间间隔
*/
mTransitioner.setStagger(LayoutTransition.CHANGE_APPEARING, 30);
mTransitioner.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 30);

/**
*	LayoutTransition.CHANGE_APPEARING和LayoutTransition.CHANGE_DISAPPEARING的
*	过渡动画效果必须使用PropertyValuesHolder所构造的动画才会有效果，不然无效！
*/
PropertyValuesHolder pvhLeft =PropertyValuesHolder.ofInt("left", 0, 0);
PropertyValuesHolder pvhTop =PropertyValuesHolder.ofInt("top", 0, 0);
PropertyValuesHolder pvhRight =PropertyValuesHolder.ofInt("right", 0, 0);
PropertyValuesHolder pvhBottom =PropertyValuesHolder.ofInt("bottom", 0, 0);

/**
* view被添加时,其他子View的过渡动画效果
*/
PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX", 1, 1.5f, 1);
final ObjectAnimator changeIn = ObjectAnimator
	.ofPropertyValuesHolder(this, pvhLeft,  pvhBottom, animator)
    .setDuration(mTransitioner.getDuration(LayoutTransition.CHANGE_APPEARING));
//设置过渡动画
mTransitioner.setAnimator(LayoutTransition.CHANGE_APPEARING, changeIn);

/**
* view移除时，其他子View的过渡动画
*/
PropertyValuesHolder pvhRotation =PropertyValuesHolder.ofFloat("scaleX", 1, 1.5f, 1);
final ObjectAnimator changeOut = ObjectAnimator
	.ofPropertyValuesHolder(this, pvhLeft, pvhBottom, pvhRotation)
    .setDuration(mTransitioner.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
mTransitioner.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, changeOut);
```

##### **注意**

LayoutTransition.APPEARING和LayoutTransition.DISAPPEARING的情况下直接使用属性动画来设置过渡动画效果即可，而对于LayoutTransition.CHANGE_APPEARING和LayoutTransition.CHANGE_DISAPPEARING必须使用**PropertyValuesHolder**所构造的动画才会有效果，不然无效，在测试效果时发现在构造动画时，"left"、"top"、"bottom"、"right"属性的变动是必须设置的,**至少设置两个**,不然动画无效，**即使这些属性不想变动也得设置**，那么我们不想改变这四个属性时该如何设置呢？这时只要传递的可变参数都一样就行如下面的(0,0)也可以是(100,100)即可。



还有点需要注意的是在使用的ofInt,ofFloat中的可变参数值时，第一个值和最后一个值必须相同，不然此属性将不会有动画效果，比如下面首位相同是有效的：

```java
PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top",100,0,100);
PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top",0,100,0);
```

#### 常用函数

| 函数名称                                                     | 说明                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| setAnimator(int transitionType, Animator animator)           | 设置不同状态下的动画过渡,transitionType取值为， APPEARING、DISAPPEARING、CHANGE_APPEARING、CHANGE_DISAPPEARING 、CHANGING |
| setDuration(long duration)                                   | 设置所有动画完成所需要的时长                                 |
| setDuration(int transitionType, long duration)               | 设置特定type类型动画时长，transitionType取值为， APPEARING、DISAPPEARING、CHANGE_APPEARING、CHANGE_DISAPPEARING 、CHANGING |
| setStagger(int transitionType, long duration)                | 设置特定type类型动画的每个子item动画的时间间隔 ,transitionType取值为: APPEARING、DISAPPEARING、CHANGE_APPEARING、CHANGE_DISAPPEARING、CHANGING |
| setInterpolator(int transitionType, TimeInterpolator interpolator) | 设置特定type类型动画的插值器, transitionType取值为: APPEARING、DISAPPEARING、CHANGE_APPEARING、CHANGE_DISAPPEARING、CHANGING |
| setStartDelay(int transitionType, long delay)                | 设置特定type类型动画的动画延时 transitionType取值为, APPEARING、DISAPPEARING、CHANGE_APPEARING、CHANGE_DISAPPEARING 、CHANGING |
| addTransitionListener(TransitionListener listener)           | 设置监听器TransitionListener                                 |

#### 监听接口TransitionListener

```java
/**
 * This interface is used for listening to starting and ending events for transitions.
 */
public interface TransitionListener {

    /**
     * 监听LayoutTransition当前对应的transitionType类型动画开始
     * @param transition LayoutTransition对象实例
     * @param container LayoutTransition绑定的容器-container
     * @param view 当前在做动画的View对象
     * @param transitionType LayoutTransition类型，取值有：APPEARING、DISAPPEARING、
     *                       CHANGE_APPEARING、CHANGE_DISAPPEARING、CHANGING
     */
    public void startTransition(LayoutTransition transition, ViewGroup container,
                                View view, int transitionType);

    /**
     * 监听LayoutTransition当前对应的transitionType类型动画结束
     * @param transition LayoutTransition对象实例
     * @param container LayoutTransition绑定的容器-container
     * @param view 当前在做动画的View对象
     * @param transitionType LayoutTransition类型，取值有：APPEARING、DISAPPEARING、
     *                       CHANGE_APPEARING、CHANGE_DISAPPEARING、CHANGING
     */
    public void endTransition(LayoutTransition transition, ViewGroup container,
                              View view, int transitionType);
}
```

# 属性动画内存泄漏

属性动画中有一类无限循环的动画，如果在Activity中播放此类动画并且在onDestroy（）方法中没有停止该动画，那么动画会一直循环下去，尽管在界面上已经无法看不到动画了，但这个时候Activity的View会被动画持有，而View又持有Activity，最终Activity无法释放。下面的动画是无限循环的，会泄露当前的Activity。

```java
public class HomeActivity extends Activity {
    Button btn_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_home);
        btn_home = (Button) findViewById(R.id.btn_home);
        ObjectAnimator animator = ObjectAnimator.ofFloat(btn_home, "ratation", 0, 360).setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }
}
```

### 解决方案

​	在当前Activity的onDestroy()方法中取消动画：animator.cancel()。

```java
@Override
protected void onDestroy() {
	super.onDestroy();
	animator.cancel();
}
```

# 优秀开源

