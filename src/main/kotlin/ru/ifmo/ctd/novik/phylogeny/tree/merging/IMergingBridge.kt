package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Dmitry Novik ITMO University
 */
interface IMergingBridge {
    val metric: Int

    fun build(nodeList: MutableList<Node>)
}