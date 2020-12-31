package ru.ifmo.ctd.novik.phylogeny.network

/**
 * @author Dmitry Novik ITMO University
 */
interface Cluster : Iterable<Node> {
    val genomeNumber: Int
    val nodes: MutableList<Node>
    val terminals: List<Node>

    fun clone(): ClusterCloneResult
}
