import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
	this.id("com.android.application")
}

apply {
	plugin("kotlin-android")
	plugin("kotlin-android-extensions")
	plugin("kotlin-kapt")
}

android {
	signingConfigs {
		create("release") {
			val keystoreFilePath = rootProject.projectDir.path + File.separator + "keystore.properties"
			loadProperties(keystoreFilePath).also {
				storeFile = File(it.getProperty("storeFile"))
				storePassword = it.getProperty("storePassword")
				keyAlias = it.getProperty("keyAlias")
				keyPassword = it.getProperty("keyPassword")
			}
		}
		getByName("debug") {
			storeFile = rootProject.file("debug_sign.jks")
			storePassword = "123456"
			keyAlias = "debug"
			keyPassword = "123456"
		}
	}
	compileSdkVersion(30)
	defaultConfig {
		applicationId = "com.example.zhanglei.myapplication"
		minSdkVersion(22)
		targetSdkVersion(30)
		versionCode = 1
		versionName = "1.0"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		// Required when setting minSdkVersion to 20 or lower（支持请求比minSdkVersion更高的API）
		multiDexEnabled = true
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = true
			isDebuggable = true
			isShrinkResources = true
			proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
			signingConfig = signingConfigs.getByName("release")
		}
		getByName("debug") {
			signingConfig = signingConfigs.getByName("debug")
		}
	}
	buildToolsVersion = "29.0.3"

	compileOptions {
		//支持新的API(java.util.streams、java.util.function等)
		this.isCoreLibraryDesugaringEnabled = true
		this.sourceCompatibility = JavaVersion.VERSION_1_8
		this.targetCompatibility = JavaVersion.VERSION_1_8
	}

	flavorDimensions("MyApp")
	productFlavors {
		create("MyAppT") {
			dimension = "MyApp"
			applicationId = "com.example.zhanglei.myapplication.test"
			resValue("string", "app_name", "我的应用测试")
		}
		create("MyAppP") {
			dimension = "MyApp"
			applicationId = "com.example.zhanglei.myapplication"
			resValue("string", "app_name", "我的应用")
		}
	}

	tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
		kotlinOptions {
			this.jvmTarget = "1.8"
		}
	}
}

android.applicationVariants.all { variant ->

	val buildType = variant.buildType.name

	//获取当前时间的"YYYY-MM-dd"格式。
	// val createTime = new Date().format("YYYY-MM-dd", java.util.TimeZone.getTimeZone("GMT+08:00"))
	println(variant.packageApplicationProvider?.get()?.outputDirectory)

	// def output = variant.outputFile()

	variant.outputs.forEach {
		if (buildType == "release" || buildType == "debug") {
			//variant.getPackageApplicationProvider().get().outputDirectory = new File("/Volumes/Morley/User/张磊")
			val fileName = "XX_${buildType}.apk"
			println("文件名：-----------------${fileName}")

			it.outputFile.renameTo(File("${it.outputFile.path}/fileName"))
		}
	}
	true
}

dependencies {
	api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
	implementation("androidx.legacy:legacy-support-v4:1.0.0")

	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.1")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.2")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.31")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.31")
	implementation("androidx.annotation:annotation:1.1.0")
	implementation("org.mockito:mockito-all:2.0.2-beta")
	implementation("androidx.core:core-ktx:1.3.2")
	implementation("org.jetbrains.anko:anko-commons:0.10.8")

	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
	implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

	implementation("androidx.core:core:1.3.2")
	implementation("androidx.appcompat:appcompat:1.2.0")
	implementation("androidx.constraintlayout:constraintlayout:2.0.4")
	implementation("androidx.recyclerview:recyclerview:1.1.0")
	implementation("androidx.cardview:cardview:1.0.0")
	implementation("androidx.navigation:navigation-fragment-ktx:2.3.2")
	implementation("androidx.navigation:navigation-ui-ktx:2.3.2")

	implementation("com.google.code.gson:gson:2.8.6")

	//lottie动画库
	implementation("com.airbnb.android:lottie:3.6.1")
	implementation("com.scwang.smart:refresh-layout-kernel:2.0.3")

	implementation("com.elvishew:xlog:1.8.0")

	//图片选择库
	implementation("com.zhihu.android:matisse:0.5.3-beta3")

	implementation("com.github.bumptech.glide:glide:4.12.0")
	//处理Glide注解，用于自定义Glide模块和自定义扩展类时必须引用
	annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

	//权限请求库
	implementation("com.permissionx.guolindev:permissionx:1.4.0")

	//视频播放库
	implementation("com.shuyu:gsyVideoPlayer-java:7.1.6")
	//是否需要ExoPlayer模式
	implementation("com.shuyu:GSYVideoPlayer-exo2:7.1.6")

	//根据你的需求ijk模式的so
	implementation("com.shuyu:gsyVideoPlayer-armv5:7.1.6")
	implementation("com.shuyu:gsyVideoPlayer-armv7a:7.1.6")
	implementation("com.shuyu:gsyVideoPlayer-arm64:7.1.6")
	implementation("com.shuyu:gsyVideoPlayer-x64:7.1.6")
	implementation("com.shuyu:gsyVideoPlayer-x86:7.1.6")

	implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")

	implementation("com.github.Heart-Beats:Downloader:v1.0.1")
	implementation("com.contrarywind:Android-PickerView:4.1.9")

	implementation("me.weishu:epic:0.11.0")
	implementation(project(mapOf("path" to ":method-proxy-library")))
}