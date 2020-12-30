package ru.ifmo.ctd.novik.phylogeny.common

data class CompressedGenomeWithOptionSet(override val reference: ReferenceSequence) : CompressedGenome, MutableGenome {
    private val polymorphismOptions = mutableSetOf<List<SNP>>()

    override fun add(option: GenomeOption): Boolean = polymorphismOptions.add(reference.computeSNP(option))

    override fun addAll(collection: Collection<GenomeOption>): Boolean {
        return polymorphismOptions.addAll(collection.map { reference.computeSNP(it) })
    }

    override fun remove(option: GenomeOption): Boolean = polymorphismOptions.remove(reference.computeSNP(option))

    override fun removeIf(predicate: (GenomeOption) -> Boolean): Boolean =
        polymorphismOptions.removeIf { predicate(CompressedGenomeOptionImpl(reference, it)) }

    override fun replace(newOptions: List<GenomeOption>) {
        polymorphismOptions.clear()
        addAll(newOptions)
    }

    override val primary: GenomeOption
        get() = CompressedGenomeOptionImpl(reference, polymorphismOptions.first())

    override val size: Int
        get() = polymorphismOptions.size

    override val isEmpty: Boolean
        get() = polymorphismOptions.isEmpty()

    override fun mutate(mutations: List<SNP>): Genome =
        CompressedConstantGenome(reference, polymorphism).mutate(mutations)

    override fun contains(option: GenomeOption): Boolean = polymorphismOptions.contains(reference.computeSNP(option))

    override fun iterator(): Iterator<GenomeOption> = polymorphismOptions.map { CompressedGenomeOptionImpl(reference, it) }.iterator()

    override val polymorphism: List<SNP>
        get() = polymorphismOptions.first()
}
