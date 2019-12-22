### \<uses-feature>

声明应用使用的单个硬件或软件功能。

#### 用途

通知任何外部实体应用所依赖的硬件和软件功能集。

#### 语法

```xml
<uses-feature
  android:name="string"
  android:required=["true" | "false"]
  android:glEsVersion="integer" />
```

#### 包含于

- \<manifest>

#### 属性

| 属性                | 描述                                                         | 是否必须 | 常用值                                   | 说明                                                         |
| ------------------- | ------------------------------------------------------------ | -------- | ---------------------------------------- | ------------------------------------------------------------ |
| android:required    | 表示应用是否需要 `android:name` 中所指定功能的布尔值。<br>true：即是规定当设备不具有该指定功能时，应用*无法正常工作，或设计为无法正常工作*。<br>false：如果设备具有该功能，应用会在必要时*优先使用该功能*，但应用*设计为不使用该指定功能也可正常工作*。 |          | 未予声明，默认为 true                    |                                                              |
| android:glEsVersion | 应用需要的 OpenGL ES 版本。高 16 位表示主版本号，低 16 位表示次版本号。例如，如要指定 OpenGL ES 2.0 版，您需将其值设置为“0x00020000”；如要指定 OpenGL ES 3.2，您需将其值设置为“0x00030002”。 |          | 不指定，系统假定应用只需要 OPENGL ES 1.0 | 应用应在其清单中至多指定一个 `android:glEsVersion` 属性。如果指定多个该属性，则系统会使用数值最高的 `android:glEsVersion`，并忽略任何其他值。<br>如果平台支持给定的 OpenGL ES版本，则其同样支持所有数值更低的 OpenGL ES版本。<br>API>=4 |
|                     |                                                              |          |                                          |                                                              |



#### 说明

借助该元素提供的 `required` 属性，您可以指定应用是否需要声明的功能并且没有该功能便无法正常工作，或者使用该功能只是一种优先选择，没有它仍然可以正常工作。



如果应用需要多个功能，则需声明多个 `<uses-feature>` 元素。例如，要求设备同时具有蓝牙和相机功能的应用会声明以下两个元素：

```xml
<uses-feature android:name="android.hardware.bluetooth" />
<uses-feature android:name="android.hardware.camera" />
```

#### 注意

- 声明功能时，切记您还必须视情况请求权限。
- API>=3

#### 根据隐含功能进行过滤

Google Play 会尝试检查清单文件中声明的*其他元素*（具体而言，即 `<uses-permission>` 元素），从而发现应用的隐含功能要求。

如果应用请求硬件相关权限，则 Google Play 会*假定该应用使用基础的硬件功能，并因此需要这些功能*，即使可能没有相应的 `<uses-feature>` 声明。对于此类权限，Google Play 会向其为应用存储的元数据中添加这些基础的硬件功能，并为这类功能设置过滤。

例如，如果应用请求 `CAMERA` 权限，但未针对 `android.hardware.camera` 声明 `<uses-feature>` 元素，则 Google Play 会认为该应用需要相机，并且不应向设备没有相机的用户显示该应用。

如果您不想让 Google Play 根据特定隐含功能进行过滤，则可以停用该行为。如要执行此操作，请在 `<uses-feature>` 元素中显式声明该功能，并加入 `android:required="false"` 属性。

##### 说明

必须了解，在 `<uses-permission>` 元素中请求的权限可能会直接影响 Google Play 对您应用的过滤方式。

#### 针对蓝牙功能的特殊处理

如果某个应用在 `<uses-permission>` 元素中声明蓝牙权限，但未在 `<uses-feature>` 元素中显式声明蓝牙功能，则 Google Play 会按照 `<uses-sdk>` 元素中指定的步骤，检查作为应用设计运行目标的 Android 平台版本。

