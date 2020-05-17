package ru.ifmo.ctd.novik.phylogeny.tools

import ru.ifmo.ctd.novik.phylogeny.common.Genome
import ru.ifmo.ctd.novik.phylogeny.common.MutableGenome
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.io.output.PrettyPrinter
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.BranchLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.RecombinationLikelihood
import ru.ifmo.ctd.novik.phylogeny.mcmc.likelihood.times
import ru.ifmo.ctd.novik.phylogeny.mcmc.modifications.HOTSPOT_DISTANCE_THRESHOLD
import ru.ifmo.ctd.novik.phylogeny.models.SubstitutionModel
import ru.ifmo.ctd.novik.phylogeny.tree.*
import ru.ifmo.ctd.novik.phylogeny.utils.*
import java.io.File
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.random.Random

private val LOCAL_RANDOM = Random(228)

const val P_RECOMBINATION = 0.1

const val RESULT_GENOMES_NUMBER = 20

const val GENOME_LENGTH = 150

val outputDirectory = File("simulated")
val recombinationOutputFile = File("simulated/recombination.txt")
val graphvizOutputFile = File("simulated/graphviz.dot")
val distancesOutputFile = File("simulated/distances.txt")
val sequencesOutputFile = File("simulated/sequences.fas")

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
        while (result.size < 2 || abs(result[0] - result[1]) < 60) {
            result.clear()
            result.add(LOCAL_RANDOM.nextInt(GENOME_LENGTH).absoluteValue)
            result.add(LOCAL_RANDOM.nextInt(GENOME_LENGTH).absoluteValue)
        }
        result
    }

    val genomes = mutableSetOf<String>()
    val edges = mutableListOf<Pair<Edge, Edge>>()
    val recombinations = mutableListOf<Recombination>()

    fun generate(): MutableList<TopologyNode> {
        val currentLeafs = mutableListOf(createNode(initialGenome))
        val topologyNodes = mutableListOf(currentLeafs.first())
        while (currentLeafs.size < RESULT_GENOMES_NUMBER) {
            val isRecombination = currentLeafs.size > 1 && LOCAL_RANDOM.nextDouble() <= P_RECOMBINATION
            if (isRecombination) {
                val hotspot = hotspots.random(LOCAL_RANDOM)

                val parents = findRecombinantParent(currentLeafs, hotspot) ?: continue

                val firstParent = currentLeafs[parents.first]
                val secondParent = currentLeafs[parents.second]
                val child = performRecombination(hotspot, firstParent, secondParent)

                val firstParentChild = mutate(firstParent)
                val secondParentChild = mutate(secondParent)

                currentLeafs.remove(firstParent)
                currentLeafs.remove(secondParent)

                currentLeafs.add(child)
                currentLeafs.add(firstParentChild)
                currentLeafs.add(secondParentChild)

                topologyNodes.add(child)
                topologyNodes.add(firstParentChild)
                topologyNodes.add(secondParentChild)
            } else {
                performSpeciation(currentLeafs, topologyNodes)
            }
        }
        return topologyNodes
    }

    private fun performSpeciation(currentLeafs: MutableList<TopologyNode>, topologyNodes: MutableList<TopologyNode>) {
        val parent = currentLeafs[LOCAL_RANDOM.nextInt(currentLeafs.size)]

        val firstChild = mutate(parent)
        val secondChild = mutate(parent)

        currentLeafs.remove(parent)
        currentLeafs.add(firstChild)
        currentLeafs.add(secondChild)
        topologyNodes.add(firstChild)
        topologyNodes.add(secondChild)
    }

    private fun findRecombinantParent(currentLeafs: List<TopologyNode>, hotspot: Int): Pair<Int, Int>? {
        var parents: Pair<Int, Int>

        for (trial in 0..1000) {
            parents = Pair(LOCAL_RANDOM.nextInt(currentLeafs.size), LOCAL_RANDOM.nextInt(currentLeafs.size))
            if (parents.first == parents.second)
                continue

            val firstParent = currentLeafs[parents.first]
            val secondParent = currentLeafs[parents.second]

            val prefix = hammingDistance(firstParent.genome.primary.substring(0, hotspot), secondParent.genome.primary.substring(0, hotspot))
            val suffix = hammingDistance(firstParent.genome.primary.substring(hotspot), secondParent.genome.primary.substring(hotspot))
            if (prefix >= HOTSPOT_DISTANCE_THRESHOLD * GENOME_LENGTH
                    && suffix >= HOTSPOT_DISTANCE_THRESHOLD * GENOME_LENGTH)
                return parents
        }
        return null
    }

    private fun performRecombination(hotspot: Int, firstParent: TopologyNode, secondParent: TopologyNode): TopologyNode {
        recombinationOutputFile.appendText("Recombination at $hotspot site: ${firstParent.node} and ${secondParent.node}\n")

        val childPrefix = firstParent.genome.primary.substring(0, hotspot)
        val secondParentPrefix = secondParent.genome.primary.substring(0, hotspot)
        recombinationOutputFile.appendText("Prefix hamming: ${hammingDistance(childPrefix, secondParentPrefix)}\n")
        val childSuffix = secondParent.genome.primary.substring(hotspot)
        val firstParentSuffix = firstParent.genome.primary.substring(hotspot)
        recombinationOutputFile.appendText("Suffix hamming: ${hammingDistance(firstParentSuffix, childSuffix)}\n")

        val child = createNode(childPrefix + childSuffix)
        recombinationOutputFile.appendText("Child: ${child.node}\n")
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

    private fun createNode(genome: String): TopologyNode {
//        genomes.add(genome)
        return TopologyNode(Node(Taxon(id, "seq${id++}", Genome(genome))))
    }

    val nodes = mutableListOf<Node>()

    private fun createIntermediateNode(): Node {
        val node = Node(createTaxon())
        nodes.add(node)
        return node
    }

    private fun mutate(parent: TopologyNode): TopologyNode {
        val variable = PoissonRandomVariable(GENOME_LENGTH * SubstitutionModel.mutationRate, LOCAL_RANDOM)
        val mutations = max(variable.next(), 1)

        val builder = StringBuilder(parent.genome.primary)
        val path = mutableListOf(parent.node)

        val mutationPositions = mutableListOf<Int>()
        for (i in 0 until mutations) {
            val index = builder.indices.random(LOCAL_RANDOM)
            if (index !in mutationPositions)
                mutationPositions.add(index)
        }

        for (i in 0 until mutations - 1) {
            mutate(builder, mutationPositions[i])
            val node = createIntermediateNode()

            val genome = builder.toString()
            (node.genome as MutableGenome).add(genome)
//            genomes.add(genome)
            createEdge(path.last(), node)
            path.add(node)
        }
        mutate(builder, mutationPositions.last())
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

    private fun mutate(builder: StringBuilder, index: Int) {
        val options = setOf('A', 'C', 'G', 'T') - setOf(builder[index])
        builder[index] = options.random(LOCAL_RANDOM)
    }

    fun clear() {
        id = 0
        genomes.clear()
        edges.clear()
        recombinations.clear()
        nodes.clear()
    }
}

fun main() {
    val generator = IndependentDataGenerator()
    if (!outputDirectory.exists()) {
        outputDirectory.mkdir()
    }

    recombinationOutputFile.writeText("Hotspots: ${generator.hotspots.joinToString(separator = " ")}\n")
//    recombinationOutputFile.writeText("")

    val genomes = generator.generate()

    val cluster = SimpleCluster((genomes.map { it.node } + generator.nodes).toMutableList())
    val topology = Topology(cluster, genomes.toMutableList(), generator.edges)

    val recombinationGroups = mutableListOf<RecombinationGroup>()
    generator.recombinations.forEach { recombination ->
        val group = RecombinationGroup(recombination.pos, mutableListOf(recombination), true)
        val child = topology.first { it.node === recombination.child }
        val edges = mutableListOf<Edge>()
        val firstParent = topology.nodes.first { it.node === recombination.firstParent }
        val secondParent = topology.nodes.first { it.node === recombination.secondParent }
        edges.add(firstParent.next.first { it.end === child })
        edges.add(secondParent.next.first { it.end === child })
        group.ambassador = RecombinationGroupAmbassador(recombination, child, edges, emptyList())
        recombinationGroups.add(group)
    }

    val rootedTopology = RootedTopology(topology, genomes[0], recombinationGroups)
    val range = 1 until genomes.size

    for (i in range) {
        rootedTopology.mergeTwoEdges(genomes[i])
    }

    val likelihood = BranchLikelihood(GENOME_LENGTH * SubstitutionModel.mutationRate) *
            RecombinationLikelihood(P_RECOMBINATION * genomes.size)

    val likelihoodValue = likelihood(rootedTopology)
    recombinationOutputFile.appendText("Likelihood: $likelihoodValue\n")

    graphvizOutputFile.writeText(rootedTopology.toGraphviz(PrettyPrinter()))

    distancesOutputFile.writeText(topology.cluster.distanceMatrix.print())
    sequencesOutputFile.writeText(genomes.joinToString(separator = "\n") { ">${it.node}\n${it.genome.primary}" })

    println(cluster.toList().size)
    println(cluster.distinct().size)
    println(topology.cluster.toGraphviz(PrettyPrinter()))
}
