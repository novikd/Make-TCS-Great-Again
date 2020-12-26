package ru.ifmo.ctd.novik.phylogeny.events

import ru.ifmo.ctd.novik.phylogeny.network.Edge
import ru.ifmo.ctd.novik.phylogeny.network.Node
import ru.ifmo.ctd.novik.phylogeny.network.TopologyNode

data class RecombinationGroupAmbassador(
    val recombination: Recombination,
    val midNode: TopologyNode,
    val edges: List<Edge>,
    val deletedPath: List<Node>
)