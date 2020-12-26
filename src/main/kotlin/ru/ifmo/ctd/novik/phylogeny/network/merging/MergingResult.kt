package ru.ifmo.ctd.novik.phylogeny.network.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.network.Branch

data class MergingResult(val cluster: Cluster, val branch: Branch)