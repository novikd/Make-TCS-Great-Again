package ru.ifmo.ctd.novik.phylogeny.tree.merging

/**
 * @author Novik Dmitry ITMO University
 */
interface IMergingBridge {
    val metric: Int

    fun build()
}

internal object EmptyMergingBridge : IMergingBridge {
    override val metric: Int = Int.MIN_VALUE

    override fun build() = Unit
}

fun emptyMergingBridge(): IMergingBridge = EmptyMergingBridge