package ru.ifmo.ctd.novik.phylogeny.network.metric

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.network.Node
import ru.ifmo.ctd.novik.phylogeny.network.merging.MergingMetaData
import ru.ifmo.ctd.novik.phylogeny.utils.*

open class BruteForceMergeMetric(private val distanceEvaluator: TaxonDistanceEvaluator) : MergeMetric {

    private enum class ProcessionResult {
        OK, FAIL
    }

    override fun compute(firstNode: Node, secondNode: Node, metaData: MergingMetaData): Int {
        if (firstNode.genome.isEmpty || secondNode.genome.isEmpty)
            return Int.MIN_VALUE
        val positions = computeDistinctPositions(metaData.firstRealTaxon, metaData.secondRealTaxon)

        val firstDistances = firstNode.computeGraphDistances()
        val secondDistances = secondNode.computeGraphDistances()

        val secondGenome = metaData.secondRealTaxon.genome.primary
        for (permutation in permutations(positions.size)) {
            val builder = StringBuilder(metaData.firstRealTaxon.genome.primary)

            var index = 0
            fun processPathPart(baseTaxon: Taxon, length: Int, distances: Map<Node, Int>): ProcessionResult {
                for (i in 0 until length) {
                    val pos = positions[permutation[index++]]
                    builder[pos] = secondGenome[pos]

                    val taxon = baseTaxon.copy(genome = builder.toString().toMutableGenome())
                    for (node in distances.keys) {
                        val distance = distanceEvaluator.evaluate(taxon, node.taxon)
                        if (distance.value < metaData.bridgeLength)
                            return ProcessionResult.FAIL
                    }
                }

                return ProcessionResult.OK
            }

            if (processPathPart(firstNode.taxon, metaData.firstClusterPart.size - 1, secondDistances) == ProcessionResult.FAIL) {
                continue
            }
            val newFirstTaxon = firstNode.taxon.copy(genome = builder.toString().toMutableGenome())

            for (i in 0 until metaData.bridgeLength) {
                val pos = positions[permutation[index++]]
                builder[pos] = secondGenome[pos]
            }
            val newSecondTaxon = secondNode.taxon.copy(genome = builder.toString().toMutableGenome())

            if (processPathPart(secondNode.taxon, metaData.secondClusterPart.size - 1, firstDistances) == ProcessionResult.FAIL) {
                continue
            }

            val firstMetric = computeDistanceDiff(newFirstTaxon, firstDistances)
            val secondMetric = computeDistanceDiff(newSecondTaxon, secondDistances)
            if (firstMetric != Int.MIN_VALUE && secondMetric != Int.MIN_VALUE)
                return -metaData.bridgeLength
        }
        return Int.MIN_VALUE
    }

    private fun computeDistanceDiff(taxon: Taxon, graphDistances: Map<Node, Int>): Int {
        var result = 0
        graphDistances.forEach { (node, graphDistance) ->
            val actualDistance = distanceEvaluator.evaluate(taxon, node.taxon)
            if (graphDistance != actualDistance.value)
                result = Int.MIN_VALUE
        }
        return result
    }
}