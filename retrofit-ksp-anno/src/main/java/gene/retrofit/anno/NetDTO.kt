package gene.retrofit.anno

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class NetSource(
    val method: String = "POST",
    val path: String,
    val list: Boolean = false,
    val check: Boolean = true,
    val params: String = "",
    val nullable: Boolean = false,
    val extra: String = ""
)

interface INetResult<D> {
    fun code(): Int
    fun isOk(): Boolean
    fun message(): String
    fun body(): D
}

/**
 * 网络数据异常，不捕获堆栈
 */
class NetException(val code: Int, override val message: String) : Exception(message, null, false, false)