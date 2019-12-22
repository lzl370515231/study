## Activity

### 语法

```xml
<activity android:allowEmbedded=["true" | "false"]
          android:allowTaskReparenting=["true" | "false"]
          android:alwaysRetainTaskState=["true" | "false"]
          android:autoRemoveFromRecents=["true" | "false"]
          android:banner="drawable resource"
          android:clearTaskOnLaunch=["true" | "false"]
          android:colorMode=[ "hdr" | "wideColorGamut"]
          android:configChanges=["mcc", "mnc", "locale",
                                 "touchscreen", "keyboard", "keyboardHidden",
                                 "navigation", "screenLayout", "fontScale",
                                 "uiMode", "orientation", "density",
                                 "screenSize", "smallestScreenSize"]
          android:directBootAware=["true" | "false"]
          android:documentLaunchMode=["intoExisting" | "always" |
                                  "none" | "never"]
          android:enabled=["true" | "false"]
          android:excludeFromRecents=["true" | "false"]
          android:exported=["true" | "false"]
          android:finishOnTaskLaunch=["true" | "false"]
          android:hardwareAccelerated=["true" | "false"]
          android:icon="drawable resource"
          android:immersive=["true" | "false"]
          android:label="string resource"
          android:launchMode=["standard" | "singleTop" |
                              "singleTask" | "singleInstance"]
          android:lockTaskMode=["normal" | "never" |
                              "if_whitelisted" | "always"]
          android:maxRecents="integer"
          android:maxAspectRatio="float"
          android:multiprocess=["true" | "false"]
          android:name="string"
          android:noHistory=["true" | "false"]  
          android:parentActivityName="string" 
          android:persistableMode=["persistRootOnly" | 
                                   "persistAcrossReboots" | "persistNever"]
          android:permission="string"
          android:process="string"
          android:relinquishTaskIdentity=["true" | "false"]
          android:resizeableActivity=["true" | "false"]
          android:screenOrientation=["unspecified" | "behind" |
                                     "landscape" | "portrait" |
                                     "reverseLandscape" | "reversePortrait" |
                                     "sensorLandscape" | "sensorPortrait" |
                                     "userLandscape" | "userPortrait" |
                                     "sensor" | "fullSensor" | "nosensor" |
                                     "user" | "fullUser" | "locked"]
          android:showForAllUsers=["true" | "false"]
          android:stateNotNeeded=["true" | "false"]
          android:supportsPictureInPicture=["true" | "false"]
          android:taskAffinity="string"
          android:theme="resource or theme"
          android:uiOptions=["none" | "splitActionBarWhenNarrow"]
          android:windowSoftInputMode=["stateUnspecified",
                                       "stateUnchanged", "stateHidden",
                                       "stateAlwaysHidden", "stateVisible",
                                       "stateAlwaysVisible", "adjustUnspecified",
                                       "adjustResize", "adjustPan"] >   
    . . .
</activity>
```

### 可包含

- \<intent-filter>
- \<meta-data>
- \<layout>

### 属性

#### android:allowEmbedded

##### 描述

表示该 Activity 可作为其他 Activity 的嵌入式子项启动。

##### 默认值

该属性的默认值为 `false`

##### 使用场景

此属性尤其适用于子项位于其他 Activity 所拥有容器（如 Display）中的情况。例如，用于 Wear 自定义通知的 Activity 必须声明此属性，以便 Wear 在其位于另一进程内的上下文流中显示 Activity。

#### android:allowTaskReparenting

##### 描述

当下一次将启动 Activity 的任务转至前台时，Activity 是否能从该任务转移至与其有相似性的任务 —“`true`”表示可以转移，“`false`”表示仍须留在启动它的任务处。该属性通常用于将应用的 Activity 转移至与该应用关联的主任务。

##### 默认值

如果未设置该属性，则对 Activity 应用由 `<application>` 元素的相应 `allowTaskReparenting` 属性所设置的值。默认值为“`false`”。

##### 使用场景

如果电子邮件消息包含网页链接，则点击该链接会调出可显示该网页的 Activity。该 Activity 由浏览器应用定义，但作为电子邮件任务的一部分启动。如果将该 Activity 的父项更改为浏览器任务，则它会在浏览器下一次转至前台时显示，在电子邮件任务再次转至前台时消失。

##### 说明

Activity 的相似性由 `taskAffinity` 属性定义。通过读取任务根 Activity 的相似性即可确定任务的相似性。因此，按照定义，根 Activity 始终位于具有同一相似性的任务中。由于具有“`singleTask`”或“`singleInstance`”启动模式的 Activity 只能位于任务的根，因此更改父项仅限于“`standard`”和“`singleTop`”模式。

#### android:alwaysRetainTaskState

##### 描述

系统是否始终保持 Activity 所在任务的状态 —“`true`”表示是，“`false`”表示允许系统在特定情况下将任务重置到其初始状态。该属性只对任务的根 Activity 有意义。

##### 默认值

