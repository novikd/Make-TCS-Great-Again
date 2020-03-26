package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Novik Dmitry ITMO University
 */
interface Cluster : Iterable<Node> {
    val nodes: List<Node>
    val terminals: List<Node>
}
