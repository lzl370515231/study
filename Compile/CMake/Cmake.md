## CMake

### 作用

生成 makefile 或者 project 文件，能测试编译器所支持的 C++ 特性。

### 命令行

```shell
ccmake directory
cmake directory
make
```

其中 directory 为 CMakeList.txt 所在目录

- 第一条语句用于配置编译选项，一般这一步不需要配置，直接执行第二条语句即可，但当出现错误时，这里就需要认为配置了，这一步才真正派上用场
- 第二条命令用于根据CMakeLists.txt生成Makefile文件
- 第三条命令用于执行Makefile文件，编译程序，生成可执行文件

### 语法

CMakeLists.txt 的语法比较简单，由命令、注释和空格组成，其中命令是不区分大小写的。命令由命令名称、小括号和参数组成，参数之间使用空格进行间隔。

#### 数据类型

CMake的基本数据类型是字符串，一组字符串在一起称为列表（list），例如：

\# 通过 set 命令构建一个 listVAR

set(VAR a b c)

使用语法 ${VariableName} 来访问名字为 VariableName 的变量的值（变量名区分大小写）。需要注意的是，即使在字符串中也可以使用 ${VariableName} 来访问变量的值：

set(VAR a b c)

\# 输出 VAR = a;b;c

message("VAR = ${VAR}")

使用语法 $ENV{VariableName} 来访问环境变量的值（ENV{VariableName} 则表示环境变量本身）

\# 输出环境变量 PATH 的值

message($ENV{PATH})

#### 条件控制和循环控制

条件控制命令为 if 命令

if(expression)

​    \#...

elseif(expression2)

​    \#...

else()

​    \#...

endif()

对于 if(string) 来说：

如果 string 为（不区分大小写）1、ON、YES、TRUE、Y、非 0 的数则表示真

如果 string 为（不区分大小写）0、OFF、NO、FALSE、N、IGNORE、空字符串、以 -NOTFOUND 结尾的字符串则表示假

如果 string 不符合上面两种情况，则 string 被认为是一个变量的名字。变量的值为第二条所述的各值则表示假，否则表示真。

\# 此策略（Policy）在 CMake2.8.0 才被引入

\# 因此这里需要指定最低 CMake 版本为 2.8

##### if…elseif…else…endif



##### while…endwhile



##### foreach…endforeach

```
foreach(loop_var RANGE start stop [step])
    ...
endforeach(loop_var)
```

start 表示起始数，stop 表示终止数，step 表示步长，示例：

```cmake
foreach(i RANGE 1 9 2)
    message(${i})
endforeach(i)
# 输出：13579
```



#### 常见变量

##### 预定义变量

PROJECT_SOURCE_DIR：工程的根目录
PROJECT_BINARY_DIR：运行 cmake 命令的目录，通常是 ${PROJECT_SOURCE_DIR}/build
PROJECT_NAME：返回通过 project 命令定义的项目名称
CMAKE_CURRENT_SOURCE_DIR：当前处理的 CMakeLists.txt 所在的路径
CMAKE_CURRENT_BINARY_DIR：target 编译目录
CMAKE_CURRENT_LIST_DIR：CMakeLists.txt 的完整路径
CMAKE_CURRENT_LIST_LINE：当前所在的行
CMAKE_MODULE_PATH：定义自己的 cmake 模块所在的路径，SET(CMAKE_MODULE_PATH ${PROJECT_SOURCE_DIR}/cmake)，然后可以用INCLUDE命令来调用自己的模块
EXECUTABLE_OUTPUT_PATH：重新定义目标二进制可执行文件的存放位置
LIBRARY_OUTPUT_PATH：重新定义目标链接库文件的存放位置

##### 环境变量

使用环境变量

```cmake
$ENV{Name}
```

写入环境变量

```cmake
set(ENV{Name} value) # 这里没有“$”符号
```

##### 系统信息

­CMAKE_MAJOR_VERSION：cmake 主版本号，比如 3.4.1 中的 3
­CMAKE_MINOR_VERSION：cmake 次版本号，比如 3.4.1 中的 4
­CMAKE_PATCH_VERSION：cmake 补丁等级，比如 3.4.1 中的 1
­CMAKE_SYSTEM：系统名称，比如 Linux-­2.6.22
­CMAKE_SYSTEM_NAME：不包含版本的系统名，比如 Linux
­CMAKE_SYSTEM_VERSION：系统版本，比如 2.6.22
­CMAKE_SYSTEM_PROCESSOR：处理器名称，比如 i686
­UNIX：在所有的类 UNIX 平台下该值为 TRUE，包括 OS X 和 cygwin
­WIN32：在所有的 win32 平台下该值为 TRUE，包括 cygwin

