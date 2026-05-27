package org.example.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class EnvTask extends DefaultTask {

    @InputFile
    abstract RegularFileProperty getJarFile()

    @Input
    abstract Property<String> getJavaExecutable()

    @Input
    abstract ListProperty<String> getJvmArgs()

    @Input
    abstract ListProperty<String> getAppArgs()

    @TaskAction
    void run() {
        def jarPath = jarFile.get().asFile.absolutePath
        def javaExe = javaExecutable.get()
        def jvmArgsList = jvmArgs.get()
        def appArgsList = appArgs.get()

        def cmd = [javaExe] + jvmArgsList + ['-jar', jarPath] + appArgsList

        println "----- Запуск приложения в альтернативном окружении -----"
        println "----- Используемая Java: ${javaExe}"
        println "----- Аргументы JVM:     ${jvmArgsList}"
        println "----- App args:          ${appArgsList}"
        println "----- Команда:           ${cmd.join(' ')}"

        project.exec {
            commandLine cmd
        }
    }
}