plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    id("io.gitlab.arturbosch.detekt") // ✅ 添加 detekt 插件
}

android {
    namespace = "iss.nus.edu.sg.mygo"
    compileSdk = 35

    defaultConfig {
        applicationId = "iss.nus.edu.sg.mygo"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }


    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.material.v1120)
    implementation(libs.material.v160)
    implementation(libs.okhttp3.okhttp)
    implementation(libs.google.gson)
    implementation(libs.androidsvg)
    implementation(libs.picasso)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout.v204)
    implementation("androidx.cardview:cardview:1.0.0")
//    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx) // 确保 fragment-ktx 依赖存在


    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.glide)
    kapt(libs.compiler.v4120)
    kapt(libs.glide.compiler)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // JUnit 4（单元测试）
    testImplementation(libs.junit)

    // Mockito（模拟对象）
    testImplementation(libs.mockito.core)

    // 协程测试（用于运行挂起函数）
    testImplementation(libs.kotlinx.coroutines.test)

    // OkHttp3（用于测试 Retrofit）
    testImplementation(libs.okhttp.v493)

    // MockWebServer（用于模拟 API 响应）
    testImplementation(libs.mockwebserver)
}

detekt {
    baseline = file("$rootDir/app/baseline.xml")  // ✅ 让 detekt 使用 baseline 文件
    toolVersion = "1.23.1"
    config.setFrom(files("$rootDir/config/detekt/detekt.yml")) // ✅ 使用 `setFrom()` 替换 `config = files(...)`
    buildUponDefaultConfig = true // ✅ 允许覆盖默认规则
}

// ✅ 兼容 Gradle 9.0 的 `reports` 配置
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)  // 生成 HTML 格式报告
        xml.required.set(true)   // 生成 XML 格式报告
        txt.required.set(false)  // 关闭 TXT 格式
    }
}
    // 报错结果存储位置：C:\Users\Lenovo\Documents\SA\AD Project\MyGo\front_android\app\build\reports\detekt