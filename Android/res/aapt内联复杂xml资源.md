### 内联复杂的XML资源

#### 示例

一个动画矢量可绘制，这是一个可绘制资源封装了一个矢量可绘制和一个动画。至少需要三个 XML 文件。

**res/drawable/avd.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<animated-vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/vectordrawable" >
    <target
        android:name="rotationGroup"
        android:animation="@anim/rotation" />
</animated-vector>
```

**res/drawable/vectordrawable.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:height="64dp"
    android:width="64dp"
    android:viewportHeight="600"
    android:viewportWidth="600" >

   <group
        android:name="rotationGroup"
        android:pivotX="300.0"
        android:pivotY="300.0"
        android:rotation="45.0" >
        <path
            android:fillColor="#000000"
            android:pathData="M300,70 l 0,-70 70,70 0,0 -70,70z" />

   </group>
</vector>
```

**res/anim/rotation.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<objectAnimator xmlns:android="http://schemas.android.com/apk/android"
    android:duration="6000"
    android:propertyName="rotation"
    android:valueFrom="0"

   android:valueTo="360" />
```

**如果只用于这个动画矢量可绘制，有一个更紧凑的方式来实现他们**

#### 使用AAPT的内联资源格式

使用AAPT 的内联资源格式，可以在同一个 XML 文件中定义所有三种资源。

res/drawable/avd.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<animated-vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt" >

    <aapt:attr name="android:drawable" >
        <vector
            android:height="64dp"
            android:width="64dp"
            android:viewportHeight="600"
            android:viewportWidth="600" >

           <group
                android:name="rotationGroup"
                android:pivotX="300.0"
                android:pivotY="300.0"
                android:rotation="45.0" >
                <path
                    android:fillColor="#000000"
                    android:pathData="M300,70 l 0,-70 70,70 0,0 -70,70z" />

           </group>
        </vector>
    </aapt:attr>

    <target android:name="rotationGroup">
        <aapt:attr name="android:animation" >
            <objectAnimator
                android:duration="6000"
                android:propertyName="rotation"
                android:valueFrom="0"

               android:valueTo="360" />
        </aapt:attr>
    </target>
</animated-vector>
```

aapt: 告诉 AAPT标记的子标记应该被视为资源并提取到它自己的资源文件中。属性名中的值指定在父标记中使用内联资源的位置。

aapt 将为所有内联资源生成资源文件和名称。使用这种内嵌格式构建的应用程序与所有版本的Android 都兼容。