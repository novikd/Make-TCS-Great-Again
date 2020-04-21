package ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood

import ru.ifmo.ctd.novik.phylogeny.models.SubstitutionModel
import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.tree.TopologyNode
import ru.ifmo.ctd.novik.phylogeny.utils.PoissonProbabilityMassFunction
import ru.ifmo.ctd.novik.phylogeny.utils.genome
import kotlin.math.ln

class BranchLikelihood(lambda: Double) : Likelihood {
    private val poisson = PoissonProbabilityMassFunction(lambda)

    override operator fun invoke(topology: RootedTopology): Double {
        return topology.edges.map { computeEdgeLikelihood(it) }.sum()
    }

    private fun computeEdgeLikelihood(edge: Edge): Double {
        var result = ln(poisson(edge.length))
        edge.nodes.zipWithNext { a, b ->
            result += a.genome.primary.zip(b.genome.primary).filter { (lhs, rhs) ->
                lhs != rhs
            }.map { ln(SubstitutionModel.nucleotideProbability[it.second]!!) }.fold(0.0, Double::plus)
        }
        return result
    }
}