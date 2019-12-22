# SurfaceView

SurfaceView的工作方式是创建一个置于应用窗口之后的新窗口。

>View是通过刷新来重绘视图，系统通过发出`VSSYNC`信号来进行屏幕的重绘，刷新的时间间隔是`16ms`,如果我们可以在16ms以内将绘制工作完成，则没有任何问题，如果我们绘制过程逻辑很复杂，并且我们的界面更新还非常频繁，这时候就会造成界面的卡顿，影响用户体验，为此Android提供了`SurfaceView`来解决这一问题。



### View 和 SurfaceView区别

1. View适用于主动更新的情况，而SurfaceView则适用于被动更新的情况，比如频繁刷新界面。
2. View在主线程中对页面进行刷新，而SurfaceView则开启一个子线程来对页面进行刷新。
3. View在绘图时没有实现双缓冲机制，SurfaceView在底层机制中就实现了双缓冲机制。

```java
public class SurfaceViewHandWriting extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mSurfaceHolder;
    //绘图的Canvas
    private Canvas mCanvas;
    //子线程标志位
    private boolean mIsDrawing;
    //画笔
    private Paint mPaint;
    //路径
    private Path mPath;
    private static final String TAG = "pyh";
    public SurfaceViewHandWriting(Context context) {
        this(context, null);
    }

    public SurfaceViewHandWriting(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceViewHandWriting(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPath = new Path();
        mPath.moveTo(0, 100);
        initView();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        //视频播放
        //player.serDisplay(holder);
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            long start = System.currentTimeMillis();
            drawSomething();
            long end = System.currentTimeMillis();
            if (end - start < 100) {
                try {
                    Thread.sleep(100 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     * 初始化View
     */
    private void initView(){
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
    }

    private void drawSomething() {
        try {
            //获得canvas对象
            mCanvas = mSurfaceHolder.lockCanvas();
            //绘制背景
            mCanvas.drawColor(Color.WHITE);
            //绘制路径
            mCanvas.drawPath(mPath, mPaint);
        }catch (Exception e){

        }finally {
            if (mCanvas != null){
                //释放canvas对象并提交画布
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
```



### 无缝衔接

接收player产生的视频流的对象是SurfaceTexture，而不是textureView。textureView只是作为一个硬件加速层来展示。所以如果需要无缝衔接的播放（比如小屏播放），textureView复不复用没关系然用。一定要保证SurfaceTexture的复用。即，textureView可以new 一个新的，但textureView.setSurfaceText()中传入的需是老的SurfaceTexture。
（当然无缝衔接播放，需要的player是老的player。不然player中的source、下载渲染情况、surface都不一样，那还怎么无缝衔接。所以player最好做成单例。需要老的就直接重用，不需要老的，就player.release()）

[Android - SurfaceView + MediaPlayer实现分段视频无缝播放](https://blog.csdn.net/ghost_Programmer/article/details/44980989)

