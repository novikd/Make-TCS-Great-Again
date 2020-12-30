package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.utils.toGenomeOption

/**
 * @author Dmitry Novik ITMO University
 */
interface GenomeOption {
    val length: Int

    operator fun get(index: Int): Char

    fun subSequence(from: Int, to: Int): GenomeOptionSubsequence

    fun mutate(snp: SNP): GenomeOption
}

interface CompressedGenomeOption : GenomeOption {
    val reference: ReferenceSequence
    val polymorphism: List<SNP>
}

interface GenomeOptionSubsequence : GenomeOption {
    val origin: GenomeOption
    val from: Int
    val to: Int

    override val length: Int
        get() = to - from

    override fun get(index: Int): Char {
        assert(index < length) { "index must be in range [0, subsequence length)" }
        return origin[from + index]
    }

    override fun subSequence(from: Int, to: Int): GenomeOptionSubsequence = error("Can't take a subsequence of another subsequence")

    override fun mutate(snp: SNP): GenomeOption = error("Subsequence must not be modified")
}

data class StringGenomeOptionSubsequence(
    override val origin: GenomeOption,
    override val from: Int,
    override val to: Int
) : GenomeOptionSubsequence {
    override fun toString(): String = origin.toString().substring(from, to)
}

data class CompressedGenomeOptionSubsequence(
    override val polymorphism: List<SNP>,
    val compressedOrigin: CompressedGenomeOption,
    override val from: Int,
    override val to: Int
) : GenomeOptionSubsequence, CompressedGenomeOption {
    override val reference: ReferenceSequence
        get() = compressedOrigin.reference
    override val origin: GenomeOption
        get() = compressedOrigin

    override fun toString(): String = origin.toString().substring(from, to)
}

data class StringGenomeOption(val value: String) : GenomeOption {
    override val length: Int
        get() = value.length

    override fun get(index: Int): Char = value[index]

    override fun subSequence(from: Int, to: Int): GenomeOptionSubsequence = StringGenomeOptionSubsequence(this, from, to)

    override fun mutate(snp: SNP): GenomeOption = buildString(length) {
        append(value)
        setCharAt(snp.index, snp.value)
    }.toGenomeOption()

    override fun toString(): String = value
}

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

interface Genome : Iterable<GenomeOption> {
    val primary: GenomeOption
    val size: Int
    val isEmpty: Boolean

    fun mutate(mutations: List<SNP>): Genome
    fun contains(option: GenomeOption): Boolean
}