# Static

## 概念

Java把内存分为栈内存和堆内存，其中栈内存用来存放一些基本类型的变量、数组和对象的引用，堆内存主要存放一些对象。在JVM加载一个类的时候，若**该类存在static修饰的成员变量和成员方法，则会为这些成员变量和成员方法在固定的位置开辟一个固定大小的内存区域**，有了这些“固定”的特性，那么JVM就可以非常方便地访问他们。同时如果静态的成员变量和成员方法不出作用域的话，它们的句柄都会保持不变。同时static所蕴含“静态”的概念表示着它是不可恢复的，即在那个地方，你修改了，他是不会变回原样的，你清理了，他就不会回来了。

同时被static修饰的成员变量和成员方法是独立于该类的，它不依赖于某个特定的实例变量，也就是说它被该类的所有实例共享。所有实例的引用都指向同一个地方，任何一个实例对其的修改都会导致其他实例的变化。

被static关键字修饰的方法或者变量不需要依赖于对象来进行访问，只要类被加载了，就可以通过类名去进行访问。

## class程序加载流程

![](.\..\png\static_class_load.png)

## Java 内存相关原理

### 寄存器

JVM内部虚拟寄存器，存取速度非常快，程序不可控制。

### 栈

 保存局部变量的值，包括：a.用来保存基本数据类型的值；b.保存类的 **实例** ，即堆区 **对象** 的引用(指针)。也可以用来保存加载方法时的帧。

------

### 堆

用来存放动态产生的数据，比如new出来的 **对象** 。注意创建出来的对象只包含属于各自的成员变量，并不包括成员方法。因为同一个类的对象拥有各自的成员变量，存储在各自的堆中，但是他们共享该类的方法，并不是每创建一个对象就把成员方法复制一次。

### 常量池

JVM为每个已加载的类型维护一个常量池，常量池就是这个类型用到的常量的一个有序集合。包括直接常量(基本类型，String)和对其他类型、方法、字段的 **符号引用(1)** 。池中的数据和数组一样通过索引访问。由于常量池包含了一个类型所有的对其他类型、方法、字段的符号引用，所以常量池在Java的动态链接中起了核心作用。 **常量池存在于堆中** 。

###  代码段

用来存放从硬盘上读取的源程序代码。

### 全局数据段

用来存放static定义的静态成员或全局变量。分配该区时内存全部清0，结果变量的初始化为0。

![](.\..\png\static_java_memory.png)

### Java内存中的几点需要注意

1. 无论是普通类型的变量还是引用类型的变量(俗称实例)，都可以作为局部变量，他们都可以出现在栈中。只不过普通类型的变量在栈中直接保存它所对应的值，而引用类型的变量保存的是一个指向堆区的指针，通过这个指针，就可以找到这个实例在堆区对应的对象。因此，普通类型变量只在栈区占用一块内存，而引用类型变量要在栈区和堆区各占一块内存。

2. 分清什么是实例什么是对象。Class a= new Class();此时a叫实例，而不能说a是对象。实例在栈中，对象在堆中，操作实例实际上是通过实例的指针间接操作对象。多个实例可以指向同一个对象。

3. 栈中的数据和堆中的数据销毁并不是同步的。方法一旦结束，栈中的局部变量立即销毁，但是堆中对象不一定销毁。因为可能有其他变量也指向了这个对象，直到栈中没有变量指向堆中的对象时，它才销毁，而且还不是马上销毁，要等垃圾回收扫描时才可以被销毁。

4. 以上的栈、堆、代码段、数据段等等都是相对于应用程序而言的。每一个应用程序都对应唯一的一个JVM实例，每一个JVM实例都有自己的内存区域，互不影响。并且这些内存区域是所有线程共享的。这里提到的栈和堆都是整体上的概念，这些堆栈还可以细分。

5. 类的成员变量在不同对象中各不相同，都有自己的存储空间(成员变量在堆中的对象中)。而类的方法却是该类的所有对象共享的，只有一套，对象使用方法的时候方法才被压入栈，方法不使用则不占用内存。

