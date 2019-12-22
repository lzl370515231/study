# JavaPoet

### 优点

1. JavaPoet是一款可以自动生成Java文件的第三方依赖。
2. 让繁杂、重复的Java文件，自动化生成，提高工作效率，简化流程

### 使用场景

- 根据编译时注解生成代码

- 根据协议文件生成对应代码

  参考 google 开源的 protobuffer 和 grpc

## 牛刀小试

### 源代码

```java
public class MainActivity extends Activity{
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
```

### 使用JavaPoet

#### 引入库

```groovy
compile 'com.squareup:javapoet:1.9.0'
```

#### Code

```java
public class Test{
	public static void main(String[] args) {
        ClassName activity = ClassName.get("android.app", "Activity");

        TypeSpec.Builder mainActivityBuilder = TypeSpec.classBuilder("MainActivity")
                .addModifiers(Modifier.PUBLIC)
                .superclass(activity);

        ClassName override = ClassName.get("java.lang", "Override");
        ClassName bundle = ClassName.get("android.os", "Bundle");
        ClassName nullable = ClassName.get("android.support.annotation", "Nullable");
        ParameterSpec savedInstanceState = ParameterSpec.builder(bundle, "savedInstanceState")
                .addAnnotation(nullable)
                .build();

        MethodSpec onCreate = MethodSpec.methodBuilder("onCreate")
                .addAnnotation(override)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(savedInstanceState)
                .addStatement("super.onCreate(savedInstanceState)")
                .addStatement("setContentView(R.layout.activity_main)")
                .build();

        TypeSpec mainActivity = mainActivityBuilder.addMethod(onCreate)
                .build();

        JavaFile file = JavaFile.builder("com.test", mainActivity).build();

        try {
            file.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

![](.\png\JavaPoet-test-1.png)

# 使用指南

![](.\png\JavaPoet源码结构.png)

## JavaPoet的常用类

1. TypeSpec————用于生成类、接口、枚举对象的类
2. MethodSpec————用于生成方法对象的类
3. ParameterSpec————用于生成参数对象的类
4. AnnotationSpec————用于生成注解对象的类
5. FieldSpec————用于配置生成成员变量的类
6. ClassName————通过包名和类名生成的对象，在JavaPoet中相当于为其指定Class
7. ParameterizedTypeName————通过MainClass和IncludeClass生成包含泛型的Class
8. JavaFile————控制生成的Java文件的输出的类

## 获取类

- ClassName.bestGuess("类全名称") 返回 ClassName 对象，这里的类全名称表示的类必须要存在，会自动导入相应的包
- ClassName.get("包名","类名")返回 ClassName 对象，不检查该类是否存在



## 占位符





## JavaPoet的常用方法

#### 设置修饰关键字

```
addModifiers(Modifier... modifiers)
复制代码
```

Modifier是一个枚举对象，枚举值为修饰关键字Public、Protected、Private、Static、Final等等。 所有在JavaPoet创建的对象都必须设置修饰符(包括方法、类、接口、枚举、参数、变量)。

#### 设置注解对象

```
addAnnotation（AnnotationSpec annotationSpec）
addAnnotation（ClassName annotation）
addAnnotation(Class<?> annotation)
复制代码
```

该方法即为类或方法或参数设置注解，参数即可以是AnnotationSpec，也可以是ClassName，还可以直接传递Class对象。 一般情况下，包含复杂属性的注解一般用AnnotationSpec，如果单纯添加基本注解，无其他附加属性可以直接使用ClassName或者Class即可。

#### 设置注释

```
addJavadoc（CodeBlock block）
addJavadoc(String format, Object... args)
复制代码
```

在编写类、方法、成员变量时，可以通过addJavadoc来设置注释，可以直接传入String对象，或者传入CodeBlock（代码块）。

## JavaPoet生成类、接口、枚举对象

在JavaPoet中生成类、接口、枚举，必须得通过TypeSpec生成，而classBuilder、interfaceBuilder、enumBuilder便是创建其关键的方法：

```
创建类：
TypeSpec.classBuilder("类名“) 
TypeSpec.classBuilder(ClassName className)

创建接口：
TypeSpec.interfaceBuilder("接口名称")
TypeSpec.interfaceBuilder(ClassName className)

创建枚举：
TypeSpec.enumBuilder("枚举名称")
TypeSpec.enumBuilder(ClassName className)
复制代码
```

#### 继承、实现接口

```
继承类：
.superclass(ClassName className)

实现接口
.addSuperinterface(ClassName className)
复制代码
```

**继承存在泛型的父类**

当继承父类存在泛型时，需要使用ParameterizedTypeName

```
ParameterizedTypeName get(ClassName rawType, TypeName... typeArguments)
复制代码
```

返回的ParameterizedTypeName对象，已经被添加泛型信息

#### 方法

```
addMethod(MethodSpec methodSpec)
复制代码
```

通过配置MethodSpec对象，使用addMethod方法将其添加进TypeSpec中。

#### 枚举

```
addEnumConstan(String enumValue)
复制代码
```

通过addEnumConstan方法添加枚举值，参数为枚举值名称。

## JavaPoet生成成员变量

JavaPoet生成成员变量是通过FieldSpec的build方法生成.

```
builder(TypeName type, String name, Modifier... modifiers)
复制代码
```

只要传入TypeName(Class)、name（名称）、Modifier（修饰符），就可以生成一个基本的成员变量。

成员变量一般来说由注解（Annotation）、修饰符（Modifier）、Javadoc(注释)、initializer(实例化)。

#### 注解

```
addAnnotation(TypeName name) 
复制代码
```

#### 修饰符

```
addModifiers(Modifier ...modifier)
复制代码
```

#### 注释

```
addJavadoc(String format, Object... args)
复制代码
```

由于上述三个方法，都在通用方法介绍过这里就不再重复介绍。

#### 实例化

```
initializer(String format, Object... args)
复制代码
```

即成员变量的实例化，例：

```
public Activity mActivity = new Activity;
复制代码
```

而**initializer方法中的内容就是“=”后面的内容**，下面看下具体的代码实现，上面的成员变量：

```
  ClassName activity = ClassName.get("android.app", "Activity");
  FieldSpec spec = FieldSpec.builder(activity, "mActivity")
                .addModifiers(Modifier.PUBLIC)
                .initializer("new $T", activity)
                .build();
复制代码
```

## JavaPoet生成方法

JavaPoet生成方法分为两种，第一种是构造方法，另一种为常规的方法。

#### 构造方法

```
MethodSpec.constructorBuilder()
复制代码
```

#### 常规方法

```
MethodSpec.methodBuilder(String name)
复制代码
```

方法的主要构成有方法参数、注解、返回值、方法体、抛出异常五种，注解可以参考通用方法addAnnotation，其他方法我们将会一一介绍：

#### 方法参数

```
addParameter(ParameterSpec parameterSpec)
复制代码
```

设置方法参数的方法通过addParameterSpec来实现，ParameterSpec的具体使用参考下一小节。

#### 返回值

```
returns(TypeName returnType)
复制代码
```

设置方法的返回值，只需传入一个TypeName对象，而TypeName是ClassName，ParameterizedTypeName的基类。

#### 方法体

在JavaPoet中，设置方法体内容有两个方法，分别是addCode和addStatement：

```
addCode()

addStatement()
复制代码
```

这两个本质上都是设置方法体内容，但是不同的是使用addStatement()方法时，你只需要专注于该段代码的内容，至于结尾的分号和换行它都会帮你做好。 而addCode（）添加的方法体内容就是一段无格式的代码片，需要开发者自己添加其格式。

#### 方法体模板

在JavaPoet中，设置方法体使用模板是比较常见的，因为addCode和addStatement方法都存在这样的一个重载:

```
addCode(String format, Object... args)

addStatement(String format, Object... args)
复制代码
```

在JavaPoet中，format中存在特定的占位符：

| 格式化规则 | 表示     |
| ---------- | -------- |
| $L         | 字面量   |
| $S         | 字符串   |
| $T         | 类、接口 |
| $N         | 变量     |

##### $T

$T 在JavaPoet代指的是TypeName，该模板主要将Class抽象出来，用传入的TypeName指向的Class来代替。

```
ClassName bundle = ClassName.get("android.os", "Bundle");
addStatement("$T bundle = new $T()",bundle)
复制代码
```

上述添加的代码内容为：

```
Bundle bundle = new Bundle();
复制代码
```

##### $N

$N在JavaPoet中代指的是一个名称，例如调用的方法名称，变量名称，这一类存在意思的名称

```
addStatement("data.$N()",toString)
复制代码
```

上述代码添加的内容：

```
data.toString();
复制代码
```

##### $S

$S在JavaPoet中就和String.format中%s一样,字符串的模板,将指定的字符串替换到$S的地方

```
.addStatement("super.$S(savedInstanceState)","onCreate")
复制代码
```

即将"onCreate"字符串代替到$S的位置上.

#### 抛出异常

```
.addException(TypeName name)
复制代码
```

设置方法抛出异常,可以使用addException方法,传入指定的异常的ClassName,即可为该方法设置其抛出该异常.

## JavaPoet生成方法参数

JavaPoet生成有参方法时,需要填充参数,而生成参数则需要通过ParameterSpec这个类。

```
addParameter(ParameterSpec parameterSpec)
复制代码
```

#### 初始化ParameterSpec

```
ParameterSpec.builder(TypeName type, String name, Modifier... modifiers)
复制代码
```

给参数设置其Class,以及参数名称,和修饰符.

通常来说参数的构成包括:参数的类型(Class)、参数的名称（name）、修饰符（modifiers）、注解（Annotation）

除了builder方法初始化类型、以及名称、修饰符之外，其余可以通过如下方法进行设置：

#### 添加修饰符

```
.addModifiers(Modifier modifier)
复制代码
```

#### 添加注解

```
addAnnotation(TypeName name) 
复制代码
```

添加修饰符、注解具体使用可参考通用方法。

## JavaPoet生成注解

在JavaPoet创建类、成员变量、方法参数、方法，都会用到注解。

如果使用不包含属性的注解可以直接通过

```
   .addAnnotation(TypeName name)
复制代码
```

直接传入TypeName或者Class进行设置。

如果使用的注解包含属性，并且不止一个时，这时候就需要生成AnnotationSpec来解决，下面简单了解下AnnotationSpec。

#### 初始化AnnotationSpec

```
AnnotationSpec.builder(ClassName type)
复制代码
```

可以发现初始化，只需传入ClassName或者Class即可。

#### 设置属性

```
addMember(String name, String format, Object... args)
复制代码
```

使用addMember可以设置注解的属性值，name对应的就是属性名称，format的内容即属性体，同样方法体的格式化在这里也是适用的。

## JavaPoet如何生成代码

如果上述内容你已经看完，那么恭喜你，你已经明白JavaPoet的意图，但是现在的你，还差最后一步，即如何生成代码。

JavaPoet中负责生成的类是JavaFile

```
JavaFile.builder(String packageName, TypeSpec typeSpec)
复制代码
```

JavaFile通过向build方法传入PackageName（Java文件的包名）、TypeSpec（生成的内容）生成。

#### 打印结果

```
javaFile.writeTo(System.out)
复制代码
```

生成的内容会输出到控制台中

#### 生成文件

```
javaFile.writeTo（File file）
复制代码
```

生成的内容会以java文件的方式，存放到你传入File文件的位置

## 结束语

当你读完了本文，如果你产生下面的想法：

1. JavaPoet原来还可以这样
2. JavaPoet编写过程为什么那么流畅，原来Java文件也可以用编程的方式生成
3. JavaPoet可不可以改进我的编码流程，提升效率

那么说明你已经对JavaPoet感兴趣了，可以自己动手尝试一下，感受下JavaPoet的魅力。

最后贴一张,JavaPoet秘籍,有了它会很好的帮助你使用JavaPoet.

![](.\png\JavaPoet.png)