package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.utils.ComparisonResult
import ru.ifmo.ctd.novik.phylogeny.utils.compareCandidates

/**
 * @author Novik Dmitry ITMO University
 */
abstract class AbstractModel : IModel {
    abstract val defaultMergingCandidate: MergingCandidate

    protected abstract fun createClusters(taxonList: List<Taxon>): MutableList<Cluster>
    protected abstract fun createMergingCandidate(first: Cluster, second: Cluster): MergingCandidate
    protected abstract fun mergeClusters(mergingCandidate: MergingCandidate): Cluster

    override fun computeTree(taxonList: List<Taxon>): Cluster {
        val clusters = createClusters(taxonList)

        while (clusters.size > 1) {
            var currentCandidate: MergingCandidate = defaultMergingCandidate

            for (i in clusters.indices) {
                val firstCandidate = clusters[i]
                for (j in i + 1 until clusters.size) {
                    val secondCandidate = clusters[j]
                    val newCandidate = createMergingCandidate(firstCandidate, secondCandidate)
                    if (compareCandidates(newCandidate, currentCandidate) == ComparisonResult.BETTER)
                        currentCandidate = newCandidate
                }
            }
            val newCluster = mergeClusters(currentCandidate)

            clusters.remove(currentCandidate.firstCandidate)
            clusters.remove(currentCandidate.secondCandidate)
            clusters.add(newCluster)
        }
        return clusters.first()
    }
}