## 概述

从Android 6.0(API23)开始，当app运行时用户授予用户的权限，而不是在安装程序的时候。

系统权限分为2种，分别为normal和dangerous.

Normal permission:对于用户隐私没有危险的，在清单文件中申请就可以直接授权。

Dangerous permission:app需要访问用户的隐私信息等，即使在清单文件注册，也需要在运行是通过用户授权。

#### 权限检查

在android6.0及以上的sdk提供了一个检查权限的方法：ContextCompat.checkSelfPermission()

```java
int permission = ContextCompat.checkSelfPermission(MainActivity.this,
      	  Manifest.permission.RECORD_AUDIO);

if (permission == PackageManager.PERMISSION_GRANTED) { //PackageManager.PERMISSION_DENIED--->表示权限被否认了
    	//表示已经授权
}

//如果在Activity中申请权限可以的调用：
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
	int permission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
}
```

#### 请求权限

在Android 6.0及以上，如果检查没有权限，需要主动请求权限。在请求权限是会弹出一个对话框提示用户，是否授权。 

请求权限的方法：requestPermissions()。在请求权限后会有一个回调方法onRequestPermissionsResult()

```java
ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

//或者 在Activity的方法调用
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1);
}

/*	参数1：requestCode-->是requestPermissions()方法传递过来的请求码。
	参数2：permissions-->是requestPermissions()方法传递过来的需要申请权限
	参数3：grantResults-->是申请权限后，系统返回的结果，
		PackageManager.PERMISSION_GRANTED表示授权成功
		PackageManager.PERMISSION_DENIED表示授权失败。
*/
```

#### 权限申请结果回调

```java
@Override
public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
	switch (requestCode) {
		case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// permission was granted, yay! Do the
				// contacts-related task you need to do.
			} else {
				// permission denied, boo! Disable the
				// functionality that depends on this permission.
			}
			return;
        }
    }
}
```
ok，对于权限的申请结果，首先验证requestCode定位到你的申请，然后验证grantResults对应于申请的结果，这里的数组对应于申请时的

第二个权限字符串数组。如果你同时申请两个权限，那么grantResults的length就为2，分别记录你两个权限的申请结果。

#### 说明

1. adb shell pm list permissions -d -g

2. 对于授权机制是这样的。如果你申请某个危险的权限，假设你的app早已被用户授权了同一组的某个危险权限，那么系统会立即授权，而不需要用户去点击授权。比如你的app对READ_CONTACTS已经授权了，当你的app申请WRITE_CONTACTS时，系统会直接授权通过。此外，对于申请时弹出的dialog上面的文本说明也是对整个权限组的说明，而不是单个权限（ps:这个dialog是不能进行定制的）。

3. 显示权限申请原因

   ```java
   if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,Manifest.permission.READ_CONTACTS)) 
   	// Show an expanation to the user *asynchronously* -- don't block
   	// this thread waiting for the user's response! After the user
   	// sees the explanation, try again to request the permission.
   }
   ```
### 示例

```java
//	Here, thisActivity is the current activity
if (ContextCompat.checkSelfPermission(thisActivity,Manifest.permission.READ_CONTACTS)
    != PackageManager.PERMISSION_GRANTED) {
    // Should we show an explanation?
	if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
			Manifest.permission.READ_CONTACTS)) {
		// Show an expanation to the user *asynchronously* -- don't block
		// this thread waiting for the user's response! After the user
		// sees the explanation, try again to request the permission.

	} else {
		// No explanation needed, we can request the permission.
		ActivityCompat.requestPermissions(thisActivity,
				new String[]{Manifest.permission.READ_CONTACTS},
				MY_PERMISSIONS_REQUEST_READ_CONTACTS);

		// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
		// app-defined int constant. The callback method gets the
		// result of the request.
	}
}
```

