package ru.ifmo.ctd.novik.phylogeny.base

import org.junit.jupiter.api.Assertions
import java.io.File

abstract class AbstractTestWithFile {
    abstract val testDirectory: String

    fun runTests(test:(File.()-> Unit)) {
        val directory = File(testDirectory)
        Assertions.assertTrue(directory.exists() && directory.isDirectory, "test directory doesn't exist")
        directory.walkTopDown().filter { it.extension == "txt" }.forEach {
            test(it)
        }
    }

}