package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistance
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.metric.BruteForceMergeMetric
import ru.ifmo.ctd.novik.phylogeny.tree.metric.MergeMetric
import ru.ifmo.ctd.novik.phylogeny.tree.metric.TCSMergeMetric
import ru.ifmo.ctd.novik.phylogeny.utils.BRUTE_FORCE_DISTANCE_THRESHOLD

/**
 * @author Dmitry Novik ITMO University
 */
class BruteForceMergingCandidate(firstCluster: Cluster,
                                 secondCluster: Cluster,
                                 taxonDistanceEvaluator: TaxonDistanceEvaluator,
                                 distance: ClusterDistance)
    : SimpleMergingCandidate(firstCluster, secondCluster, taxonDistanceEvaluator, distance) {

    override val mergeMetric: MergeMetric by lazy {
        if (distance.value > BRUTE_FORCE_DISTANCE_THRESHOLD)
            TCSMergeMetric(distance.value, taxonDistanceEvaluator)
        else
            BruteForceMergeMetric(taxonDistanceEvaluator)
    }
}