package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
data class CompressedGenomeOptionImpl(
    override val reference: ReferenceSequence,
    override val polymorphism: List<SNP>
) : CompressedGenomeOption {
    private val sequence by lazy { reference.build(polymorphism) }
    override val length: Int
        get() = reference.sequence.length

    override fun get(index: Int): Char {
        val pos = polymorphism.binarySearch { it.index.compareTo(index) }
        return if (pos < 0)
            reference.sequence[index]
        else
            polymorphism[pos].value
    }

    override fun subSequence(from: Int, to: Int): GenomeOptionSubsequence {
        val range = from until to
        val newPolymorphism = mutableListOf<SNP>()
        polymorphism.forEach { snp ->
            if (snp.index in range)
                newPolymorphism.add(SNP(snp.index - from, snp.value))
        }
        return CompressedGenomeOptionSubsequence(newPolymorphism, this, from, to)
    }

    override fun mutate(snp: SNP): GenomeOption {
        val newPolymorphism = mutableListOf<SNP>()
        polymorphism.forEach {
            if (it.index != snp.index)
                newPolymorphism.add(it)
            else if (reference.sequence[snp.index] != snp.value)
                newPolymorphism.add(snp)
        }
        return CompressedGenomeOptionImpl(reference, newPolymorphism)
    }

    override fun toString(): String = sequence
}