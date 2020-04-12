package ru.ifmo.ctd.novik.phylogeny.tree

/**
 * @author Dmitry Novik ITMO University
 */
data class TopologyNode(val node: Node) {
    private val myEdges: MutableList<Edge> = mutableListOf()

    val edges: List<Edge>
        get() = myEdges

    fun add(edge: Edge) = myEdges.add(edge)

    override fun toString(): String = "Top{$node}"
}