package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Novik Dmitry ITMO University
 */
interface Cluster : Iterable<Node> {
    val genomeNumber: Int
    val nodes: MutableList<Node>
    val terminals: List<Node>

    fun clone(): ClusterCloneResult
}
