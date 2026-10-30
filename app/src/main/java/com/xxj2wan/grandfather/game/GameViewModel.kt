package com.xxj2wan.grandfather.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var wrongFlashJob: Job? = null

    fun startRound() {
        timerJob?.cancel()
        wrongFlashJob?.cancel()

        _state.value = GameState(
            round = _state.value.round + 1,
            cells = BoardGenerator.generate(),
            phase = GamePhase.PLAYING,
        )

        timerJob = viewModelScope.launch {
            while (_state.value.phase == GamePhase.PLAYING) {
                delay(1_000)
                _state.update { it.tick() }
            }
        }
    }

    fun onCellTap(index: Int) {
        val next = _state.value.tap(index)
        _state.value = next

        if (next.wrong.isNotEmpty()) {
            // 틀린 두 칸을 잠깐 빨갛게 보여준 뒤 원래대로 돌린다
            wrongFlashJob?.cancel()
            wrongFlashJob = viewModelScope.launch {
                delay(WRONG_FLASH_MILLIS)
                _state.update { it.copy(wrong = emptySet()) }
            }
        }
        if (next.phase != GamePhase.PLAYING) timerJob?.cancel()
    }

    private companion object {
        const val WRONG_FLASH_MILLIS = 700L
    }
}
