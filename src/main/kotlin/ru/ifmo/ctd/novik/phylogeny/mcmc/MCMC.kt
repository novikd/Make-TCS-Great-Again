package ru.ifmo.ctd.novik.phylogeny.mcmc

import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.Likelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.Modification
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import kotlin.random.Random

class MCMC(val likelihood: Likelihood, val modifications: List<Modification>, private val maxIterations: Int = 1_000) {
    private var iter: Int = 0

    private fun shouldStop(): Boolean = iter == maxIterations

    fun simulation(startTopology: RootedTopology): RootedTopology {
        iter = 0
        var currentTopology = startTopology
        var currentLikelihood = likelihood(currentTopology)

        while (!shouldStop()) {
            ++iter
            val modification = modifications.random()
            val newTopology = modification(currentTopology.clone())
            val newLikelihood = likelihood(newTopology)

            if (newLikelihood > currentLikelihood || Random.nextDouble() > 0.5) {
                currentTopology = newTopology
                currentLikelihood = newLikelihood
            }
        }
        return currentTopology
    }
}