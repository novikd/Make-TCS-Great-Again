package ru.ifmo.ctd.novik.phylogeny.mcmc

import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.Likelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.Modification
import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.checkInvariant
import ru.ifmo.ctd.novik.phylogeny.utils.createCSVLogger
import ru.ifmo.ctd.novik.phylogeny.utils.debug
import ru.ifmo.ctd.novik.phylogeny.utils.logger
import kotlin.math.ln
import kotlin.math.min

internal const val LIKELIHOOD_DUMP_FILE = "likelihood_dump.csv"

class MCMC(val likelihood: Likelihood, val modifications: List<Modification>, private val maxIterations: Int = 10_000) {
    private var iter: Int = 0

    companion object {
        val log = logger()
    }

    private fun shouldStop(): Boolean = iter == maxIterations

    fun simulation(startTopology: RootedTopology): RootedTopology {
        iter = 0
        var currentTopology = startTopology
        var currentLikelihood = likelihood(currentTopology)

        log.info { "\n******* START MCMC SIMULATION *******\nInitial likelihood: $currentLikelihood\n" }

        createCSVLogger(LIKELIHOOD_DUMP_FILE, GlobalExecutionSettings.DUMP_LIKELIHOOD).use { likelihoodDump ->
            likelihoodDump.log("ITER", "LIKELIHOOD")
            while (!shouldStop()) {
                likelihoodDump.log(iter, currentLikelihood)
                ++iter
                val modification = modifications.random(GlobalExecutionSettings.RANDOM)
                val newTopology = modification(currentTopology.clone())
                val newLikelihood = likelihood(newTopology)

                val lnYX = min(newLikelihood - currentLikelihood, 0.0)

                if (newLikelihood > currentLikelihood || ln(GlobalExecutionSettings.RANDOM.nextDouble()) < lnYX) {
                    log.fine {
                        "Accepted $modification\nPrevious likelihood: $currentLikelihood\nNew likelihood: $newLikelihood"
                    }
                    currentTopology = newTopology
                    currentLikelihood = newLikelihood
                    debug {
                        log.fine { "Invariant after modification: ${currentTopology.checkInvariant()}" }
                    }
                }

                if (iter % 100 == 0) {
                    log.info { "Processed $iter iterations. Current likelihood value: $currentLikelihood" }
                }
            }
        }

        return currentTopology
    }
}