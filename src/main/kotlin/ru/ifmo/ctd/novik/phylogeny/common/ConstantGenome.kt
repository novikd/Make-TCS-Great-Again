package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.utils.buildGenomeSequence

/**
 * @author Dmitry Novik ITMO University
 */
data class ConstantGenome(override val primary: String) : Genome {
    override val size: Int
        get() = 1

    override val isEmpty: Boolean
        get() = false

    override fun mutate(mutations: List<SNP>): Genome = ConstantGenome(buildGenomeSequence(primary, mutations))

    override fun contains(genome: String): Boolean = primary == genome

    override fun iterator(): Iterator<String> = listOf(primary).iterator()

    override fun toString(): String = primary

    override fun equals(other: Any?): Boolean {
        if (other !is Genome)
            return false
        return this.primary == other.primary
    }

    override fun hashCode(): Int = primary.hashCode()
}