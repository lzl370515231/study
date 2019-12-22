### \<uses-configuration>

指示应用程序需要的硬件和软件特性。 例如，应用程序可能指定它需要物理键盘或特定的导航设备，如轨迹球。 该规范用于避免将应用程序安装到无法工作的设备上

#### 语法

```xml
<uses-configuration
  android:reqFiveWayNav=["true" | "false"]
  android:reqHardKeyboard=["true" | "false"]
  android:reqKeyboardType=["undefined" | "nokeys" | "qwerty" | "twelvekey"]
  android:reqNavigation=["undefined" | "nonav" | "dpad" | "trackball" | "wheel"]
  android:reqTouchScreen=["undefined" | "notouch" | "stylus" | "finger"] />
```

#### 包含在

- \<manifest>

#### 属性

| 属性                    | 描述                                                         | 是否必须 | 常用值                |
| ----------------------- | ------------------------------------------------------------ | -------- | --------------------- |
| android:reqFiveWayNav   | 应用程序是否需要五路导航控制                                 |          |                       |
| android:reqHardKeyboard |                                                              |          |                       |
| android:reqKeyboardType | 应用程序需要的键盘类型(如果有的话)。 此属性不区分硬件键盘和软件键盘。 如果需要某种类型的硬件键盘，请在此处指定类型并设置 |          | 见下表reqKeyboardType |
| android:reqNavigation   | 应用程序所需的导航设备(如果有的话)                           |          | 见下表 reqNavigation  |
| android:reqTouchScreen  | 应用程序需要的触摸屏类型                                     |          | 见下边 reqTouchScreen |
|                         |                                                              |          |                       |
|                         |                                                              |          |                       |
|                         |                                                              |          |                       |
|                         |                                                              |          |                       |



##### reqKeyboardType

| 值            | 描述                                           |
| ------------- | ---------------------------------------------- |
| “undefined”   | 该应用程序不需要键盘。（未定义键盘要求）默认值 |
| "nokeys"      | 该应用程序不需要键盘                           |
| "qwerty"      | 该应用程序需要一个标准的 QWERTY 键盘           |
| "`twelvekey`" | 和大多数手机一样，这款应用需要12个键盘         |

##### reqNavigation

| 值          | 描述                                                         |
| ----------- | ------------------------------------------------------------ |
| “undefined” | 应用程序不需要任何类型的导航控件。 (未定义导航要求) 这是默认值 |
| "nonav"     | 应用程序不需要导航控件                                       |
| "dpad"      | 该应用程序需要一个 D-pad (方向垫)的导航                      |
| "trackball" | 该应用程序需要一个用于导航的轨迹球                           |
| "wheel"     | 该应用程序需要一个导航轮                                     |

如果应用程序需要导航控件，但控件的确切类型并不重要，那么可以将 reqFiveWayNav 属性设置为“ true” ，而不是设置这个属性。

##### reqTouchScreen

| 值          | 描述                                     |
| ----------- | ---------------------------------------- |
| "undefined" | 该应用程序不需要触摸屏                   |
| “notouch”   | 该应用程序不需要触摸屏                   |
| "stylus"    | 这个应用程序需要一个触摸屏，用手写笔操作 |
| "finger"    | 这个应用需要一个可以用手指操作的触摸屏   |



#### 注意

1. 大多数应用程序不应该使用这个清单标记。 您应该始终支持与定向垫(d-pad)输入，以帮助视力受损的用户和支持设备，提供 d-pad 输入，除了或代替触摸。 有关如何在应用程序中支持 d-pad 输入的信息，请参阅启用焦点导航。 如果你的应用在没有触摸屏的情况下无法正常运行，那么可以使用 use-feature 标记来声明所需的触摸屏类型，从“ android.hardware. faketouch”用于基本的触摸风格事件，到“ android.hardware. touch.multitouch.jazzand”用于多个手指的不同输入。
2. 如果您的应用程序需要某种类型的触摸输入，您应该使用 use-feature 标记来声明所需的触摸屏类型，从“ android.hardware.faketouch”开始，用于基本的触摸风格事件。

