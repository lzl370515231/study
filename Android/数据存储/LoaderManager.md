## Loaders

![](.\png\Loader.png)

适用于Android3.0以及更高的版本，它提供了一套**在UI的主线程中异步加载数据的框架**。使用Loaders可以非常简单的在Activity或者Fragment中异步加载数据，一般**适用于大量的数据查询**，或者需要经常修改并及时展示的数据显示到UI上，这样可以避免查询数据的时候，造成UI主线程的卡顿。

#### 特点

- 可以适用于Activity和Fragment。
- 可以提供异步的方式加载数据。
- 监听数据源，当数据改变的时候，将新的数据发布到UI上。
- Loaders使用Cursor加载数据，在更改Cursor的时候，会自动重新连接到最后配置的Cursor中读取数据，因此不需要重新查询数据。

### LoaderManager

用于在Activity或者Fragment中管理一个或多个Loader实例。在Activity或者Fragment中，可以通过getLoaderManager()方法获取LoaderManager对象，它是一个单例模式。

#### API

##### initLoader

Loader<D> initLoader(int id,Bundle bundle,LoaderCallbacks<D> callback)：初始化一个Loader，并注册回调事件。

##### restartLoader

Loader<D> restartLoader(int id,Bundle bundle,LoaderCallbacks<D> callback)：重新启动或创建一个Loader，并注册回调事件。

##### getLoader

Loader<D> getLoader(int id)：返回给定Id的Loader，如果没有找到则返回Null。

##### destroyLoader

void destroyLoader(int id)：根据指定Id，停止和删除Loader。

##### 参数说明

**Id**是Loader的标识，因为LoaderManager可以管理一个或多个Loader，所以必须通过这个Id参数来唯一确定一个Loader。

**bundle**参数，传递一个Bundle对象给LoaderCallbacks中的onCreateLoader()去获取

### LoaderManager.LoaderCallbacks

LoaderCallbacks是LoaderManager和Loader之间的回调接口。它是一个回调接口。需要实现其定义的三个方法：

- Loader<D> onCreateLoader(int id,Bundle bundle)：根据指定Id，初始化一个新的Loader，并返回。
- void onLoadFinished(Loader<D> loader,D data)：当Loader被加载完毕后被调用，在其中处理Loader获取的Cursor数据。
- void onLoaderReset(Loader<D> loader)：当Loader被销毁的时候被调用，在其中可以使Loader的数据不可用。

是一个泛型的接口，需要指定Loader数据的类型。如果是数据源是从一个ContentProvider中获取的，一般直接使用它的子类CursorLoader。

### CursorLoader

如果需要自己开发一个装载机的话，一般并不推荐继承Loader接口，而是继承它的子类**AsyncTaskLoader**，这是一个以AsyncTask框架执行的异步加载。

Android中还提供了一个**CursorLoader**类，它是**AsyncTaskLoader**的子类，一个异步的加载数据的类，**通过ContentResolver的标准查询并返回一个Cursor**。这个类实现了Loader的协议，以一种标准的方式查询Cursor。

### 示例



