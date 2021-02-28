// Top-level build file where you can add configuration options common to all sub-projects/modules.


buildscript {

	repositories {
		google()
		// jcenter()
		maven { url = uri("https://maven.aliyun.com/repository/central") }
		maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
		maven { url = uri("https://maven.aliyun.com/repository/google") }
		maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }

		maven { url = uri("https://jitpack.io") }
	}
	dependencies {
		classpath("com.android.tools.build:gradle:4.1.2")

		val kotlinVersion = "1.4.0"
		classpath(kotlin("gradle-plugin", kotlinVersion))
	}
}

allprojects {
	repositories {
		google()
		jcenter()
		maven { url = uri("https://maven.aliyun.com/repository/central") }
		maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
		maven { url = uri("https://maven.aliyun.com/repository/google") }
		maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }

		maven { url = uri("https://jitpack.io") }
	}
}

tasks.register("clean", Delete::class) {
	this.delete(rootProject.buildDir)
}