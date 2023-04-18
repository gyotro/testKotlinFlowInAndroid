package com.example.flowapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class FlowViewModel: ViewModel() {
    val startingValue = 10
    // dal flow si emettono dei valori (simile a Mono o Flux in Java Reactive
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