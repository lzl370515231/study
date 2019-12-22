# WebView

## 简介

WebView是一个基于webkit引擎、展现web页面的控件。

> Android 的Webview 在低版本和高版本采用了不同的webkit版本内核，4.4后直接使用了Chrome。

## 作用

- 显示和渲染 Web页面
- 直接使用 html 文件（网络上或本地 assets 中）作布局
- 可和 JavaScript 交互调用

> WebView控件功能强大，除了具有一般View的属性和设置外，还可以对url请求、页面加载、渲染、页面交互进行强大的处理。

## 使用介绍

### Webview的状态

```java
//激活WebView为活跃状态，能正常执行网页的响应
webView.onResume();

//当页面被失去焦点被切换到后台不可见状态，需要执行onPause
//通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行
webView.onPause();

//当应用程序(存在webview)被切换到后台时，这个方法不仅仅针对当前的webview而是全局的全应用程序的webview
//它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗
webView.pauseTimers();
//恢复pauseTimers状态
webView.resumeTimers();

//销毁Webview
//在关闭了Activity时，如果Webview的音乐或视频，还在播放。就必须销毁Webview
//但是注意：webview调用destory时,webview仍绑定在Activity上
//这是由于自定义webview构建时传入了该Activity的context对象
//因此需要先从父容器中移除webview,然后再销毁webview:
rootLayout.removeView(webView); 
webView.destroy();
```

### 加载方式

```java
//方式1. 加载一个网页：
webView.loadUrl("http://www.google.com/");

//方式2：加载apk包中的html页面
webView.loadUrl("file:///android_asset/test.html");

//方式3：加载手机本地的html页面
webView.loadUrl("content://com.android.htmlfileprovider/sdcard/test.html");
```

### 关于前进/后退网页

```java
//是否可以后退
Webview.canGoBack() 
//后退网页
Webview.goBack()

//是否可以前进                     
Webview.canGoForward()
//前进网页
Webview.goForward()

//以当前的index为起始点前进或者后退到历史记录中指定的steps
//如果steps为负数则为后退，正数则为前进
Webview.goBackOrForward(intsteps)
```

#### 在当前 Activity中处理并消费掉该 Back事件

```java
public boolean onKeyDown(int keyCode,KeyEvent event){
    if((keyCode == KEYCODE_BACK) && mWebview.canGoBack()){
        mWebview.goBack();
        return true;
    }
    return super.onKeyDown(keyCode,event);
}
```

### 清除缓存数据

```java
clearCache(true);//清除网页访问留下的缓存，由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
clearHistory()//清除当前webview访问的历史记录，只会webview访问历史记录里的所有记录除了当前访问记录.
clearFormData()//这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据。
```

### 判断WebView 是否已经滚动到页面底端或者顶端

> getScrollY() //方法返回的是当前可见区域的顶端距整个页面顶端的距离,也就是当前内容滚动的距离。
>
> getHeight()或者getBottom() //方法都返回当前WebView这个容器的高度
>
> getContentHeight()返回的是整个html的高度,但并不等同于当前整个页面的高度,因为WebView有缩放功能,所以当前整个页面的高度实际上应该是原始html的高度再乘上缩放比例

```java
if(webview.getContentHeight() * webview.getScale() == (webview.getHeight() + webview.getScrollY())){
    //已经处于底端
}
if(webview.getScrollY() == 0){
    //处于顶端
}
```

### 长按事件

##### 1. 给WebView添加监听

```java
mWebview.setOnLongClickListener(new View.OnLongClickListener(){
    @Override
    public boolean onLongClick(View v){
        
    }
});
```



## 子类

### WebSettings

#### 作用

对Webview 进行配置和管理

#### 配置步骤&常见方法

##### 1. 添加访问网络权限

AndroidManifest.xml

##### 2. 生成一个Webview 组件（两种方式）

```java
//方式1：直接在在Activity中生成
WebView webView = new WebView(this);

//方法2：在Activity的layout文件里添加webview控件：
WebView webview = (WebView) findViewById(R.id.webView1);
```

##### 3. 进行配置-利用 WebSettings子类

```java
//声明WebSettings子类
WebSettings webSettings = webView.getSettings();

//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
webSettings.setJavaScriptEnabled(true);  

//支持插件
webSettings.setPluginsEnabled(true); 

//提高渲染的优先级
webSettings.setRenderPriority(RenderPriority.HIGH);

//设置自适应屏幕，两者合用
webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小 
webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

//缩放操作
webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

//设置文本的缩放倍数，默认为 100
webSettings.setTextZoom(2);

//支持内容重新布局
webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

//多窗口
webSettings.supportMultipleWindow();

//其他细节操作
webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存 
webSettings.setAllowFileAccess(true); //设置可以访问文件 
webSettings.setNeedInitialFocus(true);	//当webview 调用 requestFocus 时为 webview 设置节点
webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口 
webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

//设置webview的字体，默认字体为“sans-serif”
webSettings.setStandardFontFamily("");
//设置 Webview 字体的大小，默认大小为 16
webSettings.setDefaultFontSize(20);
//设置 WebView 支持的最小字体大小，默认为 8
webSettings.setMinimumFontSize(12)
```

##### 启用缓存

```java
//优先使用缓存: 
WebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//缓存模式如下：
    //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
    //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
    //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
    //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据

//不使用缓存: 
WebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
```

###### 结合使用（离线加载）

```java
if (NetStatusUtil.isConnected(getApplicationContext())) {
    webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
} else {
    webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
}

webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能

String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
webSettings.setAppCachePath(cacheDirPath); //设置  Application Caches 缓存目录
```

###### 注意

