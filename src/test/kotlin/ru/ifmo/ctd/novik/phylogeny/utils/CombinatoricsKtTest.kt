package ru.ifmo.ctd.novik.phylogeny.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

/**
 * @author Dmitry Novik ITMO University
 */
internal class CombinatoricsKtTest {

    @Test
    fun `permutations 1`() {
        assertEquals(1, permutations(1).toList().size)
    }

    fun test(n: Int, expected: List<Array<Int>>) {
        val actual = permutations(n).toList()
        assertEquals(expected.size, actual.size)
        expected.zip(actual).forEach { (e, a) -> assertArrayEquals(e, a) }
    }

    @Test
    fun `permutations 2`() {
        val expected = listOf(arrayOf(0, 1), arrayOf(1, 0))
        test(2, expected)
    }

    @Test
    fun `permutations 3`() {
        val expected = listOf(
                arrayOf(0, 1, 2),
                arrayOf(0, 2, 1),
                arrayOf(1, 0, 2),
                arrayOf(1, 2, 0),
                arrayOf(2, 0, 1),
                arrayOf(2, 1, 0))
        test(3, expected)
    }

    @Test
    fun `permutations 4`() {
        val expected = listOf(
                arrayOf(0, 1, 2, 3),
                arrayOf(0, 1, 3, 2),
                arrayOf(0, 2, 1, 3),
                arrayOf(0, 2, 3, 1),
                arrayOf(0, 3, 1, 2),
                arrayOf(0, 3, 2, 1),
                arrayOf(1, 0, 2, 3),
                arrayOf(1, 0, 3, 2),
                arrayOf(1, 2, 0, 3),
                arrayOf(1, 2, 3, 0),
                arrayOf(1, 3, 0, 2),
                arrayOf(1, 3, 2, 0),
                arrayOf(2, 0, 1, 3),
                arrayOf(2, 0, 3, 1),
                arrayOf(2, 1, 0, 3),
                arrayOf(2, 1, 3, 0),
                arrayOf(2, 3, 0, 1),
                arrayOf(2, 3, 1, 0),
                arrayOf(3, 0, 1, 2),
                arrayOf(3, 0, 2, 1),
                arrayOf(3, 1, 0, 2),
                arrayOf(3, 1, 2, 0),
                arrayOf(3, 2, 0, 1),
                arrayOf(3, 2, 1, 0))
        test(4, expected)
    }
}