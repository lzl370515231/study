# Android_Bluetooth

## Bluetooth

### 版本介绍

1.1、1.2、2.0、2.1、3.0、4.0、4.1、4.2、5.0。

1.x~3.0 之间的称之为传统蓝牙，4.x开始的蓝牙我们称之为低功耗蓝牙也就是蓝牙 ble。Android4.3+ 才支持 BLE API。

#### 蓝牙3.0标准

- 是一种全新的交替射频技术，允许蓝牙协议栈针对任一任务动态地选择正确射频
- 蓝牙3.0的数据传输率提高到了大约24Mbps(即可在需要的时候调用802.11 WI-FI用于实现高速数据传输)。
- 在传输速度上，蓝牙3.0是蓝牙2.0的八倍，可以轻松用于录像机至高清电视、PC至PMP、UMPC至打印机之间的资料传输，但是需要双方都达到此标准才能实现功能。

#### 蓝牙4.0标准

- 新版本的最大意义在于低功耗
- 同时加强不同OEM厂商之间的设备兼容性，并且降低延迟，理论最高传输速度依然为24Mbps(即3MB/s)，有效覆盖范围扩大到100米(之前的版本为10米)
- 高出3.0蓝牙版本4倍以上的降噪技术。其CVC6.0消噪技术，比3.0版本的CVC4.0消噪技术更上一层楼，更深度的降噪，让你的每一次通话、每一首歌曲都可以近在咫尺。
- 4.0以上版本的真智能—80HZCPU，高出3.0蓝牙版本5倍以上的无线传输速率。更为快捷的传输速度，让你无论听歌、通话，或者是游戏都可以拥有更加顺畅的体验。

##### 兼容性

蓝牙4.0有几种模式，如果是蓝牙4.0低功耗模式单模的设备（常称为BLE模式），是不向下兼容的。

### 分类

- 传统蓝牙

  传统蓝牙适用于较为耗电的操作，其中包含Android 设备之间的流式传输和通信等。

- 低功耗蓝牙

  BLE



## 基础流程

为了让支持蓝牙的设备能够在彼此之间传输数据，它们必须先通过*配对*过程形成通信通道。其中一台设备（*可检测到的设备*）需将自身设置为可接收传入的连接请求。另一台设备会使用*服务发现*过程找到此可检测到的设备。在可检测到的设备接受配对请求后，这两台设备会完成*绑定*过程，并在此期间交换安全密钥。二者会缓存这些密钥，以供日后使用。完成配对和绑定过程后，两台设备会交换信息。当会话完成时，发起配对请求的设备会发布已将其链接到可检测设备的通道。但是，这两台设备仍保持绑定状态，因此在未来的会话期间，只要二者在彼此的范围内且均未移除绑定，便可自动重新连接。

## 蓝牙权限

#### BLUETOOTH	(Must)

需使用此权限执行任何蓝牙通信，例如请求连接、接受连接和传输数据等。

```xml
<manifest ...>
    <uses-permission android:name="android.permission.BLUETOOTH"/>
    ...
</manifest>
```



#### ACCESS_COARSE_LOCATION 或 ACCESS_FINE_LOCATION	(Must)

需要位置权限的原因是，蓝牙扫描可用于收集有关用户位置的信息。

```xml
<manifest ...>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    ...
</manifest>
```



#### BLUETOOTH_ADMIN

让应用启动设备发现或操作蓝牙设置，还必须声明 BLUETOOTH_ADMIN 权限。除非应用是根据用户请求修改蓝牙设置的“超级管理员”，否则不应使用此权限所授予的其他功能。

```xml
<manifest ...>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    ...
</manifest>
```



## 配置文件

### Version

Android 3.0，Bluetooth API 便支持使用蓝牙配置文件。

### 概念

*蓝牙配置文件*是适用于设备间蓝牙通信的无线接口规范。举个例子：免提配置文件。如果手机要与无线耳机进行连接，则两台设备都必须支持免提配置文件。

### API 分类

#### 耳机

Android 提供 `BluetoothHeadset` 类，该类是用于控制蓝牙耳机服务的代理。其中包括蓝牙耳机和免提 (v1.5) 的配置文件。**`BluetoothHeadset` 类包含对 AT 命令的支持**。

#### A2DP

蓝牙立体声音频传输配置文件 (A2DP) 定义如何通过蓝牙连接和流式传输，将高质量音频从一个设备传输至另一个设备。Android 提供 `BluetoothA2dp` 类，该类是用于控制蓝牙 A2DP 服务的代理。

#### 健康设备 （4.0）