- 每个 Application 只调用一次 WebSettings.setAppCachePath()，WebSettings.setAppCacheMaxSize()

### WebViewClient

#### 作用

处理各种通知 & 请求事件

#### 方法

```java
WebViewClient mWebViewClient = new WebViewClient()
{
    //在网页上的所有加载都经过这个方法,这个函数我们可以做很多操作。
    //比如获取url，查看url.contains(“add”)，进行添加操作
    shouldOverrideUrlLoading(WebView view, String url)
    
	//重写此方法才能够处理在浏览器中的按键事件。
    shouldOverrideKeyEvent(WebView view, KeyEvent event)
    
	//这个事件就是开始载入页面调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。
    onPageStarted(WebView view, String url, Bitmap favicon)
    
	//在页面加载结束时调用。同样道理，我们可以关闭loading 条，切换程序动作。
    onPageFinished(WebView view, String url)
    
	// 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
    onLoadResource(WebView view, String url)
    
	// 拦截替换网络请求数据,  API 11开始引入，API 21弃用
    shouldInterceptRequest(WebView view, String url)
    // 拦截替换网络请求数据,  从API 21开始引入
    shouldInterceptRequest (WebView view, WebResourceRequest request)
   
	// (报告错误信息)
    onReceivedError(WebView view, int errorCode, String description, String failingUrl)
    
	//(更新历史记录)
    doUpdateVisitedHistory(WebView view, String url, boolean isReload)
    
	//(应用程序重新请求网页数据)
    onFormResubmission(WebView view, Message dontResend, Message resend)
    
	//（获取返回信息授权请求）
    onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,String realm)
    
	//重写此方法可以让webview处理https请求。
    onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
    
	// (WebView发生改变时调用)
    onScaleChanged(WebView view, float oldScale, float newScale)
    
	//（Key事件未被加载时调用）
    onUnhandledKeyEvent(WebView view, KeyEvent event)
    
}
```



#### shouldOverrideUrlLoading()

##### 作用

打开网页时不调用系统浏览器， 而是在本WebView中显示；在网页上的所有加载都经过这个方法,这个函数我们可以做很多操作。

```java
//定义Webview组件（2种）

//选择加载方式（4种）

//复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
webView.setWebViewClient(new WebViewClient(){
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}
});
```

#### onPageStarted()

##### 作用

开始载入页面调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。

```java
webView.setWebViewClient(new WebViewClient(){
	@Override
	public void  onPageStarted(WebView view, String url, Bitmap favicon) {
		//设定加载开始的操作
	}
});
```

#### onPageFinished()

##### 作用

在页面加载结束时调用。我们可以关闭loading 条，切换程序动作。

```java
webView.setWebViewClient(new WebViewClient(){
	@Override
	public void onPageFinished(WebView view, String url) {
		//设定加载结束的操作
	}
});
```

#### onLoadResource()

##### 作用

在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。

```java
webView.setWebViewClient(new WebViewClient(){
	@Override
	public boolean onLoadResource(WebView view, String url) {
		//设定加载资源的操作
	}
});
```

#### onReceiveError()

##### 作用

加载页面的服务器出现错误时（如404）调用。

> App里面使用webview控件的时候遇到了诸如404这类的错误的时候，若也显示浏览器里面的那种错误提示页面就显得很丑陋了，那么这个时候我们的app就需要加载一个本地的错误提示页面，即webview如何加载一个本地的页面

```java
//步骤1：写一个html文件（error_handle.html），用于出错时展示给用户看的提示页面
//步骤2：将该html文件放置到代码根目录的assets文件夹下

//步骤3：复写WebViewClient的onRecievedError方法
//该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
webView.setWebViewClient(new WebViewClient(){
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
        switch(errorCode){
            case HttpStatus.SC_NOT_FOUND:
                view.loadUrl("file:///android_assets/error_handle.html");
                break;
        }
    }
});
```

#### onReceivedSslError()

##### 作用

处理 https 请求

> webView默认是不处理https请求的，页面显示空白，需要进行如下设置：

```java
webView.setWebViewClient(new WebViewClient(){
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
        handler.proceed();	//表示等待证书响应
        // handler.cancel();	//表示挂起连接，为默认方式
        // handler.handleMessage(null);	//可做其他处理
    }
});
```

### WebChromeClient

#### 作用

辅助 Webview 处理 Javascript 的对话框，网站图标，网站标题等等。方法中的代码都是由Android端自己处理。

#### 方法

```java
WebChromeClient mWebChromeClient = new WebChromeClient() {

    //获得网页的加载进度，显示在右上角的TextView控件中
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress < 100) {
            String progress = newProgress + "%";
        } else {
        }
    }

    //获取Web页中的title用来设置自己界面中的title
    //当加载出错的时候，比如无网络，这时onReceiveTitle中获取的标题为 找不到该网页,
    //因此建议当触发onReceiveError时，不要使用获取到的title
    @Override
    public void onReceivedTitle(WebView view, String title) {
        MainActivity.this.setTitle(title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        //
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        //
        return true;
    }

    @Override
    public void onCloseWindow(WebView window) {
    }

    //处理alert弹出框，html 弹框的一种方式
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        //
        return true;
    }

    //处理confirm弹出框
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
            result) {
        //
        return true;
    }

    //处理prompt弹出框
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        //
        return true;
    }
};
```



#### onProgressChanged()

##### 作用

获得网页的加载进度并显示

```java
webview.setWebChromeClient(new WebChromeClient(){
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress < 100) {
            String progress = newProgress + "%";
            progress.setText(progress);
        } else {
            
        }
    }
});
```

#### onReceivedTitle（）

##### 作用

获取 Web 页中的标题

