package ru.ifmo.ctd.novik.phylogeny.utils

import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ConfigurationDelegate<T> : ReadOnlyProperty<T, List<Int>> {
    override fun getValue(thisRef: T, property: KProperty<*>): List<Int> {
        val reader = File("parameters").bufferedReader()
        val parameterName = "[${property.name}]"
        val line = reader.lineSequence().first { line -> line.startsWith(parameterName) }
        return line.split(" ").drop(1).map { it.toInt() }
    }
}