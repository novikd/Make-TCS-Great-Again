package ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood

import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.LnPoissonProbabilityMassFunction
import ru.ifmo.ctd.novik.phylogeny.utils.debug
import ru.ifmo.ctd.novik.phylogeny.utils.logger

class RecombinationLikelihood(lambda: Double) : Likelihood {

    private val poisson = LnPoissonProbabilityMassFunction(lambda)

    companion object {
        val log = logger()
    }

    override fun invoke(topology: RootedTopology): Double {
        val usedHotspots = topology.recombinationGroups.filter { it.isUsed }.map { it.hotspot }.toSet().size
        val result = poisson(usedHotspots)
        debug { log.info { "Recombination likelihood: $result with $usedHotspots" } }
        return result
    }
}