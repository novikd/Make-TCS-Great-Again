package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology

class ChangeRootModification : Modification {
    override fun invoke(topology: RootedTopology): RootedTopology {
        return RootedTopology(topology.topology, topology.topology.nodes.random())
    }
}