# RecyclerView

## 基本使用

#### RecyclerView.Adapter

##### RvAdapter

```java
public abstract  class RvAdapter<T>  extends RecyclerView.Adapter {

    protected List<T> mList;
    protected Context mContext;
    protected RvListener mRvListener;
    protected LayoutInflater mInflater;

    public RvAdapter(List<T> list,Context context,RvListener rvListener){
        mList = list;
        mContext = context;
        mRvListener = rvListener;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RvHolder)holder).bindHolder(mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public abstract RvHolder getHolder(ViewGroup parent, int viewType);
}
```

##### NormalRvAdapter

```java
public class NormalRvAdapter extends RvAdapter<String> {


    public NormalRvAdapter(List<String> list, Context context, RvListener rvListener) {
        super(list, context, rvListener);
        mList = list;
        mContext = context;
        mRvListener = rvListener;
    }

    @Override
    public RvHolder getHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_main,null,false);
        return new NormalHolder(view,mRvListener);
    }
}
```



#### RecyclerView.ViewHolder

##### RvHolder

```java
public abstract class RvHolder<T>  extends RecyclerView .ViewHolder{

    private RvListener mRvListener;

    public RvHolder(View itemView,RvListener rvListener){
        super(itemView);
    }

    public abstract void bindHolder(T item,int pos);
}
```

##### NormalHolder

```java
public class NormalRvAdapter extends RvAdapter<String> {

    public NormalRvAdapter(List<String> list, Context context, RvListener rvListener) {
        super(list, context, rvListener);
        mList = list;
        mContext = context;
        mRvListener = rvListener;
    }

    @Override
    public RvHolder getHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_main,null,false);
        return new NormalHolder(view,mRvListener);
    }
}
```



#### RvListener

```java
public interface RvListener {

    void onItemClick(View view);
    void onLongClick(View view);
}
```

#### Activity

```java
@Override
protected void onCreate(Bundle savedInstanceState){
    //.....
    mRv = findViewById(R.id.rv);
    mList = new ArrayList<>();
    for(int i=0;i<50;i++){
        mList.add("------>"+i);
    }
    //mLayoutManager = new LinearLayoutManager(this);
	//mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
    mLayoutManager = new GridLayoutManager(this,2);
    
    //设置固定大小，避免重复计算
    mRv.setHasFixedSize(true);
    //默认动画
    mRv.setItemAnimator(new DefaultItemAnimator());
    RvAdapter rvAdapter = new NormalRvAdapter(mList, this, new RvListener() {
        @Override
        public void onItemClick(View view) {
            Log.e(TAG,"onItemClick:"+((TextView)view).getText().toString()+"");
        }
        @Override
        public void onLongClick(View view) {
            Log.e(TAG,"onLongClick:"+((TextView)view).getText().toString()+"");
        }
    });
    mRv.setAdapter(rvAdapter);
}

```

## ItemDecoration

ItemDecoration是对Item起到了装饰作用，更准确的说是对item的周边起到了装饰的作用，通过下面的图应该能帮助你理解这话的含义。

![](.\png\ItemDecoration.png)

### 使用

#### 1. 自定义 ItemDecoration

要使用ItemDecoration，我们得必须先自定义，直接继承ItemDecoration即可。

#### 2. 重写 getItemOffsets() 和 onDraw()

在实现自定义的装饰效果就必须重写getItemOffsets()和onDraw()。

#### 3. 解析getItemOffsets()和onDraw()

```java
public class MyDecorationOne extends RecyclerView.ItemDecoration {

    /**
     * 画线
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    /**
     * 设置条目周边的偏移量
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

    }
}
```

getItemOffsets()就是设置item周边的偏移量（也就是装饰区域的“宽度”）。而onDraw()才是真正实现装饰的回调方法，通过该方法可以在装饰区域任意画画，这里我们来画条分割线。

#### 4. “实现” getItemOffsets() 和 onDraw()

本例中实现的是线性列表的分割线（即使用LinearLayoutManager）。

1）当线性列表是水平方向时，分割线竖直的；当线性列表是竖直方向时，分割线是水平的。

2）当画竖直分割线时，需要在item的右边偏移出一条线的宽度；当画水平分割线时，需要在item的下边偏移出一条线的高度。

```java
/**
 * 画线
 */
@Override
public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    super.onDraw(c, parent, state);
    if (orientation == RecyclerView.HORIZONTAL) {
        drawVertical(c, parent, state);
    } else if (orientation == RecyclerView.VERTICAL) {
        drawHorizontal(c, parent, state);
    }
}

/**
 * 设置条目周边的偏移量
 */
@Override
public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    if (orientation == RecyclerView.HORIZONTAL) {
        //画垂直线
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    } else if (orientation == RecyclerView.VERTICAL) {
        //画水平线
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }
}
```

