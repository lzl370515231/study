# 自动装箱和拆箱

## 概念

- 自动装箱

  Java 自动将原始类型值转换成对应的对象。

- 自动拆箱

  将对象（Integer）转换成原始类型（int）值

## 元素

| 原始类型 | byte | short | char      | int     | long | float | double | boolean |
| -------- | ---- | ----- | --------- | ------- | ---- | ----- | ------ | ------- |
| 对象类   | Byte | Short | Character | Integer | Long | Float | Double | Boolean |

## 作用

自动装箱和自动拆箱是简化了基本数据类型和相对应对象的转化步骤，节省了常用数值的内存开销和创建对象的开销，提高了效率。

## 原理

自动装箱时编译器调用valueOf将原始类型值转换成对象，同时自动拆箱时，编译器通过调用类似intValue(),doubleValue()这类的方法将对象转换成原始类型值。

### 源码

```java
public static Integer valueOf(int i) {
	//判断i是否在-128和127之间，存在则从IntegerCache中获取包装类的实例，否则new一个新实例
	if (i >= IntegerCache.low && i <= IntegerCache.high)
		return IntegerCache.cache[i + (-IntegerCache.low)];
	return new Integer(i);
}

//使用亨元模式，来减少对象的创建（亨元设计模式大家有必要了解一下，我认为是最简单的设计模式，也许大家经常在项目中使用，不知道他的名字而已）
private static class IntegerCache {
	static final int low = -128;
	static final int high;
	static final Integer cache[];

	//静态方法，类加载的时候进行初始化cache[],静态变量存放在常量池中
	static {
		// high value may be configured by property
		int h = 127;
		String integerCacheHighPropValue =
                sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
		if (integerCacheHighPropValue != null) {
			try {
				int i = parseInt(integerCacheHighPropValue);
				i = Math.max(i, 127);
				// Maximum array size is Integer.MAX_VALUE
				h = Math.min(i, Integer.MAX_VALUE - (-low) -1);
			} catch( NumberFormatException nfe) {
				// If the property cannot be parsed into an int, ignore it.
			}
		}
		high = h;

		cache = new Integer[(high - low) + 1];
		int j = low;
		for(int k = 0; k < cache.length; k++)
			cache[k] = new Integer(j++);

		// range [-128, 127] must be interned (JLS7 5.1.7)
		assert IntegerCache.high >= 127;
	}
	private IntegerCache() {}
}
```

首先判断 i 值是否在-128和127之间，如果在-128和127之间则直接从IntegerCache.cache缓存中获取指定数字的包装类；不存在则new出一个新的包装类。IntegerCache内部实现了一个Integer的静态常量数组，在类加载的时候，执行static静态块进行初始化-128到127之间的Integer对象，存放到cache数组中。cache属于常量，存放在java的方法区中。

### 8种自动装箱类代码

```java
//boolean原生类型自动装箱成Boolean
public static Boolean valueOf(boolean b) {
	return (b ? TRUE : FALSE);
}

//byte原生类型自动装箱成Byte
public static Byte valueOf(byte b) {
	final int offset = 128;
	return ByteCache.cache[(int)b + offset];
}

//byte原生类型自动装箱成Byte
public static Short valueOf(short s) {
	final int offset = 128;
	int sAsInt = s;
	if (sAsInt >= -128 && sAsInt <= 127) { // must cache
		return ShortCache.cache[sAsInt + offset];
	}
	return new Short(s);
}

//char原生类型自动装箱成Character
public static Character valueOf(char c) {
	if (c <= 127) { // must cache
		return CharacterCache.cache[(int)c];
	}
	return new Character(c);
}
    
//int原生类型自动装箱成Integer
public static Integer valueOf(int i) {
	if (i >= IntegerCache.low && i <= IntegerCache.high)
		return IntegerCache.cache[i + (-IntegerCache.low)];
	return new Integer(i);
}

//int原生类型自动装箱成Long
public static Long valueOf(long l) {
	final int offset = 128;
	if (l >= -128 && l <= 127) { // will cache
		return LongCache.cache[(int)l + offset];
	}
	return new Long(l);
}

//double原生类型自动装箱成Double
public static Double valueOf(double d) {
	return new Double(d);
}

//float原生类型自动装箱成Float
public static Float valueOf(float f) {
	return new Float(f);
}
```

1. 只有double和float的自动装箱代码没有使用缓存，每次都是new 新的对象，其它的6种基本类型都使用了缓存策略。
2. 使用缓存策略是因为，缓存的这些对象都是经常使用到的（如字符、-128至127之间的数字），防止每次自动装箱都创建一次对象的实例。
3. 而double、float是浮点型的，没有特别的热的（经常使用到的）数据的，缓存效果没有其它几种类型使用效率高。