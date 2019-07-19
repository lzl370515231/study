# Java的锁

## 锁的内存语义

- 锁可以让临界区互斥执行，还可以让释放锁的线程向同一个锁的线程发送消息
- 锁的释放要遵循Happens-before原则（`锁规则：解锁必然发生在随后的加锁之前`）
- 锁在Java中的具体表现是 `Synchronized` 和 `Lock`

## 锁操作

### 锁的释放

线程A释放锁后，会将共享变更操作刷新到主内存中。

![](.\..\png\java_lock_release.jpg)

### 锁的获取

线程B获取锁时，JMM会将该线程的本地内存置为无效，被监视器保护的临界区必须从主内存中读取共享变量

![](.\..\png\java_lock_acquire.jpg)

### 锁的释放与获取

- 锁获取与 volatile 读有相同的内存语义，可见volatile.md 文档
- 线程A释放一个锁，实质是线程A告知下一个获取到该锁的某个线程其已变更该共享变量
- 线程B获取一个锁，实质是线程B得到了线程A告知其(在释放锁之前)变更共享变量的消息
- 线程A释放锁，随后线程B竞争到该锁，实质是线程A通过主内存向线程B发消息告知其变更了共享变量

## 锁对象、类锁、私有锁

### 对象锁

使用 synchronized 修饰**非静态**的方法以及 synchronized(this) 同步代码块使用的锁是对象锁。

### 类锁 

使用 synchronized 修饰**静态**的方法以及 synchronized(class) 同步代码块使用的锁是类锁

### 私有锁

