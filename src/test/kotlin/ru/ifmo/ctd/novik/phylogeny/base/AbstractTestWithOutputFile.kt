package ru.ifmo.ctd.novik.phylogeny.base

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File

abstract class AbstractTestWithOutputFile : AbstractTestWithFile() {
    abstract val outputExtensionPrefix: String

    fun runTestsWithOutput(test:(String.()-> String)) {
        val directory = File(testDirectory)

        runTests {
            val testOutput = test(this.absolutePath)

            val outputFile = File(directory, this.nameWithoutExtension + outputExtensionPrefix + ".out")
            outputFile.writeText(testOutput)

            val goldFile = File(directory, this.nameWithoutExtension + outputExtensionPrefix + ".gold")
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