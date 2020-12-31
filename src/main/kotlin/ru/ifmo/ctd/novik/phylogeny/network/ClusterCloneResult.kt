package ru.ifmo.ctd.novik.phylogeny.network

data class ClusterCloneResult(val result: Cluster, val generation: MutableMap<Node, Node>)