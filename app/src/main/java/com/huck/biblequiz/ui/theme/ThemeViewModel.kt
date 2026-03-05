package com.huck.biblequiz.ui.theme

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val prefs: ThemePreferences
) : ViewModel() {

    private val _colors = MutableStateFlow(prefs.load())
    val colors: StateFlow<AppColors> = _colors

    fun updateColors(colors: AppColors) {
        _colors.value = colors
        prefs.save(colors)
    }
}
