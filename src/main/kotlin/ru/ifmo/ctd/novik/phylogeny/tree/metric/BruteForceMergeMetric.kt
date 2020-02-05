package ru.ifmo.ctd.novik.phylogeny.tree.metric

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingMetaData
import ru.ifmo.ctd.novik.phylogeny.utils.computeGraphDistances
import ru.ifmo.ctd.novik.phylogeny.utils.permutations
import ru.ifmo.ctd.novik.phylogeny.utils.toMutableGenome

class BruteForceMergeMetric(private val distanceEvaluator: TaxonDistanceEvaluator) : MergeMetric {

    private enum class ProcessionResult {
        OK, FAIL
    }

    override fun compute(firstNode: Node, secondNode: Node, metaData: MergingMetaData): Int {
        val positions = getMutationPosition(metaData.firstRealTaxon.taxon, metaData.secondRealTaxon.taxon)

        val firstDistances = firstNode.computeGraphDistances()
        val secondDistances = secondNode.computeGraphDistances()

        for (permutation in permutations(positions.size)) {
            val secondGenome = metaData.secondRealTaxon.taxon.genome.primary
            val builder = StringBuilder(metaData.firstRealTaxon.taxon.genome.primary)

            var index = 0
            fun processPathPart(baseTaxon: Taxon, length: Int, distances: Map<Node, Int>): ProcessionResult {
                for (i in 0 until length) {
                    val pos = positions[permutation[index++]]
                    builder[pos] = secondGenome[pos]

                    val taxon = baseTaxon.copy(genome = builder.toString().toMutableGenome())
                    for (node in distances.keys) {
                        if (distanceEvaluator.evaluate(taxon, node.taxon) < metaData.bridgeLength)
                            return ProcessionResult.FAIL
                    }
                }

                return ProcessionResult.OK
            }

            if (processPathPart(firstNode.taxon, metaData.firstDistance, secondDistances) == ProcessionResult.FAIL) {
                continue
            }
            val newFirstTaxon = firstNode.taxon.copy(genome = builder.toString().toMutableGenome())

            for (i in 0 until metaData.bridgeLength) {
                val pos = positions[permutation[index++]]
                builder[pos] = secondGenome[pos]
            }
            val newSecondTaxon = secondNode.taxon.copy(genome = builder.toString().toMutableGenome())

            if (processPathPart(secondNode.taxon, metaData.secondDistance, firstDistances) == ProcessionResult.FAIL) {
                continue
            }

            val firstMetric = computeDistanceDiff(newFirstTaxon, firstDistances)
            val secondMetric = computeDistanceDiff(newSecondTaxon, secondDistances)
            if (firstMetric != Int.MIN_VALUE && secondMetric != Int.MIN_VALUE)
                return -metaData.bridgeLength
        }
        return Int.MIN_VALUE
    }

    private fun getMutationPosition(firstTaxon: Taxon, secondTaxon: Taxon): List<Int> {
        val firstGenome = firstTaxon.genome.primary
        val secondGenome = secondTaxon.genome.primary
        val positions = mutableListOf<Int>()
        firstGenome.zip(secondGenome).forEachIndexed {
            index, (first, second) -> if (first != second) positions.add(index)
        }
        return positions
    }

    private fun computeDistanceDiff(taxon: Taxon, graphDistances: Map<Node, Int>): Int {
        var result = 0
        graphDistances.forEach { (node, graphDistance) ->
            val actualDistance = distanceEvaluator.evaluate(taxon, node.taxon)
            if (graphDistance != actualDistance)
                result = Int.MIN_VALUE
        }
        return result
    }
}