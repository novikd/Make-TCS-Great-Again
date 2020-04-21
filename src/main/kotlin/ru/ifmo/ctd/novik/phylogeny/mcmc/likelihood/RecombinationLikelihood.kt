package ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood

import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology

class RecombinationLikelihood : Likelihood {
    override fun invoke(topology: RootedTopology): Double {
        return 0.0
    }
}