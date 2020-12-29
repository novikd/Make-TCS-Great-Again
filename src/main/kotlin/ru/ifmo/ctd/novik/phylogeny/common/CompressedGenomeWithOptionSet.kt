package ru.ifmo.ctd.novik.phylogeny.common

data class CompressedGenomeWithOptionSet(override val reference: ReferenceSequence) : CompressedGenome, MutableGenome {
    private val polymorphismOptions = mutableSetOf<List<SNP>>()

    override fun add(genome: String): Boolean = polymorphismOptions.add(reference.computeSNP(genome))

    override fun addAll(collection: Collection<String>): Boolean {
        return polymorphismOptions.addAll(collection.map { reference.computeSNP(it) })
    }

    override fun remove(genome: String): Boolean = polymorphismOptions.remove(reference.computeSNP(genome))

    override fun removeIf(predicate: String.() -> Boolean): Boolean =
        polymorphismOptions.removeIf { predicate(reference.build(it)) }

    override fun replace(newOptions: List<String>) {
        polymorphismOptions.clear()
        addAll(newOptions)
    }

    override val primary: String
        get() = reference.build(polymorphismOptions.first())

    override val size: Int
        get() = polymorphismOptions.size

    override val isEmpty: Boolean
        get() = polymorphismOptions.isEmpty()

    override fun mutate(mutations: List<SNP>): Genome =
        CompressedConstantGenome(reference, polymorphism).mutate(mutations)

    override fun contains(genome: String): Boolean = polymorphismOptions.contains(reference.computeSNP(genome))

    override fun iterator(): Iterator<String> = polymorphismOptions.map { reference.build(it) }.iterator()

    override val polymorphism: List<SNP>
        get() = polymorphismOptions.first()
}
