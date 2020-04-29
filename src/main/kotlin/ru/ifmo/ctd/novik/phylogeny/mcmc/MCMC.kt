package ru.ifmo.ctd.novik.phylogeny.mcmc

import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.Likelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.Modification
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.GlobalRandom
import ru.ifmo.ctd.novik.phylogeny.utils.checkInvariant
import ru.ifmo.ctd.novik.phylogeny.utils.debug
import ru.ifmo.ctd.novik.phylogeny.utils.logger
import kotlin.math.ln
import kotlin.math.min

class MCMC(val likelihood: Likelihood, val modifications: List<Modification>, private val maxIterations: Int = 1_000) {
    private var iter: Int = 0

    companion object {
        val log = logger()
    }

    private fun shouldStop(): Boolean = iter == maxIterations

    fun simulation(startTopology: RootedTopology): RootedTopology {
        iter = 0
        var currentTopology = startTopology
        var currentLikelihood = likelihood(currentTopology)

        log.info { "\n******* START MCMC SIMULATION *******\n" }

        while (!shouldStop()) {
            ++iter
            val modification = modifications.random(GlobalRandom)
            val newTopology = modification(currentTopology.clone())
            val newLikelihood = likelihood(newTopology)

            val lnYX = min(newLikelihood - currentLikelihood, 0.0)

            if (newLikelihood > currentLikelihood || ln(GlobalRandom.nextDouble()) < lnYX) {
                log.info {
                    "Accepted $modification\nPrevious likelihood: $currentLikelihood\nNew likelihood: $newLikelihood"
                }
                currentTopology = newTopology
                currentLikelihood = newLikelihood
                debug {
                    log.info { "Invariant after modification: ${currentTopology.checkInvariant()}" }
                }
            }

            if (iter % 100 == 0) {
                log.info { "Processed $iter iterations. Current likelihood value: $currentLikelihood" }
            }
        }
        return currentTopology
    }
}