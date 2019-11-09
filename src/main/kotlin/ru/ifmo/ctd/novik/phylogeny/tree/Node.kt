package ru.ifmo.ctd.novik.phylogeny.tree

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.common.createTaxon

/**
 * @author Novik Dmitry ITMO University
 */
data class Node(val taxon: Taxon) {
    val isRealTaxon = !taxon.genome.startsWith("intermediate")
    var neighbors: Array<Node> = emptyArray()

    constructor() : this(createTaxon())

    fun addNeighbor(node: Node) {
        neighbors += node
    }

    private fun dfs(node: Node, prev: Node? = null): MutableList<Path> {
        val result = mutableListOf<Path>()
        if (prev != null && node.isRealTaxon) {
            result.add(mutableListOf())
            return result
        }

        for (neighbor in node.neighbors) {
            if (neighbor == prev)
                continue
            result.addAll(dfs(neighbor, node))
        }

        result.forEach { x -> x.add(node) }
        return result
    }

    fun getAdjacentTaxonList(): List<Path> {
        val result = dfs(this)
        if (result.isEmpty())
            result.add(mutableListOf(this))
        return result
    }

    override fun toString(): String {
        return taxon.genome
    }
}