> 每个网页的页面都有一个标题，比如www.baidu.com这个页面的标题即“百度一下，你就知道”，那么如何知道当前webview正在加载的页面的title并进行设置呢？

```java
webview.setWebChromeClient(new WebChromeClient(){
    @Override
    public void onReceivedTitle(WebView view, String title) {
       titleview.setText(title);
    }
});
```



## 缓存

### 缓存模式

> LOAD_CACHE_ONLY：不使用网络，只读取本地缓存数据
>
> LOAD_DEFAULT：默认。根据 cache-control 决定是否从网络上取数据
>
> LOAD_NO_CACHE：不使用缓存，只从网络获取数据
>
> LOAD_CACHE_ELSE_NETWORK：只要本地有，无论是否过期，或者 no-cache，都使用缓存中的数据

### 启用缓存

```java
//优先使用缓存: 
WebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); 
//缓存模式如下：
    //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
    //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
    //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
    //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。

//不使用缓存: 
WebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
```

#### 结合使用

```java
if (NetStatusUtil.isConnected(getApplicationContext())) {
    webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);	//根据cache-control决定是否从网络上取数据。
} else {
    webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);	//没网，则从本地获取，即离线加载
}

webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能

String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
webSettings.setAppCachePath(cacheDirPath); //设置  Application Caches 缓存目录
```

##### 注意

- 每个 Application 只调用一次 WebSettings.setAppCachePath()，WebSettings.setAppCacheMaxSize()



### 清楚缓存数据

```java
//清除网页访问留下的缓存
//由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序
Webview.clearCache(true);

//清除当前webview访问的历史记录
//只会webview访问历史记录里的所有记录除了当前访问记录
Webview.clearHistory()；

//这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
Webview.clearFormData()；
```



## Android与 JS的交互

Android 与 Js 的沟通桥梁 Webview。

### Android去调用JS的代码

#### 方法

- 通过Webview 的 loadUrl()
- 通过 Webview 的 evaluateJavascript()

#### loadUrl()

##### 1. html文件

```html
<html>
    <head>
        <meta charset="utf-8">
        <title>Carson_Ho</title>
        
        //JS 代码
        <script>
            //Android 需要调用的方法
            function callJS(){
                alert("Android调用了 JS 的callJS方法");
            }
        </script>
    </head>
</html>
```

##### 2. Android通过 Webview 设置调用的 JS代码

```java
mWebView =(WebView) findViewById(R.id.webview);
WebSettings webSettings = mWebView.getSettings();

// 设置与Js交互的权限
webSettings.setJavaScriptEnabled(true);
// 设置允许JS弹窗
webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

// 先载入JS代码
// 格式规定为:file:///android_asset/文件名.html
mWebView.loadUrl("file:///android_asset/javascript.html");

button = (Button) findViewById(R.id.button);

button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        // 通过Handler发送消息
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                // 注意调用的JS方法名要对应上
                // 调用javascript的callJS()方法
                mWebView.loadUrl("javascript:callJS()");
            }
        });
    }
});
// 由于设置了弹窗检验调用结果,所以需要支持js对话框
// webview只是载体，内容的渲染需要使用webviewChromClient类去实现
// 通过设置WebChromeClient对象处理JavaScript的对话框
//设置响应js 的Alert()函数
mWebView.setWebChromeClient(new WebChromeClient() {
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        b.setTitle("Alert");
        b.setMessage(message);
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        b.setCancelable(false);
        b.create().show();
        return true;
    }
});
```

##### 注意

JS代码调用一定要在 onPageFinished（） 回调之后才能调用，否则不会调用。

#### 通过WebView的evaluateJavascript（）

##### 优点

该方法比第一种方法效率更高、使用更简洁

> 1. 因为该方法的执行不会使页面刷新，而第一种方法(loadUrl)的执行则会
> 2. Android 4.4 后才使用

```java
// 只需要将第一种方法的loadUrl()换成下面该方法即可
mWebView.evaluateJavascript（"javascript:callJS()", new ValueCallback<String>() {
    @Override
    public void onReceiveValue(String value) {
        //此处为 js 返回的结果
    }
});
```

#### 方法对比

| 调用方式                 | 优点     | 缺点                                  | 使用场景                           |
| ------------------------ | -------- | ------------------------------------- | ---------------------------------- |
| 使用 loadUrl()           | 方便简洁 | 效率低；获取返回值麻烦                | 不需要获取返回值，对性能要求较低时 |
| 使用evaluateJavascript() | 效率高   | 向下兼容性差（仅Android 4.4以上可用） | Android 4.4以上                    |

#### 使用建议

两种方法混合使用，即Android 4.4以下使用方法1，Android4.4以上使用方法2

```java
// Android版本变量
final int version = Build.VERSION.SDK_INT;
// 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
if (version < 18) {
    mWebView.loadUrl("javascript:callJS()");
} else {
    mWebView.evaluateJavascript（"javascript:callJS()", new ValueCallback<String>() {
        @Override
        public void onReceiveValue(String value) {
            //此处为 js 返回的结果
        }
    });
}
```



### JS去调用Android的代码

#### 方法

- 通过Webview的 addJavascriptInterface() 进行对象映射。
- 通过WebviewClient 的 shouldOverrideUrlLoading() 方法回调拦截 url。
- 通过WebChromeClient 的 onJsAlert()、onJsConfirm()、onJsPrompt() 方法回调拦截 JS对话框 alert()、confirm()、prompt() 消息。

#### 通过Webview 的 addJavascriptInterface() 进行对象映射

##### 1. 定义一个与JS对象映射的Android类

AndroidJs.java

```java
// 继承自Object类
public class AndroidtoJs extends Object {

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void hello(String msg) {
        System.out.println("JS调用了Android的hello方法");
    }
}
```

