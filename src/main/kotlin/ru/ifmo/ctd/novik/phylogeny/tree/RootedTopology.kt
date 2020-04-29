package ru.ifmo.ctd.novik.phylogeny.tree

import ru.ifmo.ctd.novik.phylogeny.utils.*

data class RootedTopology(
        val topology: Topology,
        val root: TopologyNode,
        val recombinationGroups: MutableList<RecombinationGroup> = mutableListOf()
) {

    val nodes: List<TopologyNode>
        get() = topology.nodes

    val edges: List<Edge>
        get() = topology.flatMap { it.next }

    val genomes: List<Pair<Node, String>>
        get() = topology.cluster.map { node -> node to node.genome.primary }

    companion object {
        val log = logger()
    }

    fun clone(): RootedTopology {
        val (newTopology, generation, topGeneration) = topology.clone()
        val newRoot = newTopology.find { node -> node.node.taxon === root.node.taxon }!!

        edges.forEach { edge ->
            val newStart = topGeneration[edge.start]!!
            val newEnd = topGeneration[edge.end]!!
            val newEdge = newStart.edges.first { it.end === newEnd }
            newStart.next.add(newEdge)
        }

        val newRecombinationGroups = mutableListOf<RecombinationGroup>()
        recombinationGroups.forEach { group ->
            val newElements = mutableListOf<Recombination>()
            group.elements.forEach {
                newElements.add(Recombination(
                        generation[it.firstParent]!!,
                        generation[it.secondParent]!!,
                        generation[it.child]!!,
                        it.pos))
            }

            val newGroup = group.copy(elements = newElements)
            if (group.isUsed != newGroup.isUsed)
                error("Group usage info lost")
            if (group.isUsed) {
                val (recombination, midNode, createdEdges, deletedPath) = group.ambassador!!
                val newDeletedPath = mutableListOf(generation[deletedPath.first()]!!)
                for (i in 1 until deletedPath.lastIndex) {
                    newDeletedPath.add(deletedPath[i])
                }
                newDeletedPath.add(generation[deletedPath.last()]!!)

                val newCreatedEdges = createdEdges.map { edge ->
                    topGeneration[edge.start]!!.next.first { it.end === topGeneration[edge.end]!! }
                }

                newGroup.ambassador = RecombinationGroupAmbassador(
                        Recombination(
                                generation[recombination.firstParent]!!,
                                generation[recombination.secondParent]!!,
                                generation[recombination.child]!!,
                                recombination.pos),
                        topGeneration[midNode]!!,
                        newCreatedEdges,
                        newDeletedPath
                )
            }

            newRecombinationGroups.add(newGroup)
        }

        val newRootedTopology = RootedTopology(newTopology, newRoot, newRecombinationGroups)
        debug {
            if (edges.size != newRootedTopology.edges.size)
                error("Edges number must be equal")
            newRootedTopology.checkInvariant()
        }
        return newRootedTopology
    }

    fun add(recombination: Recombination): RecombinationGroup {
        val group = getRecombinationGroup(recombination)

        group.add(recombination)
        return group
    }

    fun getOrCreateNode(node: Node): TopologyNode {
        val topologyNode = nodes.find { it.node === node }
        if (topologyNode != null) {
            return topologyNode
        }

        val containingEdge = edges.first { it.contains(node) }
        topology.remove(containingEdge)
        val (newNode, inEdge, revInEdge, outEdge, revOutEdge) = containingEdge.split(node)
        topology.nodes.add(newNode)
        topology.add(Pair(inEdge, revInEdge))
        topology.add(Pair(outEdge, revOutEdge))
        return newNode
    }

    private fun getRecombinationGroup(recombination: Recombination): RecombinationGroup {
        val group = recombinationGroups.find { recombination in it }
        if (group != null) {
            log.info {
                "Reuse recombination group: { ${recombination.firstParent.nodeName} + ${recombination.secondParent.nodeName} -> ${recombination.child.nodeName} }"
            }
            return group
        }

        log.info {
            "New recombination group: { ${recombination.firstParent.nodeName} + ${recombination.secondParent.nodeName} -> ${recombination.child.nodeName} }"
        }
        val newGroup = RecombinationGroup(recombination.pos)
        recombinationGroups.add(newGroup)
        return newGroup
    }
}