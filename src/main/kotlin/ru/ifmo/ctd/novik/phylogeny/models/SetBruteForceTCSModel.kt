package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.network.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.network.merging.SetBruteForceMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.network.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.network.merging.SimpleMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.utils.BRUTE_FORCE_DISTANCE_THRESHOLD

/**
 * @author Dmitry Novik ITMO University
 */
class SetBruteForceTCSModel(distanceEvaluator: ClusterDistanceEvaluator) : TCSModel(distanceEvaluator) {
    override fun createMergingCandidate(first: Cluster, second: Cluster, genomNumber: Int): MergingCandidate {
        val distance = evaluate(first, second)
        assert(distance.value > 0) { "Can't create candidate with equal parts" }
        return if (distance.value > BRUTE_FORCE_DISTANCE_THRESHOLD || genomNumber >= 6_000)
            SimpleMergingCandidate(first, second, taxonDistanceEvaluator, distance)
        else
            SetBruteForceMergingCandidate(first, second, distance)
    }
}