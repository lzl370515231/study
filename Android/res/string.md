### xliff:g

XML Localization Interchange File Format，即 XML 本地化数据交换格式。

#### 用法

1. 修改 xml 文件中的 resources 标签，指定 xliff 格式的命名空间
2. 在字符串标签中引入
3. 在 java 文件中通过 Resources 类的 getString(@StringRes int id,Object... formatArgs) 方法给占位符提供值，并获取对应的字符串。 

#### 示例

- 在 xml 中定义字符串以及指定命名空间

  ```xml
  <resources xmlns:xliff="urn:oasis:names:tc:xliff:document:1.2">
  
      <string name="xliff_string">
          今天是<xliff:g id="WEEK">%1$s</xliff:g>,<xliff:g id="DAY">%2$d</xliff:g>号
      </string>
  
  </resources>
  ```

  

- 在java 代码中引用并提供参数

```java
String s = getResources().getString(R.string.xliff_string,"星期一",13);
```

最终 s 的内容是：今天是星期一，13号。



### Quantity Strings（Plurals）

针对语法数量的一致性，不同语言有不同规则。例如，在英语中，数量 1 是一种特殊情况。我们会写成“1 book”，但如果是任何其他数量，则会写成“*n* books”。这种对单复数的区分很常见，但其他语言拥有更细致的区分。

决定为给定语言和数量使用哪种情况的规则可能非常复杂，因此 Android 为您提供getQuantityString()等方法来选择合适资源。

##### 注

Pulrals 集合是一种简单资源，可以使用 name 属性（并非 XML 文件的名称）中提供的值对其进行引用。因此，您可以在一个 `<resources>` 元素下，将 plurals 资源与其他简单资源合并到一个 XML 文件中。

#### 文件位置

res/values/filename.xml。\<plurals> 元素的 name 用作资源ID

#### 资源引用

- Java	R.plurals.plural_name

#### 语法

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <plurals
        name="plural_name">
        <item
            quantity=["zero" | "one" | "two" | "few" | "many" | "other"]
            >text_string</item>
    </plurals>
</resources>
```

#### 属性

##### \<plurals>

根据事物数量提供其中的一个字符串。包含一个或多个 `<item>` 元素。

##### \<item>

| 属性     | 描述                           | 是否必须 | 常用值          | 说明   |
| -------- | ------------------------------ | -------- | --------------- | ------ |
| quantity | 表示应在何时使用该字符串的值。 |          | 见下表 quantity | 关键字 |
|          |                                |          |                 |        |
|          |                                |          |                 |        |
|          |                                |          |                 |        |
|          |                                |          |                 |        |
|          |                                |          |                 |        |
|          |                                |          |                 |        |

###### quantity

| 值    | 描述                                                         |
| ----- | ------------------------------------------------------------ |
| zero  | 当某种语言要求对数字 0（如阿拉伯语中的 0）进行特殊处理时     |
| one   | 当某种语言要求对 1 这类数字（如英语和大多数其他语言中的数字 1；在俄语中，任何末尾为 1 但非 11 的数字均属此类）进行特殊处理时。 |
| two   | 当某种语言要求对 2 这类数字（如威尔士语中的 2，或斯洛文尼亚语中的 102）进行特殊处理时。 |
| few   | 当某种语言要求对“小”数字（如捷克语中的 2、3 和 4；或波兰语中末尾为 2、3 或 4，但非 12、13 或 14 的数字）进行特殊处理时。 |
| many  | 当某种语言要求对“大”数字（如马耳他语中末尾为 11 至 99 的数字）进行特殊处理时 |
| other | 当某种语言未要求对给定数量（如中文中的所有数字，或英语中的 42）进行特殊处理时 |

#### 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <plurals name="numberOfSongsAvailable">
        <!--
             As a developer, you should always supply "one" and "other"
             strings. Your translators will know which strings are actually
             needed for their language. Always include %d in "one" because
             translators will need to use %d for languages where "one"
             doesn't mean 1 (as explained above).
          -->
        <item quantity="one">%d song found.</item>
        <item quantity="other">%d songs found.</item>
    </plurals>
</resources>
```

```java
int count = getNumberOfSongsAvailable();
Resources res = getResources();
String songsFound = res.getQuantityString(R.plurals.numberOfSongsAvailable, count, count);
```

