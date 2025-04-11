package org.db.wordassist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.system.measureTimeMillis

@Composable
fun WordScreen(
  wordViewModel: WordViewModel = viewModel()

) {

  var resultWords by remember { mutableStateOf(listOf<String>()) }
  var resultVisible by remember { mutableStateOf(false) }
  var executionTime by remember { mutableLongStateOf(0L) }

  LaunchedEffect(Unit) {
    wordViewModel.loadWords()
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(12.dp),
    horizontalAlignment = CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {

    Box(
      Modifier
        .fillMaxSize()
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = CenterHorizontally
      ) {
        wordViewModel.guesses.forEachIndexed { index, guess ->
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            for (i in 0 until 5) {
              val boxColor = wordViewModel.guessColors[index][i]
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .background(boxColor, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = guess.getOrNull(i)?.toString() ?: "",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimary
                )
              }
            }
          }
        }
        if (wordViewModel.guesses.size < 6) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            for (i in 0 until 5) { // Assuming the word length is 5

              Box(
                modifier = Modifier
                  .size(48.dp)
                  .background(wordViewModel.currentColors[i], RoundedCornerShape(4.dp))
                  .clickable {
                    wordViewModel.cycleColor(i)
                  },
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = wordViewModel.currentGuess.getOrNull(i)?.toString() ?: "",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.background
                )
              }
            }
          }
        }
        Keyboard(
          onKeyPress = { char -> wordViewModel.updateGuess(char) },
          onBackspace = { wordViewModel.deleteLastChar() },
          keyStates = wordViewModel.keyStates
        )

        Spacer(modifier = Modifier.height(4.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ElevatedButton(
            colors = ButtonDefaults.elevatedButtonColors(
              containerColor = MaterialTheme.colorScheme.background
            ),
            onClick = {
              resultVisible = true
              val time = measureTimeMillis {
                resultWords = wordViewModel.findWords()
              }
              executionTime = time
            }) {
            Text(
              text = "Submit",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onBackground
            )
          }

          ElevatedButton(
            colors = ButtonDefaults.elevatedButtonColors(
              containerColor = MaterialTheme.colorScheme.background
            ),
            onClick = {
              resultVisible = false
              wordViewModel.reset()
            }) {
            Text(
              text = "Clear",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onBackground
            )
          }
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()

        if (resultVisible) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {

            Text(
              text = "${resultWords.size} words - time $executionTime mS",
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onTertiary,
              fontWeight = FontWeight.Normal
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            Box(
              modifier = Modifier
                .width(350.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
            ) {
              LazyVerticalGrid(
                columns = GridCells.Fixed(3), // Use 3 columns
                contentPadding = PaddingValues(14.dp)
              ) {
                itemsIndexed(resultWords) { index, word ->
                  Text(
                    text = word,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.padding(4.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

