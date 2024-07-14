package gene.net.repository

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class NetSource(val method: String = "get", val path: String, val withCache: Boolean = false)
