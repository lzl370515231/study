# ContentProvider

ContentProvider内容提供者（四大组件之一）主要**用于在不同的应用程序之间实现数据共享的功能**。

### 使用

ContentProvider可以理解为一个Android应用对外开放的接口，只要是符合它所定义的Uri格式的请求，均可以正常访问执行操作。其他的Android应用可以使用ContentResolver对象通过与ContentProvider**同名的方法**请求执行，被执行的就是ContentProvider中的同名方法。

![](.\png\CoontentProvider.png)

### Android自带的 ContentProvider

- Browser：存储如浏览器的信息。
- CallLog：存储通话记录等信息。
- Contacts Provider：存储联系人(通讯录)等信息。
- MediaStore：存储媒体文件的信息。
- Settings：存储设备的设置和首选项信息。

### ContentProvider方法

#### 抽象方法

```java
//初始化提供者
boolean onCreate();

//查询数据，返回一个数据Cursor对象。其中参数selection和selectionArgs是外部程序提供的查询条件
Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);

//插入一条数据。参数values是需要插入的值
Uri insert(Uri uri, ContentValues values);

//根据条件更新数据
int update(Uri uri, ContentValues values, String selection, String[] selectionArgs);

//根据条件删除数据
int delete(Uri uri, String selection, String[] selectionArgs);

//获取uri对象所对应的MIME类型
String getType(Uri uri);
```

备注：还有两个非常有意思的方法，必须要提一下，call()和bulkInsert()方法，使用call，理论上可以在ContentResolver中执行ContentProvider暴露出来的任何方法，而bulkInsert()方法用于插入多条数据。

### Uri

在Android中，Uri是一种比较常见的资源访问方式。对于ContentProvider而言，Uri也是有固定格式的:

<srandard_prefix>://<authority>/<data_path>/<id>

- <srandard_prefix>：ContentProvider的 srandard_prefix 始终是**content://**
- <authority>：ContentProvider的名称
- <data_path>：请求的数据类型
- <id>：指定请求的特定数据

### UriMatcher

用来帮助内容提供者匹配Uri。

#### 两个方法

- void addURI(String authority,String path,int code)：添加一个Uri匹配项

  **authority**为AndroidManifest.xml中注册的ContentProvider中的authority属性；

  **path**为一个路径，可以设置通配符，**#表示任意数字，\*表示任意字符**；

  **code**为自定义的一个Uri代码。

- int match(Uri uri)：匹配传递的Uri，返回addURI()传递的code参数。

### 示例代码

#### PersonContentProvider.java

```java
package com.example.contentprovidertest01;

import com.example.contentprovidertest01.dao.PersonDao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class PersonContentProvider extends ContentProvider {

    private final String TAG = "PersonContentProvider";
    private static final UriMatcher URI_MATCHER = new UriMatcher(
            UriMatcher.NO_MATCH);// 默认的规则是不匹配的
    private static final int PERSON = 1; // 操作单行记录
    private static final int PERSONS = 2; // 操作多行记录
    // 往UriMatcher中添加匹配规则。注意，这里面的url不要写错了，我就是因为写错了，半天没调试出来。哎···
    static {
        // 添加两个URI筛选
        URI_MATCHER.addURI("com.example.contentprovidertest01.PersonContentProvider",
                "person", PERSONS);
        // 使用通配符#，匹配任意数字
        URI_MATCHER.addURI("com.example.contentprovidertest01.PersonContentProvider",
                "person/#", PERSON);
    }

    public PersonContentProvider() {
    }

    @Override
    public boolean onCreate() {
        // 初始化一个数据持久层
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri resultUri = null;
        // 解析Uri，返回Code
        int flag = URI_MATCHER.match(uri);
        switch (flag) {
        case PERSONS:
            //调用数据库的访问方法   
            long id = personDao.insertPerson(values); //执行插入操作的方法，返回插入当前行的行号
            resultUri = ContentUris.withAppendedId(uri, id);
            Log.i(TAG,"--->>插入成功, id=" + id);
            Log.i(TAG,"--->>插入成功, resultUri=" + resultUri.toString());
            break;
        }
        return resultUri;
    }

    //方法：删除记录。注：参数：selection和selectionArgs是查询的条件，是由外部（另一个应用程序）传进来的
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = -1; //影响数据库的行数
        try {
            int flag = URI_MATCHER.match(uri);
            switch (flag) {
            case PERSON:
                // delete from student where id=?
                // 单条数据，使用ContentUris工具类解析出结尾的Id
                long id = ContentUris.parseId(uri);
                String where_value = "id = ?";
                String[] args = { String.valueOf(id) };
                count = personDao.deletePerson(where_value, args);
                break;
            case PERSONS:
                count = personDao.deletePerson(selection, selectionArgs);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "--->>删除成功,count=" + count);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int count = -1;
        try {
            int flag = URI_MATCHER.match(uri);
            switch (flag) {
            case PERSON:
                long id = ContentUris.parseId(uri);
                String where_value = " id = ?";
                String[] args = { String.valueOf(id) };
                count = personDao.updatePerson(values, where_value, args);
                break;
            case PERSONS:
                count = personDao.updatePerson(values, selection, selectionArgs);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "--->>更新成功，count=" + count);
        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            int flag = URI_MATCHER.match(uri);
            switch (flag) {
            case PERSON:
                long id = ContentUris.parseId(uri);
                String where_value = " id = ?";
                String[] args = { String.valueOf(id) };
                cursor = personDao.queryPersons(where_value, args);
                break;
            case PERSONS:
                cursor = personDao.queryPersons(selection, selectionArgs);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "--->>查询成功，Count=" + cursor.getCount());
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int flag = URI_MATCHER.match(uri);
        switch (flag) {
        case PERSON:
            return "vnd.android.cursor.item/person"; 
                // 如果是单条记录，则为vnd.android.cursor.item/
                // + path
        case PERSONS:
            return "vnd.android.cursor.dir/persons";
                // 如果是多条记录，则为vnd.android.cursor.dir/
                // + path
        }
        return null;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Log.i(TAG, "--->>" + method);
        Bundle bundle = new Bundle();
        bundle.putString("returnCall", "call被执行了");
        return bundle;
    }
}
```

