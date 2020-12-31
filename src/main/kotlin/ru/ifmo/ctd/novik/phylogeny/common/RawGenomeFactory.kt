package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
class RawGenomeFactory : GenomeFactory {
    override fun create(): MutableGenome = GenomeWithOptionSet()
}