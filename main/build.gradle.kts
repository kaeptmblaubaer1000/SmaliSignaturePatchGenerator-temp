plugins {
    application
    id("org.jetbrains.kotlin.jvm")
}

tasks.withType(CreateStartScripts::class.java).getByName("startScripts") {
    classpath = files("lib/SmaliSignaturePatchGenerator.jar")
}

val transformClassesWithProguard = task("transformClassesWithProguard", proguard.gradle.ProGuardTask::class) {
    injars(project.tasks.getAt(JavaPlugin.JAR_TASK_NAME).outputs.files.plus(project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)))
    libraryjars("${System.getProperty("java.home")}/lib/rt.jar")
    libraryjars("${System.getProperty("java.home")}/lib/jce.jar")
    libraryjars("${System.getProperty("java.home")}/jmods/java.base.jmod")
    libraryjars("${System.getProperty("java.home")}/jmods/java.compiler.jmod")
    libraryjars("${System.getProperty("java.home")}/jmods/java.logging.jmod")
    libraryjars("${System.getProperty("java.home")}/jmods/java.xml.jmod")
    libraryjars("${System.getProperty("java.home")}/jmods/jdk.unsupported.jmod")
    outjars(buildDir.toPath().resolve("proguard").resolve("SmaliSignaturePatchGenerator.jar"))

    keepclasseswithmembers("""public class com.github.kaeptmblaubaer1000.smalisignaturepatchgenerator.Main {
    public static void main(java.lang.String[]);
} """)
    dontobfuscate()
    keepattributes("org.jetbrains.annotations.Nullable")
    keepattributes("org.jetbrains.annotations.NotNull")
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


            into("lib") {
                from(transformClassesWithProguard) {
                    include("SmaliSignaturePatchGenerator.jar")
                }
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(project(":mainlib"))


    implementation(project(":cli"))
}
