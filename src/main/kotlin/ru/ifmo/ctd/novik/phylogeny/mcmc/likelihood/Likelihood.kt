package ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood

import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology

interface Likelihood {
    operator fun invoke(topology: RootedTopology): Double
}