### Menu



#### 文件位置

res/menu/filename.xml

#### 资源引用

- Java：R.menu.filename
- xml： [package：]menu.filename

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@[+][package:]id/resource_name"
          android:title="string"
          android:titleCondensed="string"
          android:icon="@[package:]drawable/drawable_resource_name"
          android:onClick="method name"
          android:showAsAction=["ifRoom" | "never" | "withText" | "always" | "collapseActionView"]
          android:actionLayout="@[package:]layout/layout_resource_name"
          android:actionViewClass="class name"
          android:actionProviderClass="class name"
          android:alphabeticShortcut="string"
          android:alphabeticModifiers=["META" | "CTRL" | "ALT" | "SHIFT" | "SYM" | "FUNCTION"]
          android:numericShortcut="string"
          android:numericModifiers=["META" | "CTRL" | "ALT" | "SHIFT" | "SYM" | "FUNCTION"]
          android:checkable=["true" | "false"]
          android:visible=["true" | "false"]
          android:enabled=["true" | "false"]
          android:menuCategory=["container" | "system" | "secondary" | "alternative"]
          android:orderInCategory="integer" />
    <group android:id="@[+][package:]id/resource name"
           android:checkableBehavior=["none" | "all" | "single"]
           android:visible=["true" | "false"]
           android:enabled=["true" | "false"]
           android:menuCategory=["container" | "system" | "secondary" | "alternative"]
           android:orderInCategory="integer" >
        <item />
    </group>
    <item >
        <menu>
          <item />
        </menu>
    </item>
</menu>
```

#### 属性

已混淆属性

| 属性                        | 描述                                                         | 是否必须 | 常用值                     | 说明                                                    |
| --------------------------- | ------------------------------------------------------------ | -------- | -------------------------- | ------------------------------------------------------- |
| android:titleCondensed      | 原始标题，用于标题过长                                       |          |                            |                                                         |
| android:onClick             | 此方法优先于标准回调 onOptionsItemSelected()                 |          |                            | API>=11<br>该方法优先于标准回调 onOptionsItemSelected() |
| android:showAsAction        | 菜单如何显示                                                 |          | 见下表 showAsAction        |                                                         |
| android:actionLayout        | 布局资源，用作动作视图的布局                                 |          |                            |                                                         |
| android:actionViewClass     | 作为动作视图的View名，例如：`android.widget.SearchView`      |          |                            |                                                         |
| android:actionProviderClass | 代替操作项的限定类名，例如：`android.widget.ShareActionProvider` |          |                            |                                                         |
| android:alphabeticShortcut  | 字母快捷键                                                   |          |                            |                                                         |
| android:numericShortcut     | 数字快捷键                                                   |          |                            |                                                         |
| android:alphabeticModifiers | 菜单项的字母快捷键修饰符                                     |          | 见下表 alphabeticModifiers |                                                         |
| android:checkable           | 是否可选中                                                   |          |                            |                                                         |
| android:checked             | 是否选中                                                     |          |                            |                                                         |
| android:menuCategory        | 优先级                                                       |          |                            |                                                         |
| android:orderInCategory     | 组内的重要性顺序                                             |          | 见下表 orderInCategory     |                                                         |
|                             |                                                              |          |                            |                                                         |

##### showAsAction

| 值                 | 描述                                                         |
| ------------------ | ------------------------------------------------------------ |
| ifRoom             | 如果有空间，则菜单显示在标题栏中                             |
| witchText          | 包括标题文本，可以使用管道符号分割                           |
| never              | 从不显示在标题栏，需要点击下拉                               |
| always             | 总是放在标题栏中                                             |
| collapseActionView | 与layout联动（用于Material Design）与`android:actionLayoutor`和`android:actionViewClass`一起使用 |

##### alphabeticModifiers

| 值      | 描述             |
| ------- | ---------------- |
| META    | 对应Meta元键     |
| CTRL    | 对应Control元键  |
| ALT     | 对应Alt元键      |
| SHIFT   | 对应Shift元键    |
| SYM     | 对应Sym元键      |
| FUNCTIN | 对应Function元键 |

##### orderInCategory

| 值          | 描述                                       |
| ----------- | ------------------------------------------ |
| container   | 对于属于容器的项目                         |
| system      | 对于系统提供的项目                         |
| secondary   | 对于用户提供的辅助（不经常使用）选项的项目 |
| alternative | 对于对当前显示的数据执行备用操作的项目     |

##### \<group>

\<group>菜单组包含一个或多个的 item

| 属性                      | 描述             | 是否必须 | 常见值                   |
| ------------------------- | ---------------- | -------- | ------------------------ |
| android:checkableBehavior | 可选中的行为     |          | 见下表 checkableBehavior |
| android:menuCategory      | 定义组优先级     |          |                          |
| android:orderInCategory   | 组内的重要性顺序 |          |                          |

###### checkableBehavior

| 值     | 描述                             |
| ------ | -------------------------------- |
| none   | 不可选中                         |
| all    | 可以选中所有项目（使用复选框）   |
| single | 智能选中一个项目（使用单选按钮） |

#### 使用

##### res/menu/

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:android="http://schemas.android.com/apk/res/android"
    >

    <item android:title="分享"
        app:showAsAction="always"
        android:orderInCategory="2"/>
    <item android:title="查看"
        android:menuCategory="container"
        android:orderInCategory="1"/>
</menu>
```

