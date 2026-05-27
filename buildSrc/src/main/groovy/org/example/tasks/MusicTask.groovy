package org.example.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

abstract class MusicTask extends DefaultTask {

    @InputFile
    @Optional
    abstract RegularFileProperty getSoundFile()

    @TaskAction
    void play() {
        def sound = soundFile.getOrNull()?.asFile
        if (sound == null || !sound.exists()) {
            println "Файл музыки не найден :("
            return
        }

        println "playing success sound..."
        try {
            def audio = AudioSystem.getAudioInputStream(sound)
            Clip clip = AudioSystem.getClip()
            clip.open(audio)
            clip.loop(2)
            println "sound successfully played"
        } catch (Exception e) {
            println "ошибка воспроизведения: ${e.message}"
        }
    }
}