##### 2. html 文件

```html
<!DOCTYPE html>
<html>
   <head>
      <meta charset="utf-8">
      <title>Carson</title>  
      <script>
         
        
         function callAndroid(){
        // 由于对象映射，所以调用test对象等于调用Android映射的对象
            test.hello("js调用了android中的hello方法");
         }
      </script>
   </head>
   <body>
      //点击按钮则调用callAndroid函数
      <button type="button" id="button1" "callAndroid()"></button>
   </body>
</html>
```

##### 3. 在Android里通过Webview设置 Android类与 JS代码的映射

```java
 mWebView = (WebView) findViewById(R.id.webview);
WebSettings webSettings = mWebView.getSettings();

// 设置与Js交互的权限
webSettings.setJavaScriptEnabled(true);

// 通过addJavascriptInterface()将Java对象映射到JS对象
//参数1：Javascript对象名
//参数2：Java对象名
mWebView.addJavascriptInterface(new AndroidtoJs(), "test");//AndroidtoJS类对象映射到js的test对象

// 加载JS代码
// 格式规定为:file:///android_asset/文件名.html
mWebView.loadUrl("file:///android_asset/javascript.html");
```

##### 特点

###### 优点

使用简单，仅将 Android 对象和 JS对象映射即可

###### 缺点

存在严重的漏洞问题。**详见 问题---》addJavascriptInterface()漏洞**

#### 通过 WebviewClient 的方法 shouldOverrideUrlLoading()回调拦截 url

##### 原理

- Android通过 WebViewClient 的回调方法shouldOverrideUrlLoading ()拦截 url
- 解析该 url 的协议
- 如果检测到是预先约定好的协议，就调用相应方法

##### 1. 在JS约定所需要的Url协议

```html
<!DOCTYPE html>
<html>
   <head>
      <meta charset="utf-8">
      <title>Carson_Ho</title>
      
     <script>
         function callAndroid(){
            /*约定的url协议为：js://webview?arg1=111&arg2=222*/
            document.location = "js://webview?arg1=111&arg2=222";
         }
      </script>
</head>

<!-- 点击按钮则调用callAndroid（）方法  -->
   <body>
     <button type="button" id="button1" "callAndroid()">点击调用Android代码</button>
   </body>
</html>
```

当该JS通过Android的`mWebView.loadUrl("file:///android_asset/javascript.html")`加载后，就会回调`shouldOverrideUrlLoading （）`

##### 2. 在Android通过WebViewClient复写shouldOverrideUrlLoading （）

```java
mWebView = (WebView) findViewById(R.id.webview);
WebSettings webSettings = mWebView.getSettings();

// 设置与Js交互的权限
webSettings.setJavaScriptEnabled(true);
// 设置允许JS弹窗
webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
// 步骤1：加载JS代码
// 格式规定为:file:///android_asset/文件名.html
mWebView.loadUrl("file:///android_asset/javascript.html");

// 复写WebViewClient类的shouldOverrideUrlLoading方法
mWebView.setWebViewClient(new WebViewClient() {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 步骤2：根据协议的参数，判断是否是所需要的url
        // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
        //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）
        
        Uri uri = Uri.parse(url);
        // 如果url的协议 = 预先约定的 js 协议
        // 就解析往下解析参数
        if ( uri.getScheme().equals("js")) {
            // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
            // 所以拦截url,下面JS开始调用Android需要的方法
            if (uri.getAuthority().equals("webview")) {
                //  步骤3：
                // 执行JS所需要调用的逻辑
                System.out.println("js调用了Android的方法");
                // 可以在协议上带有参数并传递到Android上
                HashMap<String, String> params = new HashMap<>();
                Set<String> collection = uri.getQueryParameterNames();
            }
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }
});
```

##### 特点

###### 优点

不存在方式1 的漏洞

###### 缺点

JS获取Android方法的返回值复杂

#### 通过 WebChromeClient 的 onJsAlert()、onJsConfirm()、onJsPrompt()方法回调拦截JS对话框 alert()、confirm()、prompt()消息

| 方法      | 作用       | 返回值         | 备注                                                         |
| --------- | ---------- | -------------- | ------------------------------------------------------------ |
| alert()   | 弹出警告框 | 没有           | 在文本加入 \n 可换行                                         |
| confirm() | 弹出确认框 | 两个返回值     | 返回布尔值：<br>通过该值可判断点击时确认还是取消：<br>true表示点击了确认，false表示点击了取消 |
| prompt()  | 弹出输入框 | 任意设置返回值 | 点击“确认”：返回输入框中的值<br>点击“取消”：返回null         |

##### 原理

Android通过 `WebChromeClient` 的`onJsAlert()`、`onJsConfirm()`、`onJsPrompt（）`方法回调分别拦截JS对话框
（即上述三个方法），得到他们的消息内容，然后解析即可。

> 1. 常用的拦截是：拦截 JS 的输入框（即prompt() 方法）
> 2. 因为只有 prompt() 可以返回任意类型的值，操作最全面方便、更加灵活；而alert() 对话框没有返回值；confirm()对话框只能返回两种状态（确定/取消）两个值

##### 1. 加载JS代码

```html
<!DOCTYPE html>
<html>
   <head>
      <meta charset="utf-8">
      <title>Carson_Ho</title>
      
     <script>
        
	function clickprompt(){
    // 调用prompt（）
    var result=prompt("js://demo?arg1=111&arg2=222");
    alert("demo " + result);
}

      </script>
</head>

<!-- 点击按钮则调用clickprompt()  -->
   <body>
     <button type="button" id="button1" "clickprompt()">点击调用Android代码</button>
   </body>
</html>
```

