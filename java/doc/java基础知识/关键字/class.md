# class 类



## 内部类

### 内部类基础

#### 概念

在Java中，可以将一个类定义在另一个类里面或者一个方法里面，这样的类称为内部类。广泛意义上的内部类一般来说包括这四种：成员内部类、局部内部类、匿名内部类和静态内部类。

#### 成员内部类

最普通的内部类

```java
class Circle {
    double radius = 0;

    public Circle(double radius) {
        this.radius = radius;
    }

    class Draw {     //内部类
        public void drawSahpe() {
            System.out.println("drawshape");
        }
    }
}
```

成员内部类可以无条件访问外部类的所有成员属性和成员方法（包括private成员和静态成员）。

```java
class Circle {
    private double radius = 0;
    public static int count =1;
    public Circle(double radius) {
        this.radius = radius;
    }

    class Draw {     //内部类
        public void drawSahpe() {
            System.out.println(radius);  //外部类的private成员
            System.out.println(count);   //外部类的静态成员
        }
    }
}
```

不过要注意的是，当成员内部类拥有和外部类同名的成员变量或者方法时，会发生隐藏现象，即默认情况下访问的是成员内部类的成员。如果要访问外部类的同名成员，需要以下面的形式进行访问：

```java
外部类.this.成员变量
外部类.this.成员方法
```

虽然成员内部类可以无条件地访问外部类的成员，而外部类想访问成员内部类的成员却不是这么随心所欲了。在外部类中如果要访问成员内部类的成员，**必须先创建一个成员内部类的对象**，再通过指向这个对象的引用来访问：

```java
class Circle {
    private double radius = 0;
 
    public Circle(double radius) {
        this.radius = radius;
        getDrawInstance().drawSahpe();   //必须先创建成员内部类的对象，再进行访问
    }
     
    private Draw getDrawInstance() {
        return new Draw();
    }
     
    class Draw {     //内部类
        public void drawSahpe() {
            System.out.println(radius);  //外部类的private成员
        }
    }
}
```

成员内部类是依附外部类而存在的，也就是说，如果要创建成员内部类的对象，前提是必须存在一个外部类的对象。创建成员内部类对象。

```java
public class Test {
    public static void main(String[] args)  {
        //第一种方式：
        Outter outter = new Outter();
        Outter.Inner inner = outter.new Inner();  //必须通过Outter对象来创建

        //第二种方式：
        Outter.Inner inner1 = outter.getInnerInstance();
    }
}
 
class Outter {
    private Inner inner = null;
    public Outter() {
         
    }
     
    public Inner getInnerInstance() {
        if(inner == null)
            inner = new Inner();
        return inner;
    }
      
    class Inner {
        public Inner() {
             
        }
    }
}
```

#### 局部内部类

局部内部类是定义在一个方法或者一个作用域里面的类，它和成员内部类的区别在于局部内部类的访问仅限于方法内或者该作用域内。

```java
class People{
    public People() {
         
    }
}
 
class Man{
    public Man(){
         
    }
     
    public People getWoman(){
        class Woman extends People{   //局部内部类
            int age =0;
        }
        return new Woman();
    }
}
```

###### 注意

局部内部类就像是方法里面的一个局部变量一样，**是不能有public、protected、private以及static修饰符**的。

#### 匿名内部类

```java
bt.setOnClickListener(new OnClickListener(){
	@Override
	public void onClick(View v){
		//TODO Auto-generated method stub
	}
});
```

使用匿名内部类能够在实现父类或者接口中的方法情况下同时产生一个相应的对象，但是**前提是这个父类或者接口必须先存在才能这样使用**。

另一种写法

```java
private void setListener()
{
    scan_bt.setOnClickListener(new Listener1());       
}
 
class Listener1 implements View.OnClickListener{
    @Override
    public void onClick(View v) {
    // TODO Auto-generated method stub
             
    }
}
```

###### 注

匿名内部类也是不能有访问修饰符和static修饰符的。

##### 注意事项

