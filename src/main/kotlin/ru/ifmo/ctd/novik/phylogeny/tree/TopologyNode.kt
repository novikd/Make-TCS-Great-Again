package ru.ifmo.ctd.novik.phylogeny.tree

/**
 * @author Dmitry Novik ITMO University
 */
data class TopologyNode(val node: Node) {
    private val myEdges: MutableList<Edge> = mutableListOf()
    val next: MutableList<Edge> = mutableListOf()

    val edges: MutableList<Edge>
        get() = myEdges

    fun add(edge: Edge) = myEdges.add(edge)

    fun remove(edge: Edge) = myEdges.remove(edge)

    fun removeIf(predicate: (Edge.() -> Boolean)) = myEdges.removeIf(predicate)

    override fun toString(): String = "NODE: $node"
}