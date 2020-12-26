package ru.ifmo.ctd.novik.phylogeny.events

import ru.ifmo.ctd.novik.phylogeny.network.Node

data class Recombination(val firstParent: Node, val secondParent: Node, val child: Node, val pos: Int, val childIndex: Int) {
    override fun toString(): String {
        return "Recombination(firstParent=${firstParent.nodeName}, secondParent=${secondParent.nodeName}, child=${child.nodeName}, pos=$pos)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Recombination) return false

        return pos == other.pos && child == other.child
                && (firstParent == other.firstParent && secondParent == other.secondParent
                    || firstParent == other.secondParent && secondParent == other.firstParent)
    }

    override fun hashCode(): Int {
        var result = firstParent.hashCode()
        result = 31 * result + secondParent.hashCode()
        result = 31 * result + child.hashCode()
        result = 31 * result + pos
        result = 31 * result + childIndex
        return result
    }
}