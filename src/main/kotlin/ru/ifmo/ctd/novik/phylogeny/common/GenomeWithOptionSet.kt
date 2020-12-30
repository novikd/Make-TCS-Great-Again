package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.utils.buildGenomeSequence
import ru.ifmo.ctd.novik.phylogeny.utils.toGenomeOption

/**
 * @author Dmitry Novik ITMO University
 */
class GenomeWithOptionSet() : MutableGenome {
    private val genomeOptions: MutableSet<GenomeOption> = mutableSetOf()
    override val primary: GenomeOption
        get() = genomeOptions.first()

    constructor(genome: String) : this() {
        genomeOptions.add(genome.toGenomeOption())
    }

    override val isEmpty: Boolean
        get() = genomeOptions.isEmpty()

    override fun mutate(mutations: List<SNP>): Genome =
        ConstantGenome(buildGenomeSequence(primary.toString(), mutations))

    override fun add(option: GenomeOption) = genomeOptions.add(option)

    override fun addAll(collection: Collection<GenomeOption>) = genomeOptions.addAll(collection)

    override fun remove(option: GenomeOption) = genomeOptions.remove(option)

    override fun removeIf(predicate: (GenomeOption) -> Boolean) = genomeOptions.removeIf(predicate)

    override fun replace(newOptions: List<GenomeOption>) {
        genomeOptions.clear()
        genomeOptions.addAll(newOptions)
    }

    override val size: Int
        get() = genomeOptions.size

    override fun toString(): String {
        return if (isEmpty) "unknown genome" else primary.toString()
    }

    override fun contains(option: GenomeOption): Boolean = genomeOptions.contains(option)

    override fun iterator(): Iterator<GenomeOption> = genomeOptions.iterator()
}