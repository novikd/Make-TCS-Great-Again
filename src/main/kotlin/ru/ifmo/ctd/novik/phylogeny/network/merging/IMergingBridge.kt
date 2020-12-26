package ru.ifmo.ctd.novik.phylogeny.network.merging

import ru.ifmo.ctd.novik.phylogeny.network.Node

/**
 * @author Dmitry Novik ITMO University
 */
interface IMergingBridge {
    val metric: Int

    fun build(nodeList: MutableList<Node>)
}