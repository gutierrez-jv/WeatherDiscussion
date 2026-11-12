package com.example.weatherdiscussion.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherdiscussion.data.ApiResult
import com.example.weatherdiscussion.data.WeatherRepository
import com.example.weatherdiscussion.uimodels.City
import com.example.weatherdiscussion.uimodels.CityDetailUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailUIState{
    data object Loading: DetailUIState
    data class Success(val detail: CityDetailUI) : DetailUIState
    data class Error(val message: String) : DetailUIState
}

class DetailsViewModel(private val repo: WeatherRepository, val city: City) : ViewModel() {
    private val _state = MutableStateFlow<DetailUIState>(DetailUIState.Loading)
    val state: StateFlow<DetailUIState> = _state.asStateFlow()

    init{
        load()
    }

    fun load(){
        viewModelScope.launch {
            _state.value = DetailUIState.Loading
            _state.value = when(val result = repo.detail(city)){
                is ApiResult.Success -> DetailUIState.Success(result.data)
                else -> DetailUIState.Error(result.toString())
            }
        }
    }

}