```java
@Override
   public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.idea_menu,menu);
       return super.onCreateOptionsMenu(menu);
   }
```

##### app:actionViewClass

可扩展的菜单按钮，如果点击该按钮，会显示出一个视图。

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:android="http://schemas.android.com/apk/res/android"
    >
    <item android:title="搜索"
        app:actionViewClass="android.support.v7.widget.SearchView"
        app:showAsAction="always"
        android:orderInCategory="2"/>
    <item android:title="查看"
        android:menuCategory="container"
        android:orderInCategory="1"/>
</menu>
```

searchView 可放大，其实 SearchView 实现了一个接口 CollapsibleActionView。

searchView 的参数如下：

```java
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.idea_menu,menu);
    MenuItem searchItem=menu.findItem(R.id.search);
    
    SearchView searchView= (SearchView) searchItem.getActionView();
    //false 为直接展开
    //searchView.setIconified(false);
    //无法关闭，搜索框外有放大镜
    //searchView.setIconifiedByDefault(false);
    //无法关闭
    //searchView.onActionViewExpanded();
    //最大宽度
    //searchView.setMaxWidth(500);
    //提交按钮
    //searchView.setSubmitButtonEnabled(true);
    //搜索提示语
    //searchView.setQueryHint("请输入你要搜索的内容");
    //点击关闭的监听
    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            Toast.makeText(getApplicationContext()
                           ,"点击关闭"
                           ,Toast.LENGTH_SHORT)
                .show();
            return false;
        }
    });
    //点击搜索的按钮打开搜索框
    searchView.setOnSearchClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext()
                           ,"点击按钮打开搜索框",Toast.LENGTH_SHORT)
                .show();
        }
    });
    //文字输入监听
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            //输入内容提交
            Toast.makeText(getApplicationContext()
                           ,"提交内容为："+s
                           ,Toast.LENGTH_SHORT)
                .show();
            return false;
        }
        @Override
        public boolean onQueryTextChange(String s) {
            //输入内容改变
            Log.d(TAG, "输入文字改变: "+s);
            return false;
        }
    });
    return super.onCreateOptionsMenu(menu);
}
```

##### ShareActionProvider

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:android="http://schemas.android.com/apk/res/android"
    >

    <item android:title="分享"
        android:id="@+id/share"
        app:actionProviderClass="android.support.v7.widget.ShareActionProvider"
        app:showAsAction="always"/>
</menu>
```

```java
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.idea_menu,menu);
    MenuItem searchItem=menu.findItem(R.id.share);
    ShareActionProvider provider= (ShareActionProvider) MenuItemCompat.getActionProvider(searchItem);
    Intent share_intent = new Intent();
    share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
    share_intent.setType("text/plain");//设置分享内容的类型
    share_intent.putExtra(Intent.EXTRA_SUBJECT, "标题");//添加分享内容标题
    share_intent.putExtra(Intent.EXTRA_TEXT, "内容");//添加分享内容
    provider.setShareIntent(share_intent);
    return super.onCreateOptionsMenu(menu);
}
```

