package com.xxj2wan.grandfather.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xxj2wan.grandfather.game.BoardGenerator
import com.xxj2wan.grandfather.game.GamePhase
import com.xxj2wan.grandfather.game.GameState
import com.xxj2wan.grandfather.game.GameViewModel

@Composable
fun GrandfatherApp(viewModel: GameViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.Background)
            .safeDrawingPadding()
            .padding(16.dp),
    ) {
        if (state.phase == GamePhase.READY) {
            StartScreen(onStart = viewModel::startRound)
        } else {
            GameScreen(
                state = state,
                onCellTap = viewModel::onCellTap,
                onNext = viewModel::startRound,
            )
        }
    }
}

@Composable
private fun StartScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("같은 숫자\n짝 찾기", fontSize = 48.dp.fixedSp(), fontWeight = FontWeight.Black, textAlign = TextAlign.Center, lineHeight = 60.dp.fixedSp())
        Spacer(Modifier.height(32.dp))
        Text(
            "숫자 ${BoardGenerator.CELL_COUNT}개 중에\n똑같은 숫자 ${BoardGenerator.PAIR_COUNT}쌍을\n찾아서 눌러 주세요",
            fontSize = 28.dp.fixedSp(),
            textAlign = TextAlign.Center,
            lineHeight = 40.dp.fixedSp(),
        )
        Spacer(Modifier.height(48.dp))
        BigButton(text = "시작하기", onClick = onStart)
    }
}

@Composable
private fun GameScreen(state: GameState, onCellTap: (Int) -> Unit, onNext: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(state.foundPairs) {
        if (state.foundPairs > 0) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("문제 ${state.round}", fontSize = 30.dp.fixedSp(), fontWeight = FontWeight.Bold)
                Text(
                    "찾은 쌍  ${state.foundPairs} / ${state.totalPairs}",
                    fontSize = 30.dp.fixedSp(),
                    fontWeight = FontWeight.Bold,
                    color = Palette.Matched,
                )
            }
            TimerBadge(seconds = state.remainingSeconds)
        }

        Spacer(Modifier.height(16.dp))

        Board(state = state, onCellTap = onCellTap, modifier = Modifier.weight(1f))

        Spacer(Modifier.height(16.dp))

        if (state.phase == GamePhase.CLEARED || state.phase == GamePhase.TIME_UP) {
            ResultPanel(state = state, onNext = onNext)
        } else {
            Text(
                "같은 숫자 두 개를 차례로 누르세요",
                fontSize = 24.dp.fixedSp(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TimerBadge(seconds: Int) {
    val color = if (seconds <= 10) Palette.TimerWarning else Palette.Text
    Box(
        modifier = Modifier
            .size(96.dp)
            .border(6.dp, color, CircleShape)
            .background(Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text("$seconds", fontSize = 44.dp.fixedSp(), fontWeight = FontWeight.Black, color = color)
    }
}

@Composable
private fun Board(state: GameState, onCellTap: (Int) -> Unit, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val boardSize = min(maxWidth, maxHeight)
        val cellSize = boardSize / BoardGenerator.COLUMNS
        val revealed = if (state.phase == GamePhase.TIME_UP) state.pairIndices - state.matched else emptySet()

        Column(modifier = Modifier.border(3.dp, Palette.CellBorder)) {
            state.cells.chunked(BoardGenerator.COLUMNS).forEach { row ->
                Row {
                    row.forEach { cell ->
                        val (bg, fg) = when {
                            cell.index in state.matched -> Palette.Matched to Color.White
                            cell.index in state.wrong -> Palette.Wrong to Color.White
                            cell.index == state.selected -> Palette.Selected to Color.White
                            cell.index in revealed -> Palette.Reveal to Palette.Text
                            else -> Palette.CellDefault to Palette.Text
                        }
                        NumberCell(
                            number = cell.number,
                            size = cellSize,
                            background = bg,
                            textColor = fg,
                            onClick = { onCellTap(cell.index) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberCell(number: Int, size: Dp, background: Color, textColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size)
            .border(1.5.dp, Palette.CellBorder)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$number",
            fontSize = (size * 0.5f).fixedSp(),
            fontWeight = FontWeight.Black,
            color = textColor,
        )
    }
}

@Composable
private fun ResultPanel(state: GameState, onNext: () -> Unit) {
    val message = if (state.phase == GamePhase.CLEARED) {
        "잘하셨어요!\n${state.elapsedSeconds}초 만에 모두 찾으셨어요"
    } else {
        "시간이 다 됐어요\n${state.totalPairs}쌍 중 ${state.foundPairs}쌍 찾으셨어요"
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            message,
            fontSize = 28.dp.fixedSp(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 38.dp.fixedSp(),
        )
        Spacer(Modifier.height(16.dp))
        BigButton(text = "다음 문제", onClick = onNext)
    }
}

@Composable
private fun BigButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Palette.Selected),
    ) {
        Text(text, fontSize = 34.dp.fixedSp(), fontWeight = FontWeight.Bold)
    }
}
