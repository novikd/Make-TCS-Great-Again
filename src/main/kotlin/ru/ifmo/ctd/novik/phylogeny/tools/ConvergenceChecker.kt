package ru.ifmo.ctd.novik.phylogeny.tools

import ru.ifmo.ctd.novik.phylogeny.mcmc.MCMC
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.BranchLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.Likelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.RecombinationLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.times
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.CancelRecombinationModification
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.ChangeRootModification
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.HotspotMoveModification
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.Modification
import ru.ifmo.ctd.novik.phylogeny.models.SubstitutionModel
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.GlobalRandom
import ru.ifmo.ctd.novik.phylogeny.utils.distanceMatrix
import ru.ifmo.ctd.novik.phylogeny.utils.logger
import ru.ifmo.ctd.novik.phylogeny.utils.toArrays
import kotlin.math.abs

class ConvergenceChecker(
        val groundTruth: RootedTopology,
        val likelihood: Likelihood,
        val modifications: List<Modification>
) {
    private val groundTruthDistances = groundTruth.distanceMatrix.toArrays()

    companion object {
        val log = logger()
    }

    fun modifyAndRunMCMC() {
        var current = groundTruth
        for (i in 0..2) {
            val modification = modifications.random(GlobalRandom)
            log.info { "Modifying ground truth by $modification" }
            current = modification(current.clone())
        }

        val mcmc = MCMC(likelihood, modifications)
        checkConvergence(mcmc.simulation(current))
    }

    private fun checkConvergence(result: RootedTopology) {
        val realLikelihood = likelihood(groundTruth)
        val computedLikelihood = likelihood(result)
        val pdm = PathDifferenceMetric(groundTruthDistances, result.distanceMatrix.toArrays())
        log.info { "Likelihood difference: ${abs(realLikelihood - computedLikelihood)} PDM: $pdm" }
    }
}

fun main() {
    val generator = IndependentDataGenerator()
    val generationResult = generate(generator)

    val likelihood = BranchLikelihood(GENOME_LENGTH * SubstitutionModel.mutationRate) *
            RecombinationLikelihood(generationResult.genomes.size * SubstitutionModel.recombinationProbability)
    val modifications = listOf(
            ChangeRootModification(),
            HotspotMoveModification(generator.hotspots.toMutableList()),
            CancelRecombinationModification()
    )

    val checker = ConvergenceChecker(generationResult.rootedTopology, likelihood, modifications)
    for (i in 0 until 1_000) {
        checker.modifyAndRunMCMC()
    }
}