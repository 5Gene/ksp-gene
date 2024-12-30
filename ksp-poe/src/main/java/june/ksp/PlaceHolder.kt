package june.ksp

//%S - 用于替换类名、函数名等元素，会自动添加引号
//// 使用示例
//fileBuilder.addStatement("val className = %S", "MyClass")
//// 生成: val className = "MyClass"

//%T - 用于替换类型引用
//// 使用示例
//fileBuilder.addStatement("val list = %T()", List::class)
//// 生成: val list = kotlin.collections.List()

//%L - 用于替换字面量（数字、布尔值等）
//// 使用示例
//fileBuilder.addStatement("val number = %L", 42)
//// 生成: val number = 42

//%N - 用于引用变量名、函数名等标识符
//// 使用示例
//fileBuilder.addStatement("val result = %N()", "myFunction")
//// 生成: val result = myFunction()

//%P - 用于属性引用
//// 使用示例
//fileBuilder.addStatement("val prop = %P", ::myProperty)
//// 生成: val prop = this::myProperty

