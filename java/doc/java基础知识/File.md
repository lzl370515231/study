# File



#### getCanonicalPath()

返回此抽象路径名的规范路径名字符串

会将文件路径解析为与操作系统相关的唯一的规范形式的字符串。相当于将getAbsolutePath()中的“.”和“..”解析成对应的正确的路径

#### getAbsolutePath()

返回此抽象路径名的绝对路径名字符串。但不会处理“.”和“..”的情况

```java
//F:\eclipseworkspace\src\test1.txt
getCanonicalPath();
//F:\eclipseworkspace\testejb\..\src\test1.txt
getAbsolutePath();
```

