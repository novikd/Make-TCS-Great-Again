package ru.ifmo.ctd.novik.phylogeny.tools

import java.io.File
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

private val input = File("tree_simulations.txt").bufferedReader()

fun read2Int(): Pair<Int, Int> {
    val (x, y) = input.readLine()!!.split("\\s".toRegex()).map { it.toInt() }
    return Pair(x, y)
}

private fun mutate(parent: String): String {
    val random = Random.Default
    val mutationNumber = random.nextInt(parent.length / 4).absoluteValue

    val builder = StringBuilder(parent)
    for (i in 0 until mutationNumber) {
        val index = builder.indices.random()
        builder[index] = listOf('A', 'C', 'G', 'T').random()
    }

    return builder.toString()
}

fun recombinate(parent1: String, parent2: String, hotspots: List<Int>): String {
    val breakpoint = hotspots.random()
    return mutate(buildString {
        append(parent1.subSequence(0, breakpoint))
        append(parent2.subSequence(breakpoint, parent2.length))
    })
}

fun main() {
    val (n, m) = read2Int()

    val graph = Array(n) { mutableListOf<Int>() }
    val reverseGraph = Array(n) { mutableListOf<Int>() }

    for (i in 0 until m) {
        val (x, y) = read2Int()
        graph[x].add(y)
        reverseGraph[y].add(x)
    }

    val genomeLength = input.readLine().toInt()
    val initialGenome = buildString {
        for (i in 0 until genomeLength)
            append(setOf('A', 'G', 'C', 'T').random())
    }
    println("Initial genome: $initialGenome")
//    val initialGenome = input.readLine()!!

    val hotspots = mutableListOf<Int>()
    while (hotspots.size < 3) {
        val pos = Random.Default.nextInt(initialGenome.length).absoluteValue
        if (pos !in hotspots) hotspots.add(pos)
    }

    println("Hotspots: ${hotspots.sorted().joinToString(separator = " ")}")

    val visited = BooleanArray(n)

    val queue = ArrayDeque<Int>()
    queue.add(0)

    val genomes = mutableMapOf<Int, String>()
    genomes[0] = initialGenome

    while (queue.isNotEmpty()) {
        val v = queue.pop()
        visited[v] = true

        graph[v].forEach { child ->
            val notProcessedParents = reverseGraph[child].count { !visited[it] }
            if (notProcessedParents == 0) {
                genomes[child] = if (reverseGraph[child].size == 1) {
                    mutate(genomes[v]!!)
                } else {
                    val (parent1, parent2) = reverseGraph[child].map { genomes[it]!! }.shuffled()
                    recombinate(parent1, parent2, hotspots)
                }
                queue.add(child)
            }
        }
    }

    print(genomes.values.mapIndexed { index, s -> ">seq$index\n$s" }.joinToString(separator = "\n"))
}