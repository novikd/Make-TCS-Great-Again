package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.delete
import ru.ifmo.ctd.novik.phylogeny.utils.logger

class NNIModification : TreeRearrangement() {

    companion object {
        val log = logger()
    }

    override fun apply(startEdge: Edge, endEdge: Edge, topology: RootedTopology) {
        log.info { "Chosen edges: $startEdge $endEdge" }

        val startNode = startEdge.start
        val endNode = endEdge.start

        startEdge.delete(topology)
        endEdge.delete(topology)

        createNewEdge(startNode, endEdge.end, topology)
        createNewEdge(endNode, startEdge.end, topology)
    }
}