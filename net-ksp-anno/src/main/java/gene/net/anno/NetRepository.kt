package gene.net.anno

import retrofit2.Retrofit

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class NetSource(
    val method: String = "POST",
    val path: String,
    val list: Boolean = false,
    val checkResult: Boolean = true,
    val extra: String = ""
)

interface INetResult<D> {
    fun isOk(): Boolean
    fun message(): String
    fun body(): D
}

/**
 * 网络数据异常，不捕获堆栈
 */
class NetResultException(override val message: String) : Exception(message, null, false, false)


var retrofitProvider: (String) -> Retrofit = { throw RuntimeException("must set retrofitProvider = { yorRetrofit } in application") }