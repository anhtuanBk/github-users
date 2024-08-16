package co.touchlab.kampkit.presentation

import androidx.lifecycle.ViewModel

actual abstract class ViewModel actual constructor() : ViewModel() {
    actual override fun onCleared() {
        super.onCleared()
    }
}
