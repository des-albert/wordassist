package org.db.wordassist

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordViewModel(application: Application) : AndroidViewModel(application) {
  val wrongColor = Color(0xFF787C7E)
  val presentColor = Color(0xFFC9B458)
  val correctColor = Color(0xFF6AAA64)

  var guessColors = mutableStateListOf<List<Color>>()
  var guesses = mutableStateListOf<String>()

  var currentGuess by mutableStateOf("")
  var currentColors =
    mutableStateListOf<Color>(wrongColor, wrongColor, wrongColor, wrongColor, wrongColor)
  var suggestedWords = mutableStateListOf<String>()
  var keyStates = mutableStateMapOf<Char, Color>()

  private val appContext = application.applicationContext
  private var validWords = listOf<String>()


  private fun readValidWordsFromAssets(context: Context): List<String> {
    return context.assets.open("words.txt").bufferedReader().useLines { lines ->
      lines.toList()
    }
  }


  fun loadWords() {
    viewModelScope.launch {
      validWords = withContext(Dispatchers.IO) {
        readValidWordsFromAssets(appContext)
      }
    }
  }

  fun updateGuess(char: Char) {
    if (currentGuess.length < 5) {
      currentGuess += char
      currentColors[currentGuess.length - 1] = keyStates[char]  ?: currentColors[currentGuess.length - 1]
    }
  }

  fun deleteLastChar() {
    if (currentGuess.isNotEmpty()) {
      currentGuess = currentGuess.dropLast(1)
    }
  }

  fun cycleColor(index: Int) {
    when (currentColors[index]) {
      wrongColor -> currentColors[index] = presentColor
      presentColor -> currentColors[index] = correctColor
      correctColor -> currentColors[index] = wrongColor
    }

  }

  fun findWords(): List<String> {
    suggestedWords.clear()
    updateKeyStates()

    if (currentGuess.length == 5) {
      if (validWords.contains(currentGuess)) {
        guesses.add(currentGuess)
        guessColors.add(currentColors)
        currentGuess = ""
        currentColors =
          listOf(wrongColor, wrongColor, wrongColor, wrongColor, wrongColor).toMutableStateList()

        val greyLetters = mutableSetOf<Char>()
        val yellowGreenLetters = mutableSetOf<Char>()
        val yellowLetters = mutableMapOf<Int, Char>()
        val greenLetters = mutableMapOf<Int, Char>()

        guesses.forEachIndexed { guessIndex, guess ->
          guess.forEachIndexed { index, letter ->
            when (guessColors[guessIndex][index]) {
              correctColor -> {
                greenLetters[index] = letter
                if ( ! yellowGreenLetters.contains(letter) )
                  yellowGreenLetters.add(letter)
              }

              presentColor -> {
                yellowLetters[index] = letter
                if ( ! yellowGreenLetters.contains(letter) )
                  yellowGreenLetters.add(letter)
              }

              wrongColor -> if (!yellowGreenLetters.contains(letter))
                greyLetters.add(letter)
            }
          }
        }

        for (word in validWords) {

          if (greyLetters.isNotEmpty()) {
            if (word.any { it in greyLetters })
              continue
          }

          if (yellowGreenLetters.isNotEmpty()) {
            if ( ! yellowGreenLetters.all { char -> word.contains(char) })
              continue
          }

          if (yellowLetters.isNotEmpty()) {
            if ( ! yellowLetters.all { (index, char) ->
                word.contains(char) && word[index] != char
              })
              continue
          }

          if (greenLetters.isNotEmpty()) {
            if ( ! greenLetters.all { (index, char) ->
                word[index] == char
              })
              continue
          }
          suggestedWords.add(word)
        }
      }
    }
    return suggestedWords
  }

  fun reset() {
    guesses.clear()
    guessColors.clear()
    currentGuess = ""
    currentColors =
      listOf(wrongColor, wrongColor, wrongColor, wrongColor, wrongColor).toMutableStateList()
    keyStates.clear()
  }

  private fun updateKeyStates() {
    currentColors.forEachIndexed { index, c ->
      when (c) {
        correctColor -> keyStates[currentGuess[index]] = correctColor
        presentColor -> keyStates[currentGuess[index]] = presentColor
        wrongColor -> keyStates[currentGuess[index]] = wrongColor
      }
    }
  }

}