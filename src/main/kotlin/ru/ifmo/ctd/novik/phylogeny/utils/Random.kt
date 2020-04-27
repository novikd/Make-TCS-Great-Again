package ru.ifmo.ctd.novik.phylogeny.utils

import kotlin.math.ln
import kotlin.random.Random

var GlobalRandom = Random(0)

class LnPoissonProbabilityMassFunction(private val lambda: Double) {
    operator fun invoke(k: Int): Double = -lambda +
            generateSequence(1) { it + 1 }
                    .take(k)
                    .map { ln(lambda) - ln(it.toDouble()) }.sum()
}
