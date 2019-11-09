package ru.ifmo.ctd.novik.phylogeny.tree

typealias Path = MutableList<Node>

fun createEdge(v: Node, u: Node, directed: Boolean = false) {
    v.addNeighbor(u)
    if (!directed)
        u.addNeighbor(v)
}