当加载了上述 JS 代码后，就会触发回调 onJsPrompt()，具体如下：

> 1. 如果是拦截警告框（即 alert()），则触发回调 onJsAlert()
> 2. 如果是拦截确认框（即 confirm()）,则触发回调 onJsConfirm()

##### 2. 复写 WebChromeClient 中的 onJsPrompt()

```java
mWebView = (WebView) findViewById(R.id.webview);
WebSettings webSettings = mWebView.getSettings();
// 设置与Js交互的权限
webSettings.setJavaScriptEnabled(true);
// 设置允许JS弹窗
webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

// 先加载JS代码
// 格式规定为:file:///android_asset/文件名.html
mWebView.loadUrl("file:///android_asset/javascript.html");
mWebView.setWebChromeClient(new WebChromeClient() {
    // 拦截输入框(原理同方式2)
    // 参数message:代表promt（）的内容（不是url）
    // 参数result:代表输入框的返回值
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        // 根据协议的参数，判断是否是所需要的url(原理同方式2)
        // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
        //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）
        Uri uri = Uri.parse(message);
        // 如果url的协议 = 预先约定的 js 协议
        // 就解析往下解析参数
        if ( uri.getScheme().equals("js")) {
            // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
            // 所以拦截url,下面JS开始调用Android需要的方法
            if (uri.getAuthority().equals("webview")) {
                //
                // 执行JS所需要调用的逻辑
                System.out.println("js调用了Android的方法");
                // 可以在协议上带有参数并传递到Android上
                HashMap<String, String> params = new HashMap<>();
                Set<String> collection = uri.getQueryParameterNames();
                //参数result:代表消息框的返回值(输入值)
                result.confirm("js调用了Android的方法成功啦");
            }
            return true;
        }
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
    // 通过alert()和confirm()拦截的原理相同，此处不作过多讲述
    // 拦截JS的警告框
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }
    // 拦截JS的确认框
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return super.onJsConfirm(view, url, message, result);
    }
});
```

##### 总结

| 调用方式                                                     | 优点           | 缺点                                                         | 使用场景                        |
| ------------------------------------------------------------ | -------------- | ------------------------------------------------------------ | ------------------------------- |
| 通过 addJavascriptInterface() 进行添加对象映射               | 方便简洁       | Android4.2以下存在漏洞问题                                   | Android4.2 以上相对简单互调场景 |
| 通过 WebviewClient 的方法 shouldOverrideUrlLoading() 回调拦截 url | 不存在漏洞问题 | 使用复杂：需要进行协议的约束：从Native层往 Web 层传递值比较繁琐 | 不需要返回值情况下的互调场景    |
| 通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt()方法回调拦截JS对话框消息 | 不存在漏洞问题 | 使用复杂：需要进行协议的约束                                 | 能满足大多数情况下的互调场景    |

### 总结

![](.\png\Android与JS互调总结.png)

## 性能与解决方案





## 问题

### 漏洞

#### 分类

- 任意代码执行漏洞
- 密码明文存储漏洞
- 域控制不严格漏洞

#### 任意代码漏洞

##### 出现漏洞分类

- Webview 中 addJavascriptInterface() 接口
- Webview 内置导出的 searchBoxJavaBridge_对象
- Webview内置导出的 accessibility 和 accessibilityTraversal Object对象

##### addJavascriptInterface 接口引起远程代码执行漏洞

###### 原因

JS调用Android的其中一个方式是通过`addJavascriptInterface`接口进行对象映射：

```java
webview.addJavascriptInterface(new JSObject(),"myobj");
// 参数1：Android的本地对象
// 参数2：JS的对象
// 通过对象映射将Android中的本地对象和JS中的对象进行关联，从而实现JS调用Android的对象和方法
```

**漏洞产生原因是：当JS拿到Android这个对象后，就可以调用这个Android对象中所有的方法，包括系统类（java.lang.Runtime 类），从而进行任意代码执行。**

> 如可以执行命令获取本地设备的SD卡中的文件等信息从而造成信息泄露

###### 具体获取系统类的描述（结合Java反射机制）

- Android的对象有一公共的方法：getClass()
- 该方法可以获取到当前类类型 Class
- 该类有一关键的方法：Class.forName
- 该方法可以加载一个类（可加载 java.lang.Runtime 类）
- 而该类是可以执行本地命令的

```javascript
function execute(cmdArgs)  
{
    // 步骤1：遍历 window 对象
    // 目的是为了找到包含 getClass （）的对象
    // 因为Android映射的JS对象也在window中，所以肯定会遍历到
    for (var obj in window) {
        if ("getClass" in window[obj]) {
            // 步骤2：利用反射调用forName（）得到Runtime类对象
            alert(obj);
            return  window[obj].getClass().forName("java.lang.Runtime");
            // 步骤3：以后，就可以调用静态方法来执行一些命令，比如访问文件的命令
            getMethod("getRuntime",null).invoke(null,null).exec(cmdArgs);  
            // 从执行命令后返回的输入流中得到字符串，有很严重暴露隐私的危险。
            // 如执行完访问文件的命令之后，就可以得到文件名的信息了。
        }
    }
}   
```

###### 危害

- 当一些 APP 通过扫描二维码打开一个外部网页时，攻击者就可以执行这段 js 代码进行漏洞攻击。
- 在微信盛行、扫一扫行为普及的情况下，该漏洞的危险性非常大

###### 解决方案

**Android 4.2版本之后**

Google 在Android 4.2 版本中规定对被调用的函数以 `@JavascriptInterface`进行注解从而避免漏洞攻击

**Android 4.2版本之前**

在Android 4.2版本之前采用**拦截prompt（）**进行漏洞修复。

