# JAVA 异常

## 异常的体系结构

![](.\png\java-exception-体系结构.png)

- Error

  表示不希望被程序捕获或者是程序无法处理的错误。由 Java 虚拟机生成并抛出，大多数错误与代码编写者所执行的操作无关。

- Exception

  表示用户程序可能捕获的异常情况或者说是程序可以处理的异常。

## 异常分类

- 检查性异常

  在一定程度上这种异常的发生是可以预测的，并且一旦发生该种异常，就必须采取某种方式进行处理。这些异常在编译时不能被简单地忽略。

- 运行时异常

  运行时异常是可能被程序员避免的异常。与检查性异常相反，运行时异常可以在编译时被忽略。

- 错误

  错误不是异常，而是脱离程序员控制的问题。错误在代码中通常被忽略。例如，当栈溢出时，一个错误就发生了，它们在编译也检查不到的。

## Throwable 类中的常用方法

- getCause()

  抛出异常的原因。如果 cause 不存在或未知，则返回 null

- getMessage()

  返回异常的消息信息

- printStackTrace()

  对象的堆栈跟踪输出至错误输出流，作为字段 System.err 的值。

## 异常处理机制

### 抛出异常

#### throw

​	执行语句中抛出异常

#### throws

​	从方法中抛出的异常

### 捕获异常

#### try-catch

一旦某个 catch 捕获到匹配的异常类型，将进入异常处理代码。一经处理结束，就意味着整个 try-catch 语句结束。其他的 catch 子句不再有匹配和捕获异常类型的机会。

#### 说明

- try 块，如果没有 catch 块，则必须跟一个 finally 块

- finally 

  当在 try 块或 catch 块中遇到 return 语句时，finally 语句将在方法返回之前被执行。

#### finally 不执行情况

- finally 语句块发生异常
- 前面调用了 System.exit() 退出程序
- 程序所在线程死亡
- 关闭 CPU

## 异常链

将异常发生的原因一个传一个串起来，即把底层的异常信息传给上层，这样逐层抛出。 

```java
try{
    lowLevelOp();
}catch(LowLevelException le){
    throw (HighLevelException) new HighLevelException().initCause(le);
}
```

### 原理

当程序捕获到了一个底层异常，在处理部分选择了继续抛出一个更高级别的新异常给此方法的调用者。 这样异常的原因就会逐层传递。这样，位于高层的异常递归调用getCause()方法，就可以遍历各层的异常原因。 **这就是`Java异常链`的原理**。

### 作用

通过使用异常链，我们可以提高代码的可理解性、系统的可维护性和友好性。 

### 操作分类

- 捕获后抛出原来的异常，希望保留最新的异常抛出点

  fillStackTrace()

- 捕获后抛出新的异常，希望抛出完整的异常链

  initCause

### 使用

#### 方式一

```java
public class ReThrow {
    public static void f()throws Exception{
        throw new Exception("Exception: f()");
    }

    public static void g() throws Exception{
        try{
            f();
        }catch(Exception e){
            System.out.println("inside g()");
            throw e;
        }
    }
    public static void main(String[] args){
        try{
            g();
        }
        catch(Exception e){
            System.out.println("inside main()");
            e.printStackTrace(System.out);
        }
    }
}
```

输出：

```java
inside g()
inside main()
java.lang.Exception: Exception: f()
        //异常的抛出点还是最初抛出异常的函数f()
	at com.learn.example.ReThrow.f(RunMain.java:5)
	at com.learn.example.ReThrow.g(RunMain.java:10)
	at com.learn.example.RunMain.main(RunMain.java:21)
```

#### fillStackTrace----覆盖前边的异常抛出点（获取最新的异常抛出点）

在此抛出异常的时候进行设置 catch(Exception e){ (Exception)e.fillInStackTrace();} 

```java
public void g() throws Exception{
    try{
        f();
    }catch(Exception e){
    	System.out.println("inside g()");
        throw (Exception)e.fillInStackTrace();
    }
}
```

结果输出：

```java
inside g()
inside main()
java.lang.Exception: Exception: f()
        //显示的就是最新的抛出点
	at com.learn.example.ReThrow.g(RunMain.java:13)
	at com.learn.example.RunMain.main(RunMain.java:21)
```

#### 捕获异常后抛出新的异常（保留原来的异常信息，区别于捕获异常之后重新抛出）

