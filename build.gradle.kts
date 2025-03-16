import com.google.protobuf.gradle.id

plugins {
	java
	alias(libs.plugins.springframework.boot)
	alias(libs.plugins.spring.dependency.management)
	alias(libs.plugins.spotless)
	alias(libs.plugins.google.protobuf)
}

group = "dev.notyouraverage.project.one.grpc.aggregator"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["springGrpcVersion"] = "0.5.0"

dependencies {
	implementation(libs.spring.boot.actuator)
	implementation(libs.spring.boot.grpc)
	implementation(libs.spring.boot.web)
	implementation(libs.springdoc.webmvc.ui)
	implementation(libs.spring.boot.data.redis)
	implementation(libs.grpc.services)
	implementation(libs.validation.api)

	implementation(libs.opentelemetry.exporter.otlp)
	implementation(libs.micrometer.tracing.bridge.otel)

	compileOnly(libs.lombok)
	developmentOnly(libs.spring.boot.compose)
	developmentOnly(libs.spring.boot.devtools)
	annotationProcessor(libs.lombok)

	testImplementation(libs.spring.boot.test)
	testImplementation(libs.spring.grpc.test)
	testRuntimeOnly(libs.junit.platform.launcher)
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
	}
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc"
	}
	plugins {
		id("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java"
		}
	}
	generateProtoTasks {
		all().forEach {
			it.plugins {
				id("grpc") {
					option("jakarta_omit")
					option("@generated=omit")
				}
			}
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

spotless {
	java {
		removeUnusedImports()
		eclipse("4.29").configFile("spotless.xml")
		trimTrailingWhitespace()
		endWithNewline()
	}
}

tasks.named("generateProto") {
	finalizedBy("spotlessApply")
}
