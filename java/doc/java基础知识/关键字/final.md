# final

## 原因

不想被改变的原因有两个：效率、设计。

## 常量

### 编译期常量

**永远不可改变**。对于编译期常量，它在类加载的过程就已经完成了初始化，所以当类加载完成后是不可更改的，编译期可以将它代入到任何用到它的计算式中。只能使用**基本类型**，而且必须要在**定义时进行初始化**。

### 运行期常量

**希望它不会被改变**。有些变量，我们希望它可以根据对象的不同而表现不同，但同时又不希望它被改变，这个时候我们就可以使用运行期常量。对于运行期常量，它既可是基本数据类型，也可是引用数据类型。基本数据类型不可变的是其内容，而引用数据类型不可变的是其引用，引用所指定的对象内容是可变的。

## 基本用法

final关键字可以用来修饰类、方法和变量（包括成员变量和局部变量）。

### 修饰类

用final修饰一个类时，表明这个类不能被继承。也就是说，如果一个类你永远不会让他被继承，就可以用final进行修饰。final类中的成员变量可以根据需要设为final，但是要注意final类中的所有成员方法都会被隐式地指定为final方法。

#### 特别说明

在使用final修饰类的时候，要注意谨慎选择，除非这个类真的在以后不会用来继承或者出于安全的考虑，尽量不要将类设计为final类。

### 修饰方法

使用final方法的原因有两个：

​	第一个原因是把方法锁定，以防任何继承类修改它的含义；

​	第二个原因是效率。在早期的Java实现版本中，会将final方法转为内嵌调用。但是如果方法过于庞大，可能看不到内嵌调用带来的任何性能提升。在最近的Java版本中，不需要使用final方法进行这些优化了。

因此，如果只有在想**明确禁止该方法在子类中被覆盖的情况下才将方法设置为final**的。

#### 注

类的private方法会隐式地被指定为final方法。

### 修饰变量

对于一个final变量，如果是基本数据类型的变量，则其数值一旦在初始化之后便不能更改；如果是引用类型的变量，则在对其初始化之后便不能再让其指向另一个对象。

```
class Man{
	private final int i =0;
	public Man(){
		i = 1;	// 非法
		final Object obj = new Object();
		obj = new Object();	//非法
	}
}
```

## 深入理解 final 关键字

### final变量和普通变量的区别

#### 编译器优化

当用final作用于类的成员变量时，成员变量（注意是类的成员变量，局部变量只需要保证在使用之前被初始化赋值即可）必须在**定义时**或者**构造器中**进行初始化赋值，而且final变量一旦被初始化赋值之后，就不能再被赋值了。

```java
public class Test{
	public static void main(String[] args){
		String a ="hello2";
		final String b = "hello";
		String d = "hello";
		String c = d + 2;
		String e = d + 2;
        System.out.println((a == c));
        System.out.println((a == e));
	}
}
```

输出结果

```java
true
false
```

当final变量是基本数据类型以及String类型时，如果在编译期间能知道它的确切值，则**编译器会把它当做编译期常量使用**。也就是说在用到该final变量的地方，相当于直接访问的这个常量，不需要在运行时确定。这种和C语言中的宏替换有点像。因此在上面的一段代码中，由于变量b被final修饰，因此会被当做编译器常量，所以在使用到b的地方会直接将变量b 替换为它的  值。而对于变量d的访问却需要在运行时通过链接来进行。(**编译器进行优化**)

#### 编译器不进行优化

```java
public class Test {
    public static void main(String[] args){
        String a = "hello2";
        final String b = getHello();
        String c = b + 2;
        System.out.println((a == c));
    }
    public static String getHello() {
        return "hello";
    }
}
```

输出结果

```java
false
```

### 被final修饰的引用变量指向的对象内容可变吗？

```java
public class Test {
    public static void main(String[] args){
        final MyClass myClass = new MyClass();
        System.out.println(++myClass.i);
    }
}
 
class MyClass{
    public int i = 0;
}
```

输出结果

```java
1
```

说明引用变量被final修饰之后，虽然不能再指向其他对象，但是它指向的对象的内容是可变的。

### final 和 static

