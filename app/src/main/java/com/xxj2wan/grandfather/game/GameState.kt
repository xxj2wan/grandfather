package com.xxj2wan.grandfather.game

const val TIME_LIMIT_SECONDS = 60

enum class GamePhase { READY, PLAYING, CLEARED, TIME_UP }

data class GameState(
    val round: Int = 0,
    val cells: List<Cell> = emptyList(),
    val totalPairs: Int = BoardGenerator.PAIR_COUNT,
    val selected: Int? = null,
    val matched: Set<Int> = emptySet(),
    val wrong: Set<Int> = emptySet(),
    val remainingSeconds: Int = TIME_LIMIT_SECONDS,
    val phase: GamePhase = GamePhase.READY,
) {
    val foundPairs: Int get() = matched.size / 2

    val elapsedSeconds: Int get() = TIME_LIMIT_SECONDS - remainingSeconds

    /** 정답 쌍이 있는 칸들. 시간 초과 시 못 찾은 쌍을 보여줄 때 쓴다. */
    val pairIndices: Set<Int>
        get() = cells.groupBy { it.number }
            .values
            .filter { it.size == 2 }
            .flatten()
            .map { it.index }
            .toSet()

    fun tap(index: Int): GameState {
        if (phase != GamePhase.PLAYING || index in matched) return this

        val cleared = copy(wrong = emptySet())
        val first = cleared.selected ?: return cleared.copy(selected = index)
        if (first == index) return cleared.copy(selected = null)

        if (cells[first].number != cells[index].number) {
            return cleared.copy(selected = null, wrong = setOf(first, index))
        }

        val newMatched = matched + first + index
        val allFound = newMatched.size / 2 >= totalPairs
        return cleared.copy(
            selected = null,
            matched = newMatched,
            phase = if (allFound) GamePhase.CLEARED else GamePhase.PLAYING,
        )
    }

    fun tick(): GameState {
        if (phase != GamePhase.PLAYING) return this
        val next = (remainingSeconds - 1).coerceAtLeast(0)
        return if (next == 0) {
            copy(remainingSeconds = 0, selected = null, wrong = emptySet(), phase = GamePhase.TIME_UP)
        } else {
            copy(remainingSeconds = next)
        }
    }
}
