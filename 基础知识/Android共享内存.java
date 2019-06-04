使用共享内存的流程大概是：
	1. 在一个进程中创建一个共享内存。在Android应用层中，用MemoryFile描述一块共享内存，创建共享内存其实就是创建一MemoryFile对象。这一步非常简单，MemoryFile提供了相应的构造函数
		
		public MemoryFile(String name, int length) throws IOException
		
	2. 获取刚刚创建的共享内存的文件描述符FileDescroptor，并且序列化成ParcelFileDescriptor。对于获取FileDescriptor，MemoryFile提供了相应的方法
		
		public FileDescriptor getFileDescriptor() throws IOException {
			return mFD;
		}
	
	可是这个方法是隐藏的，在应用层没法直接调用。所以需要用反射来完成，具体代码如下：
		/**
		* 获取memoryFile的FileDescriptor
		* @param memoryFile 描述一块共享内存
		* @return 这块共享内存对应的文件描述符
		*/
		public static FileDescriptor getFileDescriptor(MemoryFile memoryFile){
			if(memoryFile == null){
				throw new IllegalArgumentException("memoryFile 不能为空");
			}
			FileDescriptor fd;
			fd = (FileDescriptor) ReflectUtil.invoke("android.os.MemoryFile",memoryFile,"getFileDescriptor");
			return fd;
		}

	对于把FileDescriptor序列化成ParcelFileDescriptor，ParcelFileDescriptor也提供了一隐藏的构造函数：
		public ParcelFileDescriptor(FileDescriptor fd) {
			this(fd, null);
		}
	
	所以我们还是可以通过反射来实现：
		/**
		* 获取memoryFile的ParcelFileDescriptor
		* @param memoryFile 描述一块共享内存
		* @return ParcelFileDescriptor
		*/
		public static ParcelFileDescriptor getParcelFileDescriptor(MemoryFile memoryFile){
			if(memoryFile == null){
				throw new IllegalArgumentException("memoryFile 不能为空");
			}
			ParcelFileDescriptor pfd;
			FileDescriptor fd = getFileDescriptor(memoryFile);
			pfd = (ParcelFileDescriptor) ReflectUtil.getInstance("android.os.ParcelFileDescriptor",fd);
			return pfd;
		}
	
	把刚刚得到的ParcelFileDescriptor传递到其他进程，这个比较简单直接用binder传就可以了
	
	3. 另一进程获取MemoryFile对象
		通过描述共享内存文件描述取得一个描述共享内存的MemoryFile对象，并且需要让这个MemoryFile对象指向刚刚创建的共享内存。在低版本的系统中存在一个构造函数可以直接以FileDescriptor为参数构造出一个MemoryFile对象，这样构造出来的对象刚好指向FileDescriptor描述的共享内存。但是在高版本中没有样的构造函数了。所以在这里我利用了一个取巧的方式。思路是：利用构造函数 
			public MemoryFile(String name, int length) throws IOException
		构造一个MemoryFile对象，当然此时也创建了一块新的共享内存，但是这块共享内存不是我们需要的；调用public void close()方法关闭刚刚创建的共享内存。通过前面的操作后我们得到了一个MemoryFile对象，但是这个对象没有指向任何共享内存，所以接下来我们就需要让MemoryFile对象指向我们需要的共享内存，也就是FileDescriptor描述的那块。在MemoryFile中有一个native方法：
			private static native long native_mmap(FileDescriptor fd, int length, int mode)
		
		这个方法就是把fd描述的共享内存映射到虚拟地址空间中。所以我们可以已刚刚获得的FileDescriptor 作为参数利用反射调用这个方法：
			/**
			* 打开共享内存，一般是一个地方创建了一块共享内存
			* 另一个地方持有描述这块共享内存的文件描述符，调用
			* 此方法即可获得一个描述那块共享内存的MemoryFile
			* 对象
			* @param fd 文件描述
			* @param length 共享内存的大小
			* @param mode PROT_READ = 0x1只读方式打开,
			*             PROT_WRITE = 0x2可写方式打开，
			*             PROT_WRITE|PROT_READ可读可写方式打开
			* @return MemoryFile
			*/
			public static MemoryFile openMemoryFile(FileDescriptor fd,int length,int mode){
				MemoryFile memoryFile = null;
				try {
					memoryFile = new MemoryFile("tem",1);
					memoryFile.close();
					Class<?> c = MemoryFile.class;
					Method native_mmap = null;
					Method[] ms = c.getDeclaredMethods();
					for(int i = 0;ms != null&&i<ms.length;i++){
						if(ms[i].getName().equals("native_mmap")){
							native_mmap = ms[i];
						}
					}
					ReflectUtil.setField("android.os.MemoryFile", memoryFile, "mFD", fd);
					ReflectUtil.setField("android.os.MemoryFile",memoryFile,"mLength",length);
					long address = (long) ReflectUtil.invokeMethod( null, native_mmap, fd, length, mode);
					ReflectUtil.setField("android.os.MemoryFile", memoryFile, "mAddress", address);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return memoryFile;
			}
			
		这样我们就得到了一个指向一开始我们创建的那块共享内存的MemoryFile了，接下来就可以调用它的public int readBytes(byte[] buffer, int srcOffset, int destOffset, int count)和public void writeBytes(byte[] buffer, int srcOffset, int destOffset, int count)从共享内存中读数据和往共享内存中写数据了
			
		完整代码：
			/**
			* 对memoryFile类的扩展
			* 1.从memoryFile对象中获取FileDescriptor,ParcelFileDescriptor
			* 2.根据一个FileDescriptor和文件length实例化memoryFile对象
			* Created by wuzr on 2016/7/16.
			*/
			public class MemoryFileHelper {
				/**
				* 创建共享内存对象
				* @param name 描述共享内存文件名称
				* @param length 用于指定创建多大的共享内存对象
				* @return MemoryFile 描述共享内存对象
				*/
				public static MemoryFile createMemoryFile(String name,int length){
					try {
						return new MemoryFile(name,length);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}

				public static MemoryFile openMemoryFile(ParcelFileDescriptor pfd,int length,int mode){
					if(pfd == null){
						throw new IllegalArgumentException("ParcelFileDescriptor 不能为空");
					}
					FileDescriptor fd = pfd.getFileDescriptor();
					return openMemoryFile(fd,length,mode);
				}

				/**
				* 打开共享内存，一般是一个地方创建了一块共享内存
				* 另一个地方持有描述这块共享内存的文件描述符，调用
				* 此方法即可获得一个描述那块共享内存的MemoryFile
				* 对象
				* @param fd 文件描述
				* @param length 共享内存的大小
				* @param mode PROT_READ = 0x1只读方式打开,
				*             PROT_WRITE = 0x2可写方式打开，
				*             PROT_WRITE|PROT_READ可读可写方式打开
				* @return MemoryFile
				*/
				public static MemoryFile openMemoryFile(FileDescriptor fd,int length,int mode){
					MemoryFile memoryFile = null;
					try {
						memoryFile = new MemoryFile("tem",1);
						memoryFile.close();
						Class<?> c = MemoryFile.class;
						Method native_mmap = null;
						Method[] ms = c.getDeclaredMethods();
						for(int i = 0;ms != null&&i<ms.length;i++){
							if(ms[i].getName().equals("native_mmap")){
								native_mmap = ms[i];
							}
						}
						ReflectUtil.setField("android.os.MemoryFile", memoryFile, "mFD", fd);
						ReflectUtil.setField("android.os.MemoryFile",memoryFile,"mLength",length);
						long address = (long) ReflectUtil.invokeMethod( null, native_mmap, fd, length, mode);
						ReflectUtil.setField("android.os.MemoryFile", memoryFile, "mAddress", address);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return memoryFile;
				}

				/**
				 * 获取memoryFile的ParcelFileDescriptor
				 * @param memoryFile 描述一块共享内存
				 * @return ParcelFileDescriptor
				 */
				public static ParcelFileDescriptor getParcelFileDescriptor(MemoryFile memoryFile){
					if(memoryFile == null){
						throw new IllegalArgumentException("memoryFile 不能为空");
					}
					ParcelFileDescriptor pfd;
					FileDescriptor fd = getFileDescriptor(memoryFile);
					pfd = (ParcelFileDescriptor) ReflectUtil.getInstance("android.os.ParcelFileDescriptor",fd);
					return pfd;
				}

				/**
				 * 获取memoryFile的FileDescriptor
				 * @param memoryFile 描述一块共享内存
				 * @return 这块共享内存对应的文件描述符
				 */
				public static FileDescriptor getFileDescriptor(MemoryFile memoryFile){
					if(memoryFile == null){
						throw new IllegalArgumentException("memoryFile 不能为空");
					}
					FileDescriptor fd;
					fd = (FileDescriptor) ReflectUtil.invoke("android.os.MemoryFile",memoryFile,"getFileDescriptor");
					return fd;
				}
			}
			
			
			/**
			 * 反射工具类
			 * Created by wuzr on 2016/6/27.
			 */
			public class ReflectUtil {

				/**
				 *根据类名，参数实例化对象
				 * @param className 类的路径全名
				 * @param params 构造函数需要的参数
				 * @return 返回T类型的一个对象
				 */
				public static Object getInstance(String className,Object ... params){
					if(className == null || className.equals("")){
						throw new IllegalArgumentException("className 不能为空");
					}
					try {
						Class<?> c = Class.forName(className);
						if(params != null){
							int plength = params.length;
							Class[] paramsTypes = new Class[plength];
							for (int i = 0; i < plength; i++) {
								paramsTypes[i] = params[i].getClass();
							}
							Constructor constructor = c.getDeclaredConstructor(paramsTypes);
							constructor.setAccessible(true);
							return constructor.newInstance(params);
						}
						Constructor constructor = c.getDeclaredConstructor();
						constructor.setAccessible(true);
						return constructor.newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				/**
				 * 执行instance的方法
				 * @param className 类的全名
				 * @param instance 对应的对象，为null时执行类的静态方法
				 * @param methodName 方法名称
				 * @param params 参数
				 */
				public static Object invoke(String className,Object instance,String methodName,Object ... params){
					if(className == null || className.equals("")){
						throw new IllegalArgumentException("className 不能为空");
					}
					if(methodName == null || methodName.equals("")){
						throw new IllegalArgumentException("methodName不能为空");
					}
					try {
						Class<?> c = Class.forName(className);
						if(params != null){
							int plength = params.length;
							Class[] paramsTypes = new Class[plength];
							for(int i = 0;i < plength;i++){
								paramsTypes[i] = params[i].getClass();
							}
							Method method = c.getDeclaredMethod(methodName, paramsTypes);
							method.setAccessible(true);
							return method.invoke(instance, params);
						}
						Method method = c.getDeclaredMethod(methodName);
						method.setAccessible(true);
						return method.invoke(instance);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				/**
				 * 执行指定的对方法
				 * @param instance 需要执行该方法的对象，为空时，执行静态方法
				 * @param m 需要执行的方法对象
				 * @param params 方法对应的参数
				 * @return 方法m执行的返回值
				 */
				public static Object invokeMethod(Object instance,Method m,Object ... params){
					if(m == null){
						throw new IllegalArgumentException("method 不能为空");
					}
					m.setAccessible(true);
					try {
						return m.invoke(instance,params);
					} catch (Exception e){
						e.printStackTrace();
					}
					return null;
				}

				/**
				 * 取得属性值
				 * @param className 类的全名
				 * @param fieldName 属性名
				 * @param instance 对应的对象，为null时取静态变量
				 * @return 属性对应的值
				 */
				public static Object getField(String className,Object instance,String fieldName){
					if(className == null || className.equals("")){
						throw new IllegalArgumentException("className 不能为空");
					}
					if(fieldName == null || fieldName.equals("")){
						throw new IllegalArgumentException("fieldName 不能为空");
					}
					try {
						Class c = Class.forName(className);
						Field field = c.getDeclaredField(fieldName);
						field.setAccessible(true);
						return field.get(instance);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				/**
				 * 设置属性
				 * @param className 类的全名
				 * @param fieldName 属性名
				 * @param instance 对应的对象，为null时改变的是静态变量
				 * @param value 值
				 */
				public static void setField(String className,Object instance,String fieldName,Object value){
					if(className == null || className.equals("")){
						throw new IllegalArgumentException("className 不能为空");
					}
					if(fieldName == null || fieldName.equals("")){
						throw new IllegalArgumentException("fieldName 不能为空");
					}
					try {
						Class<?> c = Class.forName(className);
						Field field = c.getDeclaredField(fieldName);
						field.setAccessible(true);
						field.set(instance, value);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				/**
				 * 根据方法名，类名，参数获取方法
				 * @param className 类名，全名称
				 * @param methodName 方法名
				 * @param paramsType 参数类型列表
				 * @return 方法对象
				 */
				public static Method getMethod(String className,String methodName,Class ... paramsType){
					if(className == null || className.equals("")){
						throw new IllegalArgumentException("className 不能为空");
					}
					if(methodName == null || methodName.equals("")){
						throw new IllegalArgumentException("methodName不能为空");
					}
					try {
						Class<?> c = Class.forName(className);
						return c.getDeclaredMethod(methodName,paramsType);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			}
			
	
	Linux共享内存
		两个关键函数：
			//该函数用来创建共享内存
			int shmget(key_t key, size_t size, int shmflg);	
			
			//要想访问共享内存，必须将其映射到当前进程的地址空间
			void *shmat(int shm_id, const void *shm_addr, int shmflg); 
			
			key_t是共享内存的唯一标识，可以说，Linux的共享内存其实是有名共享内存，而名字就是key，具体用法如下:
				
				//读取进程
				int main()  
				{  
					void *shm = NULL;//分配的共享内存的原始首地址  
					struct shared_use_st *shared;//指向shm  
					int shmid;//共享内存标识符  
					//创建共享内存  
					shmid = shmget((key_t)12345, sizeof(struct shared_use_st), 0666|IPC_CREAT);   
					//将共享内存映射到当前进程的地址空间  
					shm = shmat(shmid, 0, 0);
					//设置共享内存  
					shared = (struct shared_use_st*)shm;  
					shared->written = 0;  
					//访问共享内存
					while(1){
						if(shared->written != 0)  { 
							printf("You wrote: %s", shared->text);
							 if(strncmp(shared->text, "end", 3) == 0)  
								break;
							}}
					//把共享内存从当前进程中分离  
					if(shmdt(shm) == -1)  { }  
					//删除共享内存  
					if(shmctl(shmid, IPC_RMID, 0) == -1)   {  }  
					exit(EXIT_SUCCESS);  
				}
				
				//写进程
				int main()  
				{  
					void *shm = NULL;  
					struct shared_use_st *shared = NULL;  
					char buffer[BUFSIZ + 1];//用于保存输入的文本  
					int shmid;  
					//创建共享内存  
					shmid = shmget((key_t) 12345, sizeof(struct shared_use_st), 0666|IPC_CREAT);  
					//将共享内存连接到当前进程的地址空间  
					shm = shmat(shmid, (void*)0, 0);  
					printf("Memory attached at %X\n", (int)shm);  
					//设置共享内存  
					shared = (struct shared_use_st*)shm;  
					while(1)//向共享内存中写数据  
					{  
						//数据还没有被读取，则等待数据被读取,不能向共享内存中写入文本  
						while(shared->written == 1)  
						{  
							sleep(1);  
						}  
						//向共享内存中写入数据  
						fgets(buffer, BUFSIZ, stdin);  
						strncpy(shared->text, buffer, TEXT_SZ);  
						shared->written = 1;  
						if(strncmp(buffer, "end", 3) == 0)  
							running = 0;  
					}  
					//把共享内存从当前进程中分离  
					if(shmdt(shm) == -1)   {    }  
					sleep(2);  
					exit(EXIT_SUCCESS);  
				}
		
		Android的匿名共享内存：
			Android主要使用的方式是匿名共享内存Ashmem（Anonymous Shared Memory），跟原生的不太一样，比如它在自己的驱动中添加了互斥锁，另外通过fd的传递来实现共享内存的传递。MemoryFile是Android为匿名共享内存而封装的一个对象。
			
			MemoryFile也是进程间大数据的一个手段，开发的时候可以使用：
				
				IMemoryAidlInterface.aidl
					interface IMemoryAidlInterface {
						ParcelFileDescriptor getParcelFileDescriptor();
					}
				
				MemoryFetchService
					public class MemoryFetchService extends Service {
						@Nullable
						@Override
						public IBinder onBind(Intent intent) {
							return new MemoryFetchStub();
						}
						static class MemoryFetchStub extends IMemoryAidlInterface.Stub {
							@Override
							public ParcelFileDescriptor getParcelFileDescriptor() throws RemoteException {
								MemoryFile memoryFile = null;
								try {
									memoryFile = new MemoryFile("test_memory", 1024);
									memoryFile.getOutputStream().write(new byte[]{1, 2, 3, 4, 5});
									Method method = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
									FileDescriptor des = (FileDescriptor) method.invoke(memoryFile);
									return ParcelFileDescriptor.dup(des);
								} catch (Exception e) {}
								return null;
							}
						}
					}
				
				TestActivity.java
					Intent intent = new Intent(MainActivity.this, MemoryFetchService.class);
					bindService(intent, new ServiceConnection() {
						@Override
						public void onServiceConnected(ComponentName name, IBinder service) {

							byte[] content = new byte[10];
							IMemoryAidlInterface iMemoryAidlInterface
									= IMemoryAidlInterface.Stub.asInterface(service);
							try {
								ParcelFileDescriptor parcelFileDescriptor = iMemoryAidlInterface.getParcelFileDescriptor();
								FileDescriptor descriptor = parcelFileDescriptor.getFileDescriptor();
								FileInputStream fileInputStream = new FileInputStream(descriptor);
								fileInputStream.read(content);
							} catch (Exception e) {
							}}

						@Override
						public void onServiceDisconnected(ComponentName name) {

						}
					}, Service.BIND_AUTO_CREATE);
	
	Android系统匿名共享内存Ashmem(Anonymous Shared Memory)
		它以驱动程序的形式实现在内核空间中。它有两个特点，一是能够辅助内存管理系统来有效地管理不再使用的内存块，二是它通过Binder进程间通信机制来实现进程间的内存共享。
	
		Android系统的匿名共享内存子系统的主体是以驱动程序的形式实现在内核空间的，同时，在系统运行时库层和应用程序框架层提供了访问接口，其中，在系统运行时库层提供了C/C++调用接口，而在应用程序框架层提供了Java调用接口。
		
		
				