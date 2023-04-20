package com.example.flowapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.flowapp.ui.theme.FlowAppTheme
import kotlinx.coroutines.flow.*

class MainActivity : ComponentActivity() {
    @SuppressLint("FlowOperatorInvokedInComposition", "StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowAppTheme {
                val viewModel by viewModels<FlowViewModel>()
                // raccogliamo le emissioni del Flow
                val currentValue: State<Int> = viewModel.countdownFlow
                    // possiamo fare tutte le classiche operazioni
                    .filter {
                    time -> time % 2 == 0
                }.map { time -> time * 10 }
                    .onEach {
                        Log.d("Flow", "printing flow val $it")
                    }
                    .collectAsState(viewModel.startingValue)
                // settiamo il valore dello stateFlow, se non inseriamo collectaAsState il valore non incrementa
                val count = viewModel.stateFlow.collectAsState(0)
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        /*Text(
                            text = currentValue.value.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.Center)
                        )*/
                        Button(onClick = { viewModel.incrementStateFlow() },
                            modifier = Modifier
                                .align(Alignment.Center))
                            {
                                Text(text = "Counter is: " + count.value.toString(),
                                    style = MaterialTheme.typography.displayMedium
                                )
                            }

                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlowAppTheme {

    }
}