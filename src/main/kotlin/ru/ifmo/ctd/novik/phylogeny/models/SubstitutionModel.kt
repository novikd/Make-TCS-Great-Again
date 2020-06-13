package ru.ifmo.ctd.novik.phylogeny.models

object SubstitutionModel {
    val nucleotideProbability = mapOf('A' to 0.25, 'C' to 0.25, 'G' to 0.25, 'T' to 0.25)
    val relativeSubstitutionProbability = mapOf(
            'A' to mapOf('C' to 1.0 / 6, 'G' to 1.0 / 6, 'T' to 1.0 / 6),
            'C' to mapOf('A' to 1.0 / 6, 'G' to 1.0 / 6, 'T' to 1.0 / 6),
            'G' to mapOf('C' to 1.0 / 6, 'A' to 1.0 / 6, 'T' to 1.0 / 6),
            'T' to mapOf('C' to 1.0 / 6, 'G' to 1.0 / 6, 'A' to 1.0 / 6))
    var mutationRate: Double = 20.0/1000
    var recombinationProbability: Double = 0.001

    fun substitutionProbability(old: Char, new: Char): Double = relativeSubstitutionProbability[old]!![new]!!
}