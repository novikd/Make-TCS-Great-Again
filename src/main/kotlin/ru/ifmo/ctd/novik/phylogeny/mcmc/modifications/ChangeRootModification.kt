package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.GlobalRandom
import ru.ifmo.ctd.novik.phylogeny.utils.toRooted

class ChangeRootModification : Modification {
    override fun invoke(topology: RootedTopology): RootedTopology {
//        val newRoot = topology.topology.nodes.random(GlobalRandom)
//
//        val newTopology = topology.topology.toRooted(newRoot)
//        newTopology.recombinationGroups.addAll(topology.recombinationGroups)
//
//        return newTopology
        return topology
    }
}