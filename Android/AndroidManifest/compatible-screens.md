### \<compatible-screens>

兼容屏幕

#### 语法

```xml
<compatible-screens>
    <screen android:screenSize=["small" | "normal" | "large" | "xlarge"]
            android:screenDensity=["ldpi" | "mdpi" | "hdpi" | "xhdpi"
                                   | "280" | "360" | "420" | "480" | "560" ] />
    ...
</compatible-screens>
```

#### 示例

如果您的应用程序只兼容小屏幕和普通屏幕，不管屏幕密度如何，那么您必须指定十二个不同的屏幕元素，因为每个屏幕大小有6个不同的密度配置。 您必须声明其中的每一个; 您没有指定的大小和密度的任何组合都被视为与您的应用程序不兼容的屏幕配置。 如果您的应用程序只兼容小屏幕和普通屏幕，那么清单条目看起来是这样的:

```xml
<manifest ... >
    ...
    <compatible-screens>
        <!-- all small size screens -->
        <screen android:screenSize="small" android:screenDensity="ldpi" />
        <screen android:screenSize="small" android:screenDensity="mdpi" />
        <screen android:screenSize="small" android:screenDensity="hdpi" />
        <screen android:screenSize="small" android:screenDensity="xhdpi" />
        <screen android:screenSize="small" android:screenDensity="xxhdpi" />
        <screen android:screenSize="small" android:screenDensity="xxxhdpi" />
        <!-- all normal size screens -->
        <screen android:screenSize="normal" android:screenDensity="ldpi" />
        <screen android:screenSize="normal" android:screenDensity="mdpi" />
        <screen android:screenSize="normal" android:screenDensity="hdpi" />
        <screen android:screenSize="normal" android:screenDensity="xhdpi" />
        <screen android:screenSize="normal" android:screenDensity="xxhdpi" />
        <screen android:screenSize="normal" android:screenDensity="xxxhdpi" />
    </compatible-screens>
    <application ... >
        ...
    <application>
</manifest>
```

