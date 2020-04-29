package ru.ifmo.ctd.novik.phylogeny.tools

import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.models.SubstitutionModel
import ru.ifmo.ctd.novik.phylogeny.utils.PoissonRandomVariable
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random

private val LOCAL_RANDOM = Random(2)

const val P_RECOMBINATION = 0.02

const val RESULT_GENOMES_NUMBER = 100

const val GENOME_LENGTH = 400

fun main() {
    val initialGenome = buildString {
        for (i in 0 until GENOME_LENGTH) {
            append(setOf('A', 'G', 'T', 'C').random(LOCAL_RANDOM))
        }
    }

    val hotspots = mutableListOf<Int>()
    while (hotspots.size < 2 || abs(hotspots[0] - hotspots[1]) < 100) {
        hotspots.clear()
        hotspots.add(LOCAL_RANDOM.nextInt(initialGenome.length).absoluteValue)
        hotspots.add(LOCAL_RANDOM.nextInt(initialGenome.length).absoluteValue)
    }

    println("Hotspots: ${hotspots.joinToString(separator = " ")}")

    val currentLeafs = mutableListOf(initialGenome)
    while (currentLeafs.size < RESULT_GENOMES_NUMBER) {
        val isRecombination = LOCAL_RANDOM.nextDouble() <= P_RECOMBINATION
        if (isRecombination) {
            val firstParent = LOCAL_RANDOM.nextInt(currentLeafs.size)
            var secondParent = LOCAL_RANDOM.nextInt(currentLeafs.size)
            while (firstParent == secondParent)
                secondParent = LOCAL_RANDOM.nextInt(currentLeafs.size)

            val hotspot = hotspots.random(LOCAL_RANDOM)

            println("Recombination at $hotspot site")

            println("Prefix hamming: ${hammingDistance(currentLeafs[firstParent].substring(0, hotspot), currentLeafs[secondParent].substring(0, hotspot))}")
            println("Suffix hamming: ${hammingDistance(currentLeafs[firstParent].substring(hotspot), currentLeafs[secondParent].substring(hotspot))}")

//            println("Prefix parent: ${currentLeafs[firstParent]}")
//            println("Suffix parent: ${currentLeafs[secondParent]}")

            val child = buildString {
                append(currentLeafs[firstParent].substring(0, hotspot))
                append(currentLeafs[secondParent].substring(hotspot))
            }
//            println("Child: $child")
            currentLeafs.add(child)
        } else {
            val parent = currentLeafs[LOCAL_RANDOM.nextInt(currentLeafs.size)]

            val firstChild = mutate(parent)
            val secondChild = mutate(parent)

            currentLeafs.add(firstChild)
            currentLeafs.add(secondChild)
        }
    }

    print(currentLeafs.mapIndexed { index, s ->  ">seq$index\n$s" }.joinToString(separator = "\n"))
}

private fun mutate(parent: String): String {
    val variable = PoissonRandomVariable(parent.length * SubstitutionModel.mutationRate, LOCAL_RANDOM)
    val mutations = variable.next()

    val builder = StringBuilder(parent)
    for (i in 0 until mutations) {
        val index = builder.indices.random(LOCAL_RANDOM)
        builder[index] = listOf('A', 'C', 'G', 'T').random(LOCAL_RANDOM)
    }
    return builder.toString()
}