package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.*

class NNIModification : TreeRearrangement() {

    companion object {
        val log = logger()
    }

    override fun apply(startEdge: Edge, endEdge: Edge, topology: RootedTopology) {
        log.info { "Chosen edges: $startEdge $endEdge" }

        val createdStartEdge = getEdgeWithoutReal(startEdge, topology)
        val createdEndEdge = getEdgeWithoutReal(endEdge, topology)

        val startNode = createdStartEdge.start
        val endNode = createdEndEdge.start

        createdStartEdge.delete(topology)
        createdEndEdge.delete(topology)

        createNewEdge(startNode, createdEndEdge.end, topology)
        createNewEdge(endNode, createdStartEdge.end, topology)

        topology.mergeTwoEdges(createdStartEdge.end)
        topology.mergeTwoEdges(createdEndEdge.end)

        debug { topology.checkInvariant() }
    }
}