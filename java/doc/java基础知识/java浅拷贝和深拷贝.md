# Java 浅拷贝和深拷贝

对象拷贝(Object Copy)就是将一个对象的属性拷贝到另一个有着相同类类型的对象中去。在程序中拷贝对象是很常见的，主要是为了在新的上下文环境中复用对象的部分或全部数据。Java中有三种类型的对象拷贝：浅拷贝(Shallow Copy)、深拷贝(Deep Copy)、延迟拷贝(Lazy Copy)

## 原理

### Clone的用法和说明

1. clone 方法将对象复制了一份并返回给调用者，一般而言，clone() 方法满足：
   - 对任何的对象x，都有x.clone() !=x 克隆对象与原对象不是同一个对象 
   - 对任何的对象x，都有x.clone().getClass()= =x.getClass()克隆对象与原对象的类型一样 
   - 如果对象x的equals()方法定义恰当，那么x.clone().equals(x)应该成立。 
2. 为了获取对象的一份拷贝，可以利用Object 类的 clone() 方法。
   - 在派生类中实现Cloneable接口。覆盖基类的clone()方法，并声明为public。
   - 在派生类的clone()方法中，调用super.clone()。

## 浅拷贝

被复制对象的所有变量都含有与原来的对象相同的值，而所有的对其他对象的引用仍然指向原来的对象。换言之，浅复制**仅仅复制所考虑**的对象，而不复制它所引用的对象。

![](.\png\java-浅拷贝.png)

### 浅拷贝的实现

```java
public class Subject {
 
   private String name; 
   public Subject(String s) { 
      name = s; 
   } 

   public String getName() { 
      return name; 
   } 

   public void setName(String s) { 
      name = s; 
   } 
}
```

```java
public class Student implements Cloneable { 
 
   // 对象引用 
   private Subject subj; 
   private String name; 
 
   public Student(String s, String sub) { 
      name = s; 
      subj = new Subject(sub); 
   } 
 
   public Subject getSubj() { 
      return subj; 
   } 
 
   public String getName() { 
      return name; 
   } 
 
   public void setName(String s) { 
      name = s; 
   } 
 
   /** 
    *  重写clone()方法 
    * @return 
    */ 
   public Object clone() { 
      //浅拷贝 
      try { 
         // 直接调用父类的clone()方法
         return super.clone(); 
      } catch (CloneNotSupportedException e) { 
         return null; 
      } 
   } 
}
```

**关键点： super.clone()**

```java
private void test1(){
    // 原始对象
    Student stud = new Student("杨充", "潇湘剑雨");
    System.out.println("原始对象: " + stud.getName() + " - " + stud.getSubj().getName());

    // 拷贝对象
    Student clonedStud = (Student) stud.clone();
    System.out.println("拷贝对象: " + clonedStud.getName() + " - " + clonedStud.getSubj().getName());

    // 原始对象和拷贝对象是否一样：
    System.out.println("原始对象和拷贝对象是否一样: " + (stud == clonedStud));
    // 原始对象和拷贝对象的name属性是否一样
    System.out.println("原始对象和拷贝对象的name属性是否一样: " + (stud.getName() == clonedStud.getName()));
    // 原始对象和拷贝对象的subj属性是否一样
    System.out.println("原始对象和拷贝对象的subj属性是否一样: " + (stud.getSubj() == clonedStud.getSubj()));

    stud.setName("小杨逗比");
    stud.getSubj().setName("潇湘剑雨大侠");
    System.out.println("更新后的原始对象: " + stud.getName() + " - " + stud.getSubj().getName());
    System.out.println("更新原始对象后的克隆对象: " + clonedStud.getName() + " - " + clonedStud.getSubj().getName());
}
```

输出结果：

