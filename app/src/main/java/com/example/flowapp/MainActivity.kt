package com.example.flowapp

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowapp.ui.theme.FlowAppTheme
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("FlowOperatorInvokedInComposition", "StateFlowValueCalledInComposition",
        "CoroutineCreationDuringComposition"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowAppTheme {
                val viewModel by viewModels<FlowViewModel>()
                // raccogliamo le emissioni del Flow
                val currentValue: State<Int> = viewModel.countdownFlow
                    // possiamo fare tutte le classiche operazioni
                    .filter { time ->
                        time % 2 == 0
                    }.map { time -> time * 10 }
                    .onEach {
                        Log.d("Flow", "printing flow val $it")
                    }
                    .collectAsState(viewModel.startingValue)
                // settiamo il valore dello stateFlow, se non inseriamo collectaAsState il valore non incrementa
                val count = viewModel.stateFlow.collectAsState(0)

                // il live Data va osservato
                val liveData by viewModel.liveData.observeAsState()

                // lo state Flow va osservato
                val stateFlow by viewModel.stateFlowTest.collectAsState(this)

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = liveData.toString() ?: "null",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                //                      .size(170.dp)
                                .padding(20.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Button(
                            onClick = { viewModel.changeLiveData() },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(end = 20.dp)
                        )
                        {
                            Text(
                                text = "LiveData",
                                style = MaterialTheme.typography.displayMedium
                            )
                        }
                        Text(
                            text = stateFlow.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                //                      .size(170.dp)
                                .padding(20.dp)
                                .align(Alignment.CenterHorizontally)
                        )


                        Button(
                            onClick = { viewModel.changeStateFlow() },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(end = 20.dp)
                        )
                        {
                            Text(
                                text = "StateFlow",
                                style = MaterialTheme.typography.displayMedium
                            )
                        }
                        //val snackBarMessage = viewModel.sharedFlowTest

                        val snackbarHostState = remember { SnackbarHostState() }
                        val scope = rememberCoroutineScope()
                        var message by remember { mutableStateOf<String>("inizio") }

                        // la coroutine si mette in ascolto della emissione dello shared Flow
                        scope.launch {
                            viewModel.sharedFlowTest.collectLatest {
                                message = it
                            }
                        }

                        Scaffold(
                            modifier = Modifier
                                .padding(40.dp)
                                .align(Alignment.CenterHorizontally),
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            /*floatingActionButton = {
                                //var clickCount by remember { mutableStateOf(0) }
                                ExtendedFloatingActionButton(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    onClick = {
                                        // show snackbar as a suspend function
                                        scope.launch {
                                            viewModel.sharedFlowTest.collectLatest {
                                                snackbarHostState.showSnackbar(
                                                    "Snackbar $it"
                                                )
                                            }

              *//*                              snackbarHostState.showSnackbar(
                                                "Snackbar # ${++clickCount}"
                                            )*//*
                                        }
                                        viewModel.sbackBarEmit()
                                    }
                                ) { Text("Show snackbar") }
                            }*/
                            content = { innerPadding ->
                                /*Text(
                                    text = "Click To show Toast App Bar",
                                    modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
                                )*/
                                Button(
                                    onClick = {
                // va lanciata prima di visualizzare il messaggio (altrimenti al primo avvio non si visualizza nulla)
                                        viewModel.snackBarEmit()
                                        scope.launch {
                                        // show snackbar as a suspend function
                                         snackbarHostState.showSnackbar(
                                             duration = SnackbarDuration.Short,
                                             message = "Snackbar $message"
                                                )
                                            }
                                    }

                                            /*                              snackbarHostState.showSnackbar(
                                                                              "Snackbar # ${++clickCount}"
                                                                          )*/
,
         //                           modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 40.dp)
                                       modifier = Modifier.wrapContentSize()
                                )
                                {
                                    Text(
                                        text = "SharedFlow",
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                }
                            }
                        )


                       /* Button(
                            onClick = { viewModel.sbackBarEmit() },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(top = 40.dp)
                        )
                        {
                            Text(
                                text = "SharedFlow",
                                style = MaterialTheme.typography.displayMedium
                            )
                        }*/

                    }
                }
            }
        }
    }

/*    @Composable
    fun SnackBarTrigger(viewModel: FlowViewModel) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        LaunchedEffect(key1 = Unit) {
            viewModel.sharedFlowTest.collectLatest {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    *//*floatingActionButton = {
                        var clickCount by remember { mutableStateOf(0) }
                        ExtendedFloatingActionButton(
                            onClick = {
                                // show snackbar as a suspend function
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Snackbar # ${++clickCount}"
                                    )
                                }
                            }
                        ) { Text("Show snackbar") }
                    },*//*
                    content = { innerPadding ->
                        Text(
                            text = it,
                            modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
                        )
                    }
                )
            }
        }
    }*/

}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlowAppTheme {

    }
}