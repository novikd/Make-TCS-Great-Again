package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
data class Genome(override val primary: String) : IGenome {
    override fun isReal(): Boolean = true
    override fun process(action: String.() -> Unit) {
        action(primary)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is IGenome)
            return false
        return this.primary == other.primary
    }
}