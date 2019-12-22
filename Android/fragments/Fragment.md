# Fragment





#### addToBackStack()

addToBackStack() 其实针对的是 fragmentTransaction。而非是具体的 Fragment

每次对 FragmentTransaction 更改后需要 commit 来执行。但特别要注意，每个 FragmentTransaction 实例 commit 其实只能执行一次。

addToBackStack(flag) 将 FragmentTransaction 加入到回退栈，在回退的时候显示上一个 Fragment。  popBackStack()