**具体步骤：**

- 继承 WebView ，重写 `addJavascriptInterface` 方法，然后在内部自己维护一个对象映射关系的 Map

  > 将需要添加的 JS 接口放入该Map中

- 每次当 WebView 加载页面前加载一段本地的 JS 代码，原理是：

  > - 让JS调用一Javascript方法：该方法是通过调用prompt（）把JS中的信息（含特定标识，方法名称等）传递到Android端；
  > - 在Android的onJsPrompt（）中 ，解析传递过来的信息，再通过反射机制调用Java对象的方法，这样实现安全的JS调用Android代码。

> 关于Android返回给JS的值：可通过prompt（）把Java中方法的处理结果返回到Js中

**示例：**

```java
javascript:(function JsAddJavascriptInterface_(){
    // window.jsInterface 表示在window上声明了一个Js对象
    
    //   jsInterface = 注册的对象名
    // 它注册了两个方法，onButtonClick(arg0)和onImageClick(arg0, arg1, arg2)
    // 如果有返回值，就添加上return
    if (typeof(window.jsInterface)!='undefined') {
        console.log('window.jsInterface_js_interface_name is exist!!');
    }else {
        window.jsInterface = {
            // 声明方法形式：方法名: function(参数)
            onButtonClick:function(arg0) {
                // prompt（）返回约定的字符串
                // 该字符串可自己定义
                // 包含特定的标识符MyApp和 JSON 字符串（方法名，参数，对象名等）
                return prompt('MyApp:'+JSON.stringify({obj:'jsInterface',func:'onButtonClick',args:[arg0]}));
            },onImageClick:function(arg0,arg1,arg2) {
                return prompt('MyApp:'+JSON.stringify({obj:'jsInterface',func:'onImageClick',args:[arg0,arg1,arg2]}));
            },
        };
    }
})()

// 当JS调用 onButtonClick（） 或 onImageClick（） 时，就会回调到Android中的 onJsPrompt （）
// 我们解析出方法名，参数，对象名
// 再通过反射机制调用Java对象的方法
```

**关于该方法的其他细节**

**细节1：加载上述JS代码的时机**

- 由于当 WebView 跳转到下一个页面时，之前加载的 JS 可能已经失效

- 所以，通常需要在以下方法中加载 JS

  ```java
  onLoadResource();
  doUpdateVisitedHistory();
  onPageStarted();
  onPageFinished();
  onReceivedTitle();
  onProgressChanged();
  ```

**细节2：需要过滤掉 Object 类的方法**

- 由于最终是通过反射得到Android指定对象的方法，所以同时也会得到基类的其他方法（最顶层的基类是 Object类）

- 为了不把 getClass（）等方法注入到 JS 中，我们需要把 Object 的共有方法过滤掉，需要过滤的方法列表如下：

  ```java
  getClass()
  hashCode()
  notify()
  notifyAl()
  equals()
  toString()
  wait()
  ```

###### 总结

- 对于Android 4.2以前，需要采用**拦截prompt（）**的方式进行漏洞修复
- 对于Android 4.2以后，则只需要对被调用的函数以 @JavascriptInterface进行注解

##### searchBoxJavaBridge_接口引起远程代码执行漏洞

###### 产生原因

- 在Android 3.0以下，Android系统会默认通过`searchBoxJavaBridge_`的Js接口给 WebView 添加一个JS映射对象：`searchBoxJavaBridge_`对象
- 该接口可能被利用，实现远程任意代码。

###### 解决方案

删除 searchBoxJavaBridge_接口

```java
// 通过调用该方法删除接口
removeJavascriptInterface（）；
```

##### accessibility和 accessibilityTraversal接口引起远程代码执行漏洞

#### 密码明文存储漏洞

##### 问题分析

WebView默认开启密码保存功能

```java
mWebview.setSavePassword(true);
```

- 开启后，在用户输入密码时，会弹出提示框：询问用户是否保存密码
- 如果选择”是”，密码会被明文保到 /data/data/com.package.name/databases/webview.db 中，这样就有被盗取密码的危险

##### 解决方案

关闭密码保存提醒

```java
WebSettings.setSavePassword(false) 
```

#### 域控制不严格漏洞

##### 问题分析

```java
public class WebViewActivity extends Activity {
    private WebView webView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = (WebView) findViewById(R.id.webView);

        //webView.getSettings().setAllowFileAccess(false);                    (1)
        //webView.getSettings().setAllowFileAccessFromFileURLs(true);         (2)
        //webView.getSettings().setAllowUniversalAccessFromFileURLs(true);    (3)
        Intent i = getIntent();
        String url = i.getData().toString(); //url = file:///data/local/tmp/attack.html 
        webView.loadUrl(url);
    }
 }

/**Mainifest.xml**/
// 将该 WebViewActivity 在Mainifest.xml设置exported属性
// 表示：当前Activity是否可以被另一个Application的组件启动
android:exported="true"
```

即 A 应用可以通过 B 应用导出的 Activity 让 B 应用加载一个恶意的 file 协议的 url，从而可以获取 B 应用的内部私有文件，从而带来数据泄露威胁

> 具体：当其他应用启动此 Activity 时， intent 中的 data 直接被当作 url 来加载（假定传进来的 url 为 file:///data/local/tmp/attack.html ），其他 APP 通过使用显式 ComponentName 或者其他类似方式就可以很轻松的启动该 WebViewActivity 并加载恶意url。

##### 着重分析WebView中getSettings类的方法对 WebView 安全性的影响：

- setAllowFileAccess()
- setAllowFileAccessFromFileURLs()
- setAllowUniversalAccessFromFileURLs()

###### 1. setAllowFileAccess()

