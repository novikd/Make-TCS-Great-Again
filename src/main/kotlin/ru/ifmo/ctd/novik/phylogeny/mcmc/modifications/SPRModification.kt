package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.network.Edge
import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.*

class SPRModification : TreeRearrangement() {
    companion object {
        val log = logger()
    }

    override fun apply(startEdge: Edge, endEdge: Edge, topology: RootedTopology) {
        val (edgeToInsert, edgeWithSpot) = listOf(startEdge, endEdge).shuffled(GlobalExecutionSettings.RANDOM)
        if (edgeWithSpot.nodes.size == 2) {
            return
        }
        if (edgeToInsert.start in topology.recombinationEdges.flatMap { listOf(it.start, it.end) })
            return

        val intermediateNodes = edgeWithSpot.nodes.subList(1, edgeWithSpot.nodes.lastIndex)
        val spotNode = intermediateNodes.random(GlobalExecutionSettings.RANDOM)
        val realEdgeToInsert = getEdgeWithoutReal(edgeToInsert, topology)

        log.info { "Inserting {$realEdgeToInsert} to edge {$edgeWithSpot} via $spotNode" }

        realEdgeToInsert.delete(topology)
        val topSpotNode = topology.getOrCreateNode(spotNode)
        createNewEdge(topSpotNode, realEdgeToInsert.end, topology)
        topology.mergeTwoEdges(realEdgeToInsert.start)
        topology.mergeTwoEdges(realEdgeToInsert.end)
        debug { topology.checkInvariant() }
    }
}