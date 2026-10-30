package com.xxj2wan.grandfather.game

import kotlin.random.Random

data class Cell(val index: Int, val number: Int)

/**
 * 4x4 판에 숫자를 배치한다.
 * - [pairCount]개의 숫자는 정확히 2번씩 나온다 (찾아야 할 쌍).
 * - 나머지 칸의 숫자는 모두 서로 다르고, 쌍 숫자와도 겹치지 않는다.
 *   그래서 판 위의 "같은 숫자"는 항상 정답 쌍뿐이다.
 */
object BoardGenerator {
    const val COLUMNS = 4
    const val ROWS = 4
    const val CELL_COUNT = COLUMNS * ROWS
    const val PAIR_COUNT = 3
    val NUMBER_RANGE = 1..60

    fun generate(
        pairCount: Int = PAIR_COUNT,
        cellCount: Int = CELL_COUNT,
        numberRange: IntRange = NUMBER_RANGE,
        random: Random = Random.Default,
    ): List<Cell> {
        val singleCount = cellCount - pairCount * 2
        require(pairCount >= 1 && singleCount >= 0) { "칸 수보다 쌍이 많습니다" }
        require(pairCount + singleCount <= numberRange.count()) { "숫자 범위가 너무 좁습니다" }

        val distinct = numberRange.shuffled(random).take(pairCount + singleCount)
        val pairNumbers = distinct.take(pairCount)
        val singles = distinct.drop(pairCount)

        return (pairNumbers + pairNumbers + singles)
            .shuffled(random)
            .mapIndexed { index, number -> Cell(index, number) }
    }
}
