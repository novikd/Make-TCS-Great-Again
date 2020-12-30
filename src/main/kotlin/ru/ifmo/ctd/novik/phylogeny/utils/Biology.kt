package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.*

/**
 * @author Dmitry Novik ITMO University
 */

fun buildGenomeSequence(reference: String, snpList: List<SNP>): String = buildString(reference.length) {
    append(reference)
    snpList.forEach { (index, symbol) -> setCharAt(index, symbol) }
}

fun String.toGenomeOption(): GenomeOption = StringGenomeOption(this)

fun String.toGenome(): Genome = ConstantGenome(this)

fun String.toMutableGenome(): MutableGenome = GenomeWithOptionSet(this)

fun GenomeOption.toMutableGenome(): MutableGenome {
    if (this is CompressedGenomeOption) {
        val genome = CompressedGenomeWithOptionSet(reference)
        genome.add(this)
        return genome
    }
    return this.toString().toMutableGenome()
}

fun Genome.compress(reference: ReferenceSequence): CompressedGenome {
    if (this is CompressedGenome)
        return this
    return CompressedConstantGenome(reference, reference.computeSNP(primary.toString()))
}

internal var taxonGenerator = generateSequence(ReconstructedTaxon(0)) { ReconstructedTaxon(it.id + 1) }.iterator()

fun createTaxon(): ReconstructedTaxon = taxonGenerator.next()

fun createTaxon(genome: Genome): ReconstructedTaxon = createTaxon().copy(genome = genome)

fun List<String>.toTaxa(): List<ObservedTaxon> =
    this.mapIndexed { index, sequence -> ObservedTaxon(index, "taxon$index", sequence.toGenome()) }

fun List<ObservedTaxon>.compress(): List<ObservedTaxon> {
    val counters = Array<MutableMap<Char, Int>>(first().genome.primary.length) { mutableMapOf() }
    forEach {
        it.genome.primary.toString().forEachIndexed { index, char ->
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
    forEach { taxon -> val taxaList = sequenceToTaxa.computeIfAbsent(taxon.genome.primary.toString()) { mutableListOf() }
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

fun computeDistinctPositions(lhs: CompressedGenomeOption, rhs: CompressedGenomeOption): List<Int> {
    var i = 0
    var j = 0

    val positions = mutableListOf<Int>()
    val leftPolymorphism = lhs.polymorphism
    val rightPolymorphism = rhs.polymorphism
    while (i < leftPolymorphism.size && j < rightPolymorphism.size) {
        val iSNP = leftPolymorphism[i]
        val jSNP = rightPolymorphism[j]
        when {
            iSNP.index < jSNP.index -> {
                positions.add(iSNP.index)
                ++i
            }
            iSNP.index == jSNP.index -> {
                if (iSNP.value != jSNP.value)
                    positions.add(jSNP.index)
                ++i
                ++j
            }
            iSNP.index > jSNP.index -> {
                positions.add(jSNP.index)
                ++j
            }
        }
    }

    while (i < leftPolymorphism.size) {
        positions.add(leftPolymorphism[i].index)
        ++i
    }
    while (j < rightPolymorphism.size) {
        positions.add(rightPolymorphism[j].index)
        ++j
    }
    return positions
}

fun computeDistinctPositions(lhs: GenomeOption, rhs: GenomeOption): List<Int> {
    if (lhs is CompressedGenomeOption && rhs is CompressedGenomeOption)
        return computeDistinctPositions(lhs, rhs)
    val positions = mutableListOf<Int>()
    (lhs.toString() zip rhs.toString()).forEachIndexed {
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
    val firstPrimary = firstGenome.primary.toString()
    val secondPrimary = secondGenome.primary.toString()
    for (i in firstPrimary.indices) {
        if (firstPrimary[i] != secondPrimary[i])
            result.add(Pair(i, secondPrimary[i]))
    }
    return result
}
