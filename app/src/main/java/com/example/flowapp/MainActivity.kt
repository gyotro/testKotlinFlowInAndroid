package com.example.flowapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Surface
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
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

                // il live Data va osservato
                val liveData by viewModel.liveData.observeAsState()

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = ( liveData ?: "null" ),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
          //                      .size(170.dp)
                                .padding(20.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Button(onClick = { viewModel.changeLiveData() },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(end = 20.dp)
                            )
                            {
                                Text(text = "LiveData",
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