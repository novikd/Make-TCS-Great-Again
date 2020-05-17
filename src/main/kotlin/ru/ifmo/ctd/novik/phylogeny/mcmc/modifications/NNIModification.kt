package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.GlobalRandom

class NNIModification : Modification {
    override fun invoke(topology: RootedTopology): RootedTopology {
        val recombinationEdges = topology.recombinationGroups.filter { it.isUsed }.flatMap { it.ambassador!!.edges }

        val filteredEdges = topology.edges.filter { edge -> edge.start.edges.size > 2 && edge.end.edges.size > 2  && edge !in recombinationEdges}

        if (filteredEdges.isNotEmpty()) {
            val edge = filteredEdges.random(GlobalRandom)

            val startNode = edge.start
            val endNode = edge.end

            // TODO: look only on outgoing edges
            val startEdge = startNode.next.filterNot { it === edge }.random(GlobalRandom)
            val endEdge = endNode.next.filterNot { it.end == startNode }.random(GlobalRandom)

            val newEndEdge = startEdge.copy(start = endNode)
            val newStartEdge = endEdge.copy(start = startNode)

            startNode.edges.remove(edge)
            endNode.edges.removeIf { it.end === startNode }

            startNode.add(newStartEdge)
            endNode.add(newEndEdge)
        }

        return topology
    }
}