| minSdkVersion | targetSdkVersion | 结果                                                         |
| ------------- | ---------------- | ------------------------------------------------------------ |
| <=4           | <=4              | Google Play *不会*根据设备所报告的对 `android.hardware.bluetooth` 功能的支持情况，从任何设备中过滤该应用。 |
| <=4           | \>=5             | Google Play 会从任何不支持 `android.hardware.bluetooth` 功能（包括旧版本）的设备中过滤该应用。 |
| \>=5          | \>=5             | Google Play 会从任何不支持 `android.hardware.bluetooth` 功能（包括旧版本）的设备中过滤该应用。 |

#### 测试应用需要的功能

可以使用 Android SDK 中包含的 `aapt` 工具，确定 Google Play 如何根据应用声明的功能和权限对其进行过滤。如要执行该操作，请使用 `dump badging` 命令运行 `aapt`。这会使 `aapt` 解析应用的清单，并应用与 Google Play 所用相同的规则，从而确定应用需要的功能。

使用该工具的步骤操作：

1. 首先，构建您的应用并将其导出为未签署的 `.apk`。如果您使用 Android Studio 进行开发，请使用 Gradle 构建您的应用：

   1. 打开项目并选择 **Run > Edit Configurations**。
   2. 选择靠近 **Run/Debug Configurations** 窗口左上角的加号。
   3. 选择 **Gradle**。
   4. 在 **Name** 中输入 `Unsigned APK`
   5. 从 **Gradle project** 部分选择您的模块
   6. 在 **Tasks** 中输入 `assemble`
   7. 选择 **OK** 完成新配置
   8. 确保已在工具栏中选择 **Unsigned APK** 运行配置，并选择 **Run > Run 'Unsigned APK'**。

   您可以在 `<*ProjectName*>/app/build/outputs/apk/` 目录中找到您的未签署 `.apk`。

2. 接下来，如果 `aapt` 工具尚未出现在您的 PATH 中，请定位该工具。如果您使用的是 SDK Tools r8 或更高版本，则可以在 `<*SDK*>/build-tools/<*tools version number*>` 目录中找到 `aapt`。

3. 使用以下语法运行 `aapt`：

   ```shell
   aapt dump badging <path_to_exported_.apk>
   ```

#### 硬件功能

