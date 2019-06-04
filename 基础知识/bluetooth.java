蓝牙：
	
	蓝牙—RFCOMM协议:
		串口仿真协议（RFCOMM)，RFCOMM是一个简单的协议，其中针对9针RS-232串口仿真附加了部分条款.可支持在两个蓝牙设备之间同时保持高达60路的通信连接.RFCOMM的目的是针对如何在两个不同设备上的应用之间保证一条完整的通信路径。

	蓝牙API:
		BluetoothAdapter     本地蓝牙适配器 
		BluetoothClass      蓝牙类（主要包括服务和设备） 
		BluetoothClass.Device    蓝牙设备类 
		BluetoothClass.Device.Major    蓝牙设备管理 
		BluetoothClass.Service   蓝牙服务类 
		BluetoothDevice     蓝牙设备（远程蓝牙设备） 
		BluetoothServiceSocket    监听蓝牙连接的类 
		BluetoothSocket     蓝牙连接类
	
	
		1. BluetoothAdapter
			表示本地的蓝牙适配器 （蓝牙射频）。BluetoothAdapter 是为所有蓝牙交互的入口点。它可以发现其他蓝牙设备、 查询绑定 (配对) 设备的列表、 实例化已知的 MAC 地址的BluetoothDevice（蓝牙设备） 和创建 BluetoothServerSocket 用于侦听来自其他设备的通信。直到我们建立bluetoothSocket连接之前，都要不断操作它 。BluetoothAdapter里的方法很多，常用的有以下几个：
				cancelDiscovery() //根据字面意思，是取消发现，也就是说当我们正在搜索设备的时候调用这个方法将不再继续搜索; 
				disable()//关闭蓝牙; 
				enable()//打开蓝牙;这个方法打开蓝牙不会弹出提示，更多的时候我们需要问下用户是否打开，以下两行代码同样是打开蓝牙，不过会提示用户： 
				Intemtenabler=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
				startActivityForResult(enabler,reCode);//同startActivity(enabler); 
				getAddress()//获取本地蓝牙地址 
				getDefaultAdapter()//获取默认BluetoothAdapter，实际上，也只有这一种方法获取BluetoothAdapter 
				getName()//获取本地蓝牙名称 
				getRemoteDevice(String address)//根据蓝牙地址获取远程蓝牙设备 
				getState()//获取本地蓝牙适配器当前状态（感觉可能调试的时候更需要） 
				isDiscovering()//判断当前是否正在查找设备，是返回true 
				isEnabled()//判断蓝牙是否打开，已打开返回true，否则，返回false 
				listenUsingRfcommWithServiceRecord(String name,UUIDuuid)//根据名称，UUID创建并返回BluetoothServerSocket对象，这是创建BluetoothSocket服务器端的第一步 。第一个参数表示蓝牙服务的名称，可以是任意字符串，第二个参数是UUID。
				startDiscovery()//开始搜索，这是搜索的第一步
			
		2. BluetoothDevice
			表示远程蓝牙设备。使用此类并通过BluetoothSocket类可以请求连接远程设备，或查询这台设备的信息如其名称、 地址、 类和绑定状态。
				createRfcommSocketToServiceRecord(UUIDuuid)
			根据UUID创建并返回一个BluetoothSocket,这个方法也是我们获取BluetoothDevice的目的——创建BluetoothSocket。这个类其他的方法，如getAddress()、getName()，同BluetoothAdapter。
		
		3. BluetoothServerSocket
			表示打开服务器套接字侦听传入的请求 （类似于 TCP ServerSocket）。为了连接两台 Android设备，一台设备必须用此类打开一个服务器套接字。当远程蓝牙设备向此设备发出连接请求时，而且当连接被接收时，BluetoothServerSocket 将返回连接的 BluetoothSocket。这个类有三个方法:
				accept()
				accept(inttimeout)： //两者的区别在于后者指定了过时时间，需要注意的是，执行这两个方法的时候，直到接收到了客户端的请求（或是过期之后），都会阻塞线程，应该放在新线程里运行！ 还需要注意，这两个方法都返回一个BluetoothSocket，最后的连接也是服务器端与客户端这两个BluetoothSocket的连接。
				close(); //关闭
			
		4. BluetoothSocket
			跟BluetoothServerSocket相对，是客户端。表示一个蓝牙套接字 （类似于 TCP Socket）的接口。这是一个允许应用程序与另一台蓝牙设备通过InputStream和OutputStream来交换数据的连接点。其一共5个方法，一般都会用到:
				close(); //关闭
				connect(); //连接
				getInptuStream(); //获取输入流
				getOutputStream(); //获取输出流
				getRemoteDevice(); //获取远程设备，这里指的是获取bluetoothSocket指定连接的那个远程蓝牙设备
			
		5. BluetoothClass
			描述的一般特征和蓝牙设备的功能。这是一整套只读的属性用于定义设备的主要和次要设备类和它的服务。然而，这并不是支持所有蓝牙配置文件和服务的设备，但很适用于获取设备类型.
		
		6. BluetoothProfile
			表示一个蓝牙配置文件。蓝牙配置文件是基于蓝牙通信设备之间的无线接口规范。如免提规范(Hands-Free profile)。
		
		7. BluetoothHeadset
			蓝牙耳机与手机一起使用配置文件 ,这包括蓝牙耳机和免提（v1.5） 的配置文件。
		
		8. BluetoothA2dp
			定义了如何高质量的音频可以进行流式处理从一个设备到另一个通过蓝牙连接。”A2DP”代表先进音频分配协议。
		
		9. BluetoothHealth
			表示控制蓝牙服务健康设备协议。
		
		10. BluetoothHealthCallback
			BluetoothHealthCallback 一个抽象类，您使用来实现 BluetoothHealth 回调，你必须扩展此类并实现回调方法以接收有关更改的更新应用程序的注册和蓝牙通道状态。BluetoothHealthAppConfiguration表示一个蓝牙健康第三方应用程序注册与远程蓝牙健康设备进行通信的应用程。
		
		11. BluetoothHealthAppConfiguration
			表示一个蓝牙健康第三方应用程序注册与远程蓝牙健康设备进行通信的应用程序配置。
			
		12. BluetoothProfile.ServiceListener
			通知 BluetoothProfile IPC 客户端界面时已被连接或断开服务 （即运行一个特定的配置文件内部服务）
	
	使用蓝牙的权限：
		<uses-permission android:name="android.permission.BLUETOOTH"/>
		<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
		
		在Android 5.0 之后，需要在manifest 中申明GPS硬件模块功能的使用。
			<uses-feature android:name="android.hardware.location.gps" />
		
		在 Android 6.0 及以上，还需要打开位置权限。如果应用没有位置权限，蓝牙扫描功能不能使用（其它蓝牙操作例如连接蓝牙设备和写入数据不受影响）。
			<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
	
	UUID（universal unique identifier ， 全局唯一标识符）：
		格式如下：UUID格式一般是”xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx”，可到http://www.uuidgenerator.com 申请。UUID分为5段，是一个8-4-4-4-12的字符串，这个字符串要求永不重复。
			String uuid = java.util.UUID.randomUUID().toString();
		一般在创建Socket时需要UUID作为端口的唯一性，如果两台Android设备互联，则没有什么特殊的，如果让非Android的蓝牙设备连接Android蓝牙设备，则UUID必须使用某个固定保留的UUID。
		Android中创建UUID：
			UUID  uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
		常用固定的UUID
		蓝牙串口服务(SPP)
			 SerialPortServiceClass_UUID = '{00001101-0000-1000-8000-00805F9B34FB}'
			 LANAccessUsingPPPServiceClass_UUID = '{00001102-0000-1000-8000-00805F9B34FB}'
			拨号网络服务
			 DialupNetworkingServiceClass_UUID = '{00001103-0000-1000-8000-00805F9B34FB}'
			信息同步服务
			 IrMCSyncServiceClass_UUID = '{00001104-0000-1000-8000-00805F9B34FB}'
			 SDP_OBEXObjectPushServiceClass_UUID = '{00001105-0000-1000-8000-00805F9B34FB}'
			文件传输服务
			 OBEXFileTransferServiceClass_UUID = '{00001106-0000-1000-8000-00805F9B34FB}'
			 IrMCSyncCommandServiceClass_UUID = '{00001107-0000-1000-8000-00805F9B34FB}'
			 SDP_HeadsetServiceClass_UUID = '{00001108-0000-1000-8000-00805F9B34FB}'
			 CordlessTelephonyServiceClass_UUID = '{00001109-0000-1000-8000-00805F9B34FB}'
			 SDP_AudioSourceServiceClass_UUID = '{0000110A-0000-1000-8000-00805F9B34FB}'
			 SDP_AudioSinkServiceClass_UUID = '{0000110B-0000-1000-8000-00805F9B34FB}'
			 SDP_AVRemoteControlTargetServiceClass_UUID = '{0000110C-0000-1000-8000-00805F9B34FB}'
			 SDP_AdvancedAudioDistributionServiceClass_UUID = '{0000110D-0000-1000-8000-00805F9B34FB}'
			 SDP_AVRemoteControlServiceClass_UUID = '{0000110E-0000-1000-8000-00805F9B34FB}'
			 VideoConferencingServiceClass_UUID = '{0000110F-0000-1000-8000-00805F9B34FB}'
			 IntercomServiceClass_UUID = '{00001110-0000-1000-8000-00805F9B34FB}'
			蓝牙传真服务
			 FaxServiceClass_UUID = '{00001111-0000-1000-8000-00805F9B34FB}'
			 HeadsetAudioGatewayServiceClass_UUID = '{00001112-0000-1000-8000-00805F9B34FB}'
			 WAPServiceClass_UUID = '{00001113-0000-1000-8000-00805F9B34FB}'
			 WAPClientServiceClass_UUID = '{00001114-0000-1000-8000-00805F9B34FB}'
			蓝牙打印服务
			 HCRPrintServiceClass_UUID = '{00001126-0000-1000-8000-00805F9B34FB}'
			 HCRScanServiceClass_UUID = '{00001127-0000-1000-8000-00805F9B34FB}'
			 CommonISDNAccessServiceClass_UUID = '{00001128-0000-1000-8000-00805F9B34FB}'
			 VideoConferencingGWServiceClass_UUID = '{00001129-0000-1000-8000-00805F9B34FB}'
			 UDIMTServiceClass_UUID = '{0000112A-0000-1000-8000-00805F9B34FB}'
			 UDITAServiceClass_UUID = '{0000112B-0000-1000-8000-00805F9B34FB}'
			 AudioVideoServiceClass_UUID = '{0000112C-0000-1000-8000-00805F9B34FB}'
			 SIMAccessServiceClass_UUID = '{0000112D-0000-1000-8000-00805F9B34FB}'
			 PnPInformationServiceClass_UUID = '{00001200-0000-1000-8000-00805F9B34FB}'
			 GenericNetworkingServiceClass_UUID = '{00001201-0000-1000-8000-00805F9B34FB}'
			 GenericFileTransferServiceClass_UUID = '{00001202-0000-1000-8000-00805F9B34FB}'
			 GenericAudioServiceClass_UUID = '{00001203-0000-1000-8000-00805F9B34FB}'
			 GenericTelephonyServiceClass_UUID = '{00001204-0000-1000-8000-00805F9B34FB}'
			个人局域网服务
			 PANUServiceClass_UUID = '{00001115-0000-1000-8000-00805F9B34FB}'
			 NAPServiceClass_UUID = '{00001116-0000-1000-8000-00805F9B34FB}'
			 GNServiceClass_UUID = '{00001117-0000-1000-8000-00805F9B34FB}'
			 DirectPrintingServiceClass_UUID = '{00001118-0000-1000-8000-00805F9B34FB}'
			 ReferencePrintingServiceClass_UUID = '{00001119-0000-1000-8000-00805F9B34FB}'
			 ImagingServiceClass_UUID = '{0000111A-0000-1000-8000-00805F9B34FB}'
			 ImagingResponderServiceClass_UUID = '{0000111B-0000-1000-8000-00805F9B34FB}'
			 ImagingAutomaticArchiveServiceClass_UUID = '{0000111C-0000-1000-8000-00805F9B34FB}'
			 ImagingReferenceObjectsServiceClass_UUID = '{0000111D-0000-1000-8000-00805F9B34FB}'
			 SDP_HandsfreeServiceClass_UUID = '{0000111E-0000-1000-8000-00805F9B34FB}'
			 HandsfreeAudioGatewayServiceClass_UUID = '{0000111F-0000-1000-8000-00805F9B34FB}'
			 DirectPrintingReferenceObjectsServiceClass_UUID = '{00001120-0000-1000-8000-00805F9B34FB}'
			 ReflectedUIServiceClass_UUID = '{00001121-0000-1000-8000-00805F9B34FB}'
			 BasicPringingServiceClass_UUID = '{00001122-0000-1000-8000-00805F9B34FB}'
			 PrintingStatusServiceClass_UUID = '{00001123-0000-1000-8000-00805F9B34FB}'
			人机输入服务
			 HumanInterfaceDeviceServiceClass_UUID = '{00001124-0000-1000-8000-00805F9B34FB}'
			 HardcopyCableReplacementServiceClass_UUID = '{00001125-0000-1000-8000-00805F9B34FB}'
			
			
	蓝牙使用5步曲：
	
		1. 获取本地蓝牙适配器
			BluetoothAdapter mAdapter= BluetoothAdapter.getDefaultAdapter();
		
		2. 打开蓝牙
			//弹出对话框提示用户是否打开
			Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enabler, REQUEST_ENABLE);
			//不做提示，强行打开
			// mAdapter.enable();
			}
			补充一下，使设备能够被搜索
			Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			startActivityForResult(enabler,REQUEST_DISCOVERABLE);
		
		3. 搜索设备
			1)
				mAdapter.startDiscovery(); //是第一步,可是你会发现没有返回的蓝牙设备，怎么知道查找到了呢？
			2)定义BroadcastReceiver，代码如下
				BroadcastReceiver mReceiver = new BroadcastReceiver() {
					public void onReceive(Context context, Intent intent) {
						String action = intent.getAction();
						//找到设备
						if (BluetoothDevice.ACTION_FOUND.equals(action)) {
							BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
							if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
								Log.v(TAG, "find device:" + device.getName()+ device.getAddress());
							}
						//搜索完成
						}else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
							setTitle("搜索完成");
							if (mNewDevicesAdapter.getCount() == 0) {
								Log.v(TAG,"find over");
							}
						}//执行更新列表的代码
					}
				};
				
				注:
					要注册BroadcastReceiver。
			
		4. 建立连接
			蓝牙连接是通过BluetoothSocket建立连接，服务器端（BluetoothServerSocket）和客户端（BluetoothSocket）需指定同样的UUID，才能建立连接，因为建立连接的方法会阻塞线程，所以服务器端和客户端都应启动新线程连接。
			1) 服务器端：
				BluetoothServerSocket serverSocket = mAdapter. listenUsingRfcommWithServiceRecord(serverSocketName,UUID);
				serverSocket.accept();
			2) 客户端：
				还记得刚才在BroadcastReceiver获取了BluetoothDevice么？
				BluetoothSocket clienSocket=dcvice. createRfcommSocketToServiceRecord(UUID);
				clienSocket.connect();
			
		5. 
			1）获取流
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
			2）写出、读入BluetoothServerSocket
				BluetoothServerSocket  BluetoothAdapter.listenUsingRfcommWithServiceRecord(String name, UUID)
				通过此方法监听BluetoothSocket的连接
				
				BluetoothServerSocket.accept() 开始接收BluetoothSocket
				BluetoothServerSocket.close() 关闭服务
				BluetoothSocket
				BluetoothSocket BluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID uuid)
				通过此方法向指定的BluetoothDevice发送Socket连接
				UUID:00001101-0000-1000-8000-00805F9B34FBconnect() 尝试连接boolean isConnected() 是否已连接,要求最低sdk 14+
				BluetoothDevice getRemoteDevice() 获取当前正在或已连接的设备
				InputStream getInputStream() 获取输入流
				OutputStream getOutputStream() 获取输出流
				在读取数据时用数据流
				
				DataInputStream/DataOutputStream
				基于Socket技术实现蓝牙聊天
				
				蓝牙的配对
				查找已配对的蓝牙设备
				Set<BluetoothDevice>  BluetoothAdapter.getBondedDevices()查找附件的蓝牙设备
				BluetoothAdapter.startDiscovery())
				BluetoothAdapter.isDiscovering() 是否正在查找
				BluetoothAdapter.cancelDiscovery() 取消查找
				
				注册广播接收器接收查到的设备信息
				BluetoothAdapter.ACTION_DISCOVERY_STARTED 开始查找
				BluetoothDevice.ACTION_FOUND 查找到蓝牙设备
				BluetoothDevice.EXTRA_DEVICE 获取查找到的设备信息，此数据为ParcelableExtra，需要intent.getParcelableExtra()获取到BluetoothDevice对象
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED 查找结束
				
				判断配对状态int BluetoothDevice.getBondState()  获取设备的配对状态
				BluetoothDevice.BOND_BONDED 已配对
				BluetoothDevice.BOND_BONDING 正在配对
				BluetoothDevice.BOND_NONE 未配对
				
				与指定未配对的设备配对
				配对：通过反射获取BluetoothDevice的boolean createBond()方法，并执行
				取消配对：通过反射获取BluetoothDevice的boolean removeBond()方法，并执行
			
		
	
	说明：
		通过蓝牙固有的方法获取的 ServerSocket 和 Socket，然后基本上与Socket通信一致。
		
	
	
	Android 4.3（API Level 18）开始引入Bluetooth Low Energy（BLE，低功耗蓝牙）的核心功能并提供了相应的 API。Android BLE 使用的蓝牙协议是 GATT 协议。
	
	BluetoothGatt
		这个类提供了 Bluetooth GATT 的基本功能。例如重新连接蓝牙设备，发现蓝牙设备的 Service 等等。

	BluetoothGattService
		这一个类通过 BluetoothGatt#getService 获得，如果当前服务不可见那么将返回一个 null。这一个类对应上面说过的 Service。我们可以通过这个类的 getCharacteristic(UUID uuid) 进一步获取 Characteristic 实现蓝牙数据的双向传输。

	BluetoothGattCharacteristic
		这个类对应上面提到的 Characteristic。通过这个类定义需要往外围设备写入的数据和读取外围设备发送过来的数据。
		
		
	
	
