package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Novik Dmitry ITMO University
 */
interface Cluster {
    val taxonList: List<Node>
}

internal object EmptyCluster : Cluster {
    override val taxonList: List<Node> = emptyList()
}

fun emptyCluster(): Cluster = EmptyCluster