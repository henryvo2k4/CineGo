plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cinego"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cinego"
        minSdk = 26
        targetSdk = 36
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // 1. Glide: Load ảnh (poster, avatar) cực kỳ mượt và ổn định.
    // Hỗ trợ sẵn việc làm mờ ảnh (Blur) nếu cần.
    implementation("com.github.bumptech.glide:glide:5.0.7")

    // 2. Lottie: Làm hiệu ứng animation (ví dụ: màn hình splash, loading, tick xanh thành công)
    implementation("com.airbnb.android:lottie:6.7.1")

    // 3. ZXing: Thư viện chuẩn của Google để tạo mã QR cho vé xem phim
    implementation("com.google.zxing:core:3.5.4")

    // 4. Gson: Chuyển đổi dữ liệu JSON (Rất cần thiết khi chúng ta làm mock data hoặc gọi API sau này)
    implementation("com.google.code.gson:gson:2.13.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}