Android 4.0（API 级别 14）引入了对蓝牙健康设备配置文件 (HDP) 的支持。该配置文件允许您创建应用，从而使用蓝牙与支持蓝牙功能的健康设备（例如心率监测仪、血糖仪、温度计、台秤等）进行通信。

### 使用步骤

1. 获取默认适配器
2. 设置 BluetoothProfile.ServiceListener。此侦听器会在 BluetoothProfile 客户端连接到服务或断开服务连接时向其发送通知。
3. 适用 getProfileProxy() 与配置文件所关联的配置文件代理对象建立连接。
4. 在 `onServiceConnected()` 中，获取配置文件代理对象的句柄。
5. 获得配置文件代理对象后，您可以用其监视连接状态，并执行与该配置文件相关的其他操作。

```java
BluetoothHeadset bluetoothHeadset;

// Get the default adapter
BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

private BluetoothProfile.ServiceListener profileListener = new BluetoothProfile.ServiceListener() {
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        if (profile == BluetoothProfile.HEADSET) {
            bluetoothHeadset = (BluetoothHeadset) proxy;
        }
    }
    public void onServiceDisconnected(int profile) {
        if (profile == BluetoothProfile.HEADSET) {
            bluetoothHeadset = null;
        }
    }
};

// Establish connection to the proxy.
bluetoothAdapter.getProfileProxy(context, profileListener, BluetoothProfile.HEADSET);

// ... call functions on bluetoothHeadset

// Close proxy connection after use.
bluetoothAdapter.closeProfileProxy(bluetoothHeadset);
```

### 健康设备配置文件

#### 概念

**源设备**

向 Android 手机或平板电脑等智能设备传输医疗数据的健康设备，例如体重秤、血糖仪或温度计。

**接收设备**

接收医疗数据的智能设备。在 Android HDP 应用中，接收设备由 `BluetoothHealthAppConfiguration` 对象表示。

**注册**

用于注册接收设备（从而与特定健康设备进行通信）的过程。

**连接**

用于开放健康设备（源设备）与智能设备（接收设备）之间通道的过程。

#### 创建HDP应用

1. 获取 `BluetoothHealth` 代理对象的引用。

   与常规耳机和采用 A2DP 配置文件的设备类似，您必须使用 `BluetoothProfile.ServiceListener` 和 `HEALTH` 配置文件类型来调用 `getProfileProxy()`，以便与配置文件代理对象建立连接。

2. 创建 `BluetoothHealthCallback`，并注册充当健康接收设备的应用配置 (`BluetoothHealthAppConfiguration`)。

3. 建立与健康设备的连接。

