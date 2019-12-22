### \<category>

#### 语法

```xml
<category android:name="string" />
```

#### 注意

为了接收隐式意图，必须在意图过滤器中包含 CATEGORY default 类别。 方法 startActivity ()和 startActivityForResult ()将所有意图视为声明了 CATEGORY default 类别。 如果未在意图过滤器中声明它，则将不会解析隐式意图到活动。

#### 匹配规则

> 如果Intent中的存在category那么所有的category都必须和Activity过滤规则中的category相同。才能和这个Activity匹配。Intent中的category**数量可能少于**Activity中配置的category数量，但是Intent中的这category必须和Activity中配置的category相同才能匹配。