```java
// 设置是否允许 WebView 使用 File 协议
webView.getSettings().setAllowFileAccess(true);     
// 默认设置为true，即允许在 File 域下执行任意 JavaScript 代码
```

使用 file 域加载的 js代码能够使用进行**同源策略跨域访问**，从而导致隐私信息泄露

> 1. 同源策略跨域访问：对私有目录文件进行访问
> 2. 针对IM类产品，泄露的是聊天信息、联系人等等 
> 3. 针对浏览器类软件，泄露的是 cookie 信息泄露

如果不允许使用 file 协议，则不会存在上述的威胁；

```java
webView.getSettings().setAllowFileAccess(true);    
```

但同时也限制了 WebView 的功能，使其不能加载本地的 html 文件，如下图：

> 移动版的 Chrome 默认禁止加载 file 协议的文件

**解决方案**

- 对于不需要使用 file 协议的应用，禁用 file 协议

  ```java
  setAllowFileAccess(false); 
  ```

- 对于需要使用 file 协议的应用，禁止 file 协议加载 JavaScript

  ```java
  setAllowFileAccess(true); 
  
  // 禁止 file 协议加载 JavaScript
  if (url.startsWith("file://") {
      setJavaScriptEnabled(false);
  } else {
      setJavaScriptEnabled(true);
  }
  ```

###### 2. setAllowFileAccessFromFileURLs()

```java
// 设置是否允许通过 file url 加载的 Js代码读取其他的本地文件
webView.getSettings().setAllowFileAccessFromFileURLs(true);
// 在Android 4.1前默认允许
// 在Android 4.1后默认禁止
```

当`AllowFileAccessFromFileURLs（）`设置为 true 时，攻击者的JS代码为：

```javascript
<script>
function loadXMLDoc()
{
    var arm = "file:///etc/hosts";
    var xmlhttp;
    if (window.XMLHttpRequest)
    {
        xmlhttp=new XMLHttpRequest();
    }
    xmlhttp.onreadystatechange=function()
    {
        //alert("status is"+xmlhttp.status);
        if (xmlhttp.readyState==4)
        {
              console.log(xmlhttp.responseText);
        }
    }
    xmlhttp.open("GET",arm);
    xmlhttp.send(null);
}
loadXMLDoc();
</script>

// 通过该代码可成功读取 /etc/hosts 的内容数据
```

**解决方案**

设置 setAllowFileAccessFromFileURLs(false);

> 当设置成为 false 时，上述JS的攻击代码执行会导致错误，表示浏览器禁止从 file url 中的 javascript 读取其它本地文件。

###### 3. setAllowUniversalAccessFromFileURLs()

```java
// 设置是否允许通过 file url 加载的 Javascript 可以访问其他的源(包括http、https等源)
webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

// 在Android 4.1前默认允许（setAllowFileAccessFromFileURLs（）不起作用）
// 在Android 4.1后默认禁止
```

当`AllowFileAccessFromFileURLs（）`被设置成true时，攻击者的JS代码是：

```javascript
// 通过该代码可成功读取 http://www.so.com 的内容
<script>
function loadXMLDoc()
{
    var arm = "http://www.so.com";
    var xmlhttp;
    if (window.XMLHttpRequest)
    {
        xmlhttp=new XMLHttpRequest();
    }
    xmlhttp.onreadystatechange=function()
    {
        //alert("status is"+xmlhttp.status);
        if (xmlhttp.readyState==4)
        {
             console.log(xmlhttp.responseText);
        }
    }
    xmlhttp.open("GET",arm);
    xmlhttp.send(null);
}
loadXMLDoc();
</script>
```

**解决方案**

设置 setAllowUniversalAccessFromFileURLs(false);

###### 4. setJavaScriptEnabled()

```java
// 设置是否允许 WebView 使用 JavaScript（默认是不允许）
webView.getSettings().setJavaScriptEnabled(true);  

// 但很多应用（包括移动浏览器）为了让 WebView 执行 http 协议中的 JavaScript，都会主动设置为true，不区别对待是非常危险的。
```

即使把`setAllowFileAccessFromFileURLs（）`和`setAllowUniversalAccessFromFileURLs（）`都设置为 false，通过 file URL 加载的 javascript 仍然有方法访问其他的本地文件：**符号链接跨源攻击**

> 前提是允许 file URL 执行 javascript，即`webView.getSettings().setJavaScriptEnabled(true);`

这一攻击能奏效的原因是：**通过 javascript 的延时执行和将当前文件替换成指向其它文件的软链接就可以读取到被符号链接所指的文件**。具体攻击步骤：

1. 把恶意的 js 代码输出到攻击应用的目录下，随机命名为 xx.html，修改该目录的权限；
2. 修改后休眠 1s，让文件操作完成；
3. 完成后通过系统的 Chrome 应用去打开该 xx.html 文件
4. 等待 4s 让 Chrome 加载完成该 html，最后将该 html 删除，并且使用 ln -s 命令为 Chrome 的 Cookie 文件创建软连接

> 注：在该命令执行前 xx.html 是不存在的；执行完这条命令之后，就生成了这个文件，并且将 Cookie 文件链接到了 xx.html 上。

于是就可通过链接来访问 Chrome 的 Cookie

> 1. Google 没有进行修复，只是让Chrome 最新版本默认禁用 file 协议，所以这一漏洞在最新版的 Chrome 中并不存在
> 2. 但是，在日常大量使用 WebView 的App和浏览器，都有可能受到此漏洞的影响。通过利用此漏洞，容易出现数据泄露的危险

如果是 file 协议，禁用 javascript 可以很大程度上减小跨源漏洞对 WebView 的威胁

