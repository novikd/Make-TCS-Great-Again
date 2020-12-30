package ru.ifmo.ctd.novik.phylogeny.network

import ru.ifmo.ctd.novik.phylogeny.common.GenomeOption
import ru.ifmo.ctd.novik.phylogeny.events.Recombination
import ru.ifmo.ctd.novik.phylogeny.events.RecombinationGroupAmbassador
import ru.ifmo.ctd.novik.phylogeny.utils.*

data class RootedTopology(
        val topology: Topology,
        val root: TopologyNode,
        val recombinationAmbassadors: MutableList<RecombinationGroupAmbassador> = mutableListOf()
) {

    val nodes: List<TopologyNode>
        get() = topology.nodes

    val edges: List<Edge>
        get() = topology.flatMap { it.next }

    val genomes: List<Pair<Node, GenomeOption>>
        get() = topology.cluster.map { node -> node to node.genome.primary }

    val recombinationEdges: Set<Edge>
        get() = recombinationAmbassadors.flatMap { it.edges }.toSet()

    val recombinationEdgesOnly: Set<Edge>
        get() = recombinationAmbassadors.flatMap { it.edges.takeLast(2) }.toSet()

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

        debug {
            log.info { "Cloning recombination group ambassadors" }
        }
        val newRecombinationAmbassadors = mutableListOf<RecombinationGroupAmbassador>()
        recombinationAmbassadors.forEach { ambassador ->
            debug {
                log.info { "Cloning ambassador $ambassador" }
            }
            val (recombination, midNode, createdEdges, deletedPath) = ambassador
            debug {
                log.info { "Cloning deleted path: ${deletedPath.joinToString(" ")}" }
            }

            val newDeletedPath = mutableListOf(generation[deletedPath.first()]!!)
            for (i in 1 until deletedPath.lastIndex) {
                newDeletedPath.add(generation.computeIfAbsent(deletedPath[i]) { old -> old.copy() })
            }
            newDeletedPath.add(generation.computeIfAbsent(deletedPath.last()) { old -> old.copy() })

            debug {
                for (i in 1 until newDeletedPath.lastIndex) {
                    if (newDeletedPath[i].neighbors.size != 0)
                        error("Node for copied path ${newDeletedPath[i]} has neighbors")
                }
            }

            debug {
                log.info { "Cloning created edges" }
            }
            val newCreatedEdges = createdEdges.map { edge ->
                topGeneration[edge.start]!!.next.first { it.end === topGeneration[edge.end]!! }
            }

            debug {
                log.info { "Cloning recombination ambassador" }
            }
            val newAmbassador = RecombinationGroupAmbassador(
                    Recombination(
                            generation[recombination.firstParent]!!,
                            generation[recombination.secondParent]!!,
                            generation[recombination.child]!!,
                            recombination.pos,
                            recombination.childIndex),
                    topGeneration[midNode]!!,
                    newCreatedEdges,
                    newDeletedPath
            )
            newRecombinationAmbassadors.add(newAmbassador)
        }

        val newRootedTopology = RootedTopology(newTopology, newRoot, newRecombinationAmbassadors)
        debug {
            if (edges.size != newRootedTopology.edges.size)
                error("Edges number must be equal")
            newRootedTopology.checkInvariant()
        }
        return newRootedTopology
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

    fun add(ambassador: RecombinationGroupAmbassador) {
        recombinationAmbassadors.add(ambassador)
    }
}