Binder进程间通信机制:
	Android系统是基于Linux内核的，而Linux内核继承和兼容了丰富的Unix系统进程间通信（IPC）机制。有传统的管道（Pipe）、信号（Signal）和跟踪（Trace），这三项通信手段只能用于父进程与子进程之间，或者兄弟进程之间；后来又增加了命令管道（Named Pipe），使得进程间通信不再局限于父子进程或者兄弟进程之间；为了更好地支持商业应用中的事务处理，在AT&T的Unix系统V中，又增加了三种称为“System V IPC”的进程间通信机制，分别是报文队列（Message）、共享内存（Share Memory）和信号量（Semaphore）；后来BSD Unix对“System V IPC”机制进行了重要的扩充，提供了一种称为插口（Socket）的进程间通信机制。
	
		IPC						数据拷贝次数
	共享内存						0
	Binder							1
	Socket/管道/消息队列			2
	
	Android需要建立一套新的IPC机制来满足系统对通信方式，传输性能和安全性(可靠的UID/PID)的要求，这就是Binder。Binder基于Client-Server通信模式，传输过程只需一次拷贝，为发送方添加UID/PID身份，既支持实名Binder也支持匿名Binder，安全性高。它是基于OpenBinder来实现的。
	
	在Android系统的Binder机制中，由一系统组件组成，分别是Client、Server、Service Manager和Binder驱动程序，其中Client、Server和Service Manager运行在用户空间，Binder驱动程序运行内核空间。Binder就是一种把这四个组件粘合在一起的粘结剂了，其中，核心组件便是Binder驱动程序了，Service Manager提供了辅助管理的功能，Client和Server正是在Binder驱动和Service Manager提供的基础设施上，进行Client-Server之间的通信。Service Manager和Binder驱动已经在Android平台中实现好，开发者只要按照规范实现自己的Client和Server组件就可以了。
		http://www.cnblogs.com/innost/archive/2011/01/09/1931456.html
		https://blog.csdn.net/freshui/article/details/54926111
	
	Binder驱动
		用户通过/dev/binder访问该它。驱动负责进程之间Binder通信的建立，Binder在进程之间的传递，Binder引用计数管理，数据包在进程之间的传递和交互等一系列底层支持。驱动和应用程序之间定义了一套接口协议，主要功能由ioctl()接口实现，不提供read()，write()接口，因为ioctl()灵活方便，且能够一次调用实现先写后读以满足同步交互，而不必分别调用write()和read()。Binder驱动的代码位于linux目录的drivers/misc/binder.c中。
	
	
	ServerManager
		SMgr是一个进程，Server是另一个进程，Server向SMgr注册Binder必然会涉及进程间通信。当前实现的是进程间通信却又要用到进程间通信，这就好象蛋可以孵出鸡前提却是要找只鸡来孵蛋。Binder的实现比较巧妙：预先创造一只鸡来孵蛋：SMgr和其它进程同样采用Binder通信，SMgr是Server端，有自己的Binder对象（实体），其它进程都是Client，需要通过这个Binder的引用来实现Binder的注册，查询和获取。SMgr提供的Binder比较特殊，它没有名字也不需要注册，当一个进程使用BINDER_SET_CONTEXT_MGR命令将自己注册成SMgr时Binder驱动会自动为它创建Binder实体（这就是那只预先造好的鸡）。其次这个Binder的引用在所有Client中都固定为0而无须通过其它手段获得。也就是说，一个Server若要向SMgr注册自己Binder就必需通过0这个引用号和SMgr的Binder通信。类比网络通信，0号引用就好比域名服务器的地址，你必须预先手工或动态配置好。要注意这里说的Client是相对SMgr而言的，一个应用程序可能是个提供服务的Server，但对SMgr来说它仍然是个Client。
	
	Client 获得实名Binder的引用
		SMgr收到这个连接请求，从请求数据包里获得Binder的名字，在查找表里找到该名字对应的条目，从条目中取出Binder的引用，将该引用作为回复发送给发起请求的Client。
	
	匿名Binder
		Server端可以通过已经建立的Binder连接将创建的Binder实体传给Client，当然这条已经建立的Binder连接必须是通过实名Binder实现。由于这个Binder没有向SMgr注册名字，所以是个匿名Binder。Client将会收到这个匿名Binder的引用，通过这个引用向位于Server中的实体发送请求。匿名Binder为通信双方建立一条私密通道，只要Server没有把匿名Binder发给别的进程，别的进程就无法通过穷举或猜测等任何方式获得该Binder的引用，向该Binder发送请求。
	
	Binder 协议
		Binder协议基本格式是（命令+数据），使用ioctl(fd, cmd, arg)函数实现交互。命令由参数cmd承载，数据由参数arg承载，随cmd不同而不同。
		
		常见的Binder通信命令字
			BINDER_WRITE_READ
				含义：
					该命令向Binder写入或读取数据。参数分为两段：写部分和读部分。如果write_size不为0就先将write_buffer里的数据写入Binder；如果read_size不为0再从Binder中读取数据存入read_buffer中。write_consumed和read_consumed表示操作完成时Binder驱动实际写入或读出的数据个数。
				
				数据：
					struct binder_write_read {
						signed long write_size;
						signed long write_consumed;
						unsigned long write_buffer;
						signed long read_size;
						signed long read_consumed;
						unsigned long read_buffer;
					};
			
			BINDER_SET_MAX_THREADS
				含义:
					该命令告知Binder驱动接收方（通常是Server端）线程池中最大的线程数。由于Client是并发向Server端发送请求的，Server端必须开辟线程池为这些并发请求提供服务。告知驱动线程池的最大值是为了让驱动发现线程数达到该值时不要再命令接收端启动新的线程。
					
				参数：
					int max_threads;
			
			BINDER_SET_CONTEXT_MGR
				含义:
					将当前进程注册为SMgr。系统中同时只能存在一个SMgr。只要当前的SMgr没有调用close()关闭Binder驱动就不能有别的进程可以成为SMgr。
					
				参数:
					无
				
			BINDER_THREAD_EXIT：
				含义：
					通知Binder驱动当前线程退出了。Binder会为所有参与Binder通信的线程（包括Server线程池中的线程和Client发出请求的线程）建立相应的数据结构。这些线程在退出时必须通知驱动释放相应的数据结构。
				
				参数：	
					无
			
			BINDER_VERSION：
				含义：
					获得Binder驱动的版本号。
		这其中最常用的命令是BINDER_WRITE_READ。该命令的参数包括两部分数据：一部分是向Binder写入的数据，一部分是要从Binder读出的数据，驱动程序先处理写部分再处理读部分。这样安排的好处是应用程序可以很灵活地处理命令的同步或异步。例如若要发送异步命令可以只填入写部分而将read_size置成0；若要只从Binder获得数据可以将写部分置空即write_size置成0；若要发送请求并同步等待返回数据可以将两部分都置上。
	