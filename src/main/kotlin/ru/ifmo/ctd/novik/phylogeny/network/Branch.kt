package ru.ifmo.ctd.novik.phylogeny.network

data class Branch(val nodes: List<Node>) {
    val length: Int
        get() = nodes.size - 1
}