#### 5. 画出一条华丽的分割线

因为getItemOffsets()是相对每个item而言的，即每个item都会偏移出相同的装饰区域。而onDraw()则不同，它是相对Canvas来说的，通俗的说就是要自己找到要画的线的位置，这是自定义ItemDecoration中唯一比较难的地方了。

```java
/**
 * 在构造方法中加载系统自带的分割线（就是ListView用的那个分割线）
 */
public MyDecorationOne(Context context, int orientation) {
    this.orientation = orientation;
    int[] attrs = new int[]{android.R.attr.listDivider};
    TypedArray a = context.obtainStyledAttributes(attrs);
    mDivider = a.getDrawable(0);
    a.recycle();
}   
```

```java
/**
 * 画竖直分割线
 */
private void drawVertical(Canvas c, RecyclerView parent, RecyclerView.State state) {
    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
        View child = parent.getChildAt(i);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        int left = child.getRight() + params.rightMargin;
        int top = child.getTop() - params.topMargin;
        int right = left + mDivider.getIntrinsicWidth();
        int bottom = child.getBottom() + params.bottomMargin;
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }
}

/**
 * 画水平分割线
 */
private void drawHorizontal(Canvas c, RecyclerView parent, RecyclerView.State state) {
    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
        View child = parent.getChildAt(i);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        int left = child.getLeft() - params.leftMargin;
        int top = child.getBottom() + params.bottomMargin;
        int right = child.getRight() + params.rightMargin;
        int bottom = top + mDivider.getIntrinsicHeight();
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }
}
```

仅对水平分割线的左、上坐标进行图解，其他坐标的计算以此类推。

![](.\png\图解分割线的坐标.png)

### 使用ItemDecoration 绘制表格

#### 1. 分析

我们知道ItemDecoration就是装饰item周边用的，画条分割线只需要2步，1是在item的下方偏移出一定的宽度，2是在偏移出来的位置上画线。画表格线其实也一样，除了画item下方的线，还画item右边的线就好了（当然换成左边也行）。

#### 2. 实现

本例中使用的是网格列表（即使用GridLayoutManager）。

##### 1）自定义分割线

```java
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
       android:shape="rectangle">

    <solid android:color="#f00"/>
    <size
        android:width="2dp"
        android:height="2dp"/>

</shape>
```

##### 2) 自定义ItemDecoration

实现上跟画分割线没多大差别。

```java
public class MyDecorationTwo extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;

    public MyDecorationTwo(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.divider);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int top = child.getTop() - params.topMargin;
            int right = left + mDivider.getIntrinsicWidth();
            int bottom = child.getBottom() + params.bottomMargin;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft() - params.leftMargin;
            int top = child.getBottom() + params.bottomMargin;
            int right = child.getRight() + params.rightMargin;
            int bottom = top + mDivider.getMinimumHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
    }
}
```

#### 3. 有瑕疵

可以看出下面的效果是有问题的，表格的最后一列和最后一行不应该出现边边。

![](.\png\表格.gif)

#### 4. 修复

既然知道表格的最后一列和最后一行不应该出现边边，那就让最后一列和最后一行的边边消失就好了。有以下几个思路。

1. 在onDraw()方法中，判断当前列是否为最后一列和判断当前行是否为最后一行来决定是否绘制边边。
2. 在getItemOffsets()方法中对行列进行判断，来决定是否设置条目偏移量（当偏移量为0时，自然就看不出边边了）。

这里我选用第二种方式。这里要说明一下，getItemOffsets()有两个，一个是getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)，另一个是getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent)，第二个已经过时，但是该方法中有回传当前item的position，所以我选用了过时的getItemOffsets()。

```java
@Override
public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
    super.getItemOffsets(outRect, itemPosition, parent);
    int right = mDivider.getIntrinsicWidth();
    int bottom = mDivider.getIntrinsicHeight();

    if (isLastSpan(itemPosition, parent)) {
        right = 0;
    }

    if (isLastRow(itemPosition, parent)) {
        bottom = 0;
    }
    outRect.set(0, 0, right, bottom);
}

public boolean isLastRow(int itemPosition, RecyclerView parent) {
    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
    if (layoutManager instanceof GridLayoutManager) {
        int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        int itemCount = parent.getAdapter().getItemCount();
        if ((itemCount - itemPosition - 1) < spanCount)
            return true;
    }
    return false;
}

public boolean isLastSpan(int itemPosition, RecyclerView parent) {
    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
    if (layoutManager instanceof GridLayoutManager) {
        int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        if ((itemPosition + 1) % spanCount == 0)
            return true;
    }
    return false;
}
```

#### 5. 效果

![](.\png\表格2.gif)