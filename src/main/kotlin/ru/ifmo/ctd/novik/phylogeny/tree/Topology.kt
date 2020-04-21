package ru.ifmo.ctd.novik.phylogeny.tree

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.utils.topology

/**
 * @author Dmitry Novik ITMO University
 */
data class Topology(
        val cluster: Cluster,
        val nodes: List<TopologyNode>,
        val edges: List<Pair<Edge, Edge>>
) : Iterable<TopologyNode> {

    fun clone(): Topology {
        return cluster.clone().topology()
    }

    override fun iterator(): Iterator<TopologyNode> = nodes.iterator()
}