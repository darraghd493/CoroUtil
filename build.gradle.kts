@file:Suppress("DEPRECATION")

plugins {
	id("fabric-loom") version "1.7-SNAPSHOT"
}

// Project properties:
val minecraftVersion: String by project
val loaderVersion: String by project
val fabricVersion: String by project

val modId: String by project
val modName: String by project
val modVersion: String by project
val modDescription: String by project
val modAuthors: String by project
val modLicense: String by project
val mavenGroup: String by project
val archivesBaseName: String by project

group = mavenGroup
version = modVersion

// Toolchains:
java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

base {
	archivesName = "${archivesBaseName}-fabric"
}

// Dependencies:
repositories {
	maven {
		url = uri("https://jitpack.io")
	}
	maven {
		url = uri("https://maven.terraformersmc.com/")
	}
	maven {
		url = uri("https://maven.shedaniel.me/")
	}
	maven {
		url = uri("https://maven.siphalor.de/")
	}
	maven {
		url = uri("https://maven.isxander.dev/releases")
	}
	maven {
		url = uri("https://maven.parchmentmc.net/")
	}
	maven {
		url = uri("https://api.modrinth.com/maven")
	}
	flatDir {
		dirs("libs")
	}
}

val annotationImplementation: Configuration by configurations.creating {
	configurations.compileOnly.get().extendsFrom(this)
	configurations.testCompileOnly.get().extendsFrom(this)
	configurations.annotationProcessor.get().extendsFrom(this)
	configurations.testAnnotationProcessor.get().extendsFrom(this)
}

dependencies {
	annotationImplementation("org.projectlombok:lombok:1.18.36")

	// Original dependencies:
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	@Suppress("UnstableApiUsage")
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-1.21.1:2024.11.17") // TODO: Use plugin
	})
	modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
	modImplementation("maven.modrinth:forge-config-api-port:v21.1.3-1.21.1-Fabric")
	implementation("com.electronwill.night-config:core:3.7.3")
	implementation("com.electronwill.night-config:toml:3.7.3")
}

// Loom:
loom {
	accessWidenerPath = file("src/main/resources/coroutil.accesswidener")
	@Suppress("UnstableApiUsage")
	mixin {
		defaultRefmapName = "${modId}.refmap.json"
	}
}

// Task:
tasks.named<ProcessResources>("processResources") {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand(
			mapOf(
				"mod_id" to modId,
				"version" to modVersion,
				"mod_name" to modName,
				"mod_description" to modDescription,
				"mod_authors" to modAuthors,
				"mod_license" to modLicense
			)
		)
	}
}

tasks.named<JavaCompile>("compileJava") {
	options.release.set(21)
}

tasks.named<Jar>("jar") {
	from("LICENSE") {
		rename { "${it}_${archivesBaseName}" }
	}
}
