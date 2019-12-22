# SwipeRefreshLayout

## 使用

#### 1. 布局文件

```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.honor.fighting.putorefreshlist.MainActivity">
    <android.support.v4.widget.SwipeRefreshLayout
        android:id="@+id/swipe_refresh_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <ListView
            android:id="@+id/list_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    </android.support.v4.widget.SwipeRefreshLayout>
</FrameLayout>
```

#### 代码

```java
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeLayout;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> data;
    private boolean isRefresh = false;//是否刷新中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        //设置SwipeRefreshLayout
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        //设置进度条的颜色主题，最多能设置四种 加载颜色是循环播放的，只要没有完成刷新就会一直循环，holo_blue_bright>holo_green_light>holo_orange_light>holo_red_light
       // mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
       //         android.R.color.holo_green_light,
       //         android.R.color.holo_orange_light,
       //         android.R.color.holo_red_light);

        //上面的方法已经废弃
        mSwipeLayout.setColorSchemeResources(Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED);


        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setDistanceToTriggerSync(300);
        // 设定下拉圆圈的背景
        mSwipeLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        // 设置圆圈的大小
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE); 

        //设置下拉刷新的监听
        mSwipeLayout.setOnRefreshListener(this);
    }

    /*
     * 初始化  设置ListView
     */
    private void init() {
        mListView = (ListView) findViewById(R.id.listview);

        data = new ArrayList<String>();

        for (int i = 1; i < 10; i++) {
            data.add("这是第" + i + "个数据");
        }

        //初始化adapter
        mAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, data);

        mListView.setAdapter(mAdapter);
    }

    /*
     * 监听器SwipeRefreshLayout.OnRefreshListener中的方法，当下拉刷新后触发
     */
    public void onRefresh() {
        //检查是否处于刷新状态
        if (!isRefresh) {
            isRefresh = true;
            //模拟加载网络数据，这里设置4秒，正好能看到4色进度条
            new Handler().postDelayed(new Runnable() {
                public void run() {

                    //显示或隐藏刷新进度条
                    mSwipeLayout.setRefreshing(false);
                    //修改adapter的数据
                    data.add("这是新添加的数据");
                    mAdapter.notifyDataSetChanged();
                    isRefresh = false;
                }
            }, 4000);
        }
    }
}
```

### 高级用法

```java
swipe_refresh_layout.setRefreshing(false);//取消刷新,因此刷新图标
swipe_refresh_layout.setRefreshing(true);//设置为刷新状态，显示刷新图标
swipe_refresh_layout.setEnabled(false);//设置为不能刷新
boolean refreshing = swipe_refresh_layout.isRefreshing();//是否正在刷新
```

#### 样式设置

```java
//设置刷新时旋转图标的颜色，这是一个可变参数，当设置多个颜色时，旋转一周改变一次颜色。
swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary);
```

## 特性

- 此布局应作为视图的父视图，该视图将根据手势刷新，并且只能支持一个直接子视图。 