package ru.ifmo.ctd.novik.phylogeny.tree

import ru.ifmo.ctd.novik.phylogeny.utils.genome

data class RootedTopology(val topology: Topology, val root: TopologyNode) {

    val nodes: List<TopologyNode>
        get() = topology.nodes

    val edges: List<Edge>
        get() = collectEdges(root)

    val genomes: List<Pair<Node, String>>
        get() = topology.cluster.map { node -> node to node.genome.primary }

    fun clone(): RootedTopology {
        val newTopology = topology.clone()
        val newRoot = newTopology.find { node -> node.node.taxon === root.node.taxon }!!
        return RootedTopology(newTopology, newRoot)
    }

    private fun collectEdges(node: TopologyNode,
                             prev: TopologyNode? = null,
                             visited: MutableSet<TopologyNode> = mutableSetOf()): List<Edge> {
        visited.add(node)
        val result = mutableListOf<Edge>()

        node.edges.forEach { edge ->
            if (edge.end !in visited) {
                result.addAll(collectEdges(edge.end, node, visited))
            }
            if (edge.end != prev) {
                result.add(edge)
            }
        }
        return result
    }
}