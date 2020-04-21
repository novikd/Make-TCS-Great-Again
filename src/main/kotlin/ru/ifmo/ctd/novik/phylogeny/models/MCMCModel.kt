package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Phylogeny
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.mcmc.MCMC
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.BranchLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.RecombinationLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.times
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.ChangeRootModification
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.HotspotMoveModification
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.Modification
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.NNIModification
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.*

class MCMCModel : IModel {
    override fun computePhylogeny(taxonList: List<Taxon>): Phylogeny {
        TODO("Not yet implemented")
    }

    override fun computeTopology(taxonList: List<Taxon>): RootedTopology {
        val baseModel = PhylogeneticModel.SET_BRUTE_FORCE_TCS.create()
        val phylogeny = baseModel.computePhylogeny(taxonList)
        phylogeny.unify()

        val baseTopology = phylogeny.cluster.topology()
        val length = phylogeny.cluster.terminals.first().genome.primary.length
        val likelihood = BranchLikelihood(length * SubstitutionModel.mutationRate) * RecombinationLikelihood()
        val hotspots = mutableListOf<Int>(2)
        val modifications = listOf(ChangeRootModification(), NNIModification(), HotspotMoveModification(hotspots))
        val mcmc = MCMC(likelihood, modifications)
        return mcmc.simulation(baseTopology.toRooted())
    }
}