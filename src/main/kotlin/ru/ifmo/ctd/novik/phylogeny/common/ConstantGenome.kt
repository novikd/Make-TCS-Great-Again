package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.utils.buildGenomeSequence

/**
 * @author Dmitry Novik ITMO University
 */
data class ConstantGenome(val sequence: String) : Genome {
    override val primary: GenomeOption = StringGenomeOption(sequence)

    override val size: Int
        get() = 1

    override val isEmpty: Boolean
        get() = false

    override fun mutate(mutations: List<SNP>): Genome = ConstantGenome(buildGenomeSequence(sequence, mutations))

    override fun contains(option: GenomeOption): Boolean = sequence == option.toString()

    override fun iterator(): Iterator<GenomeOption> = listOf(primary).iterator()

    override fun toString(): String = sequence

    override fun equals(other: Any?): Boolean {
        if (other !is Genome)
            return false
        return this.primary == other.primary
    }

    override fun hashCode(): Int = sequence.hashCode()
}