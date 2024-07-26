# auto-service

![](https://img.shields.io/badge/AutoService-1.1.1-brightgreen.svg)

![](https://img.shields.io/badge/ksp-2.0.0+-brightgreen.svg)

[![License](https://img.shields.io/badge/LICENSE-Apache%202-green.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)

# 自动生成Retrofit调用

比如，接口调用 /api/v1/publisher/status 返回值 StatusDTO
1, 定一接口约定数据结构

```kotlin
data class StatusDTO(val deploymentId: String)
```

2, 先定一接口

```kotlin
interface API {
    @POST("api/v1/publisher/status")
    public suspend fun postDataDTO(
        @QueryMap params: Map<String, @JvmSuppressWildcards Any> = emptyMap(),
    ): NetResult<StatusDTO>
}
```

3, 使用

```kotlin
val result = retrofit.create(API::class.java).postDataDTO()
```

## 添加KSP插件

```kotlin
apply("com.google.devtools.ksp")
```
## 添加依赖
```kotlin
ksp("io.github.5gene:net-repository:0.0.2")
implementation("io.github.5gene:net-repository-anno:0.0.2")
```