蓝牙传输协议：
	
	A2DP 全名是Advenced Audio Distribution Profile蓝牙音频传输模型协定。AVRCP全名是Audio Video Remote Cortrol Profile音频/视频远程控制配置文件。
	

Android使用蓝牙：
	1. 获得BluetoothAdapter
	2. 使能Bluetooth
	3. 设备发现
		配对完成和连接
			配对完成：
				共享一些信息
			连接：
				共享一个RFCOMM双方能够传递数据
	
	4. 查询配对设备
		getBoundDevices() 	查询配对设备集合
		startDiscovery() 	启动设备发现	---> 异步(12s),立即返回的值表明设备发现启动是否成功
		为了接收每一个设备的信息，应用程序必须为 ACTION_FOUND intent 注册一个 BroadcastReceiver
		EXTRA_DEVICE 和 EXTRA_CLASS 标志，分别包含了 BluetoothDevice 和 Bluetoothclass信息
	
	5. 使能发现能力
		startActivityForResult(intent,int);		ACTION_REQUEST_DISCOVERABLE	(120s)		可通过EXTRA_DISCOVERABLE_DURATION进行修改该时长，最长为 (3600s)
		发现模式改变 BroadcastReceiver  ACTION_SCAN_MODE_CHANGED 	
	
	6. 连接设备
		获得 BluetoothSocket
		server端在连接建立被接受时获得
		client端在为server端打开RFCOMM 通道获得
		
		
			
		







				

