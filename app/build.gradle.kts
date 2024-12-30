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
    arg("ksp.logLevel", "info")
}
android {
    namespace = "com.example.auto.service"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    //每次都执行ksp
    outputs.upToDateWhen { false }
}

dependencies {
    implementation(project(":retrofit-ksp-anno"))
    "ksp"(project(":retrofit-ksp"))
    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
}

//Retrofit retrofit = new Retrofit.Builder()
//    .baseUrl("https://api.github.com/")
//    .build();
//
//GitHubService service = retrofit.create(GitHubService.class);