package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.GlobalRandom
import ru.ifmo.ctd.novik.phylogeny.utils.createEdge
import ru.ifmo.ctd.novik.phylogeny.utils.logger
import ru.ifmo.ctd.novik.phylogeny.utils.mergeTwoEdges

class CancelRecombinationModification : Modification {

    companion object {
        val log = logger()
    }

    override fun invoke(topology: RootedTopology): RootedTopology {
        val usedGroups = topology.recombinationGroups.filter { it.isUsed }
        if (usedGroups.isEmpty()) return topology

        val group = usedGroups.random(GlobalRandom)
        val (recombination, midNode, _, path) = group.ambassador!!

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
        val edgeToMidNode = child.edges.find { it.end === midNode }
        if (edgeToMidNode != null) {
            for (i in 1 until edgeToMidNode.nodes.lastIndex) {
                val node = edgeToMidNode.nodes[i]
                node.neighbors.forEach { neighbor -> neighbor.neighbors.removeIf { it === node } }
                topology.topology.cluster.nodes.remove(node)
            }

            topology.topology.remove(edgeToMidNode)
            child.remove(edgeToMidNode)
        }

        if (midNode.edges.isEmpty()) {
            topology.topology.nodes.remove(midNode)
            topology.topology.cluster.nodes.remove(midNode.node)
        }

        var current = recombination.child
        for (i in 1 until path.size) {
            val node = path[i]
            createEdge(current, node)
            current = node
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

        group.setUnused()

        return topology
    }
}