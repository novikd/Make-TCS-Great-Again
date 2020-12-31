package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface CompressedGenomeOption : GenomeOption {
    val reference: ReferenceSequence
    val polymorphism: List<SNP>
}