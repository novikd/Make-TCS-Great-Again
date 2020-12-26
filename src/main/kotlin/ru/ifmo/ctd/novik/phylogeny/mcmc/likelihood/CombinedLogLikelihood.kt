package ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood

import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology

/**
 * This class represents likelihood function, which can be represented as multiplication of two likelihood terms.
 * Both terms must be computed as log-likelihood.
 */
class CombinedLogLikelihood(val firstTerm: Likelihood, val secondTerm: Likelihood) : Likelihood {
    override fun invoke(topology: RootedTopology): Double = firstTerm(topology) + secondTerm(topology)
}

operator fun Likelihood.times(other: Likelihood): Likelihood = CombinedLogLikelihood(this, other)