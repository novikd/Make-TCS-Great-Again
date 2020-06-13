package ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood

import ru.ifmo.ctd.novik.phylogeny.models.SubstitutionModel
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
        val usedHotspots = topology.recombinationAmbassadors.map { it.recombination.pos }.toSet().size
        val result = poisson(usedHotspots)
        val massFunction = LnPoissonProbabilityMassFunction(SubstitutionModel.recombinationProbability * topology.topology.cluster.nodes.size)
        val res = massFunction(topology.recombinationAmbassadors.size)
        debug { log.info { "Recombination likelihood: $res with ${topology.recombinationAmbassadors.size}" } }
        return res
    }
}