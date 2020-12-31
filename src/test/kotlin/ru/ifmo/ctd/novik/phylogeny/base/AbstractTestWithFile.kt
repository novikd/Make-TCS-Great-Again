package ru.ifmo.ctd.novik.phylogeny.base

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.utils.resetGlobalSettings
import java.io.File

abstract class AbstractTestWithFile {
    abstract val testDirectory: String

    abstract fun runTest(testCaseFile: File)

    @TestFactory
    fun generateTestCases(): Iterator<DynamicTest> {
        val directory = File(testDirectory)
        Assertions.assertTrue(directory.exists() && directory.isDirectory, "test directory doesn't exist")
        return directory.walkTopDown().filter { it.extension == "txt" }.sortedBy { it.name }.flatMap {
            listOf(false, true).map { compressionEnabled ->
                dynamicTest("${it.name} <compression ${if (compressionEnabled) "ON" else "OFF"}>") {
                    resetGlobalSettings()
                    GlobalExecutionSettings.COMPRESSION_ENABLED = compressionEnabled
                    runTest(it)
                }
            }
        }.iterator()
    }
}