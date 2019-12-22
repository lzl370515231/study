### 向上导航功能

在应用中，不是主入口点的每个屏幕（所有不是主屏幕的屏幕）都应提供导航功能，以便用户可通过在应用栏中点按“向上”按钮返回到应用层次结构中的逻辑父屏幕。

只需要在 AndroidManifest.xml 文件中声明哪个 Activity 是逻辑父屏幕即可。如：

```xml
<activity android:name=".DisplayMessageActivity"
          android:parentActivityName=".MainActivity">
    <!-- The meta-data tag is required if you support API level 15 and lower -->
    <meta-data
               android:name="android.support.PARENT_ACTIVITY"
               android:value=".MainActivity" />
</activity>
```

