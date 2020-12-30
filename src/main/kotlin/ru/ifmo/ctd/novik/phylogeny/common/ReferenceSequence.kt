package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.utils.buildGenomeSequence

/**
 * @author Dmitry Novik ITMO University
 */
data class ReferenceSequence(val sequence: String) {
    fun build(snpList: List<SNP>): String = buildGenomeSequence(sequence, snpList)

    fun computeSNP(genome: String): List<SNP> {
        val result = mutableListOf<SNP>()
        (sequence zip genome).forEachIndexed { index, (ref, curr) ->
            if (ref != curr)
                result.add(SNP(index, curr))
        }
        return result
    }

    fun computeSNP(option: GenomeOption): List<SNP> {
        if (option is CompressedGenomeOption)
            return option.polymorphism
        return computeSNP(option.toString())
    }
}
