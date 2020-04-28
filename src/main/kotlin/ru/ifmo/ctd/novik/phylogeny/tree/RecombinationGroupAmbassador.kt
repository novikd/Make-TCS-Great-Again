package ru.ifmo.ctd.novik.phylogeny.tree

data class RecombinationGroupAmbassador(val recombination: Recombination, val midNode: TopologyNode, val deletedPath: List<Node>)