package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Dmitry Novik ITMO University
 */
data class CompressedConstantGenome(
    override val reference: ReferenceSequence,
    override val polymorphism: List<SNP>
) : CompressedGenome {
    override val primary = CompressedGenomeOptionImpl(reference, polymorphism)

    override val size: Int
        get() = 1

    override val isEmpty: Boolean
        get() = false

    override fun mutate(mutations: List<SNP>): Genome {
        var i = 0
        var j = 0

        val resultMutations = mutableListOf<SNP>()
        while (i < polymorphism.size && j < mutations.size) {
            val iSNP = polymorphism[i]
            val jSNP = mutations[j]
            when {
                iSNP.index < jSNP.index -> {
                    resultMutations.add(iSNP)
                    ++i
                }
                iSNP.index == jSNP.index -> {
                    if (reference.sequence[jSNP.index] != jSNP.value)
                        resultMutations.add(jSNP)
                    ++i
                    ++j
                }
                iSNP.index > jSNP.index -> {
                    if (reference.sequence[jSNP.index] != jSNP.value)
                        resultMutations.add(jSNP)
                    ++j
                }
            }
        }

        while (i < polymorphism.size) {
            resultMutations.add(polymorphism[i])
            ++i
        }
        while (j < mutations.size) {
            val jSNP = mutations[j]
            if (reference.sequence[jSNP.index] != jSNP.value)
                resultMutations.add(jSNP)
            ++j
        }
        return CompressedConstantGenome(reference, resultMutations)
    }

    override fun toString(): String = primary.toString()

    override fun contains(option: GenomeOption): Boolean = polymorphism == reference.computeSNP(option)

    override fun iterator() = listOf(primary).iterator()
}
