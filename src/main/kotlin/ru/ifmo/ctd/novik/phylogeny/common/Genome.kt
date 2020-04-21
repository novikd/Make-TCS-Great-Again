package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
data class Genome(override val primary: String) : IGenome {
    override val isReal: Boolean
        get() = true

    override val size: Int
        get() = 1

    override val isEmpty: Boolean
        get() = false

    override fun contains(genome: String): Boolean = primary == genome

    override fun iterator(): Iterator<String> = listOf(primary).iterator()

    override fun toString(): String = primary

    override fun equals(other: Any?): Boolean {
        if (other !is IGenome)
            return false
        return this.primary == other.primary
    }

    override fun hashCode(): Int = primary.hashCode()
}