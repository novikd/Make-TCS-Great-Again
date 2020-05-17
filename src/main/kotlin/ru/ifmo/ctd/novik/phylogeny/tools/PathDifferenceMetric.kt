package ru.ifmo.ctd.novik.phylogeny.tools

import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt

object PathDifferenceMetric {
    operator fun invoke(distances1: Array<IntArray>, distances2: Array<IntArray>): Double {
        if (distances1.size != distances2.size) {
            error("Array sizes must be equal")
        }
        var result = 0.0
        for (i in distances1.indices) {
            if (distances1[i].size != distances2[i].size) {
                error("Array sizes must be equal")
            }

            for (j in distances1[i].indices) {
                result += (distances1[i][j] - distances2[i][j]).toDouble().pow(2.0)
            }
        }
        return sqrt(result)
    }
}

fun readFile(file: File): Array<IntArray> {
    val reader = file.bufferedReader()
    val n = reader.readLine().split("\\s".toRegex())[0].toInt()
    val result = Array(n) { IntArray(n) }
    for (i in 0 until n) {
        result[i] = reader.readLine().split("\\s".toRegex()).map { it.toInt() }.toIntArray()
    }
    return result
}

fun main() {
    val groundTruthFile = File("simulated/distances.txt")
    val distancesFile = File("distances.txt")

    val groundTruthDistances = readFile(groundTruthFile)
    val distances = readFile(distancesFile)
    val metric = PathDifferenceMetric(groundTruthDistances, distances)

    println("PDM value: $metric")
}