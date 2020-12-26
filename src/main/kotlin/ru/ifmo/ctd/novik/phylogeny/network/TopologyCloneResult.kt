package ru.ifmo.ctd.novik.phylogeny.network

data class TopologyCloneResult(
        val topology: Topology,
        val generation: MutableMap<Node, Node>,
        val topGeneration: Map<TopologyNode, TopologyNode>
)