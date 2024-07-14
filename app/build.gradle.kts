plugins {
    //https://github.com/google/ksp/releases
    //libs.plugins.android.application必须在最上面
    alias(libs.plugins.android.application)
    alias(wings.plugins.compose)
    alias(libs.plugins.ksp)
}

ksp {
    arg("option1", "value1")
    arg("option2", "value2")
}
android {
    namespace = "com.example.auto.service"
}

dependencies {
    ksp(project(":net-repository-anno"))
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.bundles.androidx.benchmark)
    // https://mvnrepository.com/artifact/com.google.auto.service/auto-service
    implementation(":net-repository-anno")
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.5.0")

}

//Retrofit retrofit = new Retrofit.Builder()
//    .baseUrl("https://api.github.com/")
//    .build();
//
//GitHubService service = retrofit.create(GitHubService.class);