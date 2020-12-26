package ru.ifmo.ctd.novik.phylogeny.tools

import ru.ifmo.ctd.novik.phylogeny.mcmc.MCMC
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.BranchLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.Likelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.RecombinationLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.times
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.*
import ru.ifmo.ctd.novik.phylogeny.models.SubstitutionModel
import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.distanceMatrix
import ru.ifmo.ctd.novik.phylogeny.utils.logger
import ru.ifmo.ctd.novik.phylogeny.utils.toArrays
import java.io.File
import kotlin.math.abs

val output = File("convergence.txt")

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
        for (i in 0..1) {
            val modification = modifications.random(GlobalExecutionSettings.RANDOM)
            log.info { "Modifying ground truth by $modification" }
            current = modification(current.clone())
        }

        val mcmc = MCMC(likelihood, modifications)
//        output.appendText("BEFORE:\n")
//        checkConvergence(current)
//        output.appendText("AFTER:\n")
        checkConvergence(mcmc.simulation(current), current)
    }

    private fun checkConvergence(result: RootedTopology, start: RootedTopology) {
        val realLikelihood = likelihood(groundTruth)
        val computedLikelihood = likelihood(result)
        val pdm = PathDifferenceMetric(groundTruthDistances, result.distanceMatrix.toArrays())
        output.appendText("${likelihood(start)}; $computedLikelihood; ${abs(realLikelihood - computedLikelihood)}; $pdm\n")
//        output.appendText("Likelihood difference: ${abs(realLikelihood - computedLikelihood)} PDM: $pdm\n")
//        log.info { "Likelihood difference: ${abs(realLikelihood - computedLikelihood)} PDM: $pdm" }
    }
}

fun main() {
    val generator = IndependentDataGenerator(output = false)
    val generationResult = generate(generator)

    val likelihood = BranchLikelihood(GENOME_LENGTH * SubstitutionModel.mutationRate) *
            RecombinationLikelihood(generationResult.genomes.size * SubstitutionModel.recombinationProbability)
    val modifications = listOf(
            ChangeRootModification(),
//            NNIModification(),
//            SPRModification()//,
            HotspotMoveModification(generator.hotspots.toMutableList()),
            CancelRecombinationModification()
    )
    val checker = ConvergenceChecker(generationResult.rootedTopology, likelihood, modifications)

    output.writeText("")
    for (i in 0 until 1_000) {
        checker.modifyAndRunMCMC()
    }
}