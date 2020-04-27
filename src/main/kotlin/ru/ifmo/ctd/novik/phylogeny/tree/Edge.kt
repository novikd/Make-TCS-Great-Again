package ru.ifmo.ctd.novik.phylogeny.tree

/**
 * @author Dmitry Novik ITMO University
 */
data class Edge(val start: TopologyNode, val end: TopologyNode, val nodes: List<Node>) {
    val length: Int
        get() = nodes.size - 1

    companion object {
        fun create(start: TopologyNode, end: TopologyNode) = Edge(start, end, listOf(start.node, end.node))
    }

    fun reversed(): Edge = Edge(end, start, nodes.reversed())

    fun contains(node: Node): Boolean = nodes.contains(node)

    override fun toString(): String = "\"${start.node.nodeName}\" -- \"${end.node.nodeName}\""
}