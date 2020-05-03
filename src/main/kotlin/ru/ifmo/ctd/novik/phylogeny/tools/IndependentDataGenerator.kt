package ru.ifmo.ctd.novik.phylogeny.tools

import ru.ifmo.ctd.novik.phylogeny.common.Genome
import ru.ifmo.ctd.novik.phylogeny.common.MutableGenome
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.BranchLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.RecombinationLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.times
import ru.ifmo.ctd.novik.phylogeny.models.SubstitutionModel
import ru.ifmo.ctd.novik.phylogeny.tree.*
import ru.ifmo.ctd.novik.phylogeny.utils.*
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random

private val LOCAL_RANDOM = Random(2)

const val P_RECOMBINATION = 0.02

const val RESULT_GENOMES_NUMBER = 100

const val GENOME_LENGTH = 400

class IndependentDataGenerator {
    private var id = 0

    val initialGenome: String by lazy {
        buildString {
            for (i in 0 until GENOME_LENGTH) {
                append(setOf('A', 'G', 'T', 'C').random(LOCAL_RANDOM))
            }
        }
    }

    val hotspots: List<Int> by lazy {
        val result = mutableListOf<Int>()
        while (result.size < 2 || abs(result[0] - result[1]) < 100) {
            result.clear()
            result.add(LOCAL_RANDOM.nextInt(GENOME_LENGTH).absoluteValue)
            result.add(LOCAL_RANDOM.nextInt(GENOME_LENGTH).absoluteValue)
        }
        result
    }

    val edges = mutableListOf<Pair<Edge, Edge>>()
    val recombinations = mutableListOf<Recombination>()

    fun generate(): MutableList<TopologyNode> {
        val currentLeafs = mutableListOf(createNode(initialGenome))
        while (currentLeafs.size < RESULT_GENOMES_NUMBER) {
            val isRecombination = LOCAL_RANDOM.nextDouble() <= P_RECOMBINATION
            if (isRecombination) {
                val firstParent = LOCAL_RANDOM.nextInt(currentLeafs.size)
                var secondParent = LOCAL_RANDOM.nextInt(currentLeafs.size)
                while (firstParent == secondParent)
                    secondParent = LOCAL_RANDOM.nextInt(currentLeafs.size)

                val hotspot = hotspots.random(LOCAL_RANDOM)

                val child = performRecombination(hotspot, currentLeafs[firstParent], currentLeafs[secondParent])
                currentLeafs.add(child)
            } else {
                val parent = currentLeafs[LOCAL_RANDOM.nextInt(currentLeafs.size)]

                val firstChild = mutate(parent)
                val secondChild = mutate(parent)

                currentLeafs.add(firstChild)
                currentLeafs.add(secondChild)
            }
        }
        return currentLeafs
    }

    private fun performRecombination(hotspot: Int, firstParent: TopologyNode, secondParent: TopologyNode): TopologyNode {
        println("Recombination at $hotspot site: ${firstParent.node} and ${secondParent.node}")

        val childPrefix = firstParent.genome.primary.substring(0, hotspot)
        val secondParentPrefix = secondParent.genome.primary.substring(0, hotspot)
        println("Prefix hamming: ${hammingDistance(childPrefix, secondParentPrefix)}")
        val childSuffix = secondParent.genome.primary.substring(hotspot)
        val firstParentSuffix = firstParent.genome.primary.substring(hotspot)
        println("Suffix hamming: ${hammingDistance(firstParentSuffix, childSuffix)}")

        val child = createNode(childPrefix + childSuffix)
        createEdge(firstParent.node, child.node)
        createEdge(secondParent.node, child.node)

        val edgeFromFirstParent = Edge(firstParent, child, listOf(firstParent.node, child.node))
        val edgeFromSecondParent = Edge(secondParent, child, listOf(secondParent.node, child.node))

        firstParent.add(edgeFromFirstParent, directed = true)
        secondParent.add(edgeFromSecondParent, directed = true)
        val reversedFromFirstParent = edgeFromFirstParent.reversed()
        child.add(reversedFromFirstParent)
        val reversedFromSecondParent = edgeFromSecondParent.reversed()
        child.add(reversedFromSecondParent)

        edges.add(Pair(edgeFromFirstParent, reversedFromFirstParent))
        edges.add(Pair(edgeFromSecondParent, reversedFromSecondParent))

        recombinations.add(Recombination(firstParent.node, secondParent.node, child.node, hotspot))
        return child
    }

    private fun createNode(genome: String): TopologyNode = TopologyNode(Node(Taxon(id, "seq${id++}", Genome(genome))))

    private fun createIntermediateNode(): Node = Node(createTaxon())

    private fun mutate(parent: TopologyNode): TopologyNode {
        val variable = PoissonRandomVariable(GENOME_LENGTH * SubstitutionModel.mutationRate, LOCAL_RANDOM)
        val mutations = variable.next()

        val builder = StringBuilder(parent.genome.primary)
        val path = mutableListOf(parent.node)

        for (i in 0 until mutations - 1) {
            val index = builder.indices.random(LOCAL_RANDOM)
            builder[index] = listOf('A', 'C', 'G', 'T').random(LOCAL_RANDOM)

            val node = createIntermediateNode()

            (node.genome as MutableGenome).add(builder.toString())
            createEdge(path.last(), node)
            path.add(node)
        }
        val index = builder.indices.random(LOCAL_RANDOM)
        builder[index] = listOf('A', 'C', 'G', 'T').random(LOCAL_RANDOM)
        val result = createNode(builder.toString())
        createEdge(path.last(), result.node)
        path.add(result.node)

        val directEdge = Edge(parent, result, path)
        val reversedEdge = directEdge.reversed()

        parent.add(directEdge, directed = true)
        result.add(reversedEdge)

        edges.add(Pair(directEdge, reversedEdge))

        return result
    }
}

fun main() {
    val generator = IndependentDataGenerator()

    println("Hotspots: ${generator.hotspots.joinToString(separator = " ")}")

    val genomes = generator.generate()

    val cluster = SimpleCluster(genomes.map { it.node }.toMutableList())
    val topology = Topology(cluster, genomes.toMutableList(), generator.edges)

    val recombinationGroups = mutableListOf<RecombinationGroup>()
    generator.recombinations.forEach { recombination ->
        val group = RecombinationGroup(recombination.pos, mutableListOf(recombination), true)
        val child = topology.first { it.node === recombination.child }
        val edges = mutableListOf<Edge>()
        edges.addAll(child.edges
                .filter { it.contains(recombination.firstParent) || it.contains(recombination.secondParent) }
                .map { it.end.edges.first { it.end === child } }
        )
        group.ambassador = RecombinationGroupAmbassador(recombination, child, edges, emptyList())
        recombinationGroups.add(group)
    }

    val rootedTopology = RootedTopology(topology, genomes[0], recombinationGroups)
    println(genomes.size)
    val range = 1 until genomes.size

    for (i in range) {
        rootedTopology.mergeTwoEdges(genomes[i])
    }

    val likelihood = BranchLikelihood(SubstitutionModel.mutationRate) *
            RecombinationLikelihood(P_RECOMBINATION * RESULT_GENOMES_NUMBER)

    val likelihoodValue = likelihood(rootedTopology)

    println(rootedTopology.toGraphviz())

    println("Likelihood: $likelihoodValue")

    print(genomes.joinToString(separator = "\n") { ">${it.node}\n${it.genome.primary}" })
}