1. 使用匿名内部类时，我们必须是继承一个类或者实现一个接口，但是两者不可兼得，同时也只能继承一个类或者实现一个接口。
2. 匿名内部类中是不能定义构造函数的。
3. 匿名内部类中不能存在任何的静态成员变量和静态方法。
4. 匿名内部类为局部内部类，所以局部内部类的所有限制同样对匿名内部类生效。
5. 匿名内部类不能是抽象的，它必须要实现继承的类或者实现的接口的所有抽象方法。

##### 匿名内部类初始化

我们一般都是利用构造器来完成某个实例的初始化工作的，但是匿名内部类是没有构造器的！那怎么来初始化匿名内部类呢？使用构造代码块！利用构造代码块能够达到为匿名内部类创建一个构造器的效果。

```java
public class OutClass {
    public InnerClass getInnerClass(final int age,final String name){
        return new InnerClass() {
            int age_ ;
            String name_;
            //构造代码块完成初始化工作
            {
                if(0 < age && age < 200){
                    age_ = age;
                    name_ = name;
                }
            }
            public String getName() {
                return name_;
            }
            
            public int getAge() {
                return age_;
            }
        };
    }
    
    public static void main(String[] args) {
        OutClass out = new OutClass();
        
        InnerClass inner_1 = out.getInnerClass(201, "chenssy");
        System.out.println(inner_1.getName());
        
        InnerClass inner_2 = out.getInnerClass(23, "chenssy");
        System.out.println(inner_2.getName());
    }
}
```

#### 静态内部类

静态内部类是不需要依赖于外部类的，这点和类的静态成员属性有点类似，并且它不能使用外部类的非static成员变量或者方法。

```java
public class Test {
    public static void main(String[] args)  {
        Outter.Inner inner = new Outter.Inner();
    }
}
 
class Outter {
    public Outter() {
         
    }
     
    static class Inner {
        public Inner() {
             
        }
    }
}
```

#### 引用

##### 引用内部类变量

this.x

##### 引用外部类同名变量

Outter.this.x

### 深入理解内部类

#### 成员内部类可以无条件访问外部类的成员

编译器在进行编译的时候，会将成员内部类单独编译成一个字节码文件。

```java
public class Outter {
    private Inner inner = null;
    public Outter() {
         
    }
     
    public Inner getInnerInstance() {
        if(inner == null)
            inner = new Inner();
        return inner;
    }
      
    protected class Inner {
        public Inner() {
             
        }
    }
}
```

编译之后，出现了两个字节码文件：

![](.\..\png\class_内部类.jpg)

反编译Outter$Inner.class 文件得到下面信息：

```shell
>javap -v Outter$Inner
Compiled from "Outter.java"
public class com.cxh.test2.Outter$Inner extends java.lang.Object
  SourceFile: "Outter.java"
  InnerClass:
   #24= #1 of #22; //Inner=class com/cxh/test2/Outter$Inner of class com/cxh/tes
t2/Outter
  minor version: 0
  major version: 50
  Constant pool:
const #1 = class        #2;     //  com/cxh/test2/Outter$Inner
const #2 = Asciz        com/cxh/test2/Outter$Inner;
const #3 = class        #4;     //  java/lang/Object
const #4 = Asciz        java/lang/Object;
const #5 = Asciz        this$0;
const #6 = Asciz        Lcom/cxh/test2/Outter;;
const #7 = Asciz        <init>;
const #8 = Asciz        (Lcom/cxh/test2/Outter;)V;
const #9 = Asciz        Code;
const #10 = Field       #1.#11; //  com/cxh/test2/Outter$Inner.this$0:Lcom/cxh/t
est2/Outter;
const #11 = NameAndType #5:#6;//  this$0:Lcom/cxh/test2/Outter;
const #12 = Method      #3.#13; //  java/lang/Object."<init>":()V
const #13 = NameAndType #7:#14;//  "<init>":()V
const #14 = Asciz       ()V;
const #15 = Asciz       LineNumberTable;
const #16 = Asciz       LocalVariableTable;
const #17 = Asciz       this;
const #18 = Asciz       Lcom/cxh/test2/Outter$Inner;;
const #19 = Asciz       SourceFile;
const #20 = Asciz       Outter.java;
const #21 = Asciz       InnerClasses;
const #22 = class       #23;    //  com/cxh/test2/Outter
const #23 = Asciz       com/cxh/test2/Outter;
const #24 = Asciz       Inner;
 
{
final com.cxh.test2.Outter this$0;
 
public com.cxh.test2.Outter$Inner(com.cxh.test2.Outter);
  Code:
   Stack=2, Locals=2, Args_size=2
   0:   aload_0
   1:   aload_1
   2:   putfield        #10; //Field this$0:Lcom/cxh/test2/Outter;
   5:   aload_0
   6:   invokespecial   #12; //Method java/lang/Object."<init>":()V
   9:   return
  LineNumberTable:
   line 16: 0
   line 18: 9
 
  LocalVariableTable:
   Start  Length  Slot  Name   Signature
   0      10      0    this       Lcom/cxh/test2/Outter$Inner;
 
 
}
```