**UriMatcher**类的作用是：**匹配内容uri**，默认的规则是不匹配的。addURI 添加匹配规则。

#### AndroidManifest.xml

在 AndroidManifest.xml 中声明

```xml
<provider
    android:name=".内容提供者的类名"
    android:authorities="包名.内容提供者的类名" >
</provider>
```

#### ContentResolver内容访问者

通过ContentResolver来操作ContentProvider所暴露处理的接口。一般使用Content.getContentResolver()方法获取ContentResolver对象。(**单元测试**)

```java
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;

public class MyTest extends AndroidTestCase {

    public MyTest() {
        // TODO Auto-generated constructor stub
    }

    public void calltest() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.parse("content://com.example.contentprovidertest01.PersonContentProvider/person");
        Bundle bundle = contentResolver.call(uri, "method", null, null);
        String returnCall = bundle.getString("returnCall");
        Log.i("main", "-------------->" + returnCall);
    }

    //向数据库中添加记录。如果之前没有数据库，则会自动创建
    public void insert() {
        //使用内容解析者ContentResolver访问内容提供者ContentProvider
        ContentResolver contentResolver = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put("name", "生命贰号");
        values.put("address", "湖北");
        // content://authorities/person
        // http://
        Uri uri = Uri.parse("content://com.example.contentprovidertest01.PersonContentProvider/person");
        contentResolver.insert(uri, values);
    }

    //测试方法：删除单条记录。如果要删除所有记录：
    //content://com.example.contentprovidertest01.PersonContentProvider/person
    public void delete() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.parse("content://com.example.contentprovidertest01.PersonContentProvider/person/2");
        //删除id为1的记录
        contentResolver.delete(uri, null, null);
    }

    //测试方法：根据条件删除记录。
    public void deletes() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.parse("content://com.example.contentprovidertest01.PersonContentProvider/person");
        String where = "address=?"; 
        String[] where_args = { "HK" };
        contentResolver.delete(uri, where, where_args);  
        //第二个参数表示查询的条件"address=?"，第三个参数表示占位符中的具体内容
    }

    //方法：根据id修改记录。注：很少有批量修改的情况。
    public void update() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.parse("content://com.example.contentprovidertest01.PersonContentProvider/person/2");
        ContentValues values = new ContentValues();
        values.put("name", "李四");
        values.put("address", "上海");
        contentResolver.update(uri, values, null, null);
    }

    //方法：根据条件来修改记录。
    public void updates() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri
                .parse("content://com.example.contentprovidertest01.PersonContentProvider/person/student");
        ContentValues values = new ContentValues();
        values.put("name", "王五");
        values.put("address", "深圳");
        String where = "address=?";
        String[] where_args = { "beijing" };
        contentResolver.update(uri, values, where, where_args);
    }

    //测试方法：查询所有记录。如果要查询单条记录：
    //content://com.example.contentprovidertest01.PersonContentProvider/person/1
    public void query() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.parse("content://com.example.contentprovidertest01.PersonContentProvider/person");
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        while (cursor.moveToNext()) {
            Log.i("MyTest","--->>"+ cursor.getString(cursor.getColumnIndex("name")));
        }
    }

    //测试方法：根据条件查询所有记录。
    public void querys() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = Uri.parse("content://com.example.contentprovidertest01.PersonContentProvider/person");
        String where = "address=?";
        String[] where_args = { "深圳" };
        Cursor cursor = contentResolver.query(uri, null, where, where_args,null);
        while (cursor.moveToNext()) {
            Log.i("main","--->"+ cursor.getString(cursor.getColumnIndex("name")));
        }
    }
}
```

