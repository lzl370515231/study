### \<service>

#### 语法

```xml
<service android:description="string resource"
         android:directBootAware=["true" | "false"]
         android:enabled=["true" | "false"]
         android:exported=["true" | "false"]
         android:foregroundServiceType=["connectedDevice" | "dataSync" |
                                        "location" | "mediaPlayback" | "mediaProjection" |
                                        "phoneCall"]
         android:icon="drawable resource"
         android:isolatedProcess=["true" | "false"]
         android:label="string resource"
         android:name="string"
         android:permission="string"
         android:process="string" >
    . . .
</service>
```

#### 包含于

- \<application>

#### 可以包含

- \<intent-filter>
- \<meta-data>

#### 属性

| 属性                          | 描述                                                         | 是否必须 | 常用值        |
| ----------------------------- | ------------------------------------------------------------ | -------- | ------------- |
| android:directBootAware       | 服务是否可以直接引导; 也就是说，是否可以在用户解锁设备之前运行服务。 |          | 默认值"false" |
| android:foregroundServiceType | 指定服务是满足特定用例的前台服务。 例如，前台服务类型的“ location”表示应用程序正在获取设备的当前位置，通常用于继续用户发起的与设备位置相关的操作。 |          |               |
| android:isolatedProcess       | 如果设置为 true，则此服务将在与系统其余部分隔离并且没有自身权限的特殊进程下运行。 与它的唯一通信是通过服务 API (绑定和启动) |          |               |

#### 注意

1. 在直接引导期间，应用程序中的服务只能访问存储在设备保护存储中的数据。