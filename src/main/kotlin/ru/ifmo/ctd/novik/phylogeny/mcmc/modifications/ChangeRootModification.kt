package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.tree.TopologyNode
import ru.ifmo.ctd.novik.phylogeny.utils.GlobalRandom
import java.util.*

class ChangeRootModification : Modification {
    override fun invoke(topology: RootedTopology): RootedTopology {
        val newTopolgy = topology.clone()
        val newRoot = newTopolgy.topology.nodes.random(GlobalRandom)

//
//        val newTopology = topology.topology.toRooted(newRoot)
//        newTopology.recombinationGroups.addAll(topology.recombinationGroups)
//
//        return newTopology
        return topology
    }

    private fun traverse(root: TopologyNode) {
        val visited: MutableSet<TopologyNode> = mutableSetOf()
        val queue = ArrayDeque<TopologyNode>()
        queue.add(root)
        visited.add(root)

        while (queue.isNotEmpty()) {
            val node = queue.pop()

            node.edges.forEach { edge ->
                if (edge.end !in visited) {
                    visited.add(edge.end)
                    queue.add(edge.end)
                    
                }
            }
        }
    }
}