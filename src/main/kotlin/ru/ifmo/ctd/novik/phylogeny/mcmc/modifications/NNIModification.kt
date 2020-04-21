package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology

class NNIModification : Modification {
    override fun invoke(topology: RootedTopology): RootedTopology {
        val filteredEdges = topology.edges.filter { edge -> edge.start.edges.size > 2 && edge.end.edges.size > 2 }

        if (filteredEdges.isNotEmpty()) {
            val edge = filteredEdges.random()

            val startNode = edge.start
            val endNode = edge.end

            // TODO: look only on outgoing edges
            val startEdge = startNode.edges.filterNot { it === edge }.random()
            val endEdge = endNode.edges.filterNot { it.end == startNode }.random()

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