package gene.retrofit.anno

import retrofit2.Retrofit


var retrofitProvider: (String) -> Retrofit = {
    throw RuntimeException("must set retrofitProvider = { yorRetrofit } in application")
}