```java
public class Test{
	public static void main(String[] args)  {
        MyClass myClass1 = new MyClass();
        MyClass myClass2 = new MyClass();
        System.out.println(myClass1.i);
        System.out.println(myClass1.j);
        System.out.println(myClass2.i);
        System.out.println(myClass2.j);
 
    }
}

class MyClass {
    public final double i = Math.random();
    public static double j = Math.random();
}
```

运行结果显示，每次打印的**两个 j 值都是一样**的，**而i 的值却是不同的**。（final 只针对于实例不可变，static 是针于类对象）。

#### 成员变量

该变量一旦赋值就不能改变，我们称它为“全局常量”。可以通过类名直接访问。

#### 成员方法

可继承但不可改变。可以通过类名直接访问。

### 匿名内部类中使用的外部局部变量为什么只能是final变量

内部类并不是直接调用方法传递的参数，而是利用自身的构造器对传入的参数进行备份，自己内部方法调用的实际上时自己的属性而不是外部方法传递进来的参数。

​	在内部类中的属性和外部方法的参数两者从外表上看是同一个东西，但实际上却不是，所以他们两者是可以任意变化的，也就是说在内部类中我对属性的改变并不会影响到外部的形参，而然这从程序员的角度来看这是不可行的，毕竟站在程序的角度来看这两个根本就是同一个，如果内部类该变了，而外部方法的形参却没有改变这是难以理解和不可接受的，所以为了保持参数的一致性，就规定**使用final来避免形参的不改变**。

#### 原理

```java
public class Test{
	public static void main(String[] args){
		
	}
	public void test(final int b){
		final int a = 10;
		new Thread(){
			public void run(){
				System.out.println(a);
				System.out.println(b);
			}
		}.start();
	}
}
```

编译成两个class文件：Test.class 和 Test1.class。默认情况下，编译器会为匿名内部类和局部内部类起名为Outter1.class。默认情况下，编译器会为匿名内部类和局部内部类起名为Outterx.class（x为正整数）。

![](.\..\png\class_匿名内部类_final.png)

当test方法执行完毕之后，变量a的生命周期就结束了，而此时Thread对象的生命周期很可能还没有结束，那么在Thread的run方法中继续访问变量a就变成不可能了，但是又要实现这样的效果，怎么办呢？Java采用了 复制  的手段来解决这个问题。将这段代码的字节码反编译可以得到下面的内容：

![](.\..\png\class_匿名内部类_final_反编译.jpg)

在run方法中有一条指令：

```shell
bipush 10
```

这条指令表示将操作数10压栈，表示使用的是一个本地局部变量。这个过程是在编译期间由编译器默认进行，如果这个变量的值在编译期间可以确定，则编译器默认会在匿名内部类（局部内部类）的常量池中添加一个内容相等的字面量或直接将相应的字节码嵌入到执行字节码中。这样一来，匿名内部类使用的变量是另一个局部变量，只不过值和方法中局部变量的值相等，因此和方法中的局部变量完全独立开。



再看一个例子：

```java
public class Test{
	public static void main(String[] args){
		
	}
	public void test(final int a){
		new Thread(){
			public void run()0{
				System.out.println(a);
			}
		}.start();
	}
}
```

反编译得到：

![](.\..\png\class_匿名内部类_final_反编译_2.png)

可看到匿名内部类Test$1的构造器含有两个参数，一个是指向外部类对象的引用，一个是int型变量，很显然，这里是将变量test方法中的形参a以参数的形式传进来对匿名内部类中的拷贝（变量a的拷贝）进行赋值初始化。

**也就说如果局部变量的值在编译期间就可以确定，则直接在匿名内部里面创建一个拷贝。如果局部变量的值无法在编译期间确定，则通过构造器传参的方式来对拷贝进行初始化赋值。**

从上面可以看出，在run方法中访问的变量a根本就不是test方法中的局部变量a。这样一来就解决了前面所说的生命周期不一致的问题。但是新的问题又来了，既然在run方法中访问的变量a和test方法中的变量a不是同一个变量，当在run方法中改变变量a的值的话，会出现什么情况？

**对，会造成数据不一致性**，这样就达不到原本的意图和要求。为了解决这个问题，java编译器就限定必须将变量a限制为final变量，不允许对变量a进行更改（对于引用类型的变量，是不允许指向新的对象），这样数据不一致性的问题就得以解决了。



