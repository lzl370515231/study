### action

#### 语法

```xml
<intent-filter>
	<action android:name="string" />
</intent-filter>
```



#### 匹配规则

> Intent 中的 Action 必须能够和 Activity 过滤规则中的 Action 匹配（这里的匹配是完全相等）。一个过滤规则中有多个 Action，那么只要 Intent 中的 action 能够和 Activity 过滤规则中的任何一个 action 相同即可匹配成功。

