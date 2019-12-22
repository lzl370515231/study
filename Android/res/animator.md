# animator(动画)

## 分类

- 属性动画
- 视图动画
  - 补见动画
  - 帧动画

# 属性动画

#### 文件位置

res/animator/filename.xml

#### 数据类型

- ValueAnimator
- ObjectAnimator
- AnimatorSet

#### 常见属性

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

##### 注

文件必须具有单个根元素：set、objectanimator 或 valueanimator。可以在 set内将动画元素放在一起，也可以是其他的 set 动画元素。

### set 元素

#### android:ordering

指定动画集合中的播放顺序

| Value                | Description          |
| -------------------- | -------------------- |
| sequentially         | 顺序播放集合中的动画 |
| `together` (default) | 同时播放集合中的动画 |

### objectAnimator

对象动画

| value                | 描述                                 | 要求     | 常见值                                                       |
| -------------------- | ------------------------------------ | -------- | ------------------------------------------------------------ |
| android:propertyName | 动画名称                             | required |                                                              |
| android:valueTo      | 动画结束值                           | required |                                                              |
| android:valueFrom    | 动画起始值                           |          |                                                              |
| android:duration     | 动画时间。毫秒为单位，默认为 300ms。 |          |                                                              |
| android:startOffset  | 动画延迟的毫秒数                     |          |                                                              |
| android:repeatCount  | 重复的次数                           |          | 默认值为0<br>-1为无限重复<br>1为重复一次，即播放两次         |
| android:repeatMode   | 重复播放的模式                       |          | 必须设置 repeatCount,此属性才有效<br>reverse：翻转播放<br>repeat：重复播放 |
| android:valueType    | 动画值的数据类型                     |          | 如果值是颜色，则不要指定此属性。动画框架自动处理颜色值<br>initType：指定动画值为整数<br>floatType(dafault)：指定动画值为浮点数 |

### \<animator>

| value               | 描述                     | 要求     | 常见值                                                       |
| ------------------- | ------------------------ | -------- | ------------------------------------------------------------ |
| android:valueTo     | 动画的结束值             | required |                                                              |
| android:valueFrom   | 动画的起始值             | required |                                                              |
| android:duration    | 动画时间，毫秒为单位     |          | 默认为300ms                                                  |
| android:startOffset | 动画延迟时间，毫秒为单位 |          |                                                              |
| android:repeatCount | 动画重复次数             |          | 默认值为0<br>-1为无限<br>1为重复1次，即播放两次              |
| android:repeatMode  | 动画重复模式             |          | 必须设置 repeatCount才有效<br>reverse：翻转<br>repeat：重复  |
| android:valueType   | 值的数据类型             |          | 颜色值，则不要指定此属性<br>initType：指定动画值为整数<br>floatType：指定动画值为浮点数 |

### 示例

```xml
<set android:ordering="sequentially">
    <set>
        <objectAnimator
            android:propertyName="x"
            android:duration="500"
            android:valueTo="400"
            android:valueType="intType"/>
        <objectAnimator
            android:propertyName="y"
            android:duration="500"
            android:valueTo="300"
            android:valueType="intType"/>
    </set>
    <objectAnimator
        android:propertyName="alpha"
        android:duration="500"
        android:valueTo="1f"/>
</set>
```

```java
AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,R.animator.property_animator);
set.setTarget(mObject);
set.start();
```

# 补见动画

#### 文件位置

res/anim/filename.xml

#### 资源引用

- java	R.anim.filename
- xml     [package:]anim/filename

#### 属性示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@[package:]anim/interpolator_resource"
    android:shareInterpolator=["true" | "false"] >
    <alpha
        android:fromAlpha="float"
        android:toAlpha="float" />
    <scale
        android:fromXScale="float"
        android:toXScale="float"
        android:fromYScale="float"
        android:toYScale="float"
        android:pivotX="float"
        android:pivotY="float" />
    <translate
        android:fromXDelta="float"
        android:toXDelta="float"
        android:fromYDelta="float"
        android:toYDelta="float" />
    <rotate
        android:fromDegrees="float"
        android:toDegrees="float"
        android:pivotX="float"
        android:pivotY="float" />
    <set>
        ...
    </set>
</set>
```

### 属性

#### \<set>

| value                     | 描述           | 是否必须 | 常见值           |
| ------------------------- | -------------- | -------- | ---------------- |
| android:interpolator      | 插值器         |          |                  |
| android:shareInterpolator | 是否共享插值器 |          | true：所有子元素 |

#### \<alpha>

淡入或淡出动画。

| value             | 描述       | 是否必须 | 常见值                       |
| ----------------- | ---------- | -------- | ---------------------------- |
| android:fromAlpha | 起始透明值 |          | 0.0是透明的<br>1.0是不透明的 |
| android:toAlpha   | 结束透明值 |          |                              |

#### \<scale>

缩放

| 属性               | 描述            | 是否必须 | 常见值        |
| ------------------ | --------------- | -------- | ------------- |
| android:fromXScale | 开始 x 大小偏移 |          | 1.0是没有变化 |
| android:toXScale   | 结束x大小偏移   |          | 1.0是没有变化 |
| android:fromYScale | 开始 y 大小偏移 |          | 1.0是没有变化 |
| android:toYScale   | 结束 y 大小偏移 |          | 1.0是没有变化 |
| android:pivotX     | 缩放的X的参考点 |          |               |
| android:pivotY     | 缩放的Y的参考点 |          |               |

#### \<translate>

平移动画

| 属性               | 描述 | 是否必须 | 常见值 |
| ------------------ | ---- | -------- | ------ |
| android:fromXDelta |      |          |        |
| android:toXDelta   |      |          |        |
| android:fromYDelta |      |          |        |
| android:toYDelta   |      |          |        |

#### \<rotate>

旋转动画。

| 属性                | 描述            | 是否必须 | 常见值 |
| ------------------- | --------------- | -------- | ------ |
| android:fromDegrees | 开始角度        |          |        |
| android:toDegrees   | 结束角度        |          |        |
| android:pivotX      | 旋转点 X 的坐标 |          |        |
| android:pivotY      | 旋转点 Y 的坐标 |          |        |

### 示例

```xml
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:shareInterpolator="false">
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
    <set
        android:interpolator="@android:anim/accelerate_interpolator"
        android:startOffset="700">
        <scale
            android:fromXScale="1.4"
            android:toXScale="0.0"
            android:fromYScale="0.6"
            android:toYScale="0.0"
            android:pivotX="50%"
            android:pivotY="50%"
            android:duration="400" />
        <rotate
            android:fromDegrees="0"
            android:toDegrees="-45"
            android:toYScale="0.0"
            android:pivotX="50%"
            android:pivotY="50%"
            android:duration="400" />
    </set>
