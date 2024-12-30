package gene.net.anno

import retrofit2.Retrofit

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class NetSource(
    val method: String = "POST",
    val path: String,
    val list: Boolean = false,
    val checkResult: Boolean = true,
    val params: String = "",
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
class NetResultException(val code: Int, override val message: String) : Exception(message, null, false, false)


var retrofitProvider: (String) -> Retrofit = { throw RuntimeException("must set retrofitProvider = { yorRetrofit } in application") }