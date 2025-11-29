package june.ksp.poe

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec

fun ParameterSpec.Builder.defaultString(def: String): ParameterSpec.Builder {
    return defaultValue("%S", def)
}

fun ParameterSpec.Builder.defaultNull(): ParameterSpec.Builder {
    return defaultValue("%L", null)
}

fun ParameterSpec.Builder.defaultEmptyList(): ParameterSpec.Builder {
    return defaultValue("%M()", MemberName("kotlin.collections", "emptyList"))
}

fun ParameterSpec.Builder.defaultEmptyMap(): ParameterSpec.Builder {
    return defaultValue("%M()", MemberName("kotlin.collections", "emptyMap"))
}


/**
 * 给参数添加参数
 * fun(@annoType("param") param:String)
 */
fun ParameterSpec.Builder.annotation(annotation: ClassName, more: AnnotationSpec.Builder.() -> Unit = {}): ParameterSpec.Builder {
    return addAnnotation(AnnotationSpec.builder(annotation).apply(more).build())
}

