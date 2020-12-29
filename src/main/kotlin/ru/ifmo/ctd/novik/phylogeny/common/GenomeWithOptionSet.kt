package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
class GenomeWithOptionSet() : MutableGenome {
    private val genomeOptions: MutableSet<String> = mutableSetOf()
    override val primary: String
        get() = genomeOptions.first()

    constructor(genome: String) : this() {
        genomeOptions.add(genome)
    }

    override val isEmpty: Boolean
        get() = genomeOptions.isEmpty()

    override fun add(genome: String) = genomeOptions.add(genome)

    override fun addAll(collection: Collection<String>) = genomeOptions.addAll(collection)

    override fun remove(genome: String) = genomeOptions.remove(genome)

    override fun removeIf(predicate: (String.() -> Boolean)) = genomeOptions.removeIf(predicate)

    override fun replace(newOptions: List<String>) {
        genomeOptions.clear()
        genomeOptions.addAll(newOptions)
    }

    override val size: Int
        get() = genomeOptions.size

    override fun toString(): String {
        return if (isEmpty) "unknown genome" else primary
    }

    override fun contains(genome: String): Boolean = genomeOptions.contains(genome)

    override fun iterator(): Iterator<String> = genomeOptions.iterator()
}