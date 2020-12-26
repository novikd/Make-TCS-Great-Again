package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.common.MutableGenome
import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.network.Edge
import ru.ifmo.ctd.novik.phylogeny.network.Node
import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.network.TopologyNode
import ru.ifmo.ctd.novik.phylogeny.utils.computeDistinctPositions
import ru.ifmo.ctd.novik.phylogeny.utils.createEdge
import ru.ifmo.ctd.novik.phylogeny.utils.genome
import ru.ifmo.ctd.novik.phylogeny.utils.split

/**
 * @author Dmitry Novik ITMO University
 */
abstract class TreeRearrangement : Modification {
    override fun invoke(topology: RootedTopology): RootedTopology {
        val recombinationEdges = topology.recombinationEdges

        val filteredEdges = topology.edges.filter {
            edge -> edge.start.edges.size > 2 && edge.start.next.size > 1 && edge.start.next.any{ it != edge && it !in recombinationEdges }
                && edge.end.edges.size > 2   && edge.end.next.size > 1 && edge.end.next.any { it !in recombinationEdges } && edge !in recombinationEdges
        }

        if (filteredEdges.isNotEmpty()) {
            val edge = filteredEdges.random(GlobalExecutionSettings.RANDOM)

            val startNode = edge.start
            val endNode = edge.end

            val startEdge = startNode.next.filterNot { it === edge || it in recombinationEdges }.random(GlobalExecutionSettings.RANDOM)
            val endEdge = endNode.next.filterNot { it in recombinationEdges }.random(GlobalExecutionSettings.RANDOM)

            apply(startEdge, endEdge, topology)
        }
        return topology
    }

    protected abstract fun apply(startEdge: Edge, endEdge: Edge, topology: RootedTopology)

    protected fun createNewEdge(startNode: TopologyNode, endNode: TopologyNode, topology: RootedTopology) {
        val positions = computeDistinctPositions(startNode.node.taxon, endNode.node.taxon)
        val path = mutableListOf(startNode.node)
        for (i in 0 until positions.lastIndex) {
            val current = path.last()
            val newNode = Node()
            createEdge(current, newNode)

            val builder = StringBuilder(current.genome.primary)
            builder[positions[i]] = endNode.genome.primary[positions[i]]
            (newNode.genome as MutableGenome).add(builder.toString())
            path.add(newNode)
            topology.topology.cluster.nodes.add(newNode)
        }
        createEdge(path.last(), endNode.node)
        path.add(endNode.node)

        val newEdge = Edge(startNode, endNode, path)
        startNode.add(newEdge, directed = true)
        val revNewEdge = newEdge.reversed()
        endNode.add(revNewEdge)
        topology.topology.add(Pair(newEdge, revNewEdge))
    }

    protected fun getEdgeWithoutReal(edge: Edge, topology: RootedTopology): Edge {
        for (i in 1 until edge.nodes.lastIndex) {
            val node = edge.nodes[i]
            if (node.isRealTaxon) {
                topology.topology.remove(edge)
                val (newNode, inEdge, revInEdge, outEdge, revOutEdge) = edge.split(node)
                topology.topology.nodes.add(newNode)
                topology.topology.add(Pair(inEdge, revInEdge))
                topology.topology.add(Pair(outEdge, revOutEdge))
                return inEdge
            }
        }
        return edge
    }
}