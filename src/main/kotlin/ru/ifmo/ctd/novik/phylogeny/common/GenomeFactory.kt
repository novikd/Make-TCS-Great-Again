package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
interface GenomeFactory {
    fun create(): MutableGenome
}