6. 栈中的数据可以共享

   int a = 3; int b = 3； 编译器先处理int a = 3；首先它会在栈中创建一个变量为a的引用，然后查找有没有字面值为3的地址，没找到，就开辟一个存放3这个字面值的地址，然后将a指向3的地址。接着处理int b = 3；在创建完b的引用变量后，由于在栈中已经有3这个字面值，便将b直接指向3的地址。这样，就出现了a与b同时均指向3的情况。

   特别注意的是，这种字面值的引用与类对象的引用不同。假定两个类对象的引用同时指向一个对象，如果一个对象引用变量修改了这个对象的内部状态，那么另一个对象引用变量也即刻反映出这个变化。相反，通过字面值的引用来修改其值，不会导致另一个指向此字面值的引用的值也跟着改变的情况。如上例，我们定义完a与b的值后，再令a=4；那么，b不会等于4，还是等于3。在编译器内部，遇到a=4；时，它就会重新搜索栈中是否有4的字面值，如果没有，重新开辟地址存放4的值；如果已经有了，则直接将a指向这个地址。因此a值的改变不会影响到b的值。（**普通变量和引用变量的区别**）
   

   

## 注意

由于他在类加载的时候就存在了，它不依赖于任何实例，所以static方法必须实现，也就是说他不能是抽象方法abstract。

## 局限

1. 它只能调用static变量。
2. 它只能调用static方法。
3. 不能以任何形式引用this、super。
4. static变量在定义时必须要进行初始化，且初始化时间要早于非静态变量。

## static常用方式

### static 方法

- static 修饰的方法我们称之为静态方法，我们通过类名对其进行直接调用。由于他在类加载的时候就存在了，它不依赖于任何实例，所以 static 方法必须实现，也就是说他不能是抽象方法 abstract。
- static方法一般称作静态方法，由于静态方法不依赖于任何对象就可以进行访问，因此对于静态方法来说，是没有this的，因为它不依附于任何对象。
- 由于这个特性，在静态方法中不能访问类的非静态成员变量和非静态成员方法，因为非静态成员方法/变量都是必须依赖具体的对象才能够被调用。
- 虽然在静态方法中不能访问非静态成员方法和非静态成员变量，但是在非静态成员方法中是可以访问静态成员方法/变量的。

```java
public MyObject{
	private static String str1 = "staticProperty";
	public static void print(){
		System.out.println(str1);
		System.out.println(str2);	//非法
	}
}
```

### static 变量

static变量也称作静态变量，静态变量和非静态变量的区别是：静态变量被所有的对象所共享，在内存中只有一个副本，它当且仅当在类初次加载时会被初始化。而非静态变量是对象所拥有的，在创建对象的时候被初始化，存在多个副本，各个对象拥有的副本互不影响。

static成员变量的初始化顺序按照定义的顺序进行初始化。

#### 静态变量与实例变量的区别：

静态变量是随着类加载时被完成初始化的，它在内存中仅有一个，且JVM也只会为它分配一次内存，同时类所有的实例都共享静态变量，可以直接通过类名来访问它。

但是实例变量则不同，它是伴随着实例的，每创建一个实例就会产生一个实例变量，它与该实例同生共死。

### static代码块

static块可以置于类中的任何地方，类中可以有多个static块。在类初次被加载的时候，会按照static块的顺序来执行每个static块，并且只会执行一次。

```java
class Person{
	private static Date startDate,endDate;
	static{
		startDate = Date.valueOf("1946");
		endDate = Date.valueOf("1964");
	}
}
```

#### 优化程序

```java
class Person{
	private Date birthDate;
	
	boolean isBornBoomer(){
		Date startDate = Date.valueOf("1946");
		Date endDate = Date.vauleOf("1964");
		....
	}
}
```

每次isBornBoomer被调用的时候，都会生成 startDate 和 birthDate 两个对象，造成了空间浪费，改成这样效率会更好：

```java
class Person{
	private static Date startDate,endDate;
	static{
		startDate = Date.valueOf("1946");
		endDate = Date.valueOf("1964");
	}
}
```

## static 误区

### static关键字会改变类中成员的访问权限

与C/C++中的static不同，Java中的static关键字不会影响到变量或者方法的作用域。在Java中能够影响到访问权限的只有private、public、protected（包括包访问权限）这几个关键字。

### 能通过this访问静态成员变量吗？

静态成员变量虽然独立于对象，但是不代表不可以通过对象去访问，所有的静态方法和静态变量都可以通过对象访问（只要访问权限足够）。

### static能作用于局部变量吗？

在C/C++中static是可以作用域局部变量的，但是在Java中切记：**static是不允许用来修饰局部变量**。

## static 导致的内存泄漏

在dalvik虚拟机中，static变量所指向的内存引用，如果不把它设置为null，GC是永远不会回收这个对象的。

### 非静态内部类的静态引用

如下：

```java
public class SecondActivity extends Activity{
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			SecondActivity.this.finish();
			this.removeMessages(0);
		}
	};
	private static Haha haha;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		haha = new Haha();
		mHandler.sendEmptyMessageDelayed(0,2000);
	}
	class Haha{}
}
```

