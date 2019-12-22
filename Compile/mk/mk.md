## mk

### 命令行

##### 文件处于 jni目录下

```shell
ndk-build -C ./jni
```

##### 文件处于非 jni目录下

```shell
ndk-build NDK_PROJECT_PATH= . APP_BUILD_SCRIPT=./Android.mk NDK_APPLICATION_MK=./Application.mk
```