4. 成功连接至健康设备后，使用文件描述符对健康设备执行读/写操作。接收的数据需使用实现 [IEEE 11073](http://standards.ieee.org/develop/wg/PHD.html) 规范的健康管理器进行解释。

5. 完成后，关闭健康通道并取消注册该应用。该通道在长期闲置时也会关闭。

## 传统蓝牙

### 设置蓝牙

1. 获取 BluetoothAdapter

   所有蓝牙 Activity 都需要 `BluetoothAdapter`。如要获取 `BluetoothAdapter`，请调用静态的 `getDefaultAdapter()` 方法。此方法会返回一个 `BluetoothAdapter` 对象，表示设备自身的蓝牙适配器（蓝牙无线装置）。整个系统只有一个蓝牙适配器，并且您的应用可使用此对象与之进行交互。如果 `getDefaultAdapter()` 返回 `null`，则表示设备不支持蓝牙。

   ```java
   BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
   if (bluetoothAdapter == null) {
       // Device doesn't support Bluetooth
   }
   ```

   

2. 启用蓝牙

   调用 `isEnabled()`，以检查当前是否已启用蓝牙。如果此方法返回 false，则表示蓝牙处于停用状态。如要请求启用蓝牙，请调用 `startActivityForResult()`，从而传入一个 `ACTION_REQUEST_ENABLE` Intent 操作。此调用会发出通过系统设置启用蓝牙的请求（无需停止应用）。

   ```java
   if (!bluetoothAdapter.isEnabled()) {
       Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
       startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
   }
   ```

应用还可选择侦听 ACTION_STATE_CHANGED 广播 Intent，每当蓝牙状态发生变化时，系统都会广播此 Intent。此广播包含额外字段 EXTRA_STATE 和 EXTRA_PREVIOUS_STATE，二者分别包含新的和旧的蓝牙状态。这些额外字段可能为以下值：STATE_TURNING_ON、STATE_ON、STATE_TURNING_OFF 和 STATE_OFF。如果您的应用需检测对蓝牙状态所做的运行时更改，请侦听此广播。

### 查找设备

利用 `BluetoothAdapter`，您可以通过设备发现或查询配对设备的列表来查找远程蓝牙设备。

#### 被配对与被连接之间的区别

- *被配对*是指两台设备知晓彼此的存在，具有可用于身份验证的共享链路密钥，并且能够与彼此建立加密连接。
- *被连接*是指设备当前共享一个 RFCOMM 通道，并且能够向彼此传输数据。当前的 Android Bluetooth API 要求规定，只有先对设备进行配对，然后才能建立 RFCOMM 连接。在使用 Bluetooth API 发起加密连接时，系统会自动执行配对。

#### 查询已配对设备

在执行设备发现之前，您必须查询已配对的设备集，以了解所需的设备是否处于已检测到状态。为此，请调用 `getBondedDevices()`。此方法会返回一组表示已配对设备的 `BluetoothDevice` 对象。例如，您可以查询所有已配对设备，并获取每台设备的名称和 MAC 地址。

```java
Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

if (pairedDevices.size() > 0) {
    // There are paired devices. Get the name and address of each paired device.
    for (BluetoothDevice device : pairedDevices) {
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address
    }
}
```

如要发起与蓝牙设备的连接，您只需从关联的 `BluetoothDevice` 对象获取 MAC 地址，您可通过调用 `getAddress()` 检索此地址。

##### 注：

执行设备发现将消耗蓝牙适配器的大量资源。在找到要连接的设备后，请务必使用 `cancelDiscovery()` 停止发现，然后再尝试连接。此外，您不应在连接到设备的情况下执行设备发现，因为发现过程会大幅减少可供任何现有连接使用的带宽。

#### 启用可检测性

见发现设备--》启用设备可检测性

### 发现设备

如要开始发现设备，只需调用 `startDiscovery()`。该进程为异步操作，并且会返回一个布尔值，指示发现进程是否已成功启动。发现进程通常包含约 12 秒钟的查询扫描，随后会对发现的每台设备进行页面扫描，以检索其蓝牙名称。

应用必须针对 `ACTION_FOUND` Intent 注册一个 BroadcastReceiver，以便接收每台发现的设备的相关信息。系统会为每台设备广播此 Intent。Intent 包含额外字段 `EXTRA_DEVICE` 和 `EXTRA_CLASS`，二者又分别包含 `BluetoothDevice` 和 `BluetoothClass`。

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    ...

    // Register for broadcasts when a device is discovered.
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    registerReceiver(receiver, filter);
}

// Create a BroadcastReceiver for ACTION_FOUND.
private final BroadcastReceiver receiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Discovery has found a device. Get the BluetoothDevice
            // object and its info from the Intent.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address
        }
    }
};

@Override
protected void onDestroy() {
    super.onDestroy();
    ...

    // Don't forget to unregister the ACTION_FOUND receiver.
    unregisterReceiver(receiver);
}
```

#### 启用设备可检测性

希望将本地设备设为可被其他设备检测到，请使用 `ACTION_REQUEST_DISCOVERABLE` Intent 调用 `startActivityForResult(Intent, int)`。这样便可发出启用系统可检测到模式的请求，从而无需导航至设置应用，避免暂停使用您的应用。默认情况下，设备处于可检测到模式的时间为 120 秒（2 分钟）。通过添加 `EXTRA_DISCOVERABLE_DURATION` Extra 属性，您可以定义不同的持续时间，最高可达 3600 秒（1 小时）。

##### 注:

如果将 `EXTRA_DISCOVERABLE_DURATION` Extra 属性的值设置为 0，则设备将始终处于可检测到模式。此配置安全性低，因而非常不建议使用。

```java
Intent discoverableIntent =
        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
