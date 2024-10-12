plugins {
    //https://github.com/google/ksp/releases
    //libs.plugins.android.application必须在最上面
    alias(vcl.plugins.android.application)
    alias(vcl.plugins.gene.compose)
    alias(vcl.plugins.ksp)
}

ksp {
    arg("NetResult", "com.example.ksptt.NetResult")
    arg("option2", "value2")
}
android {
    namespace = "com.example.auto.service"
}

dependencies {
    implementation(project(":net-ksp-anno"))
    "ksp"(project(":net-ksp-repository"))
    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.5.0")

}

//Retrofit retrofit = new Retrofit.Builder()
//    .baseUrl("https://api.github.com/")
//    .build();
//
//GitHubService service = retrofit.create(GitHubService.class);