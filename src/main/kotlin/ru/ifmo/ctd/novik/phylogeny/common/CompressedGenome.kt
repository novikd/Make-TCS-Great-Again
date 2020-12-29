package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface CompressedGenome : Genome {
    val reference: ReferenceSequence
    val polymorphism: List<SNP>
}