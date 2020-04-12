package ru.ifmo.ctd.novik.phylogeny.tree

/**
 * @author Dmitry Novik ITMO University
 */
data class Topology(val nodes: List<TopologyNode>, val edges: List<Pair<Edge, Edge>>) : Iterable<TopologyNode> {
    override fun iterator(): Iterator<TopologyNode> = nodes.iterator()
}