startActivity(discoverableIntent);
```

如果尚未在设备上启用蓝牙，则启用设备可检测性会自动启用蓝牙。



设备将在分配的时间内以静默方式保持可检测到模式。希望在可检测到模式发生变化时收到通知，则可以为 `ACTION_SCAN_MODE_CHANGED` Intent 注册 BroadcastReceiver。此 Intent 将包含额外字段 `EXTRA_SCAN_MODE` 和 `EXTRA_PREVIOUS_SCAN_MODE`，二者分别提供新的和旧的扫描模式。每个 Extra 属性可能拥有以下值：

- `SCAN_MODE_CONNECTABLE_DISCOVERABLE`

  设备处于可检测到模式。

- `SCAN_MODE_CONNECTABLE`

  设备未处于可检测到模式，但仍能收到连接。

- `SCAN_MODE_NONE`

  设备未处于可检测到模式，且无法收到连接。

如果要发起对远程设备的连接，则无需启用设备可检测性。只有当希望应用对接受传入连接的服务器套接字进行托管时，才有必要启用可检测性，因为在发起对其他设备的连接之前，远程设备必须能够发现这些设备。

### 连接设备

如要在两台设备之间创建连接，您必须同时实现服务器端和客户端机制，因为其中一台设备必须开放服务器套接字，而另一台设备必须使用服务器设备的 MAC 地址发起连接。服务器设备和客户端设备均会以不同方法获得所需的 `BluetoothSocket`。接受传入连接后，服务器会收到套接字信息。在打开与服务器相连的 RFCOMM 通道时，客户端会提供套接字信息。

当服务器和客户端在同一 RFCOMM 通道上分别拥有已连接的 `BluetoothSocket` 时，即可将二者视为彼此连接。这种情况下，每台设备都能获得输入和输出流式传输，并开始传输数据，相关详细介绍请参阅[管理连接](https://developer.android.com/guide/topics/connectivity/bluetooth?hl=zh-cn#ManageAConnection)部分。

#### 连接技术

一种实现技术是自动将每台设备准备为一个服务器，从而使每台设备开放一个服务器套接字并侦听连接。在此情况下，任一设备都可发起与另一台设备的连接，并成为客户端。或者，其中一台设备可显式托管连接并按需开放一个服务器套接字，而另一台设备则发起连接。

#### 作为服务器连接

当您需要连接两台设备时，其中一台设备必须保持开放的 `BluetoothServerSocket`，从而充当服务器。服务器套接字的用途是侦听传入的连接请求，并在接受请求后提供已连接的 `BluetoothSocket`。从 `BluetoothServerSocket` 获取 `BluetoothSocket` 后，您可以（并且应该）舍弃 `BluetoothServerSocket`，除非您的设备需要接受更多连接。

##### 步骤

1. 通过调用 `listenUsingRfcommWithServiceRecord()` 获取 `BluetoothServerSocket`

   该字符串是服务的可识别名称，系统会自动将其写入到设备上的新服务发现协议 (SDP) 数据库条目。此名称没有限制，可直接使用您的应用名称。SDP 条目中也包含通用唯一标识符 (UUID)，这也是客户端设备连接协议的基础。换言之，当客户端尝试连接此设备时，它会携带 UUID，从而对其想要连接的服务进行唯一标识。为了让服务器接受连接，这些 UUID 必须互相匹配。

   UUID 是一种标准化的 128 位格式，可供字符串 ID 用来对信息进行唯一标识。UUID 的特点是其足够庞大，因此您可以选择任意随机 ID，而不会与其他任何 ID 发生冲突。在本例中，其用于对应用的蓝牙服务进行唯一标识。如要获取供应用使用的 UUID，您可以从网络上的众多随机 UUID 生成器中任选一种，然后使用 `fromString(String)` 初始化一个 `UUID`。

2. 通过调用 accept() 开始侦听连接请求

   当服务器接受连接或异常发生时，该调用便会返回。只有当远程设备发送包含 UUID 的连接请求，并且该 UUID 与使用此侦听服务器套接字注册的 UUID 相匹配时，服务器才会接受连接。连接成功后，`accept()` 将返回已连接的 `BluetoothSocket`。

3. 如果无需接受更多连接，请调用 close()

   此方法调用会释放服务器套接字及其所有资源，但不会关闭 `accept()` 所返回的已连接的 `BluetoothSocket`。与 TCP/IP 不同，RFCOMM 一次只允许每个通道有一个已连接的客户端，因此大多数情况下，在接受已连接的套接字后，您可以立即在 `BluetoothServerSocket` 上调用 `close()`。

由于 accept() 是阻塞调用，因此您不应在主 Activity 界面线程中执行该调用，这样您的应用才仍然可以响应其他用户的交互。通常，您可以在应用所管理的新线程中完成所有涉及 BluetoothServerSocket 或 BluetoothSocket 的工作。如要取消 accept() 等被阻塞的调用，请通过另一个线程，在 BluetoothServerSocket 或 BluetoothSocket 上调用 close()。请注意，BluetoothServerSocket 或 BluetoothSocket 中的所有方法都是线程安全的方法。

##### 示例

```java
private class AcceptThread extends Thread {
    
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread() {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket);
                mmServerSocket.close();
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
```

#### 作为客户端连接

如果远程设备在开放服务器套接字上接受连接，则为了发起与此设备的连接，您必须首先获取表示该远程设备的 `BluetoothDevice` 对象。然后，您必须使用 `BluetoothDevice` 来获取 `BluetoothSocket` 并发起连接。

##### 步骤

1. 使用 `BluetoothDevice`，通过调用 `createRfcommSocketToServiceRecord(UUID)` 获取 `BluetoothSocket`。

   此方法会初始化 `BluetoothSocket` 对象，以便客户端连接至 `BluetoothDevice`。此处传递的 UUID 必须与服务器设备在调用 `listenUsingRfcommWithServiceRecord(String, UUID)` 开放其 `BluetoothServerSocket` 时所用的 UUID 相匹配。如要使用匹配的 UUID，请通过硬编码方式将 UUID 字符串写入您的应用，然后通过服务器和客户端代码引用该字符串。

2. 通过调用 `connect()` 发起连接。请注意，此方法为阻塞调用。

   当客户端调用此方法后，系统会执行 SDP 查找，以找到带有所匹配 UUID 的远程设备。如果查找成功并且远程设备接受连接，则其会共享 RFCOMM 通道以便在连接期间使用，并且 `connect()` 方法将会返回。如果连接失败，或者 `connect()` 方法超时（约 12 秒后），则此方法将引发 `IOException`。

##### 示例

```java
private class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        manageMyConnectedSocket(mmSocket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
```

请注意，此段代码在尝试连接之前先调用了 `cancelDiscovery()`。您应始终在 `connect()` 之前调用 `cancelDiscovery()`，这是因为无论当前是否正在执行设备发现，`cancelDiscovery()` 都会成功。但是，如果应用需要确定是否正在执行设备发现，您可以使用 `isDiscovering()` 进行检测。

### 管理连接

成功连接多台设备后，每台设备都会有已连接的 `BluetoothSocket`。这一点非常有趣，因为这表示您可以在设备之间共享信息。使用 `BluetoothSocket` 传输数据的一般过程如下所示：

1. 使用 `getInputStream()` 和 `getOutputStream()`，分别获取通过套接字处理数据传输的 `InputStream` 和 `OutputStream`。
2. 使用 `read(byte[])` 和 `write(byte[])` 读取数据以及将其写入数据流。

还需考虑实现细节。具体来说，您应使用专门的线程从数据流读取数据，以及将数据写入数据流。这一点非常重要，因为 `read(byte[])` 和 `write(byte[])` 方法都是阻塞调用。`read(byte[])` 方法将会阻塞，直至从数据流中读取数据。`write(byte[])` 方法通常不会阻塞，但若远程设备调用 `read(byte[])` 方法的速度不够快，进而导致中间缓冲区已满，则该方法可能会保持阻塞状态以实现流量控制。因此，线程中的主循环应专门用于从 `InputStream` 中读取数据。您可使用线程中单独的公共方法，发起对 `OutputStream` 的写入操作。

#### 示例

```java
public class MyBluetoothService {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler handler; // handler that gets info from Bluetooth service

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
```

当构造函数获取必要的数据流后，线程会等待通过 `InputStream` 传入的数据。当 `read(byte[])` 返回数据流中的数据时，将使用来自父类的 `Handler` 成员将数据发送到主 Activity。然后，线程会等待从 `InputStream` 中读取更多字节。

发送传出数据不外乎从主 Activity 调用线程的 `write()` 方法，并传入要发送的字节。此方法会调用 `write(byte[])`，从而将数据发送到远程设备。如果在调用 `write(byte[])` 时引发 `IOException`，则线程会发送一条 Toast 至主 Activity，向用户说明设备无法将给定的字节发送到另一台（连接的）设备。

借助线程的 `cancel()` 方法，您可通过关闭 `BluetoothSocket` 随时终止连接。当您结束蓝牙连接的使用时，应始终调用此方法。

### 关键类和接口

#### BluetoothAdapter

表示本地蓝牙适配器（蓝牙无线装置）。`BluetoothAdapter` 是所有蓝牙交互的入口点。借助该类，您可以发现其他蓝牙设备、查询已绑定（已配对）设备的列表、使用已知的 MAC 地址实例化 `BluetoothDevice`，以及通过创建 `BluetoothServerSocket` 侦听来自其他设备的通信。

#### BluetoothDevice

表示远程蓝牙设备。借助该类，您可以通过 `BluetoothSocket` 请求与某个远程设备建立连接，或查询有关该设备的信息，例如设备的名称、地址、类和绑定状态等。

#### BluetoothSocket

表示蓝牙套接字接口（类似于 TCP `Socket`）。这是允许应用使用 `InputStream` 和 `OutputStream` 与其他蓝牙设备交换数据的连接点。

#### BluetoothServerSocket

表示用于侦听传入请求的开放服务器套接字（类似于 TCP `ServerSocket`）。如要连接两台 Android 设备，其中一台设备必须使用此类开放一个服务器套接字。当远程蓝牙设备向此设备发出连接请求时，该设备接受连接，然后返回已连接的 `BluetoothSocket`。

#### BluetoothClass

描述蓝牙设备的一般特征和功能。这是一组只读属性，用于定义设备的类和服务。虽然这些信息会提供关于设备类型的有用提示，但该类的属性未必描述设备支持的所有蓝牙配置文件和服务。

#### BluetoothProfile

表示蓝牙配置文件的接口。*蓝牙配置文件*是适用于设备间蓝牙通信的无线接口规范。举个例子：免提配置文件。如需了解有关配置文件的详细讨论，请参阅[使用配置文件](https://developer.android.com/guide/topics/connectivity/bluetooth?hl=zh-cn#Profiles)。

#### BluetoothHeadset

提供蓝牙耳机支持，以便与手机配合使用。这包括蓝牙耳机配置文件和免提 (v1.5) 配置文件。

#### BluetoothA2dp

定义如何使用蓝牙立体声音频传输配置文件 (A2DP)，通过蓝牙连接将高质量音频从一个设备流式传输至另一个设备。

#### BluetoothHealth

表示用于控制蓝牙服务的健康设备配置文件代理。

#### BluetoothHealthCallback

用于实现 `BluetoothHealth` 回调的抽象类。您必须扩展此类并实现回调方法，以接收关于应用注册状态和蓝牙通道状态变化的更新内容。

#### BluetoothHealthAppConfiguration

表示第三方蓝牙健康应用注册的应用配置，该配置旨在实现与远程蓝牙健康设备的通信。

#### BluetoothProfile.ServiceListener

当 `BluetoothProfile` 进程间通信 (IPC) 客户端连接到运行特定配置文件的内部服务或断开该服务连接时，向该客户端发送通知的接口。



## 主流 4.0BLE蓝牙

### 特点

1. 低功耗蓝牙比传统蓝牙，传输速度更快，覆盖范围更广，安全性更高，延迟更短，耗电极低等等优点
2. 传统的一般通过socket方式，而低功耗蓝牙是通过Gatt协议来实现

### 特别注意

- 当用户使用BLE将设备进行配对时，这两个设备之间通信的数据可以被用户设备商的所有应用程序访问。

### BLE工作模式

#### 主从模式

USR-BLE100支持主设备模式，可以与一个从设备进行连接。在此模式下可以对周围设备进行搜索并选择需要连接的从设备进行连接。同时可以设置默认连接从设备的MAC地址，这样模块上电之后就可以查找此模块并进行连接。并且支持白名单功能，用户只需要把需要连接的设备的MAC写入白名单中，模块搜索到符合白名单的设备时进行连接。

用户不需要关注串口数据与无线数据包之间的数据转换过程，只需通过简单的参数设置，即可实现主设备串口与从设备串口之间的数据透明通信。

##### 主机连接从设备可以分为3种方式

###### 采用搜索的方式

**使用前需要设置如下参数：**

1. 设置工作模式为主设备模式 AT+MODE =M
2. 开启搜索模式 AT+SCAN
3. 如果搜索到从设备，如果序号是1，可以使用快速连接命令进行连接。 AT+CONN=1
4. 完成设置后，等待指示灯常亮即可代表连接成功，此时两个串口可以进行数据的透明传输。

###### 从设备MAC地址

如果你知道要连接的从设备的MAC地址也可以采用下面的方式进行连接：

1. 使用 MAC 绑定AT指令设置模块上电默认连接从设备MAC地址

   AT+CONNADD=FFFFFFFFFF11

2. 设置完成之后使用重启指令重启模块，模块重启之后连接设置的从设备的地址

   AT+Z

#### 从设备模式

BLE支持从设备模式，在此模式下完全符合BLE4.1协议，用户可以根据协议自己开发APP。此模式下包含一个串口收发的Service，用户可以通过UUID找到它，里面有两个通道，分别是读和写。用户可以操作这两个通道进行数据的传输。

  如果用户使用USR-BLE100的主设备与该从设备相连接，那么就无需关注里面的协议，两个设备的串口直接就可以进行数据的透明传输，为用户建立一个简单的无线传输通道。

   在此模式下，用户需要将模块的工作模式设置为从设备模式。用户如果自己开发APP需要我们模块的UUID进行连接，UUID为：0x31,0x01,0x9b,0x5f,0x80,0x00,0x00,0x80,0x00,0x10,0x00,0x00,0xd0,0xcd,0x03,0x00我们提供连接的示例程序。

1. 设置模块工作模式为从设备，指令为 AT+MODE=S
2. 用户可以通过下面指令查询模块的连接情况 AT+LINK
3. 用户也可以使用下面指令将现在连接断开 AT+DISCONN
4. 用户如果不想模块被发现和连接，可以使用下面指令关闭广播数据 AT+ADP=OFF

#### 广播模式

比如说USR-BLE100支持广播模式，在这种模式下模块可以一对多进行广播。用户可以通过AT指令设置模块广播的数据，模块可以在低功耗的模式下持续的进行广播，应用于极低功耗，小数据量，单向传输的应用场合，比如无线抄表，室内定位等功能。

常见的蓝牙模块的工作模式
在此模式下，用户可以设置模块进行小数据量广播，用户需要在APP开发时调用BLE标准的接口进行获取，数据需要使用AT指令进行设置

1. 首先将模块模式设置到广播模式 AT+MODE=B

2. 使用AT指令设置模块要发送的数据，数据位16进制长度不超过30字节，广播格式请参考BLE 协议。

   AT+ADVDATA=0201041Aff4c000215B9407F30F5F8466EAFF925556B57FE6D0001000251

3. 通过蓝牙监听软件可以获取到监听的数据包

#### 组网模式

USR-BLE100支持Mesh组网模式，在这种模式下模块可以实现简单的自组网络，每个模块只需要设置相同的通讯密码就可以加入到同一网络当中，每一个模块都可以发起数据，每个模块可以收到数据并且进行回复。并且不需要网关，即使某一个设备出现故障也会跳过并选择最近的设备进行传输。

USR-BLE100支持Mesh组网模式，可以简单的将多个模块加入到网络中来，利用星型网络和中继技术，每个网络可以连接超过65000个节点，网络和网络还可以互连，最终可将无数蓝牙模块通过手机、平板电脑或PC进行互联或直接操控。并且不需要网关，即使某一个设备出现故障也会跳过并选择最近的设备进行传输。整个联网过程只需要设备上电并设置通讯密码就可以自动组网，真正实现简单互联。

1. 首先我们需要将模块模式切换到Mesh组网模式

   AT+MODE=F

2. 设置通讯密码，模块间联网靠密码进行区分 AT+PASS=123456

3. 重启模块，模块进入组网模式 AT+Z

4. 此时我们将多个模块按照这种方式进行设置，当一个模块串口发送数据时，周围靠近的模块就会收到， 然后将其输出到串口，并且将数据再发送给周围未收到数据的模块，依次类推。

5. 当收到数据的设备需要回复时直接串口发送，最终第一次发送的模块会收到回复，完成网络内部通讯。

### BLE组成

- Service

- Characteristic

- Descriptor

  这三部分都用UUID作为唯一标识符。UUID为这种格式：0000ffe1-0000-1000-8000-00805f9b34fb。比如有3个Service，那么就有三个不同的UUID与Service对应。这些UUID都写在硬件里，我们通过BLE提供的API可以读取到。

- 一个BLE终端可以包含多个Service， 一个Service可以包含多个Characteristic，一个Characteristic包含一个value和多个Descriptor，一个Descriptor包含一个Value。

  

  Characteristic是比较重要的，是手机与BLE终端交换数据的关键，读取设置数据等操作都是操作Characteristic的相关属性。

### API相关介绍

#### GATT

1. Gneric Attribute Profile。通过ble连接，读写属性类小数据Profile通用的规范。现在所有的ble应用Profile 都是基于GATT。
2. GATT是基于ATT Potocal的ATT针对BLE设备专门做的具体就是传输过程中使用尽量少的数据，每个属性都有个唯一的UUID，属性chartcteristics and Service的形式传输。
3. Service是Characteristic的集合。
4. Characteristic 特征类型。

#### 操作步骤

1. 首先获取BluetoothManager
2. 获取BluetoothAdapter
3. 创建BluetoothAdapter.LeScanCallback
4. 开始搜索设备
5. BluetoothDevice 描述了一个蓝牙设备 提供了getAddress()设备Mac地址,getName()设备的名称
6. 开始连接设备
7. 连接到设备之后获取设备的服务(Service)和服务对应的Characteristic
8. 获取到特征之后，找到服务中可以向下位机写指令的特征，向该特征写入指令
9. 写入成功之后，开始读取设备返回来的数据
10. 断开连接
11. 数据的转换方法

##### 权限

```xml
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
```

除了蓝牙权限外，如果需要BLE feature则还需要声明uses-feature：

```xml
<uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
```

**required**为true时，则应用只能在支持BLE的Android设备上安装运行；required为false时，Android设备均可正常安装运行，需要在代码运行时判断设备是否支持BLE feature：

```java
if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
    Toast.makeText(this.R.string.ble_no_supported,Toast.LENGTH_SHORT).show();
    finish();
}
```

##### 1. 开启蓝牙

**Android 4.3+**

```java
private BluetoothManager bluetoothManager;
bluetoothManager =   (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
mBluetoothAdapter = bluetoothManager.getAdapter();
```

**或者是：**

```java
mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
```

##### 2. 判断手机设备是否有蓝牙模块

```java
// 检查设备上是否支持蓝牙
if (mBluetoothAdapter == null) {
    showToast("没有发现蓝牙模块");
    return;
}
```

##### 3. 开启蓝牙设备

###### 方式一

```java
if (!mBluetoothAdapter.isEnabled()) {
    mBluetoothAdapter.enable();
}
```

###### 方式二

```java
if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
}
```

##### 4. 扫描蓝牙设备

```java
private void scanLeDevice(final boolean enable) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        if (enable) {
            devices.clear();//清空集合
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }
            }, INTERVAL_TIME);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            try {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } catch (Exception e) {
                
            }
        }
    }
}
```

只需要搜索指定UUID的外设，你可以调用 startLeScan(UUID[], BluetoothAdapter.LeScanCallback)方法。
其中UUID数组指定你的应用程序所支持的GATT Services的UUID。

**LeScanCallback的初始化代码如下：**

```java
private void initCallBack(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (device != null) {
                            if (!TextUtils.isEmpty(device.getName())) {
                                // devices.add(device);
                                String name = device.getName();
                                if (name.contains(BluetoothDeviceAttr.OYGEN_DEVICE_NAME)) {
                                    if (!devices.contains(device)) {
                                        devices.add(device);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        };
    } else {
        getToast("设备蓝牙版本过低");
        return;
    }
}
```

那么如果在设备多的情况下我们讲搜出很多的设备。我们可以选择我们所需要的地址进行链接。但是这类要注意的是：搜索时，你只能搜索传统蓝牙设备或者BLE设备，两者完全独立，不可同时被搜索。

##### 5. 进行链接设备

```java
final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
if (device == null) {
    Log.w(TAG, "Device not found.  Unable to connect.");
    return false;
}
// We want to directly connect to the device, so we are setting the autoConnect
// parameter to false.
mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
```

第一个参数是上下文对象Context，第二个参数是是否自动连接，第三个是蓝牙的GattCallback回调。

**GattCallback**

- onConnectionStateChange 连接状态改变

- onServicesDiscovered 发现服务
- onCharacteristicRead 读取字符
- onCharacteristicWrite 写入指令成功
- onCharacteristicChanged 蓝牙回传数据处理
- onDescriptorRead 读的描述返回
- onDescriptorWrite 写的描述返回
- onReliableWriteCompleted
- onReadRemoteRssi 手机距离设备的远近

```java
private BluetoothGattCallback GattCallback = new BluetoothGattCallback() {
    // 这里有9个要实现的方法，看情况要实现那些，用到那些就实现那些
    //当连接状态发生改变的时候
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
        
    }
    
    //回调响应特征写操作的结果
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
        
    }
    
    //回调响应特征读操作的结果。
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        
    }
    
    //当服务被发现的时候回调的结果
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    }
    
    //当连接能被被读的操作
    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
      }  
    ......
};
```

##### 6. 发送数据给蓝牙设备

其中服务通道，数据通道，指令，是根据蓝牙设备协议确定的

```java
String serveGallery = "xxx-xxx-xxx-xxx";//服务通道
String gallery = "xxx-xxx-xxx-xxx";//数据通道
BluetoothGattCharacteristic bluetoothGattCharacteristic = bleGattCharMap.get(serveGallery).get(gallery);
//byte 为指令
bluetoothGattCharacteristic.setValue(new byte[]{ (byte) 0XBE, 0X02,0X03, (byte) 0XED});
bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
```

发送数据成功后，返回的数据会在上面实例的BluetoothGattCallback类的回调方法中回应，再根据业务需求做相应的处理。

##### 7. 接收Gatt 的通知

setCharacteristicNotification() 方法。当设备的特性发生改变时，BLE应用程序通常会要求得到通知。如下：

```java
private BluetoothGatt bluetoothGatt;
BluetoothGattCharacteristic characteristic;
boolean enabled;
...
bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
...
BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
        UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
bluetoothGatt.writeDescriptor(descriptor);
```

一旦某个属性启用了通知，则触发 onCharacteristicChanged()回调：

```java
@Override
// Characteristic notification
public void onCharacteristicChanged(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic) {
    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
}
```

##### 8. 关闭客户端

```java
public void close(){
    if(bluetoothGatt == null){
        return;
    }
    bluetoothGatt.close();
    bluetoothGatt = null;
}
```

