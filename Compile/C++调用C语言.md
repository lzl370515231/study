### 问题

C++ 调用 C语言，经常会出现引用找不着的问题，如下图所示：

![](.\png\C++调用C.png)

### 原理

由于C++支持重载，故会对函数名负带返回值类型和参数类型。如下图所示：

![](.\png\C++编译生成函数名.png)

C语言函数名：

![](.\png\C编译生成函数名.png)

因此会导致找不到函数名。

### 解决办法

#### C

```c
#ifdef __cplusplus
extern "C" {
#endif
	int main(){
    	return 0;
	}

	int func(){
    	return 0;
	}
    
#ifdef __cplusplus
}
#endif
```

#### C++

```c++
int main(){
    func();
}
```

