package gene.net.repository

import retrofit2.Retrofit

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class NetSource(val method: String = "get", val path: String, val list: Boolean = false)

var retrofitProvider: () -> Retrofit = { throw RuntimeException("must set retrofitProvider = { yorRetrofit } in application") }