使用 `getQuantityString()` 方法时，如果您的字符串包含带有数字的[字符串格式设置](https://developer.android.com/guide/topics/resources/string-resource#FormattingAndStyling)，则您需要传递两次 `count`。例如，对于字符串 `%d songs found`，第一个 `count` 参数会选择相应的复数字符串，第二个 `count` 参数会被插入 `%d` 占位符内。如果您的复数字符串没有字符串格式设置，则无需向 `getQuantityString` 传递第三个参数。

### 格式和样式

#### 处理特殊字符

转义字符。

#### 设置字符串格式

```xml
<string name="welcome_messages">Hello, %1$s! You have %2$d new messages.</string>
```

```java
String text = getString(R.string.welcome_messages, username, mailCount);
```

#### 使用HTML 标记设置样式

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="welcome">Welcome to <b>Android</b>!</string>
</resources>
```

##### 支持以下 HTML元素

| 功能             | 方式                                                         |
| ---------------- | ------------------------------------------------------------ |
| 粗体             | \<b>、\<em>                                                  |
| 斜体             | <i>、<cite>、<dfn>                                           |
| 文本放大 25%     | <big>                                                        |
| 文本缩小 20%     | <small>                                                      |
| 设置字体属性     | <font face="font_family" color="hex_color">。可能的字体系列示例包括 `monospace`、`serif` 和 `sans_serif`。 |
| 设置等宽字体系列 | <tt>                                                         |
| 删除线           | <s>、<strike>、<del>                                         |
| 下划线           | <u>                                                          |
| 上标             | <sup>                                                        |
| 下标             | <sub>                                                        |
| 列表标记         | <ul>、<li>                                                   |
| 换行符           | <br/>                                                        |
| 区隔标记         | <div>                                                        |
| CSS 样式         | <span style=”color\|background_color\|text-decoration">      |
| 段落             | <p dir=”rtl \| ltr" style="...">                             |

##### 示例

```xml
<resources>
  <string name="welcome_messages">Hello, %1$s! You have &lt;b>%2$d new messages&lt;/b>.</string>
</resources>
```

如上所示，带格式的字符串中添加了 `<b>` 元素。请注意，开括号使用 `&lt;` 符号实现了 HTML 转义。

调用 `fromHtml(String)`，以将 HTML 文本转换成带样式的文本。

```java
String text = getString(R.string.welcome_messages, username, mailCount);
Spanned styledText = Html.fromHtml(text, FROM_HTML_MODE_LEGACY);
```

由于 `fromHtml(String)` 方法会设置所有 HTML 实体的格式，因此请务必使用 **htmlEncode(String)**转义带格式文本的字符串中任何可能存在的 HTML 字符。例如，如果您打算对包含“<”或“&”等字符的字符串进行格式设置，则在设置格式前必须先转义这类字符。如此一来，当通过 `fromHtml(String)` 传递带格式的字符串时，字符才会以最初的编写形式显示。

```java
String escapedUsername = TextUtils.htmlEncode(username);

String text = getString(R.string.welcome_messages, escapedUsername, mailCount);
Spanned styledText = Html.fromHtml(text);
```

### 使用 Spannable 设置样式

`Spannable` 是一种文本对象，您可使用颜色和字体粗细等字体属性对其进行样式设置。您可以使用 `SpannableStringBuilder` 生成文本，然后对文本应用 `android.text.style` 软件包中定义的样式

### 使用注解设置样式

您可以通过使用 strings.xml 资源文件中的 `Annotation` 类和 `<annotation>` 标记，应用复杂样式或自定义样式。借助注解标记，您可以通过在 XML 文件中定义自定义键值对来标记自定义样式的部分字符串，框架随后会将该 XML 文件转换成 `Annotation` span。然后，您便可检索这些注解，并使用键和值来应用样式。

![](.\png\string-使用注解设置样式.png)

#### 示例

##### 添加自定义字体

1. 添加<annotation> 标记并定义键值对。在此情况下，键为 font，而值是我们要使用的字体类型：title_emphasis

   ```xml
   // values/strings.xml
   <string name="title">Best practices for <annotation font="title_emphasis">text</annotation> on Android</string>
   
   // values-es/strings.xml
   <string name="title"><annotation font="title_emphasis">Texto</annotation> en Android: mejores prácticas</string>
   ```

   

2. 加载字符串资源并找到包含 font 键的注解。然后，创建一个自定义 span，并用其替换现有 span。

   ```java
   // get the text as SpannedString so we can get the spans attached to the text
   SpannedString titleText = (SpannedString) getText(R.string.title_about);
   
   // get all the annotation spans from the text
   Annotation[] annotations = titleText.getSpans(0, titleText.length(), Annotation.class);
   
   // create a copy of the title text as a SpannableString.
   // the constructor copies both the text and the spans. so we can add and remove spans
   SpannableString spannableString = new SpannableString(titleText);
   
   // iterate through all the annotation spans
   for (Annotation annotation: annotations) {
       // look for the span with the key font
       if (annotation.getKey().equals("font")) {
           String fontName = annotation.getValue();
           // check the value associated to the annotation key
           if (fontName.equals("title_emphasis")) {
               // create the typeface
               Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto_mono);
               // set the span at the same indices as the annotation
               spannableString.setSpan(new CustomTypefaceSpan(typeface),
                                       titleText.getSpanStart(annotation),
                                       titleText.getSpanEnd(annotation),
                                       Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
           }
       }
   }
   
   // now, the spannableString contains both the annotation spans and the CustomTypefaceSpan
   styledText.text = spannableString;
   ```

   

#### 注解 span 和文本打包

`Annotation` span 也是 `ParcelableSpans`，因此需对键值对进行打包和拆包。只要包的接收方了解如何解释注解，您便可使用 `Annotation` span 向打包文本应用自定义样式。

如要在向 Intent Bundle 传递文本时保留自定义样式，您首先需在文本中添加 `Annotation` span。您可以使用 <annotation> 标记在 XML 资源中执行此操作（如上例所示），或通过创建新的 `Annotation` 并将其设置为 span，在代码中执行此操作（如下所示）：

```java
SpannableString spannableString = new SpannableString("My spantastic text");
Annotation annotation = new Annotation("font", "title_emphasis");
spannableString.setSpan(annotation, 3, 7, 33);

// start Activity with text with spans
Intent intent = new Intent(this, MainActivity.class);
intent.putExtra(TEXT_EXTRA, spannableString);
this.startActivity(intent);
```

以 `SpannableString` 的形式从 `Bundle` 中检索文本，然后解析附加的注解（如上例所示）。

```java
// read text with Spans
SpannableString intentCharSequence = (SpannableString)intent.getCharSequenceExtra(TEXT_EXTRA);
```

