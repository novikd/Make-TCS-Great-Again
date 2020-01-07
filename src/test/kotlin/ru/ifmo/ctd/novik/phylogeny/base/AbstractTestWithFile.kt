package ru.ifmo.ctd.novik.phylogeny.base

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File

abstract class AbstractTestWithFile {
    abstract val testDirectory: String
    abstract val outputExtensionPrefix: String

    fun runTests(test:(String.()-> String)) {
        val directory = File(testDirectory)
        assertTrue(directory.exists() && directory.isDirectory, "test directory doesn't exist")
        directory.walkTopDown().filter { it.extension == "txt" }.forEach {
            val testOutput = test(it.absolutePath)

            val outputFile = File(directory, it.nameWithoutExtension + outputExtensionPrefix + ".out")
            outputFile.writeText(testOutput)

            val goldFile = File(directory, it.nameWithoutExtension + outputExtensionPrefix + ".gold")
            val testGold: String
            val msg: String
            if (!goldFile.exists()) {
                testGold = ""
                msg = "Gold file '${goldFile.name}' not found!\n"
            } else {
                testGold = goldFile.readText().replace("\r", "")
                msg = "${outputFile.name} and ${goldFile.name} files are different!\n"
            }

            assertEquals(testGold, testOutput, msg)
            outputFile.delete()
        }
    }
}