package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import java.lang.Integer.min

/**
 * @author Novik Dmitry ITMO University
 */
class EditDistanceEvaluator : TaxonDistanceEvaluator {
    override fun evaluate(lhs: Taxon, rhs: Taxon): Int {
        val left = lhs.genome
        val right = rhs.genome
        val dp = Array(left.length) { IntArray(right.length) }
        for (i in 1..left.length) {
            for (j in 1..right.length) {
                dp[i][j] = 0
            }
        }

        for (i in left.indices)
            dp[i][0] = i

        for (j in right.indices)
            dp[0][j] = j

        for (i in 1 until left.length) {
            for (j in 1 until right.length) {
                dp[i][j] = min(
                    min(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1
                    ),
                    dp[i - 1][j - 1] + (if (left[i] != right[j]) 1 else 0)
                )
            }
        }

        return dp[left.length - 1][right.length - 1]
    }
}