package com.example.flowapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlowViewModel: ViewModel() {

    // ora proviamo uno state Flow: utile quando dobbiamo memorizzare lo stato di uno screen
    // da best practice si crea uno stateflow mutabile ed uno immutabile

    // stateFlow e ShardedFlow Sono degli hot flow: se non c'è nessuno che li colletta, vengono persi
    private val _stateFlow = MutableStateFlow(0) // mutable
    val stateFlow = _stateFlow.asStateFlow() // aggiungendo asStateFlow() lo si fa immutabile

    // stessa cosa con uno sharedFlow
    private val _sharedFlow = MutableSharedFlow<Int>(replay = 5) // creazione di uno sharedFlow, con replay (non obbligatorio) creiamo una sorta di cache che ci immagazzina il valore
    val sharedFlow = _sharedFlow.asSharedFlow() // aggiungendo asStateFlow() lo si fa immutabile

    // gli sharedFlow sono utilizzati per catturare eventi che vanno eseguiti una singola volta,
    // ad esempio in Compose si possono utilizzare con i LaunchedEffect, che vengono utilizzati una sola volta

/*    LaunchedEffect(key1 = "true") {
        viewModel.sharedFlow.collect{ number ->

        }
    }*/
    fun emitSharedFlow(square: Int) {
        viewModelScope.launch {
            _sharedFlow.emit( square * square)
        }
    }

    fun incrementStateFlow() {
        _stateFlow.value += 1
    }

    val startingValue = 10
    // dal flow si emettono dei valori (simile a Mono o Flux in Java Reactive
    // questo modo di creare i flow è detto cold flow, xkè emette valori ed aspetta che qualcuni li colletti
    val countdownFlow = flow<Int> {
        var currentValue = startingValue
        // viene emesso il valore iniziale
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000)
            currentValue--
            emit(currentValue)
        }
    }
}