package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.network.Node

data class ClusterCloneResult(val result: Cluster, val generation: MutableMap<Node, Node>)