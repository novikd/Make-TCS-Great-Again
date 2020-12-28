package ru.ifmo.ctd.novik.phylogeny.base

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.File

abstract class AbstractTestWithFile {
    abstract val testDirectory: String

    abstract fun runTest(testCaseFile: File)

    @TestFactory
    fun generateTestCases(): Iterator<DynamicTest> {
        val directory = File(testDirectory)
        Assertions.assertTrue(directory.exists() && directory.isDirectory, "test directory doesn't exist")
        return directory.walkTopDown().filter { it.extension == "txt" }.sortedBy { it.name }.map {
            dynamicTest(it.name) { runTest(it) }
        }.iterator()
    }
}