| 分类               | 功能值                                                    | 描述                                                         | 说明                                                         |
| ------------------ | --------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 音频硬件功能       | android.hardware.audio.low_latency                        | 应用使用设备的低延迟时间音频管道，该管道可以减少处理声音输入或输出时的滞后和延迟。 |                                                              |
|                    | android.hardware.audio.output                             | 应用使用设备的扬声器、音频耳机插孔、蓝牙流式传输能力或类似机制传输声音。 |                                                              |
|                    | android.hardware.audio.pro                                | 应用使用设备的高端音频功能和性能能力。                       |                                                              |
|                    | android.hardware.microphone                               | 应用使用设备的麦克风记录音频                                 |                                                              |
| 蓝牙硬件功能       | android.hardware.bluetooth                                | 应用使用设备的蓝牙功能，通常用于与其他支持蓝牙的设备进行通信 |                                                              |
|                    | android.hardware.bluetooth_le                             | 应用使用设备的低功耗蓝牙无线电功能                           |                                                              |
| 相机硬件功能       | android.hardware.camera                                   | 应用使用设备的后置相机。只有前置相机的设备不会列出该功能，因此如果您的应用可与任何朝向的相机通信，请改用 `android.hardware.camera.any` 功能。 |                                                              |
|                    | android.hardware.camera.any                               | 应用使用设备的其中一个相机或用户为设备连接的外置相机。如果您的应用并未要求相机必须是后置相机，请使用此值来代替 `android.hardware.camera`。 |                                                              |
|                    | android.hardware.camera.autofocus                         | 应用使用设备相机支持的自动对焦功能。                         | 通过使用此功能，应用暗示其还会使用 `android.hardware.camera` 功能（除非使用 `android:required="false"` 声明此父功能）。 |
|                    | android.hardware.camera.capability.manual_post_processing | 应用使用设备相机支持的 `MANUAL_POST_PROCESSING` 功能。       | 您的应用可通过该功能替换相机的自动白平衡功能。使用 `android.colorCorrection.transform`、`android.colorCorrection.gains` 以及 `TRANSFORM_MATRIX` 的 `android.colorCorrection.mode`。 |
|                    | android.hardware.camera.capability.manual_sensor          | 应用使用设备相机支持的 `MANUAL_SENSOR` 功能。                | 此功能隐含对自动曝光锁定 (`android.control.aeLock`) 的支持，从而让相机的曝光时间和灵敏度一直固定在特定值。 |
|                    | android.hardware.camera.capability.raw                    | 应用使用设备相机支持的 `RAW` 功能。                          | 此功能暗示设备可以保存 DNG（原始）文件，并且设备的相机会提供应用直接处理这些原始图像所需的 DNG 相关元数据。 |
|                    | android.hardware.camera.external                          | 应用与用户为设备连接的外置相机进行通信。但此功能无法保证外置相机可供您的应用使用。 |                                                              |
|                    | android.hardware.camera.flash                             | 应用使用设备相机所支持的闪光灯功能。                         | 通过使用此功能，应用暗示其还会使用 `android.hardware.camera` 功能（除非使用 `android:required="false"` 声明此父功能）。 |
|                    | android.hardware.camera.front                             | 应用使用设备的前置相机。                                     | 通过使用此功能，应用暗示其还会使用 `android.hardware.camera` 功能（除非使用 `android:required="false"` 声明此父功能）。 |
|                    | android.hardware.camera.level.full                        | 应用使用至少一个设备相机所提供的 `FULL` 级图像捕捉支持。提供 `FULL` 支持的相机可提供快速捕捉功能、逐帧控制和手动后期处理控制。 |                                                              |
| 设备界面硬件功能   | android.hardware.type.automotive                          | 将应用设计为在车辆内的一组屏幕上显示其界面。用户通过硬按钮、轻触、旋转控制器以及类鼠标界面与应用进行交互。车辆的屏幕通常出现在车辆的中控台或仪表板中。这些屏幕的尺寸和分辨率通常有限。 | 切记，由于用户是在驾车时使用这类应用界面，应用必须尽量不要让驾驶员分心。 |
|                    | android.hardware.type.television                          | 已弃用；请改用 `android.software.leanback`<br><br>将应用设计为在电视上显示其界面。此功能将“电视”定义为一种典型的起居室电视体验：显示在大屏幕上，用户坐在远处，主流输入形式是类似方向键的设备，并且一般不使用鼠标、指示器或触摸设备。 |                                                              |
|                    | android.hardware.type.watch                               | 将应用设计为在手表上显示其界面。用户会将手表佩戴在身体（例如手腕）上。用户在很近的距离与设备互动。 |                                                              |
| 指纹硬件功能       | android.hardware.fingerprint                              | 应用使用设备的生物识别硬件读取指纹                           |                                                              |
| 手柄硬件功能       | android.hardware.gamepad                                  | 应用捕获来自设备本身或其所连接手柄的游戏控制器输入           |                                                              |
| 红外线硬件功能     | android.hardware.consumerir                               | 应用使用设备的红外线 (IR) 功能，通常是为了与其他消费 IR 设备进行通信 |                                                              |
| 定位硬件功能       | android.hardware.location                                 | 应用使用设备上的一项或多项功能来确定位置，例如 GPS 位置、网络位置或小区位置。 |                                                              |
|                    | android.hardware.location.gps                             | 应用使用通过设备的全球定位系统 (GPS) 接收器获得的精确位置坐标。 | 通过使用此功能，应用暗示其还会使用 `android.hardware.location` 功能（除非使用属性 `android:required="false"` 声明此父功能） |
|                    | android.hardware.location.network                         | 应用使用通过设备所支持的基于网络的地理定位系统获得的粗略位置坐标。 | 通过使用此功能，应用暗示其还会使用 `android.hardware.location` 功能（除非使用属性 `android:required="false"` 声明此父功能）。 |
| NFC 硬件功能       | android.hardware.nfc                                      | 应用使用设备的近距离无线通信 (NFC) 功能                      |                                                              |
|                    | android.hardware.nfc.hce                                  | 应用使用托管在设备上的 NFC 卡模拟                            |                                                              |
| OpenGL ES 硬件功能 | android.hardware.opengles.aep                             | 应用使用安装在设备上的 **OpenGL ES Android 扩展包**          |                                                              |
| 传感器硬件功能     | android.hardware.sensor.accelerometer                     | 应用使用通过设备的加速计读取的运动信息，以检测设备的当前方向。例如，应用可以使用加速计读数，以确定何时切换至纵向或横向方向。 |                                                              |
|                    | android.hardware.sensor.ambient_temperature               | 应用使用设备的外界（环境）温度传感器。例如，天气应用可以报告室内或室外温度。 |                                                              |
|                    | android.hardware.sensor.barometer                         | 应用使用设备的气压计。例如，天气应用可以报告气压。           |                                                              |
|                    | android.hardware.sensor.compass                           | 应用使用设备的磁力计（指南针）。例如，导航应用可以显示用户当前面朝的方向。 |                                                              |
|                    | android.hardware.sensor.gyroscope                         | 应用使用设备的陀螺仪来检测旋转和倾斜，从而形成一个六轴定向系统。通过使用该传感器，应用可以更流畅地检测其是否需切换至纵向或横向方向。 |                                                              |
|                    | android.hardware.sensor.hifi_sensors                      | 应用使用设备的高保真 (Hi-Fi) 传感器。例如，游戏应用可以检测用户的高精度移动。 |                                                              |
|                    | android.hardware.sensor.heartrate                         | 应用使用设备的心率监测器。例如，健身应用可以报告用户心率随时间的变化趋势。 |                                                              |
|                    | android.hardware.sensor.heartrate.ecg                     | 应用使用设备的心电图 (ECG) 心率传感器。例如，健身应用可以报告更详细的用户心率信息。 |                                                              |
|                    | android.hardware.sensor.light                             | 应用使用设备的光传感器。                                     |                                                              |
|                    | android.hardware.sensor.proximity                         | 应用使用设备的近程传感器。例如，在检测到用户的手持设备贴近身体时，电话应用可以关闭设备的屏幕。 |                                                              |
|                    | android.hardware.sensor.relative_humidity                 | 应用使用设备的相对湿度传感器。例如，天气应用可以利用湿度来计算和报告当前露点。 |                                                              |
|                    | android.hardware.sensor.stepcounter                       | 应用使用设备的计步器。例如，健身应用可以报告用户需要走多少步才能达到每天的计步目标 |                                                              |
|                    | android.hardware.sensor.stepdetector                      | 应用使用设备的步测器。例如，健身应用可以利用每步的间隔时间来推测用户正在进行的锻炼类型。 |                                                              |
| 屏幕硬件功能       | android.hardware.screen.landscape                         |                                                              |                                                              |
|                    | android.hardware.screen.portrait                          | 应用要求设备使用纵向或横向方向。如果您的应用同时支持这两种方向，则无需声明任一功能。 | 默认情况下假定两种方向均非要求的方向，这样便可在支持一种或同时支持两种方向的设备上安装您的应用。不过，如果应用的任一 Activity 利用 [`android:screenOrientation`](https://developer.android.com/guide/topics/manifest/activity-element.html#screen) 属性请求在特定方向下运行，则此声明意味着您的应用需要该方向。例如，如果您使用 `"landscape"`、`"reverseLandscape"` 或 `"sensorLandscape"` 声明 [`android:screenOrientation`](https://developer.android.com/guide/topics/manifest/activity-element.html#screen)，则您的应用只能安装在支持横向方向的设备上。 |
| 电话硬件功能       | android.hardware.telephony                                | 应用使用设备的电话功能，例如提供数据通信服务的无线电话       | 最佳做法是，您仍应使用 `<uses-feature>` 元素来声明对该方向的要求。如果您使用 [`android:screenOrientation`](https://developer.android.com/guide/topics/manifest/activity-element.html#screen) 为 Activity 声明某个方向，但实际并无此要求，则可通过使用 `<uses-feature>` 元素并加入 `android:required="false"` 来声明该方向，进而停用这一要求。 |
|                    | android.hardware.telephony.cdma                           | 应用使用码分多址接入 (CDMA) 无线电话系统                     | 通过使用此功能，应用暗示其还会使用 `android.hardware.telephony` 功能（除非使用 `android:required="false"` 声明此父功能） |
|                    | android.hardware.telephony.gsm                            | 应用使用全球移动通讯系统 (GSM) 无线电话系统                  | 通过使用此功能，应用暗示其还会使用 `android.hardware.telephony` 功能（除非使用 `android:required="false"` 声明此父功能） |
| 触摸屏硬件功能     | android.hardware.faketouch                                | 应用使用基本的轻触交互事件，例如点按和拖动                   | 当声明为必需时，此功能表示只有当设备可模拟触摸屏（“假触摸”界面）或实际具有触摸屏时，应用才能与之兼容。<br><br>默认情况下，应用需要 `android.hardware.faketouch` 功能。如果您想将自己的应用限制为仅供拥有触摸屏的设备使用，则必须按如下方式显式声明要求提供触摸屏 android:required="true"<br><br>所有未显示要求 `android.hardware.touchscreen` 的应用也可在支持 `android.hardware.faketouch` 的设备上运行。 |
|                    | android.hardware.faketouch.multitouch.distinct            | 应用在假触摸界面上区分两根或更多“手指”的触摸轨迹。这是 `android.hardware.faketouch` 功能的一个超集。当声明为必需时，此功能表示只有当设备模拟区分两根或更多手指的触摸轨迹，或实际具有触摸屏时，应用才能与之兼容。<br><br>不同于 `android.hardware.touchscreen.multitouch.distinct` 所定义的区分式多点触摸，通过假触摸界面支持区分式多点触摸的输入设备并不支持所有双指手势，因为输入会转换成屏幕上的光标移动。换言之，在此类设备上，单指手势移动光标，双指划动触发单指触摸事件，而其他双指手势则触发相应的双指触摸事件。<br><br>如果设备提供双指触摸触控板进行光标移动，则其可支持此功能。 |                                                              |
|                    | android.hardware.faketouch.multitouch.jazzhand            | 应用在假触摸界面上区分五根或更多“手指”的触摸轨迹。当声明为必需时，此功能表示只有当设备模拟区分五根或更多手指的触摸轨迹，或实际具有触摸屏时，应用才能与之兼容。<br><br>不同于 `android.hardware.touchscreen.multitouch.jazzhand` 所定义的区分式多点触摸，通过假触摸界面支持五指式多点触摸的输入设备并不支持所有五指手势，因为输入会转换成屏幕上的光标移动。换言之，在此类设备上，单指手势移动光标，多指划手势触发单指触摸事件，而其他多指手势则触发相应的多指触摸事件。<br><br>如果设备提供五指触摸触控板进行光标移动，则其可支持此功能。 | 如果设备提供五指触摸触控板进行光标移动，则其可支持此功能。   |
|                    | android.hardware.touchscreen                              | 应用利用设备的触摸屏功能来实现比基本触摸事件交互性更强的手势，例如滑屏。这是 `android.hardware.faketouch` 功能的一个超集。<br><br>默认情况下，您的应用需要该功能。因此，如果设备默认只提供模拟触摸界面（“假触摸”），则其无法使用您的应用。如果您希望提供假触摸界面的设备（甚至只提供方向键控制器的设备）可使用您的应用，则必须在声明 `android.hardware.touchscreen` 时加入 `android:required="false"`，从而显式声明不要求提供触摸屏。如果您的应用使用（但并不需要）真触摸屏界面，则应添加此声明。所有未显示要求 `android.hardware.touchscreen` 的应用也可在支持 `android.hardware.faketouch` 的设备上运行。<br><br>如果您的应用确实需要触摸界面（以便执行滑屏之类的更高级触摸手势），则您无需声明任何触摸界面功能，因为它们默认为必需功能。不过，最好还是显式声明应用使用的所有功能。<br><br>如果您需要进行更复杂的触摸交互（例如多指手势），则应声明您的应用使用高级触摸屏功能。 |                                                              |
|                    | android.hardware.touchscreen.multitouch                   | 应用使用设备基本的两点式多点触摸功能（例如实现双指张合手势），但不需要独立追踪触摸轨迹。这是 `android.hardware.touchscreen` 功能的一个超集。<br><br>通过使用此功能，应用暗示其还会使用 `android.hardware.touchscreen` 功能（除非使用 `android:required="false"` 声明此父功能） |                                                              |
|                    | android.hardware.touchscreen.multitouch.distinct          | 应用使用设备的高级多点触摸功能，从而独立追踪两点或更多点的轨迹。该功能是 `android.hardware.touchscreen.multitouch` 功能的一个超集。<br><br>通过使用此功能，应用暗示其还会使用 `android.hardware.touchscreen.multitouch` 功能（除非使用 `android:required="false"` 声明此父功能） |                                                              |
|                    | android.hardware.touchscreen.multitouch.jazzhand          | 应用使用设备的高级多点触摸功能，从而独立追踪五点或更多点的轨迹。该功能是 `android.hardware.touchscreen.multitouch` 功能的一个超集。<br><br>通过使用此功能，应用暗示其还会使用 `android.hardware.touchscreen.multitouch` 功能（除非使用 `android:required="false"` 声明此父功能） |                                                              |
| USB 硬件功能       | android.hardware.usb.accessory                            | 应用表现为 USB 设备，与 USB 主机相连。                       |                                                              |
|                    | android.hardware.usb.host                                 | 应用使用与设备相连的 USB 附件。设备充当 USB 主机。           |                                                              |
| Vulkan 硬件功能    | android.hardware.vulkan.compute                           | 应用使用 Vulkan 计算功能。此功能指明应用要求硬件加速的 Vulkan 实现。功能版本指明应用在 Vulkan 1.0 要求之外所需的可选计算功能级别。例如，如果您的应用需要 Vulkan 计算级别 0 支持，则应声明以下功能：见下代码 vulkan |                                                              |
|                    | android.hardware.vulkan.level                             | 应用使用 Vulkan 级别的功能。此功能指明应用需要硬件加速的 Vulkan 实现。功能版本指明应用需要的可选硬件功能级别。例如，如果您的应用需要 Vulkan 硬件级别 0 支持，则应声明以下功能：见下代码 vulkan level |                                                              |
|                    | android.hardware.vulkan.version                           | 应用使用 Vulkan。此功能指明应用需要硬件加速的 Vulkan 实现。功能版本指明应用所需最低版本的 Vulkan API 支持。例如，如果您的应用需要 Vulkan 1.0 支持，则应声明以下功能：见下代码 vulkan version |                                                              |
| Wi-Fi 硬件功能     | android.hardware.wifi                                     | 应用使用设备上的 802.11 网络 (Wi-Fi) 功能                    |                                                              |
|                    | android.hardware.wifi.direct                              | 应用使用设备上的 Wi-Fi Direct 网络功能                       |                                                              |

##### vulkan

```xml
<uses-feature
    android:name="android.hardware.vulkan.compute"
    android:version="0"
    android:required="true" />
```

##### vulkan level

```xml
<uses-feature
    android:name="android.hardware.vulkan.level"
    android:version="0"
    android:required="true" />
```

##### vulkan version

```xml
<uses-feature
    android:name="android.hardware.vulkan.version"
    android:version="0x400003"
    android:required="true" />
```

#### 软件功能

| 分类               | 功能值                                  | 描述                                                         | 说明                                                         |
| ------------------ | --------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 通信软件功能       | android.software.sip                    | 应用使用会话发起协议 (SIP) 服务。通过使用 SIP，应用可以支持互联网电话操作，例如视频会议和即时消息传递。 |                                                              |
|                    | android.software.sip.voip               | 应用使用基于 SIP 的互联网语音协议 (VoIP) 服务。通过使用 VoIP，应用可以支持实时互联网电话操作，例如双向视频会议。 | 通过使用此功能，应用暗示其还会使用 `android.software.sip` 功能（除非使用 `android:required="false"` 声明此父功能） |
|                    | android.software.webview                | 应用显示来自互联网的内容                                     |                                                              |
| 自定义输入软件功能 | android.software.input_methods          | 应用使用新的输入法，该输入法由开发者在 [`InputMethodService`](https://developer.android.com/reference/android/inputmethodservice/InputMethodService.html) 中定义 |                                                              |
| 设备管理软件功能   | android.software.backup                 | 应用加入处理备份和恢复操作的逻辑                             |                                                              |
|                    | android.software.device_admin           | 应用通过设备管理员来强制执行设备规范                         |                                                              |
|                    | android.software.managed_users          | 应用支持二级用户和托管配置文件                               |                                                              |
|                    | android.software.securely_removes_users | 应用可**永久性**移除用户及其相关数据。                       |                                                              |
|                    | android.software.verified_boot          | 应用加入处理设备验证启动功能结果的逻辑，该逻辑可检测设备的配置在重新启动操作期间是否发生变更。 |                                                              |
| 媒体软件功能       | android.software.midi                   | 应用利用乐器数字接口 (MIDI) 协议连接至乐器或输出声音         |                                                              |
|                    | android.software.print                  | 应用加入打印设备上所显示文档的命令                           |                                                              |
|                    | android.software.leanback               | 将应用设计为在 Android TV 设备上运行                         |                                                              |
|                    | android.software.live_tv                | 应用流式传输直播电视节目                                     |                                                              |
| 屏幕界面软件功能   | android.software.app_widgets            | 应用使用或提供应用小部件，并且只应安装在带有可供用户嵌入应用小部件的主屏幕或类似位置的设备上。 |                                                              |
|                    | android.software.home_screen            | 应用起到替代设备主屏幕的作用                                 |                                                              |
|                    | android.software.live_wallpaper         | 应用使用或提供包含动画的壁纸                                 |                                                              |

#### 隐含设备硬件用途的设备权限

| 类别   | 权限                           | 隐含此功能要求                                               |
| ------ | ------------------------------ | ------------------------------------------------------------ |
| 蓝牙   | BLUETOOTH                      | android.hardware.bluetooth                                   |
|        | BLUETOOTH_ADMIN                | android.hardware.bluetooth                                   |
| 摄像头 | CAMERA                         | `android.hardware.camera` *和*<br>`android.hardware.camera.autofocus` |
| 位置   | ACCESS_MOCK_LOCATION           | android.hardware.location                                    |
|        | ACCESS_LOCATION_EXTRA_COMMANDS | android.hardware.location                                    |
|        | INSTALL_LOCATION_PROVIDER      | android.hardware.location                                    |
|        | ACCESS_COARSE_LOCATION         | android.hardware.location<br>`android.hardware.location.network` （仅适用于目标 API 级别 20 或更低版本。） |
|        | ACCESS_FINE_LOCATION           | android.hardware.location<br>`android.hardware.location.gps` （仅适用于目标 API 级别 20 或更低版本。） |
| 麦克风 | RECORD_AUDIO                   | android.hardware.microphone                                  |
| 电话   | CALL_PHONE                     | android.hardware.telephony                                   |
|        | CALL_PRIVILEGED                | android.hardware.telephony                                   |
|        | MODIFY_PHONE_STATE             | android.hardware.telephony                                   |
|        | PROCESS_OUTGOING_CALLS         | android.hardware.telephony                                   |
|        | READ_SMS                       | android.hardware.telephony                                   |
|        | RECEIVE_SMS                    | android.hardware.telephony                                   |
|        | RECEIVE_MMS                    | android.hardware.telephony                                   |
|        | RECEIVE_WAP_PUSH               | android.hardware.telephony                                   |
|        | SEND_SMS                       | android.hardware.telephony                                   |
|        | WRITE_APN_SETTINGS             | android.hardware.telephony                                   |
|        | WRITE_SMS                      | android.hardware.telephony                                   |
| Wi-Fi  | ACCESS_WIFI_STATE              | android.hardware.wifi                                        |
|        | CHANGE_WIFI_STATE              | android.hardware.wifi                                        |
|        | CHANGE_WIFI_MULTICAST_STATE    | android.hardware.wifi                                        |

