package ru.ifmo.ctd.novik.phylogeny.tree

data class RecombinationGroupAmbassador(
        val recombination: Recombination,
        val midNode: TopologyNode,
        val edges: List<Edge>,
        val deletedPath: List<Node>
)