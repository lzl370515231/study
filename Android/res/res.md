# res

## 目录

项目 res/ 目录中支持的资源目录

| 目录      | 资源类型                                                     |
| --------- | ------------------------------------------------------------ |
| animator/ | 用于定义[属性动画](https://developer.android.com/guide/topics/graphics/prop-animation.html)的 XML 文件 |
| anim/     | 用于定义[渐变动画](https://developer.android.com/guide/topics/graphics/view-animation.html#tween-animation)的 XML 文件。（属性动画也可保存在此目录中，但为了区分这两种类型，属性动画首选 `animator/` 目录。） |
| color/    | 用于定义颜色状态列表的 XML 文件。                            |
| drawable/ | 位图文件（`.png`、`.9.png`、`.jpg`、`.gif`）或编译为以下可绘制对象资源子类型的 XML 文件：<br/>位图文件<br/> 九宫格（可调整大小的位图）<br/> 状态列表 <br/>形状 <br/>动画可绘制对象 <br/>其他可绘制对象 |
| mipmap/   | 适用于不同启动器图标密度的可绘制对象文件。如需了解有关使用 `mipmap/` 文件夹管理启动器图标的详细信息 |
| layout/   | 用于定义用户界面布局的 XML 文件                              |
| menu/     | 用于定义应用菜单（如选项菜单、上下文菜单或子菜单）的 XML 文件。 |
| raw/      | 需以原始形式保存的任意文件。如要使用原始 `InputStream` 打开这些资源，请使用资源 ID（即 `R.raw.*filename*`）调用 `Resources.openRawResource()`。<br><br>但是，如需访问原始文件名和文件层次结构，则可以考虑将某些资源保存在 `assets/` 目录（而非 `res/raw/`）下。`assets/` 中的文件没有资源 ID，因此您只能使用 `AssetManager` 读取这些文件。 |
| values/   | 包含字符串、整型数和颜色等简单值的 XML 文件。<br><br>其他 `res/` 子目录中的 XML 资源文件会根据 XML 文件名定义单个资源，而 `values/` 目录中的文件可描述多个资源。对于此目录中的文件，`<resources>` 元素的每个子元素均会定义一个资源。例如，`<string>` 元素会创建 `R.string` 资源，`<color>` 元素会创建 `R.color` 资源。<br><br>由于每个资源均使用自己的 XML 元素进行定义，因此您可以随意命名文件，并在某个文件中放入不同的资源类型。但是，您可能需要将独特的资源类型放在不同的文件中，使其一目了然。例如，对于可在此目录中创建的资源，下面给出了相应的文件名约定：<br><br>array.xml：资源数组（类型数组）<br>colors.xml：颜色值<br>dimens.xml：尺寸值<br>strings.xml：字符串值<br>styles.xml：样式 |
| xml/      | 可在运行时通过调用 Resources.getXML() 读取的任意 xml 文件。各种 XML配置文件（如可搜索配置）都必须保存在此处 |
| font/     | 带有扩展名的字体文件（如 .ttf、.otf 或 .ttc）,或包含<font-family> 元素的 XML 文件。 |

## 备用资源

1. 在 res/ 中创建以 <resources_name>-<qualifier> 形式命名的新目录
   - <resources_name> 是响应默认资源的目录名称（如上表中所定义）
   - <qualifier>是指定要使用这些资源的各个配置的名称（如表2中所定义）
   
   可以追加多个<qualifier>。以短划线将其分隔。
   
2. 将相应的备用资源保存在此新目录下。这些资源文件必须与默认资源文件完全同名。

### 有效的配置限定符

按优先级顺序列出了有效的配置限定符；如果对资源目录使用多个限定符，则必须按照表中所列顺序将其添加到目录名称中。

#### MCC 和 MNC

##### 示例

示例：
`mcc310`
`mcc310-mnc004`
`mcc208-mnc00`
等等

##### 描述

移动设备国家代码 (MCC)，（可选）后跟设备 SIM 卡中的移动设备网络代码 (MNC)。例如，`mcc310` 是指美国的任一运营商，`mcc310-mnc004` 是指美国的 Verizon 公司，`mcc208-mnc00` 是指法国的 Orange 公司。

如果设备使用无线装置连接（GSM 手机），则 MCC 和 MNC 值均来自 SIM 卡。

您也可以单独使用 MCC（例如，将国家/地区特定的合法资源加入应用）。如果只需根据语言指定，则改用*语言和地区*限定符（稍后进行介绍）。如果决定使用 MCC 和 MNC 限定符，请谨慎执行此操作并测试限定符是否按预期工作。

#### 语言和区域

##### 示例

示例：
`en`
`fr`
`en-rUS`
`fr-rFR`
`fr-rCA`
`b+en`
`b+en+US`
`b+es+419`

##### 描述

语言通过由两个字母组成的 [ISO 639-1](http://www.loc.gov/standards/iso639-2/php/code_list.php) 语言代码进行定义，可以选择后跟两个字母组成的 [ISO 3166-1-alpha-2](https://www.iso.org/obp/ui/#iso:pub:PUB500001:en) 区域码（前缀用小写字母 `r`）。

这些代码*不*区分大小写；`r` 前缀用于区分区域码。您不能单独指定区域。

Android 7.0（API 级别 24）引入对**BCP 47 语言标记**的支持，可供您用来限定特定语言和区域的资源。语言标记由一个或多个子标记序列组成，每个子标记都能优化或缩小由整体标记标识的语言范围。如需了解有关语言标记的详细信息，请参阅[用于标识语言的标记](https://tools.ietf.org/html/rfc5646)。

如要使用 BCP 47 语言标记，请将 `b+` 和两个字母的 [ISO 639-1](http://www.loc.gov/standards/iso639-2/php/code_list.php) 语言代码连接；其后还可选择使用其他子标记，用 `+` 分隔即可。

如果用户在系统设置中更改语言，则语言标记可能会在应用的生命周期中发生变更。如需了解运行时应用会因此受到何种影响，请参阅[处理运行时变更](https://developer.android.com/guide/topics/resources/runtime-changes.html)。

#### 布局方向

##### 示例

`ldrtl`
`ldltr`

##### 描述

应用的布局方向。`ldrtl` 是指“布局方向从右到左”。`ldltr` 是指“布局方向从左到右”（默认的隐式值）。

此配置适用于布局、可绘制资源或值等任何资源。

例如，若要针对阿拉伯语提供某种特定布局，并针对任何其他“从右到左”的语言（如波斯语或希伯来语）提供某种通用布局。

###### 注意：

如要为应用启用从右到左的布局功能，则必须将 **supportsRtl**设置为 `"true"`，并将 [`targetSdkVersion`](https://developer.android.com/guide/topics/manifest/uses-sdk-element.html#target) 设置为 17 或更高版本。

#### smallestWidth

##### 示例

  `sw<N>dp`

示例：
`sw320dp`
`sw600dp`
`sw720dp`
等等  

##### 描述

屏幕的基本尺寸，由可用屏幕区域的最小尺寸指定。具体而言，设备的 smallestWidth 是屏幕可用高度和宽度的最小尺寸（您也可将其视为屏幕的“最小可能宽度”）。无论屏幕的当前方向如何，您均可使用此限定符确保应用界面的可用宽度至少为 `<N>` dp。

例如，如果布局要求屏幕区域的最小尺寸始终至少为 600dp，则可使用此限定符创建布局资源 `res/layout-sw600dp/`。仅当可用屏幕的最小尺寸至少为 600dp（无论 600dp 表示的边是用户所认为的高度还是宽度）时，系统才会使用这些资源。最小宽度为设备的固定屏幕尺寸特征；**即使屏幕方向发生变化，设备的最小宽度仍会保持不变**。

使用最小宽度确定一般屏幕尺寸非常有用，因为宽度通常是设计布局时的驱动因素。界面经常会垂直滚动，但对其水平方向所需要的最小空间具有非常硬性的限制。可用宽度也是确定是否对手持式设备使用单窗格布局，或对平板电脑使用多窗格布局的关键因素。因此，您可能最关注每台设备上的最小可能宽度。

设备的最小宽度会将屏幕装饰元素和系统界面考虑在内。例如，如果设备屏幕上的某些永久性界面元素沿着最小宽度轴占据空间，则系统会声明最小宽度小于实际屏幕尺寸，因为这些屏幕像素不适用于您的界面。

以下是一些可用于常见屏幕尺寸的值：

- 320，适用于屏幕配置如下的设备：
  - 240x320 ldpi（QVGA 手机）
  - 320x480 mdpi（手机）
  - 480x800 hdpi（高密度手机）
- 480，适用于 480x800 mdpi 之类的屏幕（平板电脑/手机）。
- 600，适用于 600x1024 mdpi 之类的屏幕（7 英寸平板电脑）。
- 720，适用于 720x1280 mdpi 之类的屏幕（10 英寸平板电脑）。

当应用为多个资源目录提供不同的 smallestWidth 限定符值时，系统会使用最接近（但未超出）设备 smallestWidth 的值。

*此项为 API 级别 13 中的新增配置。*

另请参阅 [`android:requiresSmallestWidthDp`](https://developer.android.com/guide/topics/manifest/supports-screens-element.html#requiresSmallest) 属性（声明与应用兼容的最小 smallestWidth）和 `smallestScreenWidthDp` 配置字段（存放设备的 smallestWidth 值）。

#### 可用宽度

##### 示例

`w<N>dp`
示例：
`w720dp`
`w1024dp`
等等

##### 描述

指定资源应使用的最小可用屏幕宽度（以 `dp` 为单位，由 `<N>` 值定义）。当屏幕方向在横向和纵向之间切换时，此配置值也会随之变化，以匹配当前的实际宽度。

此功能往往有助于确定是否使用多窗格布局，因为即便在使用平板电脑设备时，您通常也不希望竖屏以横屏的方式使用多窗格布局。因此，您可以使用此功能指定布局所需的最小宽度，而无需同时使用屏幕尺寸和屏幕方向限定符。

应用为此配置提供具有不同值的多个资源目录时，系统会使用最接近（但未超出）设备当前屏幕宽度的值。此处的值会考虑屏幕装饰元素，因此如果设备显示屏的左边缘或右边缘上有一些永久性 UI 元素，考虑到这些 UI 元素，同时为减少应用的可用空间，设备会使用小于实际屏幕尺寸的宽度值。

*此项为 API 级别 13 中的新增配置。*

另请参阅 `screenWidthDp` 配置字段，该字段存放当前屏幕宽度。

#### 可用高度

##### 示例

  `h<N>dp`

示例：
`h720dp`
`h1024dp`
等等  

##### 描述

指定资源应使用的最小可用屏幕高度（以“dp”为单位，由 `<N>` 值定义）。当屏幕方向在横向和纵向之间切换时，此配置值也会随之变化，以匹配当前的实际高度。

对比使用此方式定义布局所需高度与使用 `w<N>dp` 定义所需宽度，二者均非常有用，且都无需同时使用屏幕尺寸和方向限定符。但大多数应用不需要此限定符，因为界面经常垂直滚动，所以高度需更有弹性，而宽度则应更固定。

当应用为此配置提供具有不同值的多个资源目录时，系统会使用最接近（但未超出）设备当前屏幕高度的值。此处的值会考虑屏幕装饰元素，因此如果设备显示屏的上边缘或下边缘上有一些永久性 UI 元素，考虑到这些 UI 元素，同时为减少应用的可用空间，设备会使用小于实际屏幕尺寸的高度值。非固定的屏幕装饰元素（例如，全屏时可隐藏的手机状态栏）并*不*在考虑范围内，标题栏或操作栏等窗口装饰亦如此，因此应用必须准备好处理稍小于其指定值的空间。

*此项为 API 级别 13 中的新增配置。*

另请参阅 `screenHeightDp` 配置字段，该字段存放当前屏幕宽度。

#### 屏幕尺寸

##### 示例

`small`
`normal`
`large`
`xlarge`

##### 描述

- `small`：尺寸类似于低密度 VGA 屏幕的屏幕。小屏幕的最小布局尺寸约为 320x426 dp。例如，QVGA 低密度屏幕和 VGA 高密度屏幕。
- `normal`：尺寸类似于中等密度 HVGA 屏幕的屏幕。标准屏幕的最小布局尺寸约为 320x470 dp。例如，WQVGA 低密度屏幕、HVGA 中等密度屏幕、WVGA 高密度屏幕。
- `large`：尺寸类似于中等密度 VGA 屏幕的屏幕。大屏幕的最小布局尺寸约为 480x640 dp。例如，VGA 和 WVGA 中等密度屏幕。
- `xlarge`：明显大于传统中等密度 HVGA 屏幕的屏幕。超大屏幕的最小布局尺寸约为 720x960 dp。在大多数情况下，屏幕超大的设备体积太大，不能放进口袋，最常见的是平板式设备。*此项为 API 级别 9 中的新增配置。*

##### 注意

- 使用尺寸限定符并不表示资源*仅*适用于该尺寸的屏幕。如果没有为备用资源提供最符合当前设备配置的限定符，则系统可能会使用其中[最匹配](https://developer.android.com/guide/topics/resources/providing-resources#BestMatch)的资源。

- 如果所有资源均使用*大于*当前屏幕的尺寸限定符，则系统**不**会使用这些资源，并且应用将在运行时崩溃（例如，如果所有布局资源均以 `xlarge` 限定符标记，但设备是标准尺寸的屏幕）。

#### 屏幕纵横比

##### 示例

long

notlong

##### 描述

- `long`：宽屏，如 WQVGA、WVGA、FWVGA
- `notlong`：非宽屏，如 QVGA、HVGA 和 VGA

*此项为 API 级别 4 中新增配置。*

此配置完全基于屏幕的纵横比（宽屏较宽），并且与屏幕方向无关。

#### 圆形屏幕

##### 示例

round

notround

##### 描述

- `round`：圆形屏幕，例如圆形可穿戴式设备
- `notround`：方形屏幕，例如手机或平板电脑

*此项为 API 级别 23 中的新增配置。*

另请参阅 `isScreenRound()` 配置方法，该方法指示屏幕是否为圆形屏幕。

##### 注意

#### 广色域

##### 示例

widecg
nowidecg

##### 描述

- {@code widecg}：显示广色域，如 Display P3 或 AdobeRGB
- {@code nowidecg}：显示窄色域，如 sRGB

*此项为 API 级别 26 中的新增配置。*

另请参阅 `isScreenWideColorGamut()` 配置方法，该方法指示屏幕是否具有广色域。

#### 高动态范围

##### 示例

highdr
lowdr

##### 描述

- {@code highdr}：显示高动态范围
- {@code lowdr}：显示低/标准动态范围

*此项为 API 级别 26 中的新增配置。*

另请参阅 `isScreenHdr()` 配置方法，该方法指示屏幕是否具有 HDR 功能。

#### 屏幕方向

##### 示例

port
land

##### 描述

- `port`：设备处于纵向（垂直）
- `land`：设备处于横向状态（水平）

如果用户旋转屏幕，此配置可能会在应用生命周期中发生变化。如需了解这会在运行时期间给应用带来哪些影响，请参阅[处理运行时变更](https://developer.android.com/guide/topics/resources/runtime-changes.html)。

另请参阅 `orientation` 配置字段，该字段指示当前的设备方向。

#### 界面模式

##### 示例

car
desk
television
appliance
watch
vrheadset

##### 描述

- `car`：设备正在车载手机座上显示
- `desk`：设备正在桌面手机座上显示
- `television`：设备正在通过电视显示内容，通过将界面投影到离用户较远的大屏幕上，为用户提供“十英尺”体验。主要面向遥控交互或其他非触控式交互
- `appliance`：设备正在用作没有显示屏的装置
- `watch`：设备配有显示屏，并且可戴在手腕上
- `vrheadset`：设备正在通过虚拟现实耳机显示内容

*此项为 API 级别 8 中的新增配置，API 13 中的新增电视配置，API 20 中的新增手表配置。*

如需了解应用在设备插入基座或从中移除时的响应方式，请阅读[确定并监控插接状态和类型](https://developer.android.com/training/monitoring-device-state/docking-monitoring.html)。

如果用户将设备插入基座，此配置可能会在应用生命周期中发生变化。您可以使用 `UiModeManager` 启用或禁用其中的部分模式。如需了解这会在运行时期间给应用带来哪些影响，请参阅[处理运行时变更](https://developer.android.com/guide/topics/resources/runtime-changes.html)。

#### 夜间模式

##### 示例

night
notnight

##### 描述

- `night`：夜间
- `notnight`：白天

*此项为 API 级别 8 中的新增配置。*

如果夜间模式停留在自动模式（默认），此配置可能会在应用生命周期中发生变化。在此情况下，该模式会根据当天的时间进行调整。您可以使用 `UiModeManager` 启用或禁用此模式。如需了解这会在运行时期间给应用带来哪些影响，请参阅[处理运行时变更](https://developer.android.com/guide/topics/resources/runtime-changes.html)。

#### 屏幕像素密度 (dpi)

##### 示例

`ldpi`
`mdpi`
`hdpi`
`xhdpi`
`xxhdpi`
`xxxhdpi`
`nodpi`
`tvdpi`
`anydpi`
`*nnn*dpi`

##### 描述

- `ldpi`：低密度屏幕；约为 120dpi。
- `mdpi`：中等密度（传统 HVGA）屏幕；约为 160dpi。
- `hdpi`：高密度屏幕；约为 240dpi。
- `xhdpi`：超高密度屏幕；约为 320dpi。*此项为 API 级别 8 中的新增配置*
- `xxhdpi`：绝高密度屏幕；约为 480dpi。*此项为 API 级别 16 中的新增配置*
- `xxxhdpi`：极高密度屏幕使用（仅限启动器图标，请参阅*支持多种屏幕*中的[注释](https://developer.android.com/guide/practices/screens_support.html#xxxhdpi-note)）；约为 640dpi。*此项为 API 级别 18 中的新增配置*
- `nodpi`：可用于您不希望为匹配设备密度而进行缩放的位图资源。
- `tvdpi`：密度介于 mdpi 和 hdpi 之间的屏幕；约为 213dpi。此限定符并非指“基本”密度的屏幕。它主要用于电视，且大多数应用都不使用该密度 — 大多数应用只会使用 mdpi 和 hdpi 资源，而且系统将根据需要对这些资源进行缩放。*此项为 API 级别 13 中的新增配置*
- `anydpi`：此限定符适合所有屏幕密度，其优先级高于其他限定符。这非常适用于[矢量可绘制对象](https://developer.android.com/training/material/drawables.html#VectorDrawables)。*此项为 API 级别 21 中的新增配置*
- `*nnn*dpi`：用于表示非标准密度，其中 `*nnn*` 是正整数屏幕密度。此限定符不适用于大多数情况。使用标准密度存储分区，可显著减少因支持市场上各种设备屏幕密度而产生的开销。

六个基本密度之间的缩放比为 3:4:6:8:12:16（忽略 tvdpi 密度）。因此，9x9 (ldpi) 位图相当于 12x12 (mdpi)、18x18 (hdpi)、24x24 (xhdpi) 位图，依此类推。

如果您认为图像资源在电视或其他某些设备上的呈现效果不够好，进而想尝试使用 tvdpi 资源，则缩放系数应为 1.33*mdpi。例如，mdpi 屏幕的 100px x 100px 图像应相当于 tvdpi 屏幕的 133px x 133px 图像。

##### 注意

使用密度限定符并不表示资源*仅*适用于该密度的屏幕。如果没有为备用资源提供最符合当前设备配置的限定符，则系统可能使用其中[最匹配](https://developer.android.com/guide/topics/resources/providing-resources#BestMatch)的资源。

#### 触摸屏类型

##### 示例

notouch

finger

##### 描述

- `notouch`：设备没有触摸屏。
- `finger`：设备有一个专供用户通过手指直接进行交互的触摸屏。

另请参阅 `touchscreen` 配置字段，该字段指示设备上的触摸屏类型。

#### 键盘可用性

##### 示例

`keysexposed`
`keyshidden`
`keyssoft`

##### 描述

- `keysexposed`：设备拥有可用的键盘。如果设备启用了软键盘（不无可能），那么即使用户*未*找到硬键盘，或者该设备没有硬键盘，也可使用此限定符。如果未提供或已禁用软键盘，则只有在配备硬键盘的情况下才可使用此限定符。
- `keyshidden`：设备具有可用的硬键盘，但其处于隐藏状态，*且*设备*未*启用软键盘。
- `keyssoft`：设备已启用软键盘（无论是否可见）。

如果您提供了 `keysexposed` 资源，但未提供 `keyssoft` 资源，则无论键盘是否可见，只要系统已启用软键盘，其便会使用 `keysexposed` 资源。

如果用户打开硬键盘，此配置可能会在应用生命周期中发生变化。如需了解这会在运行时期间给应用带来哪些影响，请参阅[处理运行时变更](https://developer.android.com/guide/topics/resources/runtime-changes.html)。

另请参阅配置字段 `hardKeyboardHidden` 和 `keyboardHidden`，二者分别指示硬键盘的可见性和任一键盘（包括软键盘）的可见性。

#### 主要的文本输入法

##### 示例

`nokeys`
`qwerty`
`12key`

##### 描述

- `nokeys`：设备没有用于文本输入的硬按键。
- `qwerty`：设备拥有标准硬键盘（无论是否对用户可见）。
- `12key`：设备拥有 12 键硬键盘（无论是否对用户可见）。

另请参阅 `keyboard` 配置字段，该字段指示可用的主要文本输入法。

#### 导航键可用性

##### 示例

`navexposed`
`navhidden`

##### 描述

- `navexposed`：导航键可供用户使用。
- `navhidden`：导航键不可用（例如，在密封盖子后面）。

如果用户显示导航键，此配置可能会在应用生命周期中发生变化。如需了解这会在运行时期间给应用带来哪些影响，请参阅[处理运行时变更](https://developer.android.com/guide/topics/resources/runtime-changes.html)。

另请参阅 `navigationHidden` 配置字段，该字段指示导航键是否处于隐藏状态。

#### 主要的非触摸导航方法

##### 示例

`nonav`
`dpad`
`trackball`
`wheel`

##### 描述

- `nonav`：除了使用触摸屏以外，设备没有其他导航设施。
- `dpad`：设备具有用于导航的方向键。
- `trackball`：设备具有用于导航的轨迹球。
- `wheel`：设备具有用于导航的方向盘（不常见）。

另请参阅 `navigation` 配置字段，该字段指示可用的导航方法类型。

#### 平台版本（API 级别）

##### 示例

示例：
`v3`
`v4`
`v7`
等等

##### 描述

设备支持的 API 级别。例如，`v1` 对应 API 级别 1（装有 Android 1.0 或更高版本系统的设备），`v4` 对应 API 级别 4（装有 Android 1.6 或更高版本系统的设备）。

#### 注意

自 Android 1.0 起便已添加部分配置限定符，因此并非所有版本的 Android 系统都支持所有限定符。使用新限定符会隐式添加平台版本限定符，因此较旧版本系统的设备必然会忽略它。例如，使用 `w600dp` 限定符会自动包括 `v13` 限定符，因为可用宽度限定符是 API 级别 13 中的新增配置。为避免出现任何问题，请始终包含一组默认资源（一组*不带限定符*的资源）。

### 限定符命名规则

#### 规则

- 您可以为单组资源指定多个限定符，并使用短划线分隔。例如，`drawable-en-rUS-land` 适用于屏幕方向为横向的美国英语设备。
- 这些限定符必须遵循有效的配置限定符中列出的顺序。例如：
  - 错误：`drawable-hdpi-port/`
  - 正确：`drawable-port-hdpi/`
- 不能嵌套备用资源目录。例如，您的目录不能为 `res/drawable/drawable-en/`。
- 值不区分大小写。在处理之前，资源编译器会将目录名称转换为小写，以免不区分大小写的文件系统出现问题。名称中使用的所有大写字母只是为了便于认读。
- 每种限定符类型仅支持一个值。例如，若要对西班牙语和法语使用相同的可绘制对象文件，则您*不能*拥有名为 `drawable-rES-rFR/` 的目录，而是需要两个包含相应文件的资源目录，如 `drawable-rES/` 和 `drawable-rFR/`。然而，您实际无需在两处复制相同的文件。相反，您可以创建指向资源的别名。

### 创建别名资源

如果您想将某一资源用于多种设备配置（但不想以默认资源的形式提供该资源），则无需将同一资源放入多个备用资源目录中。相反，您可以（在某些情况下）创建备用资源，充当默认资源目录中所保存资源的别名。

#### 注意

并非所有资源都会提供相应机制，以便您创建指向其他资源的别名。特别是，`xml/` 目录中的动画资源、菜单资源、原始资源及其他未指定资源均不提供此功能。

#### 示例

假设您有一个应用图标 `icon.png`，并且需要用于不同语言区域的独特版本。但是，加拿大英语和加拿大法语这两种语言区域需使用同一版本。您可能会认为，需要将相同图像复制到加拿大英语和加拿大法语所对应的资源目录中，但事实并非如此。相反，您可以将用于二者的图像保存为 `icon_ca.png`（除 `icon.png` 以外的任何名称），并将其放入默认的 `res/drawable/` 目录中。然后，在 `res/drawable-en-rCA/` 和 `res/drawable-fr-rCA/` 中创建 `icon.xml` 文件，使用 `<bitmap>` 元素引用 `icon_ca.png` 资源。这样，您只需存储 PNG 文件的一个版本和两个指向该版本的小型 XML 文件。（XML 文件示例如下。）

#### 可绘制对象

如要创建指向现有可绘制对象的别名，请使用 `<drawable>` 元素。例如：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <drawable name="icon">@drawable/icon_ca</drawable>
</resources>
```

如果将此文件保存为 `drawables.xml`（例如，在备用资源目录中保存为 `res/values-en-rCA/`），则系统会将其编译到可作为 `R.drawable.icon` 引用的资源中，但该文件实际是 `R.drawable.icon_ca` 资源（保存在 `res/drawable/` 中）的别名。

#### 布局

如要创建指向现有布局的别名，请使用包装在 `<merge>` 中的 `<include>` 元素。例如：

```xml
<?xml version="1.0" encoding="utf-8"?>
<merge>
    <include layout="@layout/main_ltr"/>
</merge>
```

如果将此文件保存为 `main.xml`，则系统会将其编译到可作为 `R.layout.main` 引用的资源中，但该文件实际是 `R.layout.main_ltr` 资源的别名。

#### 字符串和其他简单值

如要创建指向现有字符串的别名，您只需将所需字符串的资源 ID 用作新字符串的值。例如：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="hello">Hello</string>
    <string name="hi">@string/hello</string>
</resources>
```

`R.string.hi` 资源现在是 `R.string.hello` 的别名。

## 访问应用资源

所有资源 ID 都在您项目的 `R` 类中进行定义，该类由 `aapt` 工具自动生成。

#### 资源 ID 组成

- *资源类型*：每个资源都被分到一个“类型”组中，例如 `string`、`drawable` 和 `layout`。
- *资源名称*，它是不包括扩展名的文件名；或是 XML `android:name` 属性中的值（如资源是字符串等简单值）。

#### 访问资源的两种方法

- 代码中

  ```java
  R.string.hello
  ```

- xml中

  ```xml
  @string/hello
  ```

### 在xml中访问资源

#### 引用样式属性

利用样式属性资源，您可以在当前应用的主题背景中引用某个属性的值。借助引用样式属性，在自定义界面元素的外观时，您无需采用提供硬编码值这种方式，您可以通过为其设置样式，以匹配当前主题背景提供的标准变体来达成目的。引用样式属性的实质作用是，“在当前主题背景中使用此属性定义的样式”。

如要引用样式属性，名称语法几乎与普通资源格式完全相同，区别在于您需将 at 符号 (`@`) 改为问号 (`?`)，并且资源类型部分为可选项。例如：

```xml
?[<package_name>:][<resource_type>/]<resource_name>
```

以下代码展示了如何通过引用属性来设置文本颜色，使其匹配系统主题背景的“基本”文本颜色：

```xml
<EditText id="text"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:textColor="?android:textColorSecondary"
    android:text="@string/hello_world" />
```

在以上代码中，`android:textColor` 属性指定当前主题背景中某个样式属性的名称。Android 现在会将应用于 `android:textColorSecondary` 样式属性的值用作此微件中 `android:textColor` 的值。由于系统资源工具知道此环境中肯定存在某个属性资源，因此您无需显式声明类型（类型应为 `?android:attr/textColorSecondary`）— 您可以将 `attr` 类型排除在外。

### 访问原始文件

尽管并不常见，但您的确有可能需要访问原始文件和目录。如果确有需要，则将文件保存在 `res/` 中并没有用，因为从 `res/` 读取资源的唯一方法是使用资源 ID。您可以改为将资源保存在 `assets/` 目录中。

保存在 `assets/` 目录中的文件*没有*资源 ID，因此您无法通过 `R` 类或在 XML 资源中引用它们。您可以改为采用类似普通文件系统的方式查询 `assets/` 目录中的文件，并利用 `AssetManager` 读取原始数据。

不过，如果您只需要读取原始数据（例如视频文件或音频文件）的能力，则可将文件保存在 `res/raw/` 目录中，并利用 `openRawResource()` 读取字节流。

### 访问平台资源

Android 包含许多标准资源，例如样式、主题背景和布局。如要访问这些资源，请通过 `android` 包名称限定您的资源引用。例如，您可以将 Android 提供的布局资源用于 `ListAdapter` 中的列表项：

```
setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myarray));
```

## 利用资源提供最佳设备兼容性

为提供最佳的设备兼容性，请始终为应用正确运行所需的资源提供默认资源。然后，请使用配置限定符为特定的设备配置创建备用资源。

这条规则有一个例外：如果应用的 [`minSdkVersion`](https://developer.android.com/guide/topics/manifest/uses-sdk-element.html#min) 为 4 或更高版本，则在提供带[屏幕密度](https://developer.android.com/guide/topics/resources/providing-resources#DensityQualifier)限定符的备用可绘制对象资源时，您*不*需要默认可绘制对象资源。即使没有默认可绘制对象资源，Android 也可从备用屏幕密度中找到最佳匹配项并根据需要缩放位图。但是，为了在所有类型的设备上提供最佳体验，您应为所有三种类型的密度提供备用可绘制对象。

## Android 如何查找最佳匹配资源

1. 淘汰与设备配置冲突的资源文件。
2. 选择有效的配置限定符中（下一个）优先级最高的限定符。（从 MCC 开始，然后向下移动。）
3. 是否有资源目录包含此限定符？
   - 若无，请返回到第 2 步，看看下一个限定符。（在该示例中，除非到达语言限定符，否则答案始终为“否”。）
   - 若有，请继续执行第 4 步。
4. 淘汰不含此限定符的资源目录。
5. 返回并重复第 2 步、第 3 步和第 4 步，直到仅剩一个目录为止。

### 注意

限定符的*优先级*（有效的配置限定符中）比与设备完全匹配的限定符数量更加重要。

