### \<receiver>

#### 语法

```xml
<receiver android:directBootAware=["true" | "false"]
          android:enabled=["true" | "false"]
          android:exported=["true" | "false"]
          android:icon="drawable resource"
          android:label="string resource"
          android:name="string"
          android:permission="string"
          android:process="string" >
    . . .
</receiver>
```

#### 包含在

- \<application>

#### 可以包含

- \<intent-filter>
- \<meta-data>

#### 注册方式

- 静态注册
- 动态注册

#### 注意

限制在应用程序中设置的广播接收器的数量。 拥有太多的广播接收器会影响你的应用程序的性能和用户设备的电池寿命。 有关可以用来代替 BroadcastReceiver 类安排后台工作的 api 的更多信息，请**参见后台优化**。

#### 属性

| 属性                    | 描述                                                         | 是否必须 | 常用值          |
| ----------------------- | ------------------------------------------------------------ | -------- | --------------- |
| android:directBootAware | 广播接收器是否可以直接引导; 也就是说，它是否可以在用户解锁设备之前运行。 |          | 默认值为"false" |

