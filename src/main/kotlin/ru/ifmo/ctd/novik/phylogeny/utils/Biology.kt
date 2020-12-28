package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.*

/**
 * @author Dmitry Novik ITMO University
 */

fun String.toGenome(): Genome = ConstantGenome(this)

fun String.toMutableGenome(): MutableGenome = GenomeWithOptionSet(this)

internal var taxonGenerator = generateSequence(ReconstructedTaxon(0)) { ReconstructedTaxon(it.id + 1) }.iterator()

fun createTaxon(): ReconstructedTaxon = taxonGenerator.next()

fun List<String>.toTaxa(): List<ObservedTaxon> =
    this.mapIndexed { index, sequence -> ObservedTaxon(index, "taxon$index", sequence.toGenome()) }

fun List<ObservedTaxon>.unify(): List<Taxon> {
    val sequenceToTaxa = mutableMapOf<String, MutableList<ObservedTaxon>>()
    forEach { taxon -> val taxaList = sequenceToTaxa.computeIfAbsent(taxon.genome.primary) { mutableListOf(taxon) }
        taxaList.add(taxon)
    }

    val result = mutableListOf<Taxon>()
    sequenceToTaxa.forEach { (_, taxa) ->
        if (taxa.size == 1)
            result.add(taxa.first())
        else
            result.add(CompositeTaxon(taxa))
    }
    result.sortBy { it.id }
    return result
}

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

fun computeDifference(firstGenome: Genome, secondGenome: Genome): Set<Pair<Int, Char>> {
    val result = mutableSetOf<Pair<Int, Char>>()
    for (i in firstGenome.primary.indices) {
        if (firstGenome.primary[i] != secondGenome.primary[i])
            result.add(Pair(i, secondGenome.primary[i]))
    }
    return result
}
