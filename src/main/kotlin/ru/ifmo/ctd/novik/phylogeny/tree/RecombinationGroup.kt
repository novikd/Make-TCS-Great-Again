package ru.ifmo.ctd.novik.phylogeny.tree

import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.utils.genome

data class RecombinationGroup(
        val hotspot: Int,
        val elements: MutableList<Recombination> = mutableListOf(),
        var isUsed: Boolean = false
) {
    fun setUsed() {
        isUsed = true
    }

    fun add(recombination: Recombination) {
        elements.add(recombination)
    }

    operator fun contains(recombination: Recombination): Boolean {
        if (recombination.pos != hotspot)
            return false
        return elements.any { other ->
            hammingDistance(recombination.child.genome.primary, other.child.genome.primary) < 10
        }
    }
}