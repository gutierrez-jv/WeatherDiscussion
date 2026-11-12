package com.example.weatherdiscussion.ui.feed

import android.icu.text.SearchIterator
import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.savedState
import com.example.weatherdiscussion.data.ApiResult
import com.example.weatherdiscussion.data.WeatherRepository
import com.example.weatherdiscussion.uimodels.City
import com.example.weatherdiscussion.uimodels.CityWeatherUI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

sealed interface FeedUIState{
    data object Loading: FeedUIState
    data class Success(val cities: List<CityWeatherUI>, val isRefreshing: Boolean = false) : FeedUIState
    data class Error(val message: String): FeedUIState
}

sealed interface SearchUIState{
    data object Idle: SearchUIState
    data object Loading: SearchUIState
    data class Results(val cities: List<City>) : SearchUIState
    data class Error(val message: String) : SearchUIState
}

class FeedViewModel(private val repo: WeatherRepository, private val savedState: SavedStateHandle) : ViewModel() {

    private var cities = DEFAULT_CITIES

    private val _state = MutableStateFlow<FeedUIState>(FeedUIState.Loading)
    val state: StateFlow<FeedUIState> = _state.asStateFlow()

    init{
        load()
    }

    val query: StateFlow<String> = savedState.getStateFlow(QUERY_KEY, "")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val search: StateFlow<SearchUIState> = query
        .debounce(300.milliseconds)
        .map{ it.trim()}
        .distinctUntilChanged()
        .flatMapLatest { q ->
            if(q.length < 2) flowOf<SearchUIState>(SearchUIState.Idle)
            else flow<SearchUIState> {
                emit(SearchUIState.Loading)
                emit(
                    when(val result = repo.searchCities(q)){
                        is ApiResult.Success -> SearchUIState.Results(result.data)
                        else -> SearchUIState.Error(result.toString())
                    }
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUIState.Idle)

    fun load(){
        viewModelScope.launch {
            _state.value = FeedUIState.Loading
            _state.value = when(val result = repo.currentWeather(cities)){
                is ApiResult.Success -> FeedUIState.Success(result.data)
                else -> FeedUIState.Error(result.toString())
            }
        }
    }

    fun onQueryChange(text: String){
        savedState[QUERY_KEY] = text
    }

    fun refresh(){
        viewModelScope.launch {
            val current = _state.value as? FeedUIState.Success ?: return@launch
            _state.value = current.copy(isRefreshing = true)
            when(val result = repo.currentWeather(cities)){
                is ApiResult.Success -> _state.value = FeedUIState.Success(result.data)
                else -> {
                    _state.value = current
                }
            }
        }
    }

    fun addCity(city: City){
        onQueryChange("")
        if(cities.any() {it.lat == city.lat && it.lon == city.lon}) return
        cities = listOf(city)+cities
        if(_state.value is FeedUIState.Success) refresh() else load()
    }

    companion object {
        private const val QUERY_KEY = "query"
        val DEFAULT_CITIES = listOf(
            City("Tokyo", "JP",35.6762, 139.6503),
            City("Sydney","AU", -33.8688, 151.2093)
        )
    }
}