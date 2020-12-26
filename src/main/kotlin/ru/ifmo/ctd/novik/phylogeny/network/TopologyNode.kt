package ru.ifmo.ctd.novik.phylogeny.network

/**
 * @author Dmitry Novik ITMO University
 */
data class TopologyNode(val node: Node) {
    private val myEdges: MutableList<Edge> = mutableListOf()
    val next: MutableList<Edge> = mutableListOf()

    val edges: MutableList<Edge>
        get() = myEdges

    fun add(edge: Edge, directed: Boolean = false) {
        if ((edge.length < 1)) {
            error("Edge length must be at least 1")
        }
        myEdges.add(edge)
        if (directed)
            next.add(edge)
    }

    fun remove(edge: Edge) {
        myEdges.remove(edge)
        next.remove(edge)
    }

    fun removeIf(predicate: ((Edge) -> Boolean)) {
        myEdges.removeIf(predicate)
        next.removeIf(predicate)
    }

    override fun toString(): String = "NODE: $node"
}