**非静态内部类的静态引用**。然后在2秒之后我们要finish掉这个activity，会造成什么问题呢？我们知道，**内部类和外部类之间是相互持有引用的**，SecondActivity实例持有了haha的引用，但这里haha是用static修饰的，上面说了，虚拟机不会回收haha这个对象，从而导致SecondActivity实例也得不到回收，造成内存溢出。

### 单例

单例模式的特点就是它的生命周期和Application一样。单例也是用了其static属性，很多单例，往往需要用到context对象，而又是通过传值的方式获得，比如：

```java
public class SingleInstanceF{
	private static SingleInstanceF single;
	private Context context;
	
	private SingleInstanceF(Context context){
		this.context = context;
	}
	
	public static SingleInstanceF getInstance(Context context){
		if(single == null){
			single = new SingleInstanceF(context);
		}
		return single;
	}
}
```

在来一个Activity来用它，context传入一个this，再2秒之后关闭Activity。

```java
public class ThirdActivity extends Activity{
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			ThirdActivity.this.finish();
			this.removeMessage(0);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SingleInstanceF instanceF = SingleInstanceF.getInstance(this);
		mHandler.sendEmptyMessageDelayed(0,2000);
	}
}
```

怎么办呢，还是像上面那样，把静态对象设置为null，或者我们传入context的时候，别传this了，this可是当前Acitvity啊，传Application Context即可。但是不是都可以传Application Context呢，明显不是，有的事是Application Context干不了的，这个得看具体情况而定。

## 常见笔试面试题

1. 这段代码的输出结果是？

   ```java
   public class Test extends Base{
       static{
           System.out.println("test static");
       }
       public Test(){
           System.out.println("test constructor");
       }
       public static void main(String[] args) {
           new Test();
       }
   }
    
   class Base{
       static{
           System.out.println("base static");
       }
       public Base(){
           System.out.println("base constructor");
       }
   }
   ```

   输出结果

   ```java
   base static
   test static
   base constructor
   test constructor
   ```

   在执行开始，先要寻找到main方法，因为main方法是程序的入口，但是在执行main方法之前，必须先加载Test类，而在加载Test类的时候发现Test类继承自Base类，因此会转去先加载Base类，在加载Base类的时候，发现有static块，便执行了static块。在Base类加载完成之后，便继续加载Test类，然后发现Test类中也有static块，便执行static块。在加载完所需的类之后，便开始执行main方法。在main方法中执行new Test()的时候会先调用父类的构造器，然后再调用自身的构造器。因此，便出现了上面的输出结果。

   

2. 这段代码的输出结果是？

   1. 

   ```java
   public class Test {
       Person person = new Person("Test");
       static{
           System.out.println("test static");
       }
       public Test() {
           System.out.println("test constructor");
       }
       public static void main(String[] args) {
           new MyClass();
       }
   }
    
   class Person{
       static{
           System.out.println("person static");
       }
       public Person(String str) {
           System.out.println("person "+str);
       }
   }
   
   class MyClass extends Test {
       Person person = new Person("MyClass");
       static{
           System.out.println("myclass static");
       }
       public MyClass() {
           System.out.println("myclass constructor");
       }
   }
   ```

   输出结果

   ```java
   test static
   myclass static
   person static
   person Test
   test constructor
   person MyClass
   myclass constructor
   ```

   首先加载Test类，因此会执行Test类中的static块。接着执行new MyClass()，而MyClass类还没有被加载，因此需要加载MyClass类。在加载MyClass类的时候，发现MyClass类继承自Test类，但是由于Test类已经被加载了，所以只需要加载MyClass类，那么就会执行MyClass类的中的static块。在加载完之后，就通过构造器来生成对象。而**在生成对象的时候，必须先初始化父类的成员变量**，因此会执行Test中的Person person = new Person()，而Person类还没有被加载过，因此会先加载Person类并执行Person类中的static块，接着执行父类的构造器，完成了父类的初始化，然后就来初始化自身了，因此会接着执行MyClass中的Person person = new Person()，最后执行MyClass的构造器。

3. 这段代码输出结果是？

   ```java
   public class Test {
       static{
           System.out.println("test static 1");
       }
       public static void main(String[] args) {
       }
       static{
           System.out.println("test static 2");
       }
   }
   ```

   输出结果

   ```java
   test static 1
   test static 2
   ```

   static块可以出现类中的任何地方（只要不是方法内部，记住，任何方法内部都不行），并且执行是按照static块的顺序执行的。