##### 主要开关选项

BUILD_SHARED_LIBS：这个开关用来控制默认的库编译方式，如果不进行设置，使用 add_library 又没有指定库类型的情况下，默认编译生成的库都是静态库。如果 set(BUILD_SHARED_LIBS ON) 后，默认生成的为动态库
CMAKE_C_FLAGS：设置 C 编译选项，也可以通过指令 add_definitions() 添加
CMAKE_CXX_FLAGS：设置 C++ 编译选项，也可以通过指令 add_definitions() 添加

```cmake
add_definitions(-DENABLE_DEBUG -DABC)	#参数之间用空格分隔
```



#### 常见命令

##### aux_source_directory

###### 作用

  查找指定目录下的所有源文件，然后将结果存进指定变量名

###### 语法

  aux_source_directory(<dir> <variable>)

###### 示例

```cmake
aux_source_directory(. DIR_SRCS)

add_executable(Demo ${DIR_SRCS})
```

###### 说明



##### 自定义搜索规则

```cmake
file(GLOB SRC_LIST "*.cpp" "protocol/*.cpp")
add_library(demo ${SRC_LIST})
# 或者
file(GLOB SRC_LIST "*.cpp")
file(GLOB SRC_PROTOCOL_LIST "protocol/*.cpp")
add_library(demo ${SRC_LIST} ${SRC_PROTOCOL_LIST})
# 或者
aux_source_directory(. SRC_LIST)
aux_source_directory(protocol SRC_PROTOCOL_LIST)
add_library(demo ${SRC_LIST} ${SRC_PROTOCOL_LIST})
```



##### add_definitions

###### 作用

添加编译选项

###### 语法



###### 示例

```cmake
add_definitions("-Wall -std=c++11")
```

###### 说明



##### target_link_libraries

###### 作用

设置 target需要链接的库。

###### 语法



###### 示例



###### 说明

##### add_executable

###### 作用

生成可执行文件

###### 语法



###### 示例

```cmake
add_executable(demo demo.cpp)
```



###### 说明

##### add_library

###### 作用

生成库文件，静态库或者动态库。明确指定包含哪些源文件。

###### 语法



###### 示例

```cmake
add_library(common STATIC util.cpp) # 生成静态库
add_library(common SHARED util.cpp) # 生成动态库或共享库
```

###### 说明

##### find_library

###### 作用

查找到指定的预编译库，并将它的路径存储在变量中。

###### 语法



###### 示例

```cmake

find_library( # Sets the name of the path variable.
              log-lib
 
              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )
```

###### 说明

默认的搜索路径为 cmake 包含的系统库，因此如果是 NDK 的公共库只需要指定库的 name 即可。



##### include_directories

###### 作用

设置包含的目录

###### 语法



###### 示例

```cmake
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_BINARY_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)
```

###### 说明

##### link_directories

###### 作用

设置链接库搜索目录

###### 语法



###### 示例

```cmake
link_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/libs
)
```

###### 说明

##### set

###### 作用

设置变量的值

###### 语法



###### 示例

```cmake
set(SRC_LIST main.cpp test.cpp)
add_executable(demo ${SRC_LIST})
```

**set追加设置变量的值**

```cmake
set(SRC_LIST main.cpp)
set(SRC_LIST ${SRC_LIST} test.cpp)
add_executable(demo ${SRC_LIST})
```

**list追加或者删除变量的值**

```cmake
set(SRC_LIST main.cpp)
list(APPEND SRC_LIST test.cpp)
list(REMOVE_ITEM SRC_LIST main.cpp)
add_executable(demo ${SRC_LIST})
```



###### 说明

##### 打印信息

###### 作用



###### 语法



###### 示例

```cmake
message(${PROJECT_SOURCE_DIR})
message("build with debug mode")
message(WARNING "this is warnning message")
message(FATAL_ERROR "this build has many error") # FATAL_ERROR 会导致编译失败
```



###### 说明

##### include

###### 作用



###### 语法



###### 示例

```cmake
include(./common.cmake) # 指定包含文件的全路径
include(def) # 在搜索路径中搜索def.cmake文件
set(CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake) # 设置include的搜索路径
```



###### 说明

##### PROJECT

###### 作用

指定项目名称

###### 语法



###### 示例



###### 说明

##### ADD_DEPENDENCIES

###### 作用

定义target依赖的其它target，确保在编译本target之前，其它的target已经被构建。

###### 语法

```cmake
ADD_DEPENDENCIES(target-name depend-target1 depend-target2 ...)
```

