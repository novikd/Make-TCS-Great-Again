package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.RootedPhylogeny
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.tree.Topology
import ru.ifmo.ctd.novik.phylogeny.tree.TopologyNode

/**
 * @author Dmitry Novik ITMO University
 */
class NewickOutputClusterVisitor : OutputClusterVisitor {
    override fun visit(cluster: Cluster): String {
        TODO("Not yet implemented")
    }

    override fun visit(topology: Topology): String {
        TODO("Not yet implemented")
    }

    override fun visit(phylogeny: RootedPhylogeny): String {
        TODO("Not yet implemented")
    }

    override fun visit(topology: RootedTopology): String {
        return "${visit(topology.root)};"
    }

    private fun visit(node: TopologyNode, visited: MutableSet<TopologyNode> = mutableSetOf()): String {
        visited.add(node)
        val notVisited = node.next.filter {
            it.end !in visited
        }
        return if (notVisited.isEmpty())
            node.node.nodeName
        else notVisited.joinToString(prefix = "(", separator = ", ", postfix = ")${node.node.nodeName}") {
            "${visit(it.end, visited)}:${it.length}"
        }
    }
}