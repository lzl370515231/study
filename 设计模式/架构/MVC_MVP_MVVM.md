## MVC

MVC模式的意思是，软件可以分成三个部分。

![](.\..\png\MVC_MVP_MVVM_1.png)

> - 视图（View）：用户界面。
> - 控制器（Controller）：业务逻辑
> - 模型（Model）：数据保存

各部分之间的通信方式如下。

![](.\..\png\MVC_MVP_MVVM_2.png)

> 1. View 传送指令到 Controller
> 2. Controller 完成业务逻辑后，要求 Model 改变状态
> 3. Model 将新的数据发送到 View，用户得到反馈

所有通信都是单向的。

### Android 不适合的原因

作为View  的各个 xml 视图功能太弱了。Activity 基本上都是 View 和Controller 的合体，既要负责视图的显示又要加入控制逻辑，承担的功能过多，代码量大也就不足为奇。

## MVP

MVP 模式将 Controller 改名为 Presenter，同时改变了通信方向。

![](.\..\png\MVC_MVP_MVVM_6.png)

1. 各部分之间的通信，都是双向的。

2. View 与 Model 不发生联系，都通过 Presenter 传递。

3. View 非常薄，不部署任何业务逻辑，称为"被动视图"（Passive View），即没有任何主动性，而 Presenter非常厚，所有逻辑都部署在那里。

### 缺点

- Activity需要实现各种跟UI相关的接口，同时要在Activity中编写大量的事件，然后在事件处理中调用presenter的业务处理方法，View和Presenter只是互相持有引用并互相做回调,代码不美观。
- 这种模式中，程序的主角是UI，通过UI事件的触发对数据进行处理，更新UI就有考虑线程的问题。而且UI改变后牵扯的逻辑耦合度太高，一旦控件更改（比较TextView 替换 EditText等）牵扯的更新UI的接口就必须得换。
- 复杂的业务同时会导致presenter层太大，代码臃肿的问题。

## MVVM

MVVM 模式将 Presenter 改名为 ViewModel，与MVP类似，利用数据绑定(Data Binding)、依赖属性(Dependency Property)、命令(Command)、路由事件(Routed Event)等新特性，打造了一个更加灵活高效的架构。

![](.\..\png\MVC_MVP_MVVM_7.png)

唯一的区别是，它采用双向绑定（data-binding）：View的变动，自动反映在 ViewModel，反之亦然。

### 协作

![](.\..\png\MVVM协作图.png)

#### ViewModel与View的协作

![](.\..\png\MVVM_ViewModel-View.png)

> Context 	上下文
>
> Model		数据模型Bean
>
> Data Field		数据绑定
>
> Command		命令绑定							ClickListener
>
> Child ViewModel		子ViewModel		Messager

#### ViewModel与Model的协作



#### ViewModel与ViewModel的协作