package ru.ifmo.ctd.novik.phylogeny.utils

import kotlin.math.pow

class PoissonProbabilityMassFunction(private val lambda: Double) {
    operator fun invoke(k: Int): Double =
            Math.E.pow(-lambda) * generateSequence(1) { it + 1 }
                    .take(k)
                    .map { lambda / k }
                    .fold(1.0, Double::times)
}
