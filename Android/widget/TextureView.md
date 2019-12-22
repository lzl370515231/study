# TextureView



### SDK版本要求

- Android 4.0 引入

- 必须在硬件加速开启的窗口中

### API

| 序号 | 方法&描述                                                    |
| :--- | :----------------------------------------------------------- |
| 1    | **getSurfaceTexture()** This method returns the SurfaceTexture used by this view. |
| 2    | **getBitmap(int width, int height)** This method returns Returns a Bitmap representation of the content of the associated surface texture. |
| 3    | **getTransform(Matrix transform)** This method returns the transform associated with this texture view. |
| 4    | **isOpaque()** This method indicates whether this View is opaque. |
| 5    | **lockCanvas()** This method start editing the pixels in the surface |
| 6    | **setOpaque(boolean opaque)** This method indicates whether the content of this TextureView is opaque. |
| 7    | **setTransform(Matrix transform)** This method sets the transform to associate with this texture view. |
| 8    | **unlockCanvasAndPost(Canvas canvas)** This method finish editing pixels in the surface. |
| 9    | **setAlpha(...)**                                            |
| 10   | **setRotation(...)**                                         |



### 与SurfaceView区别

因为SurfaceView的内容不在应用窗口上，所以不能使用变换（平移、缩放、旋转等）。也难以放在ListView或者ScrollView中，不能使用UI控件的一些特性比如`View.setAlpha()`。

与SurfaceView相比，TextureView并没有创建一个单独的Surface用来绘制，这使得它可以像一般的View一样执行一些变换操作，设置透明度等。另外，Textureview必须在硬件加速开启的窗口中。

### 注意

接收player产生的视频流的对象是SurfaceTexture，而不是textureView。textureView只是作为一个硬件加速层来展示。所以如果需要无缝衔接的播放（比如小屏播放），textureView复不复用没关系然用。一定要保证SurfaceTexture的复用。即，textureView可以new 一个新的，但textureView.setSurfaceText()中传入的需是老的SurfaceTexture。

### SurfaceTextureListener

```java
@Override
public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
    
}
@Override
public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
    
}
@Override
public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,int arg2) {
    
}
@Override
public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
    
}
```

##### onSurfaceTextureAvailable

在调用TextureView的draw方法时，如果还没有初始化SurfaceTexture。那么就会初始化它。初始化好时，就会回调这个接口。SurfaceTexture初始化好时，就表示可以接收外界的绘制指令了（可以异步接收）。然后SurfaceTexture会以GL纹理信息更新到TextureView对应的HardwareLayer中。然后就会在HardwareLayer中显示。

SurfaceTexture初始化好时，就表示可以接收外界的绘制指令了（可以异步接收）。接受的方式通常有两种，也就是图中的Surface进行接收。Surface提供dequeueBuffer/queueBuffer等硬件渲染接口，和lockCanvas/unlockCanvasAndPost等软件渲染接口，使内容流的源可以往BufferQueue中填graphic buffer。

### 使用

获取用于渲染内容的SurfaceTexture。



##### 总体流程

1. 继承TextureView
2. 初始化时实现SurfaceTextureListener接口。
3. 接口实现的onSurfaceTextureAvailable（）方法中开启一个线程。
4. 在该线程的run（）方法中通过lockCanvas（）方法获取到canvas。
5. 调用canvas的api进行绘制。run（）方法可以是一个循环，这样就可以不断的绘制。
6. 通过unlockCanvasAndPost（）来结束绘制。

```java
public class MainActivity extends Activity implements SurfaceTextureListener {
    
    private TextureView myTexture;
    private Camera mCamera;
    
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTexture = new TextureView(this);
        myTexture.setSurfaceTextureListener(this);
        setContentView(myTexture);
    }
    
    @SuppressLint("NewApi")
  	@Override
   	public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
   		int arg2) {
      	mCamera = Camera.open();
      	Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
      	myTexture.setLayoutParams(new FrameLayout.LayoutParams(
      	previewSize.width, previewSize.height, Gravity.CENTER));
      	try {
         	mCamera.setPreviewTexture(arg0);
        } catch (IOException t) {
        
        }
      	mCamera.startPreview();
      	myTexture.setAlpha(1.0f);
      	myTexture.setRotation(90.0f);
   	}
   
    @Override
   	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
      	mCamera.stopPreview();
      	mCamera.release();
      	return true;
   	}
   
    @Override
   	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
   		int arg2) {
   		// TODO Auto-generated method stub
   	}
   
    @Override
   	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
   		// TODO Auto-generated method stub
   	}
}
```



```java
public class MainActivity extends Activity implements SurfaceTextureListener{
    //	private TextureView textureView;
	private MediaPlayer mMediaPlayer;
	private Surface surface;
	
	private ImageView videoImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextureView textureView=(TextureView) findViewById(R.id.textureview);
		textureView.setSurfaceTextureListener(this);//设置监听函数  重写4个方法
		
//		textureView=new TextureViewTest(this);
//		setContentView(textureView);
		videoImage=(ImageView) findViewById(R.id.video_image);
	}
	
    @Override
	public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width,int height) {
        //获取一个Surface  播放视频时 setSurface
        surface=new Surface(surfaceTexture);
		new PlayerVideo().start();//开启一个线程去播放视频
	}
    
	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,int height) {
        
	}
	
	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        
		surfaceTexture=null;
		surface=null;
		mMediaPlayer.stop();
		mMediaPlayer.release();
		return true;
	}
	
	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        
	}
	
	private class PlayerVideo extends Thread{
		@Override
		public void run(){
			 try {
				  File file=new File(Environment.getExternalStorageDirectory()+"/ansen.mp4");
				  if(!file.exists()){//文件不存在
					  copyFile();
				  }
				  mMediaPlayer= new MediaPlayer();
				  mMediaPlayer.setDataSource(file.getAbsolutePath());
                 
                 //设置 Surface
				  mMediaPlayer.setSurface(surface);
				  mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				  mMediaPlayer.setOnPreparedListener(new OnPreparedListener(){
					@Override
					public void onPrepared(MediaPlayer mp){
                        // 子线程更新UI，需改为主线程更新UI
						videoImage.setVisibility(View.GONE);
						mMediaPlayer.start();
					}
				  });
				  mMediaPlayer.prepare();
			  } catch (Exception e) {  
				  e.printStackTrace();
			  }
	    }
	}
	
	public interface PlayerController{
		public void play();
	}
	
	/**
	 * 如果sdcard没有文件就复制过去
	 */
	private void copyFile() {
	    AssetManager assetManager = this.getAssets();
	    InputStream in = null;
	    OutputStream out = null;
	    try {
	        in = assetManager.open("ansen.mp4");
	        String newFileName = Environment.getExternalStorageDirectory()+"/ansen.mp4";
	        out = new FileOutputStream(newFileName);
	        byte[] buffer = new byte[1024];
	        int read;
	        while ((read = in.read(buffer)) != -1) {
	            out.write(buffer, 0, read);
	        }
	        in.close();
	        in = null;
	        out.flush();
	        out.close();
	        out = null;
	    } catch (Exception e) {
	        Log.e("tag", e.getMessage());
	    }
	}
}
```



### 无缝衔接

[textureView的两种使用场景](https://www.jianshu.com/p/4e2916889f27)

[Android视频无缝切换，使用MediaPlayer和TextureView](https://blog.csdn.net/wenxiang423/article/details/82662113 )

