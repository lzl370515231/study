### \<manifest>

#### 语法

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="string"
          android:sharedUserId="string"
          android:sharedUserLabel="string resource" 
          android:versionCode="integer"
          android:versionName="string"
          android:installLocation=["auto" | "internalOnly" | "preferExternal"] >
    . . .
</manifest>
```

#### 必须包含

- \<application>

#### 可以包含

- \<compatible-screen>
- \<instrumentation>
- \<permission>
- \<permission-group>
- \<permission-tree>
- \<supports-gl-texture>
- \<supports-screens>
- \<uses-configuration>
- \<uses-feature>
- \<uses-permission>
- \<uses-permission-sdk-23>
- \<uses-sdk>

