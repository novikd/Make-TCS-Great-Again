package ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood

import ru.ifmo.ctd.novik.phylogeny.network.Edge
import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.LnPoissonProbabilityMassFunction
import ru.ifmo.ctd.novik.phylogeny.utils.debug
import ru.ifmo.ctd.novik.phylogeny.utils.logger

class BranchLikelihood(lambda: Double) : Likelihood {
    private val poisson = LnPoissonProbabilityMassFunction(lambda)

    companion object {
        val log = logger()
    }

    override operator fun invoke(topology: RootedTopology): Double {
        val recombinationEdges = topology.recombinationEdgesOnly
        val result = topology.edges.filter { it !in recombinationEdges }.map { computeEdgeLikelihood(it) }.sum()
        debug { log.info { "Branch likelihood: $result" } }
        return result
    }

    private fun computeEdgeLikelihood(edge: Edge): Double {
        return poisson(edge.length)
    }
}