###### 示例



###### 说明

##### ADD_SUBDIRECTORY

###### 作用

包含子目录

###### 语法

```cmake
ADD_SUBDIRECTORY(Hello)
```

###### 示例



###### 说明

##### ENABLE_TESTING

###### 作用

`ENABLE_TESTING`指令用来控制Makefile是否构建test目标，涉及工程所有目录。语法很简单，没有任何参数，`ENABLE_TESTING()`一般放在工程的主CMakeLists.txt中。

###### 语法



###### 示例



###### 说明

##### cmake_minimum_required

###### 作用

比如:`CMAKE_MINIMUM_REQUIRED(VERSION 2.5 FATAL_ERROR)`
如果cmake版本小与2.5，则出现严重错误，整个过程中止。

###### 语法

cmake_minimum_required(VERSION versionNumber [FATAL_ERROR])

###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明

##### aux_source_directory

###### 作用



###### 语法



###### 示例



###### 说明



### 示例

#### 最简单示例

```cmake
#CMake 最低版本号要求
cmake_minimum_required (VERSION 2.8)

#项目信息
project(Demo1)

#指定生成目标
add_executable(Demo main.c)
```



#### 同一目录，多个源文件

```cmake
# CMake 最低版本号要求
cmake_minimum_required (VERSION 2.8)
# 项目信息
project (Demo2)
# 查找当前目录下的所有源文件
# 并将名称保存到 DIR_SRCS 变量
aux_source_directory(. DIR_SRCS)
# 指定生成目标
add_executable(Demo ${DIR_SRCS})
```

#### 多个目录，多个源文件

对于这种情况，需要分别在项目根目录 Demo3 和 math 目录里各编写一个 CMakeLists.txt 文件。为了方便，我们可以先将 math 目录里的文件编译成静态库再由 main 函数调用。

```cmake
# 查找当前目录下的所有源文件
# 并将名称保存到 DIR_LIB_SRCS 变量
aux_source_directory(. DIR_LIB_SRCS)
# 生成链接库
add_library (MathFunctions ${DIR_LIB_SRCS})

```

使用命令 add_library 将 src 目录中的源文件编译为静态链接库

```cmake
# CMake 最低版本号要求
cmake_minimum_required (VERSION 2.8)
# 项目信息
project (Demo3)
# 查找当前目录下的所有源文件
# 并将名称保存到 DIR_SRCS 变量
aux_source_directory(. DIR_SRCS)
# 添加 math 子目录
add_subdirectory(math)
# 指定生成目标 
add_executable(Demo main.cc)
# 添加链接库
target_link_libraries(Demo MathFunctions)

```

命令 `add_subdirectory` 指明本项目包含一个子目录 math，这样 math 目录下的 CMakeLists.txt 文件和源代码也会被处理 。

命令 `target_link_libraries` 指明可执行文件 main 需要连接一个名为 MathFunctions 的链接库 。

#### 自定义编译选项

将 MathFunctions 库设为一个可选的库，如果该选项为 `ON` ，就使用该库定义的数学函数来进行运算。否则就调用标准库中的数学函数库。

##### CMakeList文件

```cmake
# CMake 最低版本号要求
cmake_minimum_required (VERSION 2.8)
# 项目信息
project (Demo4)
# 加入一个配置头文件，用于处理 CMake 对源码的设置
configure_file (
  "${PROJECT_SOURCE_DIR}/config.h.in"
  "${PROJECT_BINARY_DIR}/config.h"
  )
# 是否使用自己的 MathFunctions 库
option (USE_MYMATH
       "Use provided math implementation" ON)
# 是否加入 MathFunctions 库
if (USE_MYMATH)
  include_directories ("${PROJECT_SOURCE_DIR}/math")
  add_subdirectory (math)  
  set (EXTRA_LIBS ${EXTRA_LIBS} MathFunctions)
endif (USE_MYMATH)
# 查找当前目录下的所有源文件
# 并将名称保存到 DIR_SRCS 变量
aux_source_directory(. DIR_SRCS)
# 指定生成目标
add_executable(Demo ${DIR_SRCS})
target_link_libraries (Demo  ${EXTRA_LIBS})
```

