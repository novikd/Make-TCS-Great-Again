package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Phylogeny
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.io.output.PrettyPrinter
import ru.ifmo.ctd.novik.phylogeny.mcmc.MCMC
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.BranchLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.RecombinationLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.times
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.*
import ru.ifmo.ctd.novik.phylogeny.tools.P_RECOMBINATION
import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.*

class MCMCModel(val hotspots: List<Int>, val iterations: Int = 10_000) : IModel {
    override fun computePhylogeny(taxonList: List<Taxon>): Phylogeny {
        TODO("Not yet implemented")
    }

    override fun computeTopology(taxonList: List<Taxon>): RootedTopology {
        val baseModel = PhylogeneticModel.SET_BRUTE_FORCE_TCS.create()
        val phylogeny = baseModel.computePhylogeny(taxonList)
        phylogeny.unify()

        val baseTopology = phylogeny.cluster.topology()
        baseTopology.cluster.label()
        println(phylogeny.cluster.toGraphviz(PrettyPrinter()))
        val length = phylogeny.cluster.terminals.first().genome.primary.length
        val likelihood = BranchLikelihood(length * SubstitutionModel.mutationRate) * RecombinationLikelihood(P_RECOMBINATION * taxonList.size)
        val modifications = listOf(
                ChangeRootModification(),
                NNIModification(),
                SPRModification(),
                HotspotMoveModification(hotspots.toMutableList()),
                CancelRecombinationModification()
        )
        val mcmc = MCMC(likelihood, modifications, iterations)
        return mcmc.simulation(baseTopology.toRooted())
    }
}