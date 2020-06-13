package ru.ifmo.ctd.novik.phylogeny.tree

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.utils.genome
import java.lang.Integer.min

data class RecombinationGroup(
        val hotspot: Int,
        val elements: MutableList<Recombination> = mutableListOf(),
        var isUsed: Boolean = false
) {
    var ambassador: RecombinationGroupAmbassador? = null

    fun setUsed(ambassador: RecombinationGroupAmbassador) {
        isUsed = true
        this.ambassador = ambassador
    }

    fun setUnused() {
        isUsed = false
        this.ambassador = null
    }

    fun add(recombination: Recombination) {
        elements.add(recombination)
    }

    operator fun contains(childGenome: String): Boolean {
        return runBlocking {
            elements.map { other ->
                this.async { hammingDistance(childGenome, other.child.genome.primary) < 10 }
            }.map { it.await() }.any()
        }
    }

    operator fun contains(recombination: Recombination): Boolean {
        if (recombination.pos != hotspot)
            return false
        return elements.any { other ->
            hammingDistance(recombination.child.genome.primary, other.child.genome.primary) < 10
        }
    }

    fun contains(childIndex: Int, distances: Array<IntArray>): Boolean {
        return runBlocking {
            val subresults = mutableListOf<Deferred<Boolean>>()
            for (i in 0 until (elements.size + 999) / 1000) {
                val result = this.async {
                    for (j in i * 1000 until min(elements.size, (i + 1) * 1000)) {
                        if (distances[childIndex][elements[j].childIndex] < 10)
                            return@async true
                    }
                    return@async false
                }
                subresults.add(result)
            }
            val index = subresults.indexOfFirst { it.await() }
            if (index != -1) {
                for (i in index + 1 .. subresults.lastIndex) {
                    subresults[i].cancel()
                }
                return@runBlocking true
            }
            return@runBlocking false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecombinationGroup

        if (hotspot != other.hotspot) return false
        if (elements != other.elements) return false
        if (isUsed != other.isUsed) return false
        if (ambassador != other.ambassador) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hotspot
        result = 31 * result + elements.hashCode()
//        result = 31 * result + isUsed.hashCode()
//        result = 31 * result + (ambassador?.hashCode() ?: 0)
        return result
    }
}