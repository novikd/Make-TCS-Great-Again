package ru.ifmo.ctd.novik.phylogeny.tools

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.utils.genome

class GeneratorChecker(val generator: IndependentDataGenerator) {
    val frequencies: MutableMap<Cluster, Int> = mutableMapOf()
    val clusterMap: MutableMap<Set<String>, Cluster> = mutableMapOf()
    val clusters: MutableList<Set<String>> = mutableListOf()

    fun run() {
        for (i in 0..1_000_000) {
            val genomes = generator.generate()
            val set = (genomes.map { it.node } + generator.nodes).map { it.genome.primary.toString() }.toSet()
            val cluster = SimpleCluster((genomes.map { it.node } + generator.nodes).toMutableList())


            val other = clusters.find { it.size == set.size && it.containsAll(set) }

            if (other != null) {
                val otherCluster = clusterMap[other]!!
                val prev = frequencies[otherCluster]!!
                frequencies[otherCluster] = prev + 1
            } else {
                clusters.add(set)
                clusterMap[set] = cluster
                frequencies[cluster] = 1
            }

            generator.clear()
            println("Finished ${i + 1} iteration, ${clusters.size} clusters generated")
        }
    }
}

fun main() {
    val generator = IndependentDataGenerator()
    val checker = GeneratorChecker(generator)
    checker.run()
}