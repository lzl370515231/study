### \<supports-screens>

#### 语法

```xml
<supports-screens android:resizeable=["true"| "false"]
                  android:smallScreens=["true" | "false"]
                  android:normalScreens=["true" | "false"]
                  android:largeScreens=["true" | "false"]
                  android:xlargeScreens=["true" | "false"]
                  android:anyDensity=["true" | "false"]
                  android:requiresSmallestWidthDp="integer"
                  android:compatibleWidthLimitDp="integer"
                  android:largestWidthLimitDp="integer"/>
```

#### 包含于

- \<manifest>

#### 属性

| 属性                            | 描述                                                         | 是否必须 | 常用值 | 说明       |
| ------------------------------- | ------------------------------------------------------------ | -------- | ------ | ---------- |
| android:resizeable              | 指示应用程序是否可以根据不同屏幕大小调整大小。 默认情况下，此属性为 istrue。 如果设置为 false，系统将在 screen compatibility mode。不推荐使用 |          |        | 不推荐使用 |
| android:smallScreens            |                                                              |          |        |            |
| android:normalScreens           |                                                              |          |        |            |
| android:largeScreens            |                                                              |          |        |            |
| android:xlargeScreens           |                                                              |          |        |            |
| android:anyDensity              |                                                              |          |        |            |
| android:requiresSmallestWidthDp | Android 系统不会注意这个属性，所以它不会影响应用程序在运行时的行为。 相反，它用于在 Google Play 等服务上启用应用程序过滤。 但是，Google Play 目前不支持这个过滤属性(在 Android 3.2上) ，所以如果你的应用程序不支持小屏幕，你应该继续使用其他大小属性。 |          |        | API>=13    |
| android:compatibleWidthLimitDp  |                                                              |          |        | API>=13    |
| android:largestWidthLimitDp     |                                                              |          |        | API>=13    |
|                                 |                                                              |          |        |            |
|                                 |                                                              |          |        |            |
|                                 |                                                              |          |        |            |
|                                 |                                                              |          |        |            |
|                                 |                                                              |          |        |            |



#### 注意

Android 3.2引入了新的属性: Android: requiresSmallestWidthDp，Android: compatiblewidthdp，以及 Android: largestWidthLimitDp。 如果你正在为 Android 3.2或更高版本开发应用程序，你应该使用这些属性来声明你的屏幕尺寸支持，而不是基于通用屏幕尺寸的属性。