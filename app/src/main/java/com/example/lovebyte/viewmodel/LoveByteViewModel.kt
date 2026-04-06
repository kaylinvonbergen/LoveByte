package com.example.lovebyte.viewmodel
//Holds app state and exposes functions the UI can call, like onNextLineClicked().
import androidx.lifecycle.ViewModel
import com.example.lovebyte.data.model.DialogueChoice
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.repository.GameRepository
import com.example.lovebyte.data.model.ProgrammingLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoveByteViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        LoveByteState(
            isLoading = false
        )
    )
    val state: StateFlow<LoveByteState> = _state.asStateFlow()

    fun onLanguageSelected(language: ProgrammingLanguage) {
        _state.value = _state.value.copy(
            currentLanguage = language,
            dialogueIndex = 0,
            isMiniGameActive = false,
            isChapterComplete = false,
            errorMessage = null
        )
    }

    fun loadChapter(language: ProgrammingLanguage, chapterId: Int) {
        val updatedProgressMap = _state.value.progressMap.toMutableMap()
        updatedProgressMap[language] = chapterId

        _state.value = _state.value.copy(
            currentLanguage = language,
            progressMap = updatedProgressMap,
            dialogueIndex = 0,
            isMiniGameActive = false,
            isPaused = false,
            isChapterComplete = false,
            errorMessage = null
        )
    }

    fun onChapterSelected(chapterId: Int) {
        val currentState = _state.value
        val currentLanguage = currentState.currentLanguage

        if (currentLanguage == ProgrammingLanguage.NONE) return

        val updatedProgressMap = currentState.progressMap.toMutableMap()
        updatedProgressMap[currentLanguage] = chapterId

        _state.value = currentState.copy(
            progressMap = updatedProgressMap,
            dialogueIndex = 0,
            isMiniGameActive = false,
            isChapterComplete = false,
            errorMessage = null
        )
    }

    fun onNextLineClicked() {
        val currentState = _state.value

        if (currentState.currentLanguage == ProgrammingLanguage.NONE) return
        if (currentState.isMiniGameActive) return
        if (currentState.isPaused) return
        if (currentState.isChapterComplete) return

        val dialogueLines = GameRepository.getDialogueLines(
            currentState.currentLanguage,
            currentState.currentChapter
        )

        if (dialogueLines.isEmpty()) return

        val nextIndex = currentState.dialogueIndex + 1

        if (nextIndex >= dialogueLines.size) {
            completeCurrentChapter()
            return
        }

        val shouldStartMiniGame = nextIndex == 3

        _state.value = currentState.copy(
            dialogueIndex = nextIndex,
            isMiniGameActive = shouldStartMiniGame,
            errorMessage = null
        )
    }

    fun getCurrentDialogueLine(): String {
        val currentState = _state.value

        val dialogueLines = GameRepository.getDialogueLines(
            currentState.currentLanguage,
            currentState.currentChapter
        )

        return dialogueLines.getOrNull(currentState.dialogueIndex) ?: "End of chapter."
    }

    fun onChoiceSelected(choice: DialogueChoice) {
        _state.value = _state.value.copy(
            dialogueIndex = choice.targetNodeId,
            isMiniGameActive = false
        )
    }

    fun onRestartChapter() {
        _state.value = _state.value.copy(
            dialogueIndex = 0,
            isMiniGameActive = false,
            isPaused = false,
            isChapterComplete = false,
            errorMessage = null
        )
    }

    private fun completeCurrentChapter() {
        val currentState = _state.value
        val currentLang = currentState.currentLanguage
        val currentChapter = currentState.currentChapter

        val updatedProgressMap = currentState.progressMap.toMutableMap()
        val savedProgress = updatedProgressMap[currentLang] ?: 1

        if (currentChapter >= savedProgress) {
            updatedProgressMap[currentLang] = currentChapter + 1
        }

        _state.value = currentState.copy(
            progressMap = updatedProgressMap,
            isChapterComplete = true,
            isMiniGameActive = false,
            isPaused = false
        )
    }

    fun onMiniGameSuccess() {
        _state.value = _state.value.copy(
            isMiniGameActive = false
        )
    }

    fun onMiniGameFailed() {
        _state.value = _state.value.copy(
            isMiniGameActive = false,
            errorMessage = "Mini-game failed. Try again!"
        )
    }

    fun togglePauseMenu() {
        _state.value = _state.value.copy(
            isPaused = !_state.value.isPaused
        )
    }

    fun showResumeDialog(show: Boolean) {
        _state.value = _state.value.copy(
            showResumeDialog = show
        )
    }

    fun clearError() {
        _state.value = _state.value.copy(
            errorMessage = null
        )
    }
}