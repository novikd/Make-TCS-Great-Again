package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
class MutableGenome() : IGenome {
    private val genomeOptions: MutableSet<String> = mutableSetOf()
    override val primary: String
        get() = genomeOptions.first()

    constructor(genome: String) : this() {
        genomeOptions.add(genome)
    }

    override val isReal: Boolean
        get() = false

    fun add(genome: String) = genomeOptions.add(genome)

    fun addAll(collection: Collection<String>) = collection.forEach { x -> genomeOptions.add(x) }

    fun remove(genome: String) = genomeOptions.remove(genome)

    val size: Int
        get() = genomeOptions.size

    override fun contains(genome: String): Boolean = genomeOptions.contains(genome)

    override fun iterator(): Iterator<String> = genomeOptions.iterator()
}