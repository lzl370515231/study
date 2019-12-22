### \<provider>

#### 语法

```xml
<provider android:authorities="list"
          android:directBootAware=["true" | "false"]
          android:enabled=["true" | "false"]
          android:exported=["true" | "false"]
          android:grantUriPermissions=["true" | "false"]
          android:icon="drawable resource"
          android:initOrder="integer"
          android:label="string resource"
          android:multiprocess=["true" | "false"]
          android:name="string"
          android:permission="string"
          android:process="string"
          android:readPermission="string"
          android:syncable=["true" | "false"]
          android:writePermission="string" >
    . . .
</provider>
```

#### 包含于

- \<application>

#### 可以包含

- \<meta-data>
- \<grant-uri-permission>
- \<path-permission>

#### URI

```xml
content://com.example.project.healthcareprovider/nurses/rn
```

- scheme

  content://

- authority

  com.example.project.healthcareprovider

- path

  /nurses/rn