默认值为“`false`”。

##### 使用场景

如果该属性的值是“`true`”，则无论用户如何返回任务，该任务始终会显示最后一次的状态。例如，该属性非常适用于网络浏览器这类应用，因为其中存在大量用户不愿丢失的状态（如多个打开的标签）。

#### android:autoRemoveFromRecents

##### 描述

由具有该属性的 Activity 启动的任务是否一直保留在[概览屏幕](https://developer.android.com/guide/components/recents.html)中，直至任务中的最后一个 Activity 完成为止。若为 `true`，则自动从概览屏幕中移除任务。它会替换调用方使用的 `FLAG_ACTIVITY_RETAIN_IN_RECENTS`。它必须是布尔值“`true`”或“`false`”。

#### android:banner

##### 描述

一种为其关联项提供扩展图形化横幅的[可绘制资源](https://developer.android.com/guide/topics/resources/drawable-resource.html)。可与 `<activity>` 标记联用，为特定 Activity 提供默认横幅；也可与 [``](https://developer.android.com/guide/topics/manifest/application-element.html) 标记联用，为所有应用 Activity 提供横幅。

##### 默认值

必须将该属性设置为对包含图像的可绘制资源的引用（例如 `"@drawable/banner"`）。没有默认横幅。

##### 使用场景



##### 说明

Android TV 中会使用到

#### android:clearTaskOnLaunch

##### 描述

每当从主屏幕重新启动任务时，是否都从该任务中移除根 Activity 之外的所有 Activity —“`true`”表示始终将任务清除至只剩其根 Activity；“`false`”表示不清除。

##### 默认值

默认值为“`false`”。

##### 使用场景



##### 说明

该属性只对启动新任务的 Activity（根 Activity）有意义；任务中的所有其他 Activity 均可忽略该属性。

#### android:colorMode

##### 描述

请求在兼容设备上以广色域模式显示 Activity。在广色域模式下，窗口可以在 `SRGB` 色域之外进行渲染，从而显示更鲜艳的色彩。如果设备不支持广色域渲染，则此属性无效。

#### android:configChanges

##### 描述

列出 Activity 将自行处理的配置变更。在运行时发生配置变更时，默认情况下会关闭 Activity 并将其重启，但使用该属性声明配置将阻止 Activity 重启。相反，Activity 会保持运行状态，并且系统会调用其 `onConfigurationChanged()` 方法。

##### 默认值

| 值                     | 描述                                                         | 说明                                                         |
| ---------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| “`density`”            | 显示密度发生变更 — 用户可能已指定不同的显示比例，或者有不同的显示现处于活跃状态。 | API>=24                                                      |
| “`fontScale`”          | 字体缩放系数发生变更 — 用户已选择新的全局字号。              |                                                              |
| “`keyboard`”           | 键盘类型发生变更 — 例如，用户插入外置键盘。                  |                                                              |
| “`keyboardHidden`”     | 键盘无障碍功能发生变更 — 例如，用户显示硬键盘。              |                                                              |
| “`layoutDirection`”    | 布局方向发生变更 — 例如，自从左至右 (LTR) 更改为从右至左 (RTL)。 | API>=17                                                      |
| “`locale`”             | 语言区域发生变更 — 用户已为文本选择新的显示语言              |                                                              |
| “`mcc`”                | IMSI 移动设备国家/地区代码 (MCC) 发生变更 — 检测到 SIM 并更新 MCC。 |                                                              |
| “`mnc`”                | IMSI 移动设备网络代码 (MNC) 发生变更 — 检测到 SIM 并更新 MNC。 |                                                              |
| “`navigation`”         | 导航类型（轨迹球/方向键）发生变更。（这种情况通常不会发生。） |                                                              |
| “`orientation`”        | 屏幕方向发生变更 — 用户旋转设备                              | 如果应用面向 Android 3.2（API 级别 13）或更高版本的系统，则还应声明 `"screenSize"` 配置，因为当设备在横向与纵向之间切换时，该配置也会发生变更。 |
| “`screenLayout`”       | 屏幕布局发生变更 — 不同的显示现可能处于活跃状态。            |                                                              |
| “`screenSize`”         | 当前可用屏幕尺寸发生变更。该值表示当前可用尺寸相对于当前纵横比的变更，当用户在横向与纵向之间切换时，它便会发生变更。 | API>=13                                                      |
| “`smallestScreenSize`” | 物理屏幕尺寸发生变更。该值表示与方向无关的尺寸变更，因此它只有在实际物理屏幕尺寸发生变更（如切换到外部显示器）时才会变化。 | API>=13                                                      |
| “`touchscreen`”        | 触摸屏发生变更。                                             |                                                              |
| “`uiMode`”             | 界面模式发生变更 — 用户已将设备置于桌面或车载基座，或者夜间模式发生变更。 | API>=8                                                       |

###### 注意

如要处理所有[多窗口模式](https://developer.android.com/guide/topics/ui/multi-window)相关的配置变更，请使用 `"screenLayout"` 和 `"smallestScreenSize"`。Android 7.0（API 级别 24）或更高版本的系统支持多窗口模式。

#### android:directBootAware

##### 描述

Activity 是否*支持直接启动*，即其是否可以在用户解锁设备之前运行。

##### 默认值

默认值为 "false"

##### 说明

在直接启动期间，应用中的 Activity 仅可访问存储在*设备保护*存储区的数据。

#### android:documentLaunchMode

##### 描述

指定每次启动任务时，应如何向其添加新的 Activity 实例。该属性允许用户让多个来自同一应用的文档出现在[概览屏幕](https://developer.android.com/guide/components/recents.html)中。

##### 默认值

| 值               | 描述                                                         | 说明 |
| ---------------- | ------------------------------------------------------------ | ---- |
| “`intoExisting`” | 系统会搜索基本 Intent 的 `ComponentName` 和数据 URI 与启动 Intent 的这些内容相匹配的任务。如果发现此类任务，系统会将其清除，并在根 Activity 收到对 `onNewIntent(android.content.Intent)` 的调用时自行重启。如果未发现此类任务，系统会创建新任务。 |      |
| “`always`”       | Activity 为文档创建新任务，即便文档已经打开。这与同时设置 `FLAG_ACTIVITY_NEW_DOCUMENT` 和 `FLAG_ACTIVITY_MULTIPLE_TASK` 标记的效果相同。 |      |
| “`none`”         | 该 Activity 不会为 Activity 创建新任务。这是默认值，它只会在已设置 `FLAG_ACTIVITY_NEW_TASK` 时创建新任务。概览屏幕将按其默认方式处理此 Activity：为应用显示单个任务，该任务将从用户上次调用的任意 Activity 开始继续执行。 |      |
| “`never`”        | 即使 Intent 包含 `FLAG_ACTIVITY_NEW_DOCUMENT`，该 Activity 也不会启动到新文档中。设置此值会替换 `FLAG_ACTIVITY_NEW_DOCUMENT` 和 `FLAG_ACTIVITY_MULTIPLE_TASK` 标记的行为（如果在 Activity 中设置其中一个标志），并且概览屏幕将为应用显示单个任务，该任务将从用户上次调用的任意 Activity 开始继续执行。 |      |

###### 注意

对于除“`none`”和“`never`”以外的值，必须使用 `launchMode="standard"` 定义 Activity。如果未指定此属性，则使用 `documentLaunchMode="none"`。

#### android:enabled

##### 描述

系统是否可实例化 Activity — `"true"` 表示可以，“`false`”表示不可以。默认值为“`true`”。

##### 默认值

默认值为“true”

##### 说明

`<application> `元素拥有自己的 `enabled` 属性，该属性适用于所有应用组件，包括 Activity。只有在 `<application>` 和 `<activity>` 属性都为“`true`”（因为它们都默认使用该值）时，系统才能将 Activity 实例化。如果其中一个属性是“`false`”，则无法实例化 Activity。

#### android:excludeFromRecents

##### 描述

是否应从最近使用的应用列表（即[概览屏幕](https://developer.android.com/guide/components/recents.html)）中排除该 Activity 启动的任务。换言之，当该 Activity 是新任务的根 Activity 时，此属性确定最近使用的应用列表中是否应出现该任务。如果应从列表中*排除*任务，请设置“`true`”；如果应将其*包括*在内，则设置“`false`”。

##### 默认值

默认值为“false”

#### android:exported

此元素设置 Activity 是否可由其他应用的组件启动 —“`true`”表示可以，“`false`”表示不可以。若为“`false`”，则 Activity 只能由同一应用的组件或使用同一用户 ID 的不同应用启动。

#### android:excludeFromRecents

#### android:exported

#### android:finishOnTaskLaunch

#### android:hardwareAccelerated

#### android:icon

#### android:immersive

#### android:label

#### android:launchMode

#### android:lockTaskMode

#### android:maxRecents

#### android:maxAspectRatio

#### android:multiprocess

#### android:name

#### android:noHistory

#### android:parentActivityName

#### android:persistableMode

#### android:permission

#### android:process

#### android:relinquishTaskIdentity

#### resizeableActivity

指定应用是否支持[多窗口显示](https://developer.android.com/guide/topics/ui/multi-window.html)。您可以在 `<activity>` 或\<application>元素中设置该属性。

#### android:screenOrientation

#### android:showForAllUsers

#### android:stateNotNeeded

#### supportsPictureInPicture

#### android:taskAffinity

#### android:theme

#### android:uiOptions

#### android:windowSoftInputMode



# \<activity-alias>

### 语法

```xml
<activity-alias android:enabled=["true" | "false"]
                android:exported=["true" | "false"]
                android:icon="drawable resource"
                android:label="string resource"
                android:name="string"
                android:permission="string"
                android:targetActivity="string" >
    . . .
</activity-alias>
```

