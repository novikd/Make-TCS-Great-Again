package ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood

import ru.ifmo.ctd.novik.phylogeny.models.SubstitutionModel
import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.LnPoissonProbabilityMassFunction
import ru.ifmo.ctd.novik.phylogeny.utils.debug
import ru.ifmo.ctd.novik.phylogeny.utils.genome
import ru.ifmo.ctd.novik.phylogeny.utils.logger
import kotlin.math.ln

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
        var result = poisson(edge.length)
        var substitutions = 0
        edge.nodes.zipWithNext { a, b ->
            val differentPositions = a.genome.primary.zip(b.genome.primary).filter { (lhs, rhs) ->
                lhs != rhs
            }
            substitutions += differentPositions.size
            result += differentPositions
                    .map { ln(SubstitutionModel.substitutionProbability(it.first, it.second)) }
                    .fold(0.0, Double::plus)
        }
        if (substitutions != edge.length) {
            log.info { "Edge has mutual options: ${edge.nodes.any { it.genome.size > 1 }}" }
            log.info { "Edge with ${substitutions - edge.length}" }
        }
        return result
    }
}