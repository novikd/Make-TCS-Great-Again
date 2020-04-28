package ru.ifmo.ctd.novik.phylogeny.tree

import ru.ifmo.ctd.novik.phylogeny.common.ClusterCloneResult
import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.utils.checkInvariant
import ru.ifmo.ctd.novik.phylogeny.utils.debug

/**
 * @author Dmitry Novik ITMO University
 */
data class Topology(
        val cluster: Cluster,
        val nodes: MutableList<TopologyNode>,
        val edges: MutableList<Pair<Edge, Edge>>
) : Iterable<TopologyNode> {

    fun clone(): TopologyCloneResult {
        val (newCluster, generation) = cluster.clone()

        val topGeneration = mutableMapOf<TopologyNode, TopologyNode>()
        val newNodes = mutableListOf<TopologyNode>()
        nodes.map {
            val newNode = generation[it.node]!!
            val newTopNode = TopologyNode(newNode)
            topGeneration[it] = newTopNode
            newNodes.add(newTopNode)
        }

        val newEdges = mutableListOf<Pair<Edge, Edge>>()
        edges.forEach { (fst, snd) ->
            val newFst = Edge(topGeneration[fst.start]!!, topGeneration[fst.end]!!, fst.nodes.map { node -> generation[node]!! })
            newFst.start.add(newFst)

            val newSnd = Edge(topGeneration[snd.start]!!, topGeneration[snd.end]!!, snd.nodes.map { node -> generation[node]!! })
            newSnd.start.add(newSnd)

            newEdges.add(Pair(newFst, newSnd))
        }

        val topology = Topology(newCluster, newNodes, newEdges)
        debug {
            val invariant = topGeneration.all { (old, new) -> old.edges.size == new.edges.size } && edges.size == newEdges.size
            if (!invariant)
                error("Topology clone error")

            this.checkInvariant()
            topology.checkInvariant()
        }

        return TopologyCloneResult(topology, generation, topGeneration)
    }

    fun add(edge: Pair<Edge, Edge>) = edges.add(edge)

    fun remove(edge: Edge): Boolean = edges.removeIf { it.first == edge || it.second == edge }

    override fun iterator(): Iterator<TopologyNode> = nodes.iterator()
}