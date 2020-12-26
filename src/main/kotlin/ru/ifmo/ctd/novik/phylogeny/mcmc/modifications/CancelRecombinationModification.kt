package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.network.Edge
import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.*

class CancelRecombinationModification : Modification {

    companion object {
        val log = logger()
    }

    override fun invoke(topology: RootedTopology): RootedTopology {
        if (topology.recombinationAmbassadors.isEmpty()) return topology

        val ambassador = topology.recombinationAmbassadors.random(GlobalExecutionSettings.RANDOM)
        val (recombination, midNode, _, path) = ambassador

        if (path.first() !in topology.topology.cluster || path.last() !in topology.topology.cluster)
            return topology

        log.info { "Canceling recombination: $recombination" }

        val firstParent = topology.getOrCreateNode(recombination.firstParent)
        val secondParent = topology.getOrCreateNode(recombination.secondParent)
        val child = topology.getOrCreateNode(recombination.child)

        val firstEdge = firstParent.next.first { edge -> edge.end === midNode }
        val secondEdge = secondParent.next.first { edge -> edge.end === midNode }

        topology.topology.remove(firstEdge)
        topology.topology.remove(secondEdge)
        firstParent.remove(firstEdge)
        secondParent.remove(secondEdge)
        firstParent.node.neighbors.remove(midNode.node)
        secondParent.node.neighbors.remove(midNode.node)

        midNode.removeIf { edge -> edge.end === firstParent || edge.end === secondParent || edge.end === child }
        midNode.node.neighbors.remove(firstParent.node)
        midNode.node.neighbors.remove(secondParent.node)
        val edgeToMidNode = child.edges.find { it.end === midNode }
        if (edgeToMidNode != null) {
            val nodes = edgeToMidNode.nodes
            nodes[0].neighbors.remove(nodes[1])
            nodes[nodes.lastIndex].neighbors.remove(nodes[nodes.lastIndex - 1])
            for (i in 1 until nodes.lastIndex) {
                val node = nodes[i]
                topology.topology.cluster.nodes.remove(node)
            }

            topology.topology.remove(edgeToMidNode)
            child.remove(edgeToMidNode)
        }

        if (midNode !== child && midNode.edges.isEmpty()) {
            topology.topology.nodes.remove(midNode)

            val node = midNode.node

            debug {
                log.info { "Removing $node" }
            }
            node.neighbors.forEach { neighbor -> neighbor.neighbors.removeIf { it === node } }
            topology.topology.cluster.nodes.remove(node)
        }

        log.info { "Recreated path: ${path.joinToString(" ")}" }

        debug {
            for (i in 1 until path.lastIndex) {
                if (path[i].neighbors.size != 0)
                    error("Node for recreated path ${path[i]} has neighbors")
            }
        }

        for (i in 1 .. path.lastIndex) {
            createEdge(path[i - 1], path[i])
        }

        for (i in 1 until path.lastIndex){
            topology.topology.cluster.nodes.add(path[i])
        }

        val endNode = topology.getOrCreateNode(path.last())
        val newEdge = Edge(child, endNode, path)
        val revNewEdge = newEdge.reversed()
        child.add(newEdge)
        endNode.add(revNewEdge, directed = true)
        topology.topology.add(Pair(newEdge, revNewEdge))

        topology.mergeTwoEdges(firstParent)
        topology.mergeTwoEdges(secondParent)
        topology.mergeTwoEdges(child)

        topology.recombinationAmbassadors.remove(ambassador)

        return topology
    }
}