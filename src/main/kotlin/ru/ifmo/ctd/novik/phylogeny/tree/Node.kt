package ru.ifmo.ctd.novik.phylogeny.tree

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.utils.Path
import ru.ifmo.ctd.novik.phylogeny.utils.createTaxon

/**
 * @author Novik Dmitry ITMO University
 */
data class Node(val taxon: Taxon) {
    val isRealTaxon = taxon.genome.isReal()
    var nodeName: String = if (taxon.genome.isReal()) taxon.genome.primary else taxon.name
    val neighbors: MutableList<Node> = mutableListOf()

    constructor() : this(createTaxon())

    fun connect(node: Node) {
        neighbors.add(node)
    }

    fun getAdjacentTaxonList(): List<Path> {
        fun dfs(node: Node, prev: Node? = null): MutableList<Path> {
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

        val result = dfs(this)
        if (result.isEmpty())
            result.add(mutableListOf(this))
        return result
    }

    override fun toString(): String {
        return nodeName
    }
}