```shell
2019-03-23 13:50:57.518 24704-24704/com.ycbjie.other I/System.out: 原始对象: 杨充 - 潇湘剑雨
2019-03-23 13:50:57.519 24704-24704/com.ycbjie.other I/System.out: 拷贝对象: 杨充 - 潇湘剑雨
2019-03-23 13:50:57.519 24704-24704/com.ycbjie.other I/System.out: 原始对象和拷贝对象是否一样: false
2019-03-23 13:50:57.519 24704-24704/com.ycbjie.other I/System.out: 原始对象和拷贝对象的name属性是否一样: true
2019-03-23 13:50:57.519 24704-24704/com.ycbjie.other I/System.out: 原始对象和拷贝对象的subj属性是否一样: true
2019-03-23 13:50:57.519 24704-24704/com.ycbjie.other I/System.out: 更新后的原始对象: 小杨逗比 - 潇湘剑雨大侠
2019-03-23 13:50:57.519 24704-24704/com.ycbjie.other I/System.out: 更新原始对象后的克隆对象: 杨充 - 潇湘剑雨大侠
```

## 深拷贝

被复制对象的所有变量都含有与原来的对象相同的值，除去那些引用其他对象的变量。那些引用其他对象的变量将指向被复制过的**新对象**，而不再是原有的那些被引用的对象。换言之，深复制把要复制的对象所引用的对象都复制了一遍。

![](.\png\java-深拷贝.png)

### 深拷贝实现

- 序列化（Serializable）这个对象，再反序列化回来，就可以得到这个新的对象。
- 继续利用 clone() 方法，既然 clone() 方法

#### 使用 clone

```java
public class FatherClass implements Cloneable{
    public String name;
    public int age;
    public ChildClass child;
    
    @Override
    public Object clone(){
        try{
            //浅拷贝
            FathreClass cloneFather = (FatherClass) super.clone();
            cloneFather.child = (ChildClass) this.child.clone();
            return cloneFather;
        }catch(CloneNotSupportedException ignore){
            
        }
        return null;
    }
}

public class ChildClass implements Cloneable{
    public String name;
    public int age;
    
    @Override
    public Object clone(){
        try{
            //浅拷贝
            return super.clone();
        }catch(CloneNotSupportedException ignore){
            
        }
        return null;
    }
}
```

**最重要的代码就在 FatherClass.clone()中，它对其内的 child，在进行了一次 clone () 操作**。

对child 也进行了一次拷贝，实则是对 ChildClass进行的浅拷贝，但是对于 FatherClass 而言，则是一次深拷贝。

#### 使用序列化

- 确保对象图中的所有类都是可序列化的
- 

```java
public Object deepClone() throws IOException, OptionalDataException, 
	ClassNotFoundException {
    	// 将对象写到流里
    	OutputStream bo = new ByteArrayOutputStream();
    	//OutputStream op = new ObjectOutputStream();
    	ObjectOutputStream oo = new ObjectOutputStream(bo);
    	oo.writeObject(this);

    	// 从流里读出来
    	InputStream bi = new ByteArrayInputStream
            (((ByteArrayOutputStream)bo).toByteArray());
    	ObjectInputStream oi = new ObjectInputStream(bi);
    	return (oi.readObject());
	}

Book book2 = (Book) b1.deepClone();
```

#### new

```java
public class Student implements Cloneable { 
   // 对象引用
   private Subject subj;
   private String name;
   
   public Student(String s, String sub) {
      name = s;
      subj = new Subject(sub);
   }
   public Subject getSubj() {
      return subj;
   }
   public String getName() {
      return name;
   }
   public void setName(String s) {
      name = s;
   } 
   /** 
    * 重写clone()方法 
    * @return 
    */ 
   public Object clone() {
      // 深拷贝，创建拷贝类的一个新对象，这样就和原始对象相互独立
      Student s = new Student(name, subj.getName());
      return s;
   }
}
```

## 延迟拷贝

延迟拷贝是浅拷贝和深拷贝的一个组合，实际上很少会使用。 当最开始拷贝一个对象时，会使用速度较快的浅拷贝，还会使用一个计数器来记录有多少对象共享这个数据。当程序想要修改原始的对象时，它会决定数据是否被共享（通过检查计数器）并根据需要进行深拷贝。 

延迟拷贝从外面看起来就是深拷贝，但是只要有可能它就会利用浅拷贝的速度。当原始对象中的引用不经常改变的时候可以使用延迟拷贝。由于存在计数器，效率下降很高，但只是常量级的开销。而且, 在某些情况下, 循环引用会导致一些问题。