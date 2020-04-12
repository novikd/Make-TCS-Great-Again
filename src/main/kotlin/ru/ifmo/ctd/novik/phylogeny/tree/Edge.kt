package ru.ifmo.ctd.novik.phylogeny.tree

/**
 * @author Dmitry Novik ITMO University
 */
data class Edge(val start: TopologyNode, val end: TopologyNode, val nodes: List<Node>) {
    val length: Int
        get() = nodes.size - 1

    override fun toString(): String = "\"$start\" -- \"$end\""
}