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

    fun split(node: Node): Pair<Edge, Edge> {
        val index = nodes.indexOf(node)
        val newNode = TopologyNode(node)
        val firstPart = Edge(start, newNode, nodes.subList(0, index + 1))
        val secondPart = Edge(newNode, end, nodes.subList(index, nodes.size))
        start.remove(this)
        end.removeIf { this.end === end }

        start.add(firstPart)
        newNode.add(secondPart)
        end.add(secondPart.reversed())
        newNode.add(firstPart.reversed())

        return Pair(firstPart, secondPart)
    }

    override fun toString(): String = "\"$start\" -- \"$end\""
}