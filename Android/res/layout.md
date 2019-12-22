### include、merge、ViewStub区别

#### include

包含进来，直接添加的意思。以达到布局重用



#### merge

减少视图层级

在UI的结构优化中起着非常重要的作用，它可以删减多余的层级，优化UI。<merge/>多用于替换FrameLayout或者当一个布局包含另一个时，<merge/>标签消除视图层次结构中多余的视图组。例如你的主布局文件是垂直布局，引入了一个垂直布局的include，这是如果include布局使用的LinearLayout就没意义了，使用的话反而减慢你的UI表现。这时可以使用<merge/>标签优化。

```xml
<merge xmlns:android="http://schemas.android.com/apk/res/android">
 
    <Button
        android:layout_width="fill_parent" 
        android:layout_height="wrap_content"
        android:text="@string/add"/>
 
    <Button
        android:layout_width="fill_parent" 
        android:layout_height="wrap_content"
        android:text="@string/delete"/>
 
</merge>
```

#### ViewStub

<ViewStub />标签最大的优点是当你需要时才会加载，使用他并不会影响UI初始化时的性能。各种不常用的布局想进度条、显示错误消息等可以使用<ViewStub />标签，以减少内存使用量，加快渲染速度。<ViewStub />是一个不可见的，大小为*0*的*View。*

```xml

<ViewStub
    android:id="@+id/stub_import"
    android:inflatedId="@+id/panel_import"
    android:layout="@layout/progress_overlay"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom" />
```

当你想加载布局时，可以使用下面其中一种方法：

```java
((ViewStub) findViewById(R.id.stub_import)).setVisibility(View.VISIBLE);
// or
View importPanel = ((ViewStub) findViewById(R.id.stub_import)).inflate();
```

当调用*inflate()*函数的时候，*ViewStub*被引用的资源替代，并且返回引用的*view*。 这样程序可以直接得到引用的*view*而不用再次调用函数*findViewById()*来查找了。