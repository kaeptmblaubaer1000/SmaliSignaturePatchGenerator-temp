plugins {
    application
    id("org.jetbrains.kotlin.jvm")
}

val kotlin_version: String by rootProject.extra

tasks.withType(CreateStartScripts::class.java).getByName("startScripts") {
    classpath = files("lib/SmaliSignaturePatchGenerator.jar") //TODO: include this after adding ProGuard
}

val transformClassesWithProguard = task("transformClassesWithProguard", proguard.gradle.ProGuardTask::class) {
    injars(project.tasks.getAt(JavaPlugin.JAR_TASK_NAME).outputs.files.plus(project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)))
    libraryjars(arrayOf("${System.getProperty("java.home")}/lib/rt.jar", "${System.getProperty("java.home")}/lib/jce.jar"))
    outjars(buildDir.toPath().resolve("proguard").resolve("SmaliSignaturePatchGenerator.jar"))

    keep("""class com.github.kaeptmblaubaer1000.smalisignaturepatchgenerator.Main {
    public static void main(String[]);
}""")
    dontobfuscate()
}

application {
    mainClassName = "com.github.kaeptmblaubaer1000.smalisignaturepatchgenerator.Main"
    applicationName = "smali-signature-patch-generator"
}

distributions {
    getByName("main") {
        contents {
            exclude {
                it.name.endsWith(".jar") && it.name != "SmaliSignaturePatchGenerator.jar"
            }

            from(transformClassesWithProguard) {
                into("lib") {
                    include("SmaliSignaturePatchGenerator.jar")
                }
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation(project(":mainlib"))


    implementation(project(":cli"))
}
