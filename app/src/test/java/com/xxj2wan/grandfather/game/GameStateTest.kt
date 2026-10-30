package com.xxj2wan.grandfather.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameStateTest {

    @Test
    fun `판에는 정확히 3쌍만 있고 숫자는 1~60 사이다`() {
        repeat(1_000) { seed ->
            val cells = BoardGenerator.generate(random = Random(seed))
            assertEquals(16, cells.size)
            assertTrue(cells.all { it.number in 1..60 })

            val counts = cells.groupingBy { it.number }.eachCount()
            assertEquals(3, counts.values.count { it == 2 })
            assertTrue(counts.values.all { it == 1 || it == 2 })
        }
    }

    @Test
    fun `같은 숫자 두 칸을 누르면 쌍을 찾는다`() {
        val state = playing(listOf(7, 7, 1, 2, 3, 3, 4, 4))
        val after = state.tap(0).tap(1)
        assertEquals(setOf(0, 1), after.matched)
        assertNull(after.selected)
        assertEquals(GamePhase.PLAYING, after.phase)
    }

    @Test
    fun `다른 숫자를 누르면 틀린 칸으로 표시하고 선택을 푼다`() {
        val after = playing(listOf(7, 7, 1, 2)).tap(0).tap(2)
        assertEquals(setOf(0, 2), after.wrong)
        assertNull(after.selected)
        assertTrue(after.matched.isEmpty())
    }

    @Test
    fun `같은 칸을 다시 누르면 선택이 풀린다`() {
        val after = playing(listOf(7, 7)).tap(0).tap(0)
        assertNull(after.selected)
    }

    @Test
    fun `모든 쌍을 찾으면 성공`() {
        val after = playing(listOf(7, 7, 3, 3, 4, 4))
            .tap(0).tap(1).tap(2).tap(3).tap(4).tap(5)
        assertEquals(GamePhase.CLEARED, after.phase)
        assertEquals(3, after.foundPairs)
    }

    @Test
    fun `시간이 다 되면 종료되고 더 이상 누를 수 없다`() {
        var state = playing(listOf(7, 7, 3, 3, 4, 4))
        repeat(TIME_LIMIT_SECONDS) { state = state.tick() }
        assertEquals(GamePhase.TIME_UP, state.phase)
        assertEquals(state, state.tap(0))
    }

    private fun playing(numbers: List<Int>) = GameState(
        cells = numbers.mapIndexed { i, n -> Cell(i, n) },
        phase = GamePhase.PLAYING,
    )
}
