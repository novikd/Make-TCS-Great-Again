package ru.ifmo.ctd.novik.phylogeny.tree.merging

/**
 * @author Dmitry Novik ITMO University
 */
interface IMergingBridge {
    val metric: Int

    fun build()
}