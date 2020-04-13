package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.*

/**
 * @author Dmitry Novik ITMO University
 */

fun String.toGenome(): Genome = Genome(this)

fun String.toMutableGenome(): MutableGenome = MutableGenome(this)

internal var taxonGenerator = generateSequence (Taxon(0)) { Taxon(it.id + 1) }.iterator()

fun createTaxon(): Taxon = taxonGenerator.next()

fun computeDistinctPositions(lhs: String, rhs: String): List<Int> {
    val positions = mutableListOf<Int>()
    lhs.zip(rhs).forEachIndexed {
        index, (first, second) -> if (first != second) positions.add(index)
    }
    return positions
}

fun computeDistinctPositions(firstTaxon: Taxon, secondTaxon: Taxon): List<Int> {
    val firstGenome = firstTaxon.genome.primary
    val secondGenome = secondTaxon.genome.primary
    return computeDistinctPositions(firstGenome, secondGenome)
}

