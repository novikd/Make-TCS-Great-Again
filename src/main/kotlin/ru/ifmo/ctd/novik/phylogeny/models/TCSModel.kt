package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.SimpleMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.emptyMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.metric.TCSMergeMetric

/**
 * @author Novik Dmitry ITMO University
 */
class TCSModel(
    private val distanceEvaluator: ClusterDistanceEvaluator
) : ClusterDistanceEvaluator by distanceEvaluator, IModel {

    override fun computeTree(taxonList: List<Taxon>): Cluster {
        taxonDistanceEvaluator.preprocessTaxonList(taxonList)

        val clusters = taxonList.map { x -> SimpleCluster(x) }.toMutableList<Cluster>()
        while (clusters.size > 1) {
            var mergingCandidate: MergingCandidate =
                emptyMergingCandidate()
            for (i in clusters.indices) {
                val firstCandidate = clusters[i]
                for (j in i + 1 until clusters.size) {
                    val secondCandidate = clusters[j]
                    val distance = evaluate(firstCandidate, secondCandidate)
                    if (distance < mergingCandidate.distance) {
                        mergingCandidate = SimpleMergingCandidate(
                            distance,
                            firstCandidate,
                            secondCandidate
                        )
                    }
                }
            }
            val newCluster = mergingCandidate.merge(
                taxonDistanceEvaluator,
                TCSMergeMetric(mergingCandidate.distance, taxonDistanceEvaluator)
            )

            clusters.remove(mergingCandidate.firstCandidate)
            clusters.remove(mergingCandidate.secondCandidate)
            clusters.add(newCluster)
        }
        return clusters.first()
    }
}