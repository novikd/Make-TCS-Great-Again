package ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood

import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology

class RecombinationLikelihood(val hotspots: List<Int>) : Likelihood {
    override fun invoke(topology: RootedTopology): Double {
        val usedHotspots = topology.recombinationGroups.filter { it.isUsed }.map { it.hotspot }.toSet().size
        return -usedHotspots.toDouble()
    }
}