</set>
```

```java
ImageView image = (ImageView) findViewById(R.id.image);
Animation hyperspaceJump = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump);
image.startAnimation(hyperspaceJump);
```

## 插值器（Interpolators）

| 插值器类                         | 资源ID                                           |
| -------------------------------- | ------------------------------------------------ |
| AccelerateDecelerateInterpolator | @android:anim/accelerate_decelerate_interpolator |
| AccelerateInterpolator           | @android:anim/accelerate_interpolator            |
| AnticipateInterpolator           | @android:anim/anticipate_interpolator            |
| AnticipateOvershootInterpolator  | @android:anim/anticipate_overshoot_interpolator  |
| BounceInterpolator               | @android:anim/bounce_interpolator                |
| CycleInterpolator                | @android:anim/cycle_interpolator                 |
| DecelerateInterpolator           | @android:anim/decelerate_interpolator            |
| LinearInterpolator               | @android:anim/linear_interpolator                |
| OvershootInterpolator            | @android:anim/overshoot_interpolator             |

```xml
<set android:interpolator="@android:anim/accelerate_interpolator">
    ...
</set>
```

#### \<accelerateDecelerateInterpolator>

##### 描述

开始和结束缓慢，但中间加速。

##### 属性

无

#### \<accelerateInterpolator>

##### 描述

变化的速度从缓慢开始，然后加速

##### 属性

| 属性           | 描述   |
| -------------- | ------ |
| android:factor | 加速率 |

#### \<anticipateInterpolator>

##### 描述

开始向后，然后向前翻转

##### 属性

| 属性            | 描述               |
| --------------- | ------------------ |
| android:tension | 起始点后拉的张力数 |

#### \<anticipateOvershootInterpolator>

##### 描述

开始向后，向前翻转，超过目标值，然后在最终值处停止。

##### 属性

| 属性                 | 描述               |
| -------------------- | ------------------ |
| android:tension      | 张力值             |
| android:extraTension | 用来乘以张力的数量 |

#### \<bounceInterpolator>

##### 描述

在最后的反弹变化。

##### 属性

无

#### \<cycleInterpolator>

##### 描述

为指定的周期重复动画。 变化速率遵循循环模式

##### 属性

| 属性           | 描述     |
| -------------- | -------- |
| android:cycles | 循环次数 |

#### \<decelerateInterpolator>

##### 描述

变化的速度越来越慢

##### 属性

| 属性           | 描述   |
| -------------- | ------ |
| android:factor | 减速率 |

#### \<linearInterpolator>

##### 描述

线性插值。

##### 属性

无

#### \<overshootInterpolator>

##### 描述

向前抛出并超过最后一个值，然后返回。

##### 属性

| 属性            | 描述   |
| --------------- | ------ |
| android:tension | 张力值 |

### 自定义插值器

res/anim/my_overshoot_interpolator.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<overshootInterpolator xmlns:android="http://schemas.android.com/apk/res/android"
    android:tension="7.0"
    />
```

```xml
<scale xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@anim/my_overshoot_interpolator"
    android:fromXScale="1.0"
    android:toXScale="3.0"
    android:fromYScale="1.0"
    android:toYScale="3.0"
    android:pivotX="50%"
    android:pivotY="50%"
    android:duration="700" />
```

### 帧动画

#### 位置

res/drawable/filename.xml

#### 资源引用

- java	R.drawable.filename
- xml     [package:]drawable.filename

#### 属性示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot=["true" | "false"] >
    <item
        android:drawable="@[package:]drawable/drawable_resource_name"
        android:duration="integer" />
</animation-list>
```

#### 属性

##### \<animation-list>

| 属性            | 描述               | 常见值                              |
| --------------- | ------------------ | ----------------------------------- |
| android:oneshot | 是否只播放一次动画 | true：只播放一次<br>false：循环播放 |

##### \<item>

| 属性             | 描述                               | 常见值 |
| ---------------- | ---------------------------------- | ------ |
| android:drawable | 动画资源                           |        |
| android:duration | 显示此帧动画的持续时间，毫秒为单位 |        |

##### 示例

res/drawable/rocket.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot="false">
    <item android:drawable="@drawable/rocket_thrust1" android:duration="200" />
    <item android:drawable="@drawable/rocket_thrust2" android:duration="200" />
    <item android:drawable="@drawable/rocket_thrust3" android:duration="200" />
</animation-list>
```

将动画设置为视图的背景，然后播放动画。

```java
ImageView rocketImage = (ImageView) findViewById(R.id.rocket_image);
rocketImage.setBackgroundResource(R.drawable.rocket);

rocketAnimation = rocketImage.getBackground();
if (rocketAnimation instanceof Animatable) {
    ((Animatable)rocketAnimation).start();
}
```

