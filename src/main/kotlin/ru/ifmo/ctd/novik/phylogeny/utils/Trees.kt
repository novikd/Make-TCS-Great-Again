package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology

/**
 * @author Dmitry Novik ITMO University
 */

fun Edge.delete(topology: RootedTopology) {
    for (i in 1 until nodes.lastIndex) {
        nodes[i].neighbors.clear()
        topology.topology.cluster.nodes.remove(nodes[i])
    }
    nodes.first().neighbors.remove(nodes[1])
    nodes.last().neighbors.remove(nodes[nodes.lastIndex - 1])
    topology.topology.remove(this)

    start.remove(this)
    end.removeIf { it.end === start }
}