第38行内容

```shell
final com.cxh.test2.Outter this$0;
```

这行是一个指向外部类对象的指针。编译器会默认为成员内部类添加了一个指向外部类对象的引用，那么这个引用是如何赋初值的呢？下面接着看内部类的构造器：

```shell
public com.cxh.test2.Outter$Inner(com.cxh.test2.Outter);
```

从这里可以看出，虽然我们在定义的内部类的构造器是无参构造器，编译器还是会默认添加一个参数，该参数的类型为指向外部类对象的一个引用，所以成员内部类中的Outter this&0 指针便指向了外部类对象，因此可以在成员内部类中随意访问外部类的成员。

从这里也间接说明了成员**内部类是依赖于外部类的，如果没有创建外部类的对象，则无法对Outter this&0引用进行初始化赋值，也就无法创建成员内部类的对象**了。

#### 局部内部类和匿名内部类只能访问局部final变量

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

#### 静态内部类的特殊之处

静态内部类是**不依赖于外部类**的，也就说可以在不创建外部类对象的情况下创建内部类的对象。另外，静态内部类是不持有指向外部类对象的引用的。

### 内部类的使用场景和好处

1. 每个内部类都能独立的继承一个接口的实现，所以无论外部类是否已经继承了某个(接口的)实现，对于内部类都没有影响。内部类使得多继承的解决方案变得完整。
2. 方便将存在一定逻辑关系的类组织在一起，又可以对外界隐藏。
3. 方便编写事件驱动程序。
4. 方便编写线程代码。

##### 内部类继承

```java
class Outter extends One{
	class inner extends Two{
		
	}
}
```

##### 内部类被继承

```java
class WithInner {
    class Inner{
         
    }
}
class InheritInner extends WithInner.Inner {
      
    // InheritInner() 是不能通过编译的，一定要加上形参
    InheritInner(WithInner wi) {
        wi.super(); //必须有这句调用
    }
  
    public static void main(String[] args) {
        WithInner wi = new WithInner();
        InheritInner obj = new InheritInner(wi);
    }
}
```

1. 成员内部类的引用方式必须为 Outter.Inner
2. 构造器中必须有指向外部类对象的引用，并通过这个引用调用super()。

### 常见的与内部类相关的笔试面试题

#### 内部类的创建

##### 创建静态内部类对象

外部类类名.内部类类名 xxx = new 外部类类名.内部类类名();

##### 创建成员内部类对象

外部类类名.内部类类名 xxx = 外部类对象名.new 内部类类名();

```java
public class Test{
	public static void main(String[] args){
		// 初始化Bean1
		Test test = new Test();
        Test.Bean1 bean1 = test.new Bean1();
		bean1.I++;
		// 初始化Bean2
		Test.Bean2 b2 = new Test.Bean2();
		bean2.J++;
		//初始化Bean3
		Bean bean = new Bean();
        Bean.Bean3 bean3 = bean.new Bean3();
		bean3.k++;
	}
	class Bean1{
		public int I = 0;
	}
 
	static class Bean2{
		public int J = 0;
	}
}
 
class Bean{
	class Bean3{
		public int k = 0;
	}
}
```

