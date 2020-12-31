package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
class CompressedGenomeFactory(val reference: ReferenceSequence) : GenomeFactory {
    override fun create(): MutableGenome = CompressedGenomeWithOptionSet(reference)
}