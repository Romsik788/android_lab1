package com.roman.lab1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roman.lab1.database.MyDatabase
import com.roman.lab1.database.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val database: MyDatabase
) : ViewModel() {

    var currentUser: User? = null
    val gameState = MutableLiveData(List(9) { SquareState.EMPTY })
    val isUserWin: MutableLiveData<Boolean?> = MutableLiveData(null)

    suspend fun isUserExist(username: String): Boolean {
        currentUser = database.userDao().findByUsername(username).firstOrNull()
        return currentUser != null
    }

    suspend fun isPasswordCorrect(username: String, password: String): Boolean {
        return database.userDao().findByUsername(username).first().password == password
    }

    fun addUser(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currentUser = User(0, username, password)
            database.userDao().insertAll(currentUser ?: User(0, username, password))
        }
    }

    fun changeSquareState(index: Int, squareState: SquareState) {
        gameState.value?.let {
            val newGameState = it.toMutableList()
            newGameState[index] = squareState
            gameState.value = newGameState
            checkForWinner()
        }
    }

    private fun checkForWinner() {
        if (gameState.value?.let { isWinner(it, SquareState.PLAYER) } == true) {
            isUserWin.value = true
        } else if (gameState.value?.let { isWinner(it, SquareState.ENEMY) } == true) {
            isUserWin.value = false
        }
    }

    private fun isWinner(state: List<SquareState>, player: SquareState): Boolean {
        if (state[0] == player && state[1] == player && state[2] == player) {
            return true
        }
        if (state[3] == player && state[4] == player && state[5] == player) {
            return true
        }
        if (state[6] == player && state[7] == player && state[8] == player) {
            return true
        }
        if (state[0] == player && state[3] == player && state[6] == player) {
            return true
        }
        if (state[1] == player && state[4] == player && state[7] == player) {
            return true
        }
        if (state[2] == player && state[5] == player && state[8] == player) {
            return true
        }
        if (state[0] == player && state[4] == player && state[8] == player) {
            return true
        }
        if (state[2] == player && state[4] == player && state[6] == player) {
            return true
        }
        return false
    }

    fun makeEnemyMove() {
        val emptySquares = gameState.value?.mapIndexedNotNull { index, squareState ->
            if (squareState == SquareState.EMPTY) index else null
        } ?: return

        if (emptySquares.isNotEmpty()) {
            gameState.value?.let {
                changeSquareState(emptySquares.random(), SquareState.ENEMY)
            }
        } else {
            isUserWin.postValue(false)
        }
    }

    fun resetGame() {
        gameState.value = List(9) { SquareState.EMPTY }
        isUserWin.value = null
    }
}