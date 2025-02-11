// buildscript 不能从其他 gradle 文件中 apply，所以这段 buildscript 脚本存在于多个子构建中
buildscript {
	// rootProject.extra 定义的扩展属性可被子工程引用
	val kotlinVersion by extra("1.6.10")
	val hiltVersion by extra("2.37")
	repositories {
		maven { url = uri("https://maven.aliyun.com/repository/central") }
		maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
		maven { url = uri("https://maven.aliyun.com/repository/google") }
		maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }

		maven { url = uri("https://jitpack.io") }

		mavenCentral()
		mavenLocal()
		google()
	}
	dependencies {
		classpath("com.android.tools.build:gradle:7.0.2")
		classpath(kotlin("gradle-plugin", kotlinVersion))

		val navVersion = "2.3.5"
		classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")

		classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")

		// 统计方法耗时插件
		classpath("gradle.plugin.cn.cxzheng.methodTracePlugin:tracemanplugin:1.0.4")

		classpath(files("plugin/plugin.jar"))
		classpath("org.javassist:javassist:3.20.0-GA")
	}
}

apply {
	from("../common_build.gradle.kts")
}
