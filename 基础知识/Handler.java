Handler:

	1. 产生消息对象
	2. 消息相关属性(what,obj,target,callback等)的附加
	3. 消息加入到消息队列
	4. Looper对消息队列的循环取出
	5. 分发(也就是执行消息内用户定义的处理逻辑)
	6. 回收消息对象

	Handler的两种基本用途:
		1.延迟执行messages或runnables
		2.将A线程的操作入队到B线程中
	
	//使用Handler进行延时操作
    mHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
            imageView.setVisibility(imageView.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
        }
    },1000);
	
	Message:
		int what;
		int arg1;
		int arg2;
		Object obj;
		Messenger replyTo;
		int sendingUid;		---->作用于Messenger
		int flags;
		long when;
		Bundle data;
		Handler target;
		Runnable callback;
		Message	next;
	
	MessageQueue:
		内部以单链表的数据结构来存储消息列表的。对外提供插入和删除的操作。获取MessageQueue实例使用Looper.myQueue()方法。mQueue = mLooper.mQueue;
		for (;;) {
			//从这里可看出MessageQueue内部是以单链表的数据结构来存储消息的
            prev = p;
            p = p.next;
            if (p == null || when < p.when) {
                break;
            }
            if (needWake && p.isAsynchronous()) {
                needWake = false;
            }
        }
        msg.next = p; // invariant: p == prev.next
        prev.next = msg;
	
	Looper：
		主要用于给一个线程轮询消息的。线程默认没有Looper,在创建Handler对象前，我们需要为线程创建Looper(Looper.myLooper()--->ThreadLocal<Looper>.get()获得线程相关的Looper)。主线程为Looper.prepareMainLooper()。一个Looper对应一个消息队列.也就是说,有几个Looper,就有几个消息队列。使用Looper.prepare()方法创建Looper，使用Looper.loop()方法运行消息队列。
		
		class LooperThread extends Thread {
			public Handler mHandler;
  
			public void run() {
				Looper.prepare();
  
				mHandler = new Handler() {
					public void handleMessage(Message msg) {
						// process incoming messages here
					}
				};
				Looper.loop();
			}
		
	Handler()创建的过程：
		public Handler(){
			mLooper = Looper.myLooper();
			mQueue = mLooper.mQueue;
		}
	
	
	说明:
		一个Looper对应一个消息队列，也就是说，有几个Looper，就有几个消息队列。
		一个线程至多能有一个Looper，也就至多能有一个消息队列。一个线程可以有多个 Handler（公用Looper 和 MessageQueue）。
		
		
	Handler 与Callback 的区别：
		Handler的回调是会抛到主线程运行的，而你自己的CallBack依旧是在那个线程中。
	
	
	如何将消息从子线程发送到主线程：
		Handler 机制+引用的传递
		
		一个Handler持有一个消息队列的引用和它构造时所属线程的Looper的引用。也就是说，一个Handle必定有它对应的消息队列和Looper。
		消息分发的过程：
			handler-->dispatchMessage()--->handler.handleMessage() 或 runnable.run()执行自定义的逻辑。
		
