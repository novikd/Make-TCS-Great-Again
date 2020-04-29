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
        val recombinationEdges = topology.recombinationGroups.filter { it.isUsed }.flatMap { it.ambassador!!.edges }.toSet()
        val result = topology.edges.filter { it !in recombinationEdges }.map { computeEdgeLikelihood(it) }.sum()
        debug { log.info { "Branch likelihood: $result" } }
        return result
    }

    private fun computeEdgeLikelihood(edge: Edge): Double {
        var result = poisson(edge.length)
        edge.nodes.zipWithNext { a, b ->
            result += a.genome.primary.zip(b.genome.primary).filter { (lhs, rhs) ->
                lhs != rhs
            }.map { ln(SubstitutionModel.nucleotideProbability[it.second]!!) }.fold(0.0, Double::plus)
//            }.map { ln(SubstitutionModel.relativeSubstitutionProbability[it.first]!![it.second]!!) }.fold(0.0, Double::plus)
        }
        return result
    }
}