> 1. 但并不能完全杜绝跨源文件泄露
> 2. 例：应用实现了下载功能，对于无法加载的页面，会自动下载到 sd 卡中；由于 sd 卡中的文件所有应用都可以访问，于是可以通过构造一个 file URL 指向被攻击应用的私有文件，然后用此 URL 启动被攻击应用的 WebActivity，这样由于该 WebActivity 无法加载该文件，就会将该文件下载到 sd 卡下面，然后就可以从 sd 卡上读取这个文件了

##### 最终解决方案

- 对于不需要使用 file 协议的应用，禁用 file 协议

  ```java
  // 禁用 file 协议；
  setAllowFileAccess(false); 
  setAllowFileAccessFromFileURLs(false);
  setAllowUniversalAccessFromFileURLs(false);
  ```

- 对于需要使用 file 协议的应用，禁止 file 协议加载 JavaScript

  ```java
  // 需要使用 file 协议
  setAllowFileAccess(true); 
  setAllowFileAccessFromFileURLs(false);
  setAllowUniversalAccessFromFileURLs(false);
  
  // 禁止 file 协议加载 JavaScript
  if (url.startsWith("file://") {
      setJavaScriptEnabled(false);
  } else {
      setJavaScriptEnabled(true);
  }
  ```

  

### 安全与证书



### 如何避免Webview 内存泄漏

#### 1. 使用getApplicationContext()

不在xml中定义 Webview ，而是在需要的时候在Activity中创建，并且Context使用 getApplicationgContext()。

```java
LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
mWebView = new WebView(getApplicationContext());
mWebView.setLayoutParams(params);
mLayout.addView(mWebView);
```

#### 2. 销毁Webview

在 Activity 销毁（ WebView ）的时候，先让 WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空。

```java
@Override
protected void onDestroy() {
    if (mWebView != null) {
        mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        mWebView.clearHistory();
        
        ((ViewGroup) mWebView.getParent()).removeView(mWebView);
        mWebView.destroy();
        mWebView = null;
    }
    super.onDestroy();
}
```

### 如何避免加载白屏的问题

#### 1. 加载HTTPS页面时导致的问题

##### 原因

当load通过SSL加密的HTTPS页面时，如果这个页面的安全证书无法得到认证，WebView就会显示成空白页。

##### 解决办法

通过重写WebViewClient的onReceivedSslError方法来接受所有网站的证书，忽略SSL错误。

```java
@Override
public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
	handler.proceed();
	super.onReceivedSslError(view, handler, error);
}
```

#### 2. H5页面使用DOM storage API 导致的页面加载问题

##### 原因

DOM storage 是HTML5提供的一种标准接口，主要将键值对存储在本地，在页面加载完毕后可以通过JavaScript来操作这些数据，但是Android 默认是没有开启这个功能的，则导致H5页面加载失败。

##### 解决办法

通过WebSettings设置开启DOM Storage功能

```java
settings.setDomStorageEnabled(true);
```

#### 3. 设置与 js 交互的权限

```java
// 设置与Js交互的权限
webSettings.setJavaScriptEnabled(true);
```

### onJsAlert只调用一次，并且页面卡死

##### 问题描述

运行程序后只弹出了一次，而且后续的 js 方法无法继续进行。

##### 解决办法

需要再每次调完后设置参数 JsResult 调用 cancel() 或者 confirm() 方法

```java
class MyWebChromeClient extends WebChromeClient {
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result){
        Toast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();
        result.cancel();
        return true;
    }
}  
```



## 案例

### 基本使用

##### AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

##### 主布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"

    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context="com.example.carson_ho.webview_demo.MainActivity">


   <!-- 获取网站的标题-->
    <TextView
        android:id="@+id/title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text=""/>

    <!--开始加载提示-->
    <TextView
        android:id="@+id/text_beginLoading"
        android:layout_below="@+id/title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text=""/>

    <!--获取加载进度-->
    <TextView
        android:layout_below="@+id/text_beginLoading"
        android:id="@+id/text_Loading"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text=""/>

    <!--结束加载提示-->
    <TextView
        android:layout_below="@+id/text_Loading"
        android:id="@+id/text_endLoading"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text=""/>
    
    <!--显示网页区域-->
    <WebView
        android:id="@+id/webView1"
        android:layout_below="@+id/text_endLoading"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:layout_marginTop="10dp" />
</RelativeLayout>
```

##### 根据需要实现的功能从而使用响应的子类及其方法

```java
public class MainActivity extends AppCompatActivity {
    WebView mWebview;
    WebSettings mWebSettings;
    TextView beginLoading,endLoading,loading,mtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mWebview = (WebView) findViewById(R.id.webView1);
        beginLoading = (TextView) findViewById(R.id.text_beginLoading);
        endLoading = (TextView) findViewById(R.id.text_endLoading);
        loading = (TextView) findViewById(R.id.text_Loading);
        mtitle = (TextView) findViewById(R.id.title);

        mWebSettings = mWebview.getSettings();

        mWebview.loadUrl("http://www.baidu.com/");

        
        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //设置WebChromeClient类
        mWebview.setWebChromeClient(new WebChromeClient() {


            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                System.out.println("标题在这里");
                mtitle.setText(title);
            }


            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    String progress = newProgress + "%";
                    loading.setText(progress);
                } else if (newProgress == 100) {
                    String progress = newProgress + "%";
                    loading.setText(progress);
                }
            }
        });


        //设置WebViewClient类
        mWebview.setWebViewClient(new WebViewClient() {
            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                System.out.println("开始加载了");
                beginLoading.setText("开始加载了");

            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                endLoading.setText("结束加载了");

            }
        });
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        super.onDestroy();
    }
}
```

