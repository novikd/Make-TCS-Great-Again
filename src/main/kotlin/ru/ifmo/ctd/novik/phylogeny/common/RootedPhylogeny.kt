package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.tree.Node

data class RootedPhylogeny(val phylogeny: Phylogeny, val root: Node)