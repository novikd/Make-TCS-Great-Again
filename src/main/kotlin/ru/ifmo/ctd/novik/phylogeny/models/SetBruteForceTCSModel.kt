package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.merging.SetBruteForceMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.SimpleMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.utils.BRUTE_FORCE_DISTANCE_THRESHOLD

/**
 * @author Dmitry Novik ITMO University
 */
class SetBruteForceTCSModel(distanceEvaluator: ClusterDistanceEvaluator) : TCSModel(distanceEvaluator) {
    override fun createMergingCandidate(first: Cluster, second: Cluster): MergingCandidate {
        val distance = evaluate(first, second)
        return if (distance.value > BRUTE_FORCE_DISTANCE_THRESHOLD)
            SimpleMergingCandidate(first, second, taxonDistanceEvaluator, distance)
        else
            SetBruteForceMergingCandidate(first, second, distance)
    }
}