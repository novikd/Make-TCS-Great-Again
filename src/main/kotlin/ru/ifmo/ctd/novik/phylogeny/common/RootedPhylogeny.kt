package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.network.Node

data class RootedPhylogeny(val phylogeny: Phylogeny, val root: Node)