`configure_file` 命令用于加入一个配置头文件 config.h ，这个文件由 CMake 从 [config.h.in](http://config.h.in/) 生成，通过这样的机制，将可以通过预定义一些参数和变量来控制代码的生成。

##### config.h.in

引用了一个 config.h 文件，这个文件预定义了 `USE_MYMATH` 的值。但我们并不直接编写这个文件，为了方便从 CMakeLists.txt 中导入配置，我们编写一个  config.h.in 文件，内容如下：

```ini
#cmakedefine USE_MYMATH
```

这样 CMake 会自动根据 CMakeLists 配置文件中的设置自动生成 config.h 文件。

##### main.c

```c
#include 
#include 
#include "config.h"
#ifdef USE_MYMATH
  #include "math/MathFunctions.h"
#else
  #include 
#endif
int main(int argc, char *argv[])
{
    if (argc < 3){
        printf("Usage: %s base exponent \n", argv[0]);
        return 1;
    }
    double base = atof(argv[1]);
    int exponent = atoi(argv[2]);
    
#ifdef USE_MYMATH
    printf("Now we use our own Math library. \n");
    double result = power(base, exponent);
#else
    printf("Now we use the standard library. \n");
    double result = pow(base, exponent);
#endif
    printf("%g ^ %d is %g\n", base, exponent, result);
    return 0;
}
```

#### 区分 debug、release版本

建立 debug/release 两目录，分别在其中执行 cmake -D CMAKE_BUILD_TYPE = Debug（或 Release）

```cmake
add_definitions(-DDEBUG)
```



#### 安装和测试

CMake 也可以指定安装规则，以及添加测试。这两个功能分别可以通过在产生 Makefile 后使用 `make install` 和 `make test` 来执行。在以前的 GNU Makefile 里，你可能需要为此编写 `install` 和 `test` 两个伪目标和相应的规则，但在 CMake 里，这样的工作同样只需要简单的调用几条命令。

##### 定制安装规则

首先在 math/CMakeList.txt 文件里添加下面两行

```cmake
# 指定 MathFunctions 库的安装路径
install (TARGETS Demo DESTINATION bin)
install (FILES MathFunctions.h DESTINATION include )
```

指明 MathFunctions 库的安装路径。之后同样修改根目录的 CMakeLists 文件，在末尾添加下面几行：

```cmake
# 指定安装路径
install (TARGETS Demo DESTINATION bin)
install (FILES "${PROJECT_BINARY_DIR}/config.h"
         DESTINATION include)
```

通过上面的定制，生成的 Demo 文件和 MathFunctions 函数库 libMathFunctions.o 文件将会被复制到 `/usr/local/bin` 中，而 MathFunctions.h 和生成的 config.h 文件则会被复制到 `/usr/local/include` 中。



##### 为工程添加测试

CMake 提供了一个称为 CTest 的测试工具。我们要做的只是在项目根目录的 CMakeLists 文件中调用一系列的 `add_test` 命令。

```cmake
# 启用测试
enable_testing()

# 测试程序是否成功运行
add_test (test_run Demo 5 2)

# 测试帮助信息是否可以正常提示
add_test (test_usage Demo)
set_tests_properties (test_usage
  PROPERTIES PASS_REGULAR_EXPRESSION "Usage: .* base exponent")
  
# 测试 5 的平方
add_test (test_5_2 Demo 5 2)
set_tests_properties (test_5_2
 PROPERTIES PASS_REGULAR_EXPRESSION "is 25")
 
# 测试 10 的 5 次方
add_test (test_10_5 Demo 10 5)
set_tests_properties (test_10_5
 PROPERTIES PASS_REGULAR_EXPRESSION "is 100000")
 
# 测试 2 的 10 次方
add_test (test_2_10 Demo 2 10)
set_tests_properties (test_2_10
 PROPERTIES PASS_REGULAR_EXPRESSION "is 1024")
```

上面的代码包含了四个测试。第一个测试 `test_run` 用来测试程序是否成功运行并返回 0 值。剩下的三个测试分别用来测试 5 的 平方、10 的 5 次方、2 的 10 次方是否都能得到正确的结果。其中 `PASS_REGULAR_EXPRESSION` 用来测试输出是否包含后面跟着的字符串。



如果要测试更多的输入数据，像上面那样一个个写测试用例未免太繁琐。这时可以通过编写宏来实现：

```cmake
# 定义一个宏，用来简化测试工作
macro (do_test arg1 arg2 result)
  add_test (test_${arg1}_${arg2} Demo ${arg1} ${arg2})
  set_tests_properties (test_${arg1}_${arg2}
    PROPERTIES PASS_REGULAR_EXPRESSION ${result})
endmacro (do_test)
 
# 使用该宏进行一系列的数据测试
do_test (5 2 "is 25")
do_test (10 5 "is 100000")
do_test (2 10 "is 1024")
```

#### 支持 gdb

CMake 支持 gdb 的设置也很容易，只需要指定 `Debug` 模式下开启 `-g` 选项：

```cmake
set(CMAKE_BUILD_TYPE "Debug")
set(CMAKE_CXX_FLAGS_DEBUG "$ENV{CXXFLAGS} -O0 -Wall -g -ggdb")
set(CMAKE_CXX_FLAGS_RELEASE "$ENV{CXXFLAGS} -O3 -Wall")
```

#### 增加环境检查

有时候可能要对系统环境做点检查，例如要使用一个平台相关的特性的时候。在这个例子中，我们检查系统是否自带 pow 函数。如果带有 pow 函数，就使用它；否则使用我们定义的 power 函数。

##### 添加CheckFunctionExits 宏

首先在顶层 CMakeLists 文件中添加 CheckFunctionExists.cmake 宏，并调用 `check_function_exists` 命令测试链接器是否能够在链接阶段找到 `pow` 函数。

```cmake
# 检查系统是否支持 pow 函数
include (${CMAKE_ROOT}/Modules/CheckFunctionExists.cmake)
check_function_exists (pow HAVE_POW)
```

将上面这段代码放在 `configure_file` 命令前。

##### 预定义相关宏变量

接下来修改 [config.h.in](http://config.h.in/) 文件，预定义相关的宏变量。

```ini
// does the platform provide pow function?
#cmakedefine HAVE_POW
```

##### 在代码中使用宏和函数

```c
#ifdef HAVE_POW
    printf("Now we use the standard library. \n");
    double result = pow(base, exponent);
#else
    printf("Now we use our own Math library. \n");
    double result = power(base, exponent);
#endif
```

#### 添加版本号

给项目添加和维护版本号是一个好习惯，这样有利于用户了解每个版本的维护情况，并及时了解当前所用的版本是否过时，或是否可能出现不兼容的情况。

首先修改顶层 CMakeLists 文件，在 `project` 命令之后加入如下两行：

```cmake
set (Demo_VERSION_MAJOR 1)
set (Demo_VERSION_MINOR 0)
```

分别指定当前的项目的主版本号和副版本号。

之后，为了在代码中获取版本信息，我们可以修改 [config.h.in](http://config.h.in/) 文件，添加两个预定义变量：

```ini
// the configured options and settings for Tutorial
#define Demo_VERSION_MAJOR @Demo_VERSION_MAJOR@
#define Demo_VERSION_MINOR @Demo_VERSION_MINOR@
```

可以直接在代码中打印版本信息了：

```c
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "config.h"
#include "math/MathFunctions.h"
int main(int argc, char *argv[])
{
    if (argc < 3){
        // print version info
        printf("%s Version %d.%d\n",
            argv[0],
            Demo_VERSION_MAJOR,
            Demo_VERSION_MINOR);
        printf("Usage: %s base exponent \n", argv[0]);
        return 1;
    }
    double base = atof(argv[1]);
    int exponent = atoi(argv[2]);
    
#if defined (HAVE_POW)
    printf("Now we use the standard library. \n");
    double result = pow(base, exponent);
#else
    printf("Now we use our own Math library. \n");
    double result = power(base, exponent);
#endif
    
    printf("%g ^ %d is %g\n", base, exponent, result);
    return 0;
}
```

#### 生成安装包

如何配置生成各种平台上的安装包，包括二进制安装包和源码安装包。为了完成这个任务，我们需要用到 CPack ，它同样也是由 CMake 提供的一个工具，专门用于打包。

首先在顶层的 CMakeLists.txt 文件尾部添加下面几行：

```
# 构建一个 CPack 安装包
include (InstallRequiredSystemLibraries)
set (CPACK_RESOURCE_FILE_LICENSE
  "${CMAKE_CURRENT_SOURCE_DIR}/License.txt")
set (CPACK_PACKAGE_VERSION_MAJOR "${Demo_VERSION_MAJOR}")
set (CPACK_PACKAGE_VERSION_MINOR "${Demo_VERSION_MINOR}")
include (CPack)
```

1. 导入 InstallRequiredSystemLibraries 模块，以便之后导入 CPack 模块；
2. 设置一些 CPack 相关变量，包括版权信息和版本信息，其中版本信息用了上一节定义的版本号；
3. 导入 CPack 模块。

接下来的工作是像往常一样构建工程，并执行 `cpack` 命令。

- 生成二进制安装包

  ```shell
  cpack -C CPackConfig.cmake
  ```

- 生成源码安装包

  ```shell
  cpack -C CPackSourceConfig.cmake
  ```

我们可以试一下。在生成项目后，执行 cpack -C CPackConfig.cmake 命令。