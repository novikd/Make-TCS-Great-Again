package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
class MutableGenome() : IGenome {
    val genomeOptions: MutableSet<String> = mutableSetOf()
    override val primary: String
        get() = genomeOptions.first()

    constructor(genome: String) : this() {
        genomeOptions.add(genome)
    }

    override fun isReal(): Boolean = false
    override fun process(action: String.() -> Unit) = genomeOptions.forEach(action)
}