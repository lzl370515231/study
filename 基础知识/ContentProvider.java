Android ContentProvider:
	
	作用：
		App 1 ------> ContentProvider ------> App 2
							|
							|
						数据源（数据库Sqlite，文件，xml，网络等等）
	
		ContentProvider=中间者角色（搬运工），真正存储&操作数据的数据源还是原来存储数据方式（数据库、文件、xml或网络）
	
	原理：
		ContentProvider的底层是采用 Android中的Binder机制
	
	ContentProvider可以理解为一个Android应用对外开放的接口，只要是符合它所定义的Uri格式的请求，均可以正常访问执行操作。其他的Android应用可以使用ContentResolver对象通过与ContentProvider同名的方法请求执行，被执行的就是ContentProvider中的同名方法。所以ContentProvider很多对外可以访问的方法，在ContentResolver中均有同名的方法，是一一对应的:
		
		ContentResolver			ContentProvider
			insert					insert
			query					query
			update					update
			delete					delete
			.....					.....
										onCreate()
										getType()
	
	
	Uri
	URI分为 系统预置 & 自定义，分别对应系统内置的数据（如通讯录、日程表等等）和自定义数据库。
　　在Android中，Uri是一种比较常见的资源访问方式。而对于ContentProvider而言，Uri也是有固定格式的：
　　　　<srandard_prefix>://<authority>/<data_path>/<id>
		<srandard_prefix>：		ContentProvider的srandard_prefix始终是content://
		<authority>：			ContentProvider的名称。
		<data_path>：			请求的数据类型。
		<id>：					指定请求的特定数据。
	
		// 设置URI
		Uri uri = Uri.parse("content://com.carson.provider/User/1") 
		// 上述URI指向的资源是：名为 `com.carson.provider`的`ContentProvider` 中表名 为`User` 中的 `id`为1的数据

		// 特别注意：URI模式存在匹配通配符* & ＃

		// *：匹配任意长度的任何有效字符的字符串
		// 以下的URI 表示 匹配provider的任何内容
		content://com.example.app.provider/* 
		// ＃：匹配任意长度的数字字符的字符串
		// 以下的URI 表示 匹配provider中的table表的所有行
		content://com.example.app.provider/table/#
	
	Android附带的ContentProvider包括：
		Browser：存储如浏览器的信息。
		CallLog：存储通话记录等信息。
		Contacts：存储联系人等信息。
		MediaStore：存储媒体文件的信息。
		Settings：存储设备的设置和首选项信息。
	
	
	MIME数据类型：
		作用：指定某个扩展名的文件用某种应用程序来打开。
			如指定.html文件采用text应用程序打开、指定.pdf文件采用flash应用程序打开
		
		ContentProvider根据URI返回MIME类型
			ContentProvider.geType(uri)
		
		MIME类型组成：
			每种MIME类型 由2部分组成 = 类型 + 子类型
			如：
				text / html
				// 类型 = text、子类型 = html
				text/css
				text/xml
				application/pdf
		
		MIME类型有2种形式：
			// 形式1：单条记录  
			vnd.android.cursor.item/自定义
			// 形式2：多条记录（集合）
			vnd.android.cursor.dir/自定义 

		注：
			1. vnd：表示父类型和子类型具有非标准的、特定的形式。
			2. 父类型已固定好（即不能更改），只能区别是单条还是多条记录
			3. 子类型可自定义
		
		实例说明：
			<-- 单条记录 -->
			// 单个记录的MIME类型
			vnd.android.cursor.item/vnd.yourcompanyname.contenttype 

			// 若一个Uri如下
			content://com.example.transportationprovider/trains/122   
			// 则ContentProvider会通过ContentProvider.geType(url)返回以下MIME类型
			vnd.android.cursor.item/vnd.example.rail


			<-- 多条记录 -->
			// 多个记录的MIME类型
			vnd.android.cursor.dir/vnd.yourcompanyname.contenttype 
			// 若一个Uri如下
			content://com.example.transportationprovider/trains 
			// 则ContentProvider会通过ContentProvider.geType(url)返回以下MIME类型
			vnd.android.cursor.dir/vnd.example.rail
		
	
	getType()中的MIME
		MIME类型就是设定某种扩展名的文件用一种应用程序来打开的方式类型。在ContentProvider中的getType方法，返回的就是一个MIME类型的字符串。如果支持需要使用ContentProvider来访问数据，就上面这个Demo，getType()完全可以只返回一个Null，并不影响效果，但是覆盖ContentProvider的getType方法对于用new Intent(String action, Uri uri方法启动activity是很重要的，如果它返回的MIME type和activity在<intent filter>中定义的data的MIME type不一致，将造成activity无法启动。这就涉及到Intent和Intent-filter的内容了，以后有机会再说，这里不再详解。
		
		从官方文档了解到，getType返回的字符串，如果URI针对的是单条数据，则返回的字符串以vnd.android.cursor.item/开头；如果是多条数据，则以vnd.adroid.cursor.dir/开头。
	
	
	组织数据的方式：
		ContentProvider主要以 表格的形式 组织数据。同时也支持文件数据，只是表格形式用得比较多。
	
	
	主要方法：
		<-- 4个核心方法 -->
		public Uri insert(Uri uri, ContentValues values) 
		// 外部进程向 ContentProvider 中添加数据

		public int delete(Uri uri, String selection, String[] selectionArgs) 
		// 外部进程 删除 ContentProvider 中的数据

		public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
		// 外部进程更新 ContentProvider 中的数据

		public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,  String sortOrder)　 
		// 外部应用 获取 ContentProvider 中的数据

		注：
			1. 上述4个方法由外部进程回调，并运行在ContentProvider进程的Binder线程池中（不是主线程）
			2. 存在多线程并发访问，需要实现线程同步
				a. 若ContentProvider的数据存储方式是使用SQLite & 一个，则不需要，因为SQLite内部实现好了线程同步，若是多个SQLite则需要，因为SQL对象之间无法进行线程同步
				b. 若ContentProvider的数据存储方式是内存，则需要自己实现线程同步

		<-- 2个其他方法 -->
		public boolean onCreate() 
		// ContentProvider创建后 或 打开系统后其它进程第一次访问该ContentProvider时 由系统进行调用
		注：运行在ContentProvider进程的主线程，故不能做耗时操作

		public String getType(Uri uri)
		//得到数据类型，即返回当前 Url 所代表数据的MIME类型
	
	
	说明：
		ContentProvider类并不会直接与外部进程交互，而是通过ContentResolver 类。
	

	构建自定义ContentProvider：
		在Android中，如果要创建自己的内容提供者的时候，需要扩展抽象类ContentProvider，并重写其中定义的各种方法。然后在AndroidManifest.xml文件中注册该ContentProvider即可。下面是ContentProvider必须要实现的几个方法：
			onCreate()：初始化提供者。
			query(Uri, String[], String, String[], String)：查询数据，返回一个数据Cursor对象。
			insert(Uri, ContentValues)：插入一条数据。
			update(Uri, ContentValues, String, String[])：根据条件更新数据。
			delete(Uri, String, String[])：根据条件删除数据。
			getType(Uri) 返回MIME类型对应内容的URI。
	
	
		call()和bulkInsert()方法，使用call，理论上可以在ContentResolver中执行ContentProvider暴露出来的任何方法，而bulkInsert()方法用于插入多条数据。
	
	
		在ContentProvider的CRUD操作，均会传递一个Uri对象，通过这个对象来匹配对应的请求。那么如何确定一个Uri执行哪项操作呢？需要用到一个UriMatcher对象，这个对象用来帮助内容提供者匹配Uri。它所提供的方法非常简单，仅有两个：
			void addURI(String authority,String path,int code)：添加一个Uri匹配项，authority为AndroidManifest.xml中注册的ContentProvider中的authority属性；path为一个路径，可以设置通配符，#表示任意数字，*表示任意字符；code为自定义的一个Uri代码。
			int match(Uri uri)：匹配传递的Uri，返回addURI()传递的code参数。
	
	
		在创建好一个ContentProvider之后，还需要在AndroidManifest.xml文件中对ContentProvider进行配置，使用一个<provider.../>节点，一般只需要设置两个属性即可访问，一些额外的属性就是为了设置访问权限而存在的，后面会详细讲解：
			android:name：provider的响应类。
			android:authorities：Provider的唯一标识，用于Uri匹配，一般为ContentProvider类的全名。
	
	
		示例：
			package com.example.contentproviderdemo;

			import com.example.dao.StudentDAO;
			import android.content.ContentProvider;
			import android.content.ContentUris;
			import android.content.ContentValues;
			import android.content.UriMatcher;
			import android.database.Cursor;
			import android.net.Uri;
			import android.os.Bundle;
			import android.util.Log;

			public class StudentProvider extends ContentProvider {

				private final String TAG = "main";
				private StudentDAO studentDao = null;
				private static final UriMatcher URI_MATCHER = new UriMatcher(
						UriMatcher.NO_MATCH);
				private static final int STUDENT = 1;
				private static final int STUDENTS = 2;
				static {
					//添加两个URI筛选
					URI_MATCHER.addURI("com.example.contentproviderdemo.StudentProvider",
							"student", STUDENTS);
					//使用通配符#，匹配任意数字
					URI_MATCHER.addURI("com.example.contentproviderdemo.StudentProvider",
							"student/#", STUDENT);        
				}

				public StudentProvider() {

				}    
				
				@Override
				public boolean onCreate() {
					// 初始化一个数据持久层
					studentDao = new StudentDAO(getContext());
					Log.i(TAG, "---->>onCreate()被调用");
					return true;
				}

				@Override
				public Uri insert(Uri uri, ContentValues values) {
					Uri resultUri = null;
					//解析Uri，返回Code
					int flag = URI_MATCHER.match(uri);
					if (flag == STUDENTS) {
						long id = studentDao.insertStudent(values);
						Log.i(TAG, "---->>插入成功, id="+id);
						resultUri = ContentUris.withAppendedId(uri, id);
					}
					return resultUri;
				}

				@Override
				public int delete(Uri uri, String selection, String[] selectionArgs) {
					int count = -1;
					try {
						int flag = URI_MATCHER.match(uri);
						switch (flag) {
						case STUDENT:
							// delete from student where id=?
							//单条数据，使用ContentUris工具类解析出结尾的Id
							long id = ContentUris.parseId(uri);
							String where_value = "id = ?";
							String[] args = { String.valueOf(id) };
							count = studentDao.deleteStudent(where_value, args);
							break;
						case STUDENTS:
							count = studentDao.deleteStudent(selection, selectionArgs);                
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Log.i(TAG, "---->>删除成功,count="+count);
					return count;
				}

				@Override
				public int update(Uri uri, ContentValues values, String selection,
						String[] selectionArgs) {
					int count = -1;
					try {            
						int flag = URI_MATCHER.match(uri);
						switch (flag) {
						case STUDENT:
							long id = ContentUris.parseId(uri);
							String where_value = " id = ?";
							String[] args = { String.valueOf(id) };
							count = studentDao.updateStudent(values, where_value, args);
							break;
						case STUDENTS:
							count = studentDao.updateStudent(values, selection,
									selectionArgs);
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Log.i(TAG, "---->>更新成功，count="+count);
					return count;
				}

				@Override
				public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
					Cursor cursor = null;
					try {
						int flag = URI_MATCHER.match(uri);
						switch (flag) {
						case STUDENT:
							long id = ContentUris.parseId(uri);
							String where_value = " id = ?";
							String[] args = { String.valueOf(id) };
							cursor = studentDao.queryStudents(where_value, args);
							break;
						case STUDENTS:
							cursor = studentDao.queryStudents(selection, selectionArgs);
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Log.i(TAG, "---->>查询成功，Count="+cursor.getCount());
					return cursor;
				}


				@Override
				public String getType(Uri uri) {
					int flag = URI_MATCHER.match(uri);
					String type = null;
					switch (flag) {
					case STUDENT:
						type = "vnd.android.cursor.item/student";
						Log.i(TAG, "----->>getType return item");
						break;
					case STUDENTS:
						type = "vnd.android.cursor.dir/students";
						Log.i(TAG, "----->>getType return dir");
						break;
					}
					return type;
				}
				@Override
				public Bundle call(String method, String arg, Bundle extras) {
					Log.i(TAG, "------>>"+method);
					Bundle bundle=new Bundle();
					bundle.putString("returnCall", "call被执行了");
					return bundle;
				}
			}
		
		
		
	
	访问权限：
		对于ContentProvider暴露出来的数据，应该是存储在自己应用内存中的数据，对于一些存储在外部存储器上的数据，并不能限制访问权限，使用ContentProvider就没有意义了。对于ContentProvider而言，有很多权限控制，可以在AndroidManifest.xml文件中对<provider>节点的属性进行配置，一般使用如下一些属性设置：

			android:grantUriPermssions:临时许可标志。
			android:permission:Provider读写权限。
			android:readPermission:Provider的读权限。
			android:writePermission:Provider的写权限。
			android:enabled:标记允许系统启动Provider。
			android:exported:标记允许其他应用程序使用这个Provider。
			android:multiProcess:标记允许系统启动Provider相同的进程中调用客户端。默认值是false，表示ContentProvider是单例的，无论哪个客户端应用的访问都将是一个ContentProvider对象(当然，必须是同一个ContentProvider，即Uri或者Authority name是一个)，如果设为true，系统会为每一个访问该ContentProvider的进程创建一个实例。因为android:multiprocess的默认值是false，所以我们在写自己的ContentProvider的时候还是要注意并发的情况。
		
	
	
	ContentResolver:(内容访问者)
		作用：
			ContentResolver类对所有的ContentProvider进行统一管理。
		
		具体使用：
			ContentResolver 类提供了与ContentProvider类相同名字 & 作用的4个方法

				// 外部进程向 ContentProvider 中添加数据
				public Uri insert(Uri uri, ContentValues values)　 

				// 外部进程 删除 ContentProvider 中的数据
				public int delete(Uri uri, String selection, String[] selectionArgs)

				// 外部进程更新 ContentProvider 中的数据
				public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)　 

				// 外部应用 获取 ContentProvider 中的数据
				public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
				
			
			// 使用ContentResolver前，需要先获取ContentResolver
			// 可通过在所有继承Context的类中 通过调用getContentResolver()来获得ContentResolver
			ContentResolver resolver =  getContentResolver(); 

			// 设置ContentProvider的URI
			Uri uri = Uri.parse("content://cn.scu.myprovider/user"); 

			// 根据URI 操作 ContentProvider中的数据
			// 此处是获取ContentProvider中 user表的所有记录 
			Cursor cursor = resolver.query(uri, null, null, null, "userid desc");
			
			
		
		示例代码：
			package com.example.contentproviderdemo;

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

				public void insert() {
					ContentResolver contentResolver = getContext().getContentResolver();
					Uri uri = Uri
							.parse("content://com.example.contentproviderdemo.StudentProvider/student");
					ContentValues values = new ContentValues();
					values.put("name", "Demo");
					values.put("address", "HK");
					Uri returnuir = contentResolver.insert(uri, values);
					Log.i("main", "-------------->" + returnuir.getPath());
				}

				public void delete() {
					ContentResolver contentResolver = getContext().getContentResolver();
					// 删除多行：content://com.example.contentproviderdemo.StudentProvider/student
					Uri uri = Uri
							.parse("content://com.example.contentproviderdemo.StudentProvider/student/2");
					contentResolver.delete(uri, null, null);
				}

				public void deletes() {
					ContentResolver contentResolver = getContext().getContentResolver();
					Uri uri = Uri
							.parse("content://com.example.contentproviderdemo.StudentProvider/student");
					String where = "address=?";
					String[] where_args = { "HK" };
					contentResolver.delete(uri, where, where_args);
				}

				public void update() {
					ContentResolver contentResolver = getContext().getContentResolver();
					Uri uri = Uri
							.parse("content://com.example.contentproviderdemo.StudentProvider/student/2");
					ContentValues values = new ContentValues();
					values.put("name", "李四");
					values.put("address", "上海");
					contentResolver.update(uri, values, null, null);
				}

				public void updates() {
					ContentResolver contentResolver = getContext().getContentResolver();
					Uri uri = Uri
							.parse("content://com.example.contentproviderdemo.StudentProvider/student");
					ContentValues values = new ContentValues();
					values.put("name", "王五");
					values.put("address", "深圳");
					String where = "address=?";
					String[] where_args = { "beijing" };
					contentResolver.update(uri, values, where, where_args);
				}

				public void query() {
					ContentResolver contentResolver = getContext().getContentResolver();
					Uri uri = Uri
							.parse("content://com.example.contentproviderdemo.StudentProvider/student/2");
					Cursor cursor = contentResolver.query(uri, null, null, null, null);
					while (cursor.moveToNext()) {
						Log.i("main",
								"-------------->"
										+ cursor.getString(cursor.getColumnIndex("name")));
					}
				}

				public void querys() {
					ContentResolver contentResolver = getContext().getContentResolver();
					Uri uri = Uri
							.parse("content://com.example.contentproviderdemo.StudentProvider/student");
					String where = "address=?";
					String[] where_args = { "深圳" };
					Cursor cursor = contentResolver.query(uri, null, where, where_args,
							null);
					while (cursor.moveToNext()) {
						Log.i("main",
								"-------------->"
										+ cursor.getString(cursor.getColumnIndex("name")));
					}
				}

				public void calltest() {
					ContentResolver contentResolver = getContext().getContentResolver();
					Uri uri = Uri
							.parse("content://com.example.contentproviderdemo.StudentProvider/student");
					Bundle bundle = contentResolver.call(uri, "method", null, null);
					String returnCall = bundle.getString("returnCall");
					Log.i("main", "-------------->" + returnCall);
				}

			}
		
		
	Android 提供了3个用于辅助ContentProvide的工具类：
		ContentUris
		UriMatcher
		ContentObserver
		
		ContentUris类：
			作用：操作 URI
			具体使用 
				核心方法有两个：withAppendedId（） &parseId（）
				// withAppendedId（）作用：向URI追加一个id
				Uri uri = Uri.parse("content://cn.scu.myprovider/user") 
				Uri resultUri = ContentUris.withAppendedId(uri, 7);  
				// 最终生成后的Uri为：content://cn.scu.myprovider/user/7

				// parseId（）作用：从URL中获取ID
				Uri uri = Uri.parse("content://cn.scu.myprovider/user/7") 
				long personid = ContentUris.parseId(uri); 
				//获取的结果为:7
			
		
		UriMatcher类
			作用：
				在ContentProvider 中注册URI
				根据 URI 匹配 ContentProvider 中对应的数据表
			具体使用：
				// 步骤1：初始化UriMatcher对象
					UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH); 
					//常量UriMatcher.NO_MATCH  = 不匹配任何路径的返回码
					// 即初始化时不匹配任何东西

				// 步骤2：在ContentProvider 中注册URI（addURI（））
					int URI_CODE_a = 1；
					int URI_CODE_b = 2；
					matcher.addURI("cn.scu.myprovider", "user1", URI_CODE_a); 
					matcher.addURI("cn.scu.myprovider", "user2", URI_CODE_b); 
					// 若URI资源路径 = content://cn.scu.myprovider/user1 ，则返回注册码URI_CODE_a
					// 若URI资源路径 = content://cn.scu.myprovider/user2 ，则返回注册码URI_CODE_b

				// 步骤3：根据URI 匹配 URI_CODE，从而匹配ContentProvider中相应的资源（match（））

				@Override   
					public String getType(Uri uri) {   
					  Uri uri = Uri.parse(" content://cn.scu.myprovider/user1");   

					  switch(matcher.match(uri)){   
					 // 根据URI匹配的返回码是URI_CODE_a
					 // 即matcher.match(uri) == URI_CODE_a
					  case URI_CODE_a:   
						return tableNameUser1;   
						// 如果根据URI匹配的返回码是URI_CODE_a，则返回ContentProvider中的名为tableNameUser1的表
					  case URI_CODE_b:   
						return tableNameUser2;
						// 如果根据URI匹配的返回码是URI_CODE_b，则返回ContentProvider中的名为tableNameUser2的表
					}   
				}
		
		
		ContentObserver类
			定义：内容观察者
			作用：观察 Uri引起 ContentProvider 中的数据变化 & 通知外界（即访问该数据访问者） 
			当ContentProvider 中的数据发生变化（增、删 & 改）时，就会触发该 ContentObserver类
			具体使用：
				// 步骤1：注册内容观察者ContentObserver
				getContentResolver().registerContentObserver(uri, notifyForDescendants, observer);
				功能：为指定的Uri注册一个ContentObserver派生类实例，当给定的Uri发生改变时，回调该实例对象去处理。
				参数：
					uri          			
						需要观察的Uri(需要在UriMatcher里注册，否则该Uri也没有意义了)
                    notifyForDescendents  	
						为false 表示精确匹配，即只匹配该Uri;为true 表示可以同时匹配其派生的Uri，举例如下：
						假设UriMatcher 里注册的Uri共有一下类型：
							1 、content://com.qin.cb/student (学生)
							2 、content://com.qin.cb/student/# 
							3、 content://com.qin.cb/student/schoolchild(小学生，派生的Uri)

						假设我们当前需要观察的Uri为content://com.qin.cb/student，如果发生数据变化的Uri为content://com.qin.cb/student/schoolchild ，当notifyForDescendents为 false，那么该ContentObserver会监听不到，但是当notifyForDescendents 为ture，能捕捉该Uri的数据库变化。
					observer
						ContentObserver的派生类实例
						
							// 提供方自定义的ContentOberver监听器
							private final class Observer extends ContentObserver {
								public SmsObserver(Handler handler) {
									super(handler);
								}
								@Override
								public void onChange(boolean selfChange, Uri uri) {
						 
									// 查询发送邮箱中的短息(处于正在发送状态的短信放在发送箱)
									Cursor cursor = getContentResolver().query(
											Uri.parse("content://com.android.xiong.ConentProviderTestA.firstContentProvider/userinfo"), null, null, null, null);
									}
								}
						 
							}


				// 步骤2：当该URI的ContentProvider数据发生变化时，通知外界（即访问该ContentProvider数据的访问者）
				public class UserContentProvider extends ContentProvider { 
					public Uri insert(Uri uri, ContentValues values) { 
						db.insert("user", "userid", values); 
						// 通知访问者
						getContext().getContentResolver().notifyChange(uri, null); 
						
					} 
				}

					// 步骤3：解除观察者
					getContentResolver().unregisterContentObserver(observer);
					功能：取消对给定Uri的观察
					参数： observer ContentObserver的派生类实例
			
		
		