package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance

/**
 * @author Dmitry Novik ITMO University
 */
class AbsoluteTaxonDistanceEvaluator : AbstractTaxonDistanceEvaluator() {
    override fun evaluate(lhs: Taxon, rhs: Taxon): TaxonDistance {
        val options = runBlocking {
            lhs.genome.map { lhsGenome ->
                this.async {
                    var currentDistance = Int.MAX_VALUE
                    val genomePairs = mutableListOf<Pair<String, String>>()

                    rhs.genome.map { rhsGenome ->
                        this.async {
                            Pair(rhsGenome, hammingDistance(lhsGenome, rhsGenome))
                        }
                    }.forEach {
                        val (rhsGenome, distance) = it.await()
                        if (distance < currentDistance) {
                            currentDistance = distance
                            genomePairs.clear()
                            genomePairs.add(Pair(lhsGenome, rhsGenome))
                        }
                    }
                    return@async Pair(currentDistance, genomePairs)
                }
            }.map { it.await() }
        }
        val (currentDistance, genomePairs) = options.fold(Pair(Int.MAX_VALUE, mutableListOf<Pair<String, String>>())) { acc, pair ->
            when {
                acc.first < pair.first -> acc
                acc.first > pair.first -> pair
                else -> Pair(acc.first, (acc.second + pair.second).toMutableList())
            }
        }
        return TaxonDistance(currentDistance, genomePairs)
    }
}