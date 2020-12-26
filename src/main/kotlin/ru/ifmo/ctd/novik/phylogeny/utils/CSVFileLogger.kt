package ru.ifmo.ctd.novik.phylogeny.utils

import java.io.Closeable
import java.io.File

/**
 * @author Dmitry Novik ITMO University
 */

interface ICSVFileLogger : Closeable {
    fun log(vararg args: Any)
}

internal class CSVFileLogger(private val filename: String) : ICSVFileLogger {
    interface CSVRecord

    private val records = mutableListOf<CSVRecord>()

    override fun log(vararg args: Any) {
        records.add(object : CSVRecord {
            override fun toString(): String {
                return args.joinToString(separator = ";")
            }
        })
    }

    override fun close() {
        val file = File(filename)
        file.writeText(records.joinToString(separator = "\n"))
    }
}

internal object EmptyCSVLogger : ICSVFileLogger {
    override fun log(vararg args: Any) {}
    override fun close() {}
}

fun createCSVLogger(filename: String, shouldDump: Boolean): ICSVFileLogger
        = if (shouldDump) CSVFileLogger(filename) else EmptyCSVLogger