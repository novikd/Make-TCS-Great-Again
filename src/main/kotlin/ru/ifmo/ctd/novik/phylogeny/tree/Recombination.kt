package ru.ifmo.ctd.novik.phylogeny.tree

data class Recombination(val firstParent: Node, val secondParent: Node, val child: Node, val pos: Int) {
    override fun toString(): String {
        return "Recombination(firstParent=${firstParent.nodeName}, secondParent=${secondParent.nodeName}, child=${child.nodeName}, pos=$pos)"
    }
}