- 方式1 ：Exception e = new Exception(); e.initCause(ex);
- 方式2 ：Exception e = new Exception(ex);

```java
class ReThrow {
    public void f(){
        try{
             g(); 
         }catch(NullPointerException ex){
             //方式1
             Exception e=new Exception();
             //将原始的异常信息保留下来
             e.initCause(ex);
             //方式2
             //Exception e=new Exception(ex);
             try {
    		    throw e;
    		} catch (Exception e1) {
    		    e1.printStackTrace();
    		}
         }
    }

    public void g() throws NullPointerException{
    	System.out.println("inside g()");
        throw new NullPointerException();
    }
}

public class RunMain {
    public static void main(String[] agrs) {
    	try{
            new ReThrow().f();
        }
        catch(Exception e){
            System.out.println("inside main()");
            e.printStackTrace(System.out);
        }
    }
}
```

在这个例子里面，我们先捕获NullPointerException异常，然后在抛出Exception异常，这时候如果我们不使用initCause方法将原始异常（NullPointerException）保存下来的话，就会丢失NullPointerException。只会显示Eception异常。下面我们来看结果：

```java
//没有调用initCause方法的输出
inside g()
java.lang.Exception
	at com.learn.example.ReThrow.f(RunMain.java:9)
	at com.learn.example.RunMain.main(RunMain.java:31)
//调用initCasue方法保存原始异常信息的输出
inside g()
java.lang.Exception
	at com.learn.example.ReThrow.f(RunMain.java:9)
	at com.learn.example.RunMain.main(RunMain.java:31)
Caused by: java.lang.NullPointerException
	at com.learn.example.ReThrow.g(RunMain.java:24)
	at com.learn.example.ReThrow.f(RunMain.java:6)
	... 1 more
```

使用initCause方法保存后，原始的异常信息会以Caused by的形式输出。

## Java异常的限制

1. 在覆盖方法的时候,只能抛出在基类方法的异常说明里列出的那些异常
2. 在基类构造器声明的异常,在子类必须抛出,子类的构造器可以抛出任何异常,但是必须抛出基类构造器的异常
3. 在基类和接口方法声明的异常,子类覆盖的方法可以不抛出基类和接口方法声明的异常以外的异常,但可以少或不抛出
4. 不能基于异常重载方法
5. 子类没有向上转型为基类和接口时,可以不捕获基类和接口的异常,反之.如有向上转型,必须捕获基类和接口的异常

## 自定义异常

**继承 Exception 类即可**

#### 使用自定义异常类，大体可分为以下几个步骤：

- 创建自定义异常类
- 在方法中通过 throw 关键字抛出异常对象
- 如果在当前抛出异常的方法中处理异常，可以使用 try-catch 语句捕获并处理；否则在方法的声明处通过 throws 关键字指明要抛出给方法调用者的异常，继续进行下一步操作
- 在出现异常方法的调用者中捕获并处理异常

```java
class MyException extends Exception {
	private int detail;
    MyException(int a){
        detail = a;
    }
    public String toString(){
        return "MyException ["+ detail + "]";
    }
}
public class TestMyException{
    static void compute(int a) throws MyException{
        System.out.println("Called compute(" + a + ")");
        if(a > 10){
            throw new MyException(a);
        }
        System.out.println("Normal exit!");
    }
    public static void main(String [] args){
        try{
            compute(1);
            compute(20);
        }catch(MyException me){
            System.out.println("Caught " + me);
        }
    }
}
```

## 总结

1. 处理运行时异常时，采用逻辑去合理规避同时辅助 try-catch 处理
2. 在多重 catch 块后面，可以加一个 catch(Exception) 来处理可能会被遗漏的异常
3. 对于不确定的代码，也可以加上 try-catch ，处理潜在的异常
4. 尽量去处理异常，切记只是简单的调用 printStackTrace() 去打印输出
5. 具体如何处理异常，要根据不同的业务需求和异常类型去决定
6. 尽量添加 finally 语句块去释放占用的资源

## 特别说明

在finally中改变返回值的做法是不好的，因为如果存在finally代码块，try中的return语句不会立马返回调用者，而是记录下返回值待finally代码块执行完毕之后再向调用者返回其值，然后如果在finally中修改了返回值，就会返回修改后的值。**（强烈建议将这种处理在编译器处设置为编译错误）**