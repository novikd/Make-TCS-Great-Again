package ru.ifmo.ctd.novik.phylogeny.base

import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File

abstract class AbstractTestWithOutputFile : AbstractTestWithFile() {
    abstract val outputExtensionPrefix: String

    abstract fun test(testCasePath: String): String

    override fun runTest(testCaseFile: File) {
        val directory = File(testDirectory)
        val testOutput = test(testCaseFile.absolutePath)

        val outputFile = File(directory, testCaseFile.nameWithoutExtension + outputExtensionPrefix + ".out")
        outputFile.writeText(testOutput)

        val goldFile = File(directory, testCaseFile.nameWithoutExtension + outputExtensionPrefix + ".gold")
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