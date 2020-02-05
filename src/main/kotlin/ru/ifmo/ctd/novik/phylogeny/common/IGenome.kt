package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface IGenome {
    val primary: String

    fun isReal(): Boolean
    fun process(action: (String.() -> Unit))
}