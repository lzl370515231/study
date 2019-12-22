### \<support-gl-texture>

声明一个应用程序支持的GL 纹理

#### 语法

```xml
<supports-gl-texture
                     android:name="string" />
```

#### 包含在

- \<manifest>

#### 属性

| 属性         | 描述                                                         | 是否必须 | 常用值                    |
| ------------ | ------------------------------------------------------------ | -------- | ------------------------- |
| android:name | 指定应用程序支持的单个 GL / 纹理压缩格式，作为描述符字符串。 |          | 见下表 compression format |
|              |                                                              |          |                           |
|              |                                                              |          |                           |



##### compression format

| 值                                  | 描述                                                         |
| ----------------------------------- | ------------------------------------------------------------ |
| GL_OES_compressed_ETC1_RGB8_texture | 爱立信纹理压缩。 在 OpenGL ES 2.0中指定，在所有支持 OpenGL ES 2.0的 android 设备中都可以使用 |
| GL_OES_compressed_paletted_texture  | 仿制的纹理压缩                                               |
| GL_AMD_compressed_3DC_texture       | 3 dc 纹理压缩                                                |
| GL_AMD_compressed_ATC_texture       |                                                              |
| GL_EXT_texture_compression_latc     | 亮度 α 纹理压缩                                              |
| GL_EXT_texture_compression_dxt1     | S3 DXT1纹理压缩。 支持运行 Nvidia tegra2平台的设备，包括 Motorala Xoom、 Motorola Atrix、 Droid Bionic 等 |
| GL_EXT_texture_compression_s3tc     |                                                              |
| GL_IMG_texture_compression_pvrtc    |                                                              |



#### 说明

如果应用程序支持多种纹理压缩格式，可以声明多个支持 gl-texture 的元素。例如：

```xml
<supports-gl-texture android:name="GL_OES_compressed_ETC1_RGB8_texture" />
<supports-gl-texture android:name="GL_OES_compressed_paletted_texture" />
```



声明的 supports-gl-texture 元素是信息性的，这意味着 Android 系统本身不会在安装时检查这些元素，以确保在设备上匹配支持。 但是，其他服务(如 Google Play)或应用程序可以检查应用程序的 support-gl-texture 声明，作为处理或与应用程序交互的一部分。 由于这个原因，声明应用程序能够支持的所有纹理压缩格式(从下面的列表中)非常重要。