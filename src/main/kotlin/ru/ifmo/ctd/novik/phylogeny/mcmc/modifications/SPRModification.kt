package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.GlobalRandom
import ru.ifmo.ctd.novik.phylogeny.utils.delete
import ru.ifmo.ctd.novik.phylogeny.utils.logger

class SPRModification : TreeRearrangement() {
    companion object {
        val log = logger()
    }

    override fun apply(startEdge: Edge, endEdge: Edge, topology: RootedTopology) {
        val (edgeToInsert, edgeWithSpot) = listOf(startEdge, endEdge).shuffled(GlobalRandom)
        if (edgeWithSpot.nodes.size == 2) {
            return
        }

        val intermediateNodes = edgeWithSpot.nodes.subList(1, edgeWithSpot.nodes.lastIndex)
        val spotNode = intermediateNodes.random(GlobalRandom)

        log.info { "Inserting {$edgeToInsert} to edge {$edgeWithSpot} via $spotNode" }

        edgeToInsert.delete(topology)
        val topSpotNode = topology.getOrCreateNode(spotNode)
        createNewEdge(topSpotNode, edgeToInsert.end, topology)
    }
}