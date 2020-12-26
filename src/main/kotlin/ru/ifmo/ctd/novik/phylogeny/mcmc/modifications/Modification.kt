package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology

interface Modification {
    operator fun invoke(topology: RootedTopology): RootedTopology
}