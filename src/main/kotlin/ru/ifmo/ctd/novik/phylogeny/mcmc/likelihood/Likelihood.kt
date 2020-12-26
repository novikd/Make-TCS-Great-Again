package ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood

import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology

interface Likelihood {
    operator fun invoke(topology: RootedTopology): Double
}