在类内部声明一个私有属性如private Object lock，在需要加锁的同步块使用 synchronized(lock）

特殊的instance变量

```java
private byte[] lock = new byte[0];	//特殊的instance变量
public void method(){
    synchronized(lock){
        //TODO 同步代码块
    }
}
```

#### 说明

零长度的byte数组对象创建起来将比任何对象都经济――查看编译后的字节码：生成零长度的byte[]对象只需3条操作码，而Object lock = new Object()则需要7行操作码。

### 特性

- 对象锁具有可重入性
- 当一个线程获得了某个对象的对象锁，则该线程仍然可以调用其他任何需要该对象锁的 synchronized 方法或 synchronized(this) 同步代码块
- 当一个线程访问某个对象的一个 synchronized(this) 同步代码块时，其他线程对该对象中所有其它 synchronized(this) 同步代码块的访问将被阻塞，因为访问的是同一个对象锁。
- 每个类只有一个类锁，但是类可以实例化成对象，因此每一个对象对应一个对象锁
- 类锁和对象锁不会产生竞争。
- 私有锁和对象锁也不会产生竞争。
- 使用私有锁可以减小锁的细粒度，减少由锁产生的开销。

由私有锁实现的等待/通知机制：

```java
Object lock = new Object();
//由等待方线程实现
synchronized(lock){
    while(条件不满足){
        lock.wait();
    }
}

//由通知方线程实现
synchronized(lock){
    //条件发生改变
    lock.notify();
}
```

## 锁分类

![](.\..\png\java_lock.png)

### 乐观锁 VS 悲观锁

#### 悲观锁

对于同一个数据的并发操作，悲观锁认为自己在使用数据的时候一定有别的线程来修改数据，因此在获取数据的时候会先加锁，确保数据不会被别的线程修改。Java中，synchronized关键字和Lock的实现类都是悲观锁。

#### 乐观锁

乐观锁认为自己在使用数据时不会有别的线程修改数据，所以不会添加锁，只是在更新数据的时候去判断之前有没有别的线程更新了这个数据。如果这个数据没有被更新，当前线程将自己修改的数据成功写入。如果数据已经被其他线程更新，则根据不同的实现方式执行不同的操作（例如报错或者自动重试）。

乐观锁在Java中是通过使用无锁编程来实现，最常采用的是**CAS算法**，Java原子类中的递增操作就通过CAS自旋实现的。

jdk5增加了并发包java.util.concurrent.*,其下面的类使用CAS算法实现了区别于synchronouse同步锁的一种乐观锁。

##### CAS算法（比较与交换）

CAS是一种无锁算法，CAS有3个操作数，内存值V，旧的预期值A，要修改的新值B。当且仅当预期值A和内存值V相同时，将内存值V修改为B，否则什么都不做。

就是指当两者进行比较时，如果相等，则证明共享数据没有被修改，替换成新值，然后继续往下运行；如果不相等，说明共享数据已经被修改，放弃已经所做的操作，然后重新执行刚才的操作。容易看出 CAS 操作是基于共享数据不会被修改的假设，采用了类似于数据库的commit-retry 的模式。当同步冲突出现的机会很少时，这种假设能带来较大的性能提升。

![](.\..\png\java_lock_悲观和乐观.png)

##### 乐观锁CAS缺点

- ABA问题。

  CAS需要在操作值的时候检查内存值是否发生变化，没有发生变化才会更新内存值。但是如果内存值原来是A，后来变成了B，然后又变成了A，那么CAS进行检查时会发现值没有发生变化，但是实际上是有变化的。ABA问题的解决思路就是在变量前面添加版本号，每次变量更新的时候都把版本号加一，这样变化过程就从“A－B－A”变成了“1A－2B－3A”。

  - JDK从1.5开始提供了**AtomicStampedReference**类来解决ABA问题，具体操作封装在compareAndSet()中。compareAndSet()首先检查当前引用和当前标志与预期引用和预期标志是否相等，如果都相等，则以原子方式将引用值和标志的值设置为给定的更新值。

- 循环时间长开销大。

  CAS操作如果长时间不成功，会导致其一直自旋，给CPU带来非常大的开销。

- 只能保证一个共享变量的原子操作。

  对一个共享变量执行操作时，CAS能够保证原子操作，但是对多个共享变量操作时，CAS是无法保证操作的原子性的。

  - Java从1.5开始JDK提供了**AtomicReference**类来保证引用对象之间的原子性，可以把多个变量放在一个对象里来进行CAS操作。

#### 使用场景

- 悲观锁适合写操作多的场景，先加锁可以保证写操作时数据正确。
- 乐观锁适合读操作多的场景，不加锁的特点能够使其读操作的性能大幅提升。

#### 示例

```java
// ------------------------- 悲观锁的调用方式 -------------------------
// synchronized
public synchronized void testMethod() {
	// 操作同步资源
}
// ReentrantLock
private ReentrantLock lock = new ReentrantLock(); // 需要保证多个线程使用的是同一个锁
public void modifyPublicResources() {
	lock.lock();
	// 操作同步资源
	lock.unlock();
}

// ------------------------- 乐观锁的调用方式 -------------------------
private AtomicInteger atomicInteger = new AtomicInteger();  // 需要保证多个线程使用的是同一个AtomicInteger
atomicInteger.incrementAndGet(); //执行自增1
```

java.util.concurrent包中的原子类，就是通过CAS来实现了乐观锁，那么我们进入原子类AtomicInteger的源码，看一下AtomicInteger的定义：

```java
public class AtomicInteger extends Number implements java.io.Serializable{
	private static final long serialVersionUID = 6214790243416807050L;
	
	//setup to use Unsafe.compareAndSwapInt for updates
	private static final Unsafe unsafe = Unsafe.getUnsafe();
	private static final long valueOffset;
    
    static{
        try{
            valueOffset = unsafe.objectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
        }catch(Exception e){
            throw new Error(ex);
        }
    }
    private volatile int value;
}
```

unsafe：获取并操作内存的数据

valueOffset：存储value在AtomicInteger中的偏移量

value：存储AtomicInteger的int值，该属性需要借助volatile关键字保证其在线程间是可见的。

```java
// ------------------------- JDK 8 -------------------------
// AtomicInteger 自增方法
public final int incrementAndGet() {
  return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
}

// Unsafe.class
public final int getAndAddInt(Object var1, long var2, int var4) {
  int var5;
  do {
      var5 = this.getIntVolatile(var1, var2);
  } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
  return var5;
}

// ------------------------- OpenJDK 8 -------------------------
// Unsafe.java
public final int getAndAddInt(Object o, long offset, int delta) {
   int v;
   do {
       v = getIntVolatile(o, offset);
   } while (!compareAndSwapInt(o, offset, v, v + delta));
   return v;
}
```

getAndAddInt()循环获取给定对象o中的偏移量处的值v，然后判断内存值是否等于v。如果相等则将内存值设置为 v + delta，否则返回false，继续循环进行重试，直到设置成功才能退出循环，并且将旧值返回。整个“比较+更新”操作封装在compareAndSwapInt()中，在JNI里是借助于一个CPU指令完成的，属于原子操作，可以保证多个线程都能够看到同一个变量的修改值。

##### 总结

乐观锁的使用：通过CAS算法保证数据的同步性或者 CAS算法+版本号来保证数据的同步性。

### 自旋锁 VS 适应性自旋锁

#### 自旋锁

阻塞或唤醒一个Java线程需要操作系统切换CPU状态来完成，这种状态转换需要耗费处理器时间。如果同步代码块中的内容过于简单，状态转换消耗的时间有可能比用户代码执行的时间还要长。

在许多场景中，同步资源的锁定时间很短，为了这一小段时间去切换线程，线程挂起和恢复现场的花费可能会让系统得不偿失。如果物理机器有多个处理器，能够让两个或以上的线程同时并行执行，我们就可以让后面那个请求锁的线程不放弃CPU的执行时间，看看持有锁的线程是否很快就会释放锁。

而为了让当前线程“稍等一下”，我们需让当前线程进行自旋，如果在自旋完成后前面锁定同步资源的线程已经释放了锁，那么当前线程就可以不必阻塞而是直接获取同步资源，从而避免切换线程的开销。这就是自旋锁。

![](.\..\png\java_自旋锁.png)

自旋锁本身是有缺点的，它不能代替阻塞。自旋等待虽然避免了线程切换的开销，但它要占用处理器时间。如果锁被占用的时间很短，自旋等待的效果就会非常好。反之，如果锁被占用的时间很长，那么自旋的线程只会白浪费处理器资源。所以，自旋等待的时间必须要有一定的限度，如果自旋超过了限定次数（默认是10次，可以使用-XX:PreBlockSpin来更改）没有成功获得锁，就应当挂起线程。

**自旋锁的实现原理同样也是CAS**，AtomicInteger中调用unsafe进行自增操作的源码中的do-while循环就是一个自旋操作，如果修改数值失败则通过循环来执行自旋，直至修改成功。

```java
public final int getAndAddInt(Object var1,long var2,int var4){
    int var5;
    do{
        var5 =  this.getIntVolatile(var1,var2);
    }while(!this.compareAndSwapInt(var1,var2,var5,var5+var4));
    return var5;
}
```

自旋锁在JDK1.4.2中引入，使用-XX:+UseSpinning来开启。JDK 6中变为默认开启，并且引入了自适应的自旋锁（适应性自旋锁）。

#### 适应性自旋锁

自适应意味着自旋的时间（次数）不再固定，而是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定。如果在同一个锁对象上，自旋等待刚刚成功获得过锁，并且持有锁的线程正在运行中，那么虚拟机就会认为这次自旋也是很有可能再次成功，进而它将允许自旋等待持续相对更长的时间。如果对于某个锁，自旋很少成功获得过，那在以后尝试获取这个锁时将可能省略掉自旋过程，直接阻塞线程，避免浪费处理器资源。

在自旋锁中另有三种常见的锁形式：TicketLock、CLHlock 和MCSlock。

### 无锁 VS 偏向锁 VS 轻量级锁 VS 重量级锁

| 锁状态   | 存储内容                                                | 存储内容 |
| -------- | ------------------------------------------------------- | -------- |
| 无锁     | 对象的hashCode、对象分代年龄、是否是偏向锁（0）         | 01       |
| 偏向锁   | 偏向线程ID、偏向时间戳、对象分代年龄、是否是偏向锁（1） | 01       |
| 轻量级锁 | 指向栈中锁记录的指针                                    | 00       |
| 重量级锁 | 指向互斥量（重量级锁）的指针                            | 10       |

#### 无锁

无锁没有对资源进行锁定，所有的线程都能访问并修改同一个资源，但同时只有一个线程能修改成功。

无锁的特点就是修改操作在循环内进行，线程会不断的尝试修改共享资源。如果没有冲突就修改成功并退出，否则就会继续循环尝试。如果有多个线程修改同一个值，必定会有一个线程能修改成功，而其他修改失败的线程会不断重试直到修改成功。上面我们介绍的CAS原理及应用即是无锁的实现。无锁无法全面代替有锁，但无锁在某些场合下的性能是非常高的。

#### 偏向锁

偏向锁是指**一段同步代码一直被一个线程所访问**，那么该线程会自动获取锁，降低获取锁的代价。

在大多数情况下，锁总是由同一线程多次获得，不存在多线程竞争，所以出现了偏向锁。其目标就是在只有一个线程执行同步代码块时能够提高性能。

当一个线程访问同步代码块并获取锁时，会在Mark Word里存储锁偏向的线程ID。在线程进入和退出同步块时不再通过CAS操作来加锁和解锁，而是检测Mark Word里是否存储着指向当前线程的偏向锁。引入偏向锁是为了在无多线程竞争的情况下尽量减少不必要的轻量级锁执行路径，因为轻量级锁的获取及释放依赖多次CAS原子指令，而偏向锁只需要在置换ThreadID的时候依赖一次CAS原子指令即可。

偏向锁只有遇到其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁，线程不会主动释放偏向锁。偏向锁的撤销，需要等待全局安全点（在这个时间点上没有字节码正在执行），它会首先暂停拥有偏向锁的线程，判断锁对象是否处于被锁定状态。撤销偏向锁后恢复到无锁（标志位为“01”）或轻量级锁（标志位为“00”）的状态。

偏向锁在JDK 6及以后的JVM里是默认启用的。可以通过JVM参数关闭偏向锁：-XX:-UseBiasedLocking=false，关闭之后程序默认会进入轻量级锁状态。

#### 轻量级锁

是指当锁是偏向锁的时候，被另外的线程所访问，**偏向锁就会升级为轻量级锁，其他线程会通过自旋的形式尝试获取锁，不会阻塞，从而提高性能**。

在代码进入同步块的时候，如果同步对象锁状态为无锁状态（锁标志位为“01”状态，是否为偏向锁为“0”），虚拟机首先将在当前线程的栈帧中建立一个名为锁记录（Lock Record）的空间，用于存储锁对象目前的Mark Word的拷贝，然后拷贝对象头中的Mark Word复制到锁记录中。

拷贝成功后，虚拟机将使用CAS操作尝试将对象的Mark Word更新为指向Lock Record的指针，并将Lock Record里的owner指针指向对象的Mark Word。

如果这个更新动作成功了，那么这个线程就拥有了该对象的锁，并且对象Mark Word的锁标志位设置为“00”，表示此对象处于轻量级锁定状态。

如果轻量级锁的更新操作失败了，虚拟机首先会检查对象的Mark Word是否指向当前线程的栈帧，如果是就说明当前线程已经拥有了这个对象的锁，那就可以直接进入同步块继续执行，否则说明多个线程竞争锁。

若当前只有一个等待线程，则该线程通过自旋进行等待。但是当自旋超过一定的次数，或者一个线程在持有锁，一个在自旋，又有第三个来访时，轻量级锁升级为重量级锁。

#### 重量锁

升级为重量级锁时，锁标志的状态值变为“10”，此时Mark Word中存储的是指向重量级锁的指针，此时等待锁的线程都会进入阻塞状态。



综上，偏向锁通过对比Mark Word解决加锁问题，避免执行CAS操作。而轻量级锁是通过用CAS操作和自旋来解决加锁问题，避免线程阻塞和唤醒而影响性能。重量级锁是将除了拥有锁的线程以外的线程都阻塞。

# synchronized

java中synchronized关键字是同步锁，同步锁是依赖于对象而存在的，而且每一个对象有且仅有一个同步锁。当我们调用某对象的synchronized方法时，就获取了该对象的同步锁。

## 条件

Java中每一个对象都可以作为锁。

## OS版本

在 JDK 1.6 之前，synchronized 是重量级锁，效率低下。

从 JDK 1.6 开始，synchronized 做了很多优化，如偏向锁、轻量级锁、自旋锁、适应性自旋锁、锁消除、锁粗化等技术来减少锁操作的开销。

## 状态

synchronized 同步锁一共包含四种状态：无锁、偏向锁、轻量级锁、重量级锁，它会随着竞争情况逐渐升级。synchronized 同步锁**可以升级但是不可以降级**，目的是为了提高获取锁和释放锁的效率。

## 使用方式

### 修饰代码块

### 修饰方法

### 修饰一个静态方法

### 修饰一个类



## 原理



