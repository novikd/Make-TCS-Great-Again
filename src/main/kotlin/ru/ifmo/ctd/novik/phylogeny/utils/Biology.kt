package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.*

/**
 * @author Dmitry Novik ITMO University
 */

fun buildGenomeSequence(reference: String, snpList: List<SNP>): String = buildString(reference.length) {
    append(reference)
    snpList.forEach { (index, symbol) -> setCharAt(index, symbol) }
}

fun String.toGenome(): Genome = ConstantGenome(this)

fun String.toMutableGenome(): MutableGenome = GenomeWithOptionSet(this)

fun Genome.compress(reference: ReferenceSequence): CompressedGenome {
    if (this is CompressedGenome)
        return this
    return CompressedConstantGenome(reference, reference.computeSNP(primary))
}

internal var taxonGenerator = generateSequence(ReconstructedTaxon(0)) { ReconstructedTaxon(it.id + 1) }.iterator()

fun createTaxon(): ReconstructedTaxon = taxonGenerator.next()

fun createTaxon(genome: Genome): ReconstructedTaxon = createTaxon().copy(genome = genome)

fun List<String>.toTaxa(): List<ObservedTaxon> =
    this.mapIndexed { index, sequence -> ObservedTaxon(index, "taxon$index", sequence.toGenome()) }

fun List<ObservedTaxon>.compress(): List<ObservedTaxon> {
    val counters = Array<MutableMap<Char, Int>>(first().genome.primary.length) { mutableMapOf() }
    forEach {
        it.genome.primary.forEachIndexed { index, char ->
            val map = counters[index]
            val value = map.getOrDefault(char, 0)
            map[char] = value + 1
        }
    }

    val sequence = buildString(counters.size) {
        counters.forEach { counter ->
            val argmax = counter.maxByOrNull { it.value }!!.key
            append(argmax)
        }
    }
    val reference = ReferenceSequence(sequence)

    return map { it.copy(genome = it.genome.compress(reference)) }
}

fun List<ObservedTaxon>.unify(): List<Taxon> {
    val sequenceToTaxa = mutableMapOf<String, MutableList<ObservedTaxon>>()
    forEach { taxon -> val taxaList = sequenceToTaxa.computeIfAbsent(taxon.genome.primary) { mutableListOf() }
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
