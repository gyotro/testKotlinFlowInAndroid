package com.example.flowapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.UserHandle
import android.text.Layout
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Surface
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowapp.ui.theme.FlowAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants
import java.nio.file.WatchEvent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    @SuppressLint(
        "FlowOperatorInvokedInComposition", "StateFlowValueCalledInComposition",
        "CoroutineCreationDuringComposition"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlowAppTheme {
                Surface(color = Color.LightGray, modifier = Modifier.fillMaxSize()) {
                    var timer by remember { mutableStateOf(value = "0:0") }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    modifier = Modifier.padding(40.dp),
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                    text = "Inserire minuti e secondi separati da :"
                                )
                                TextField(
                                    colors = TextFieldDefaults.colors(Color.Black),
                                    value = timer,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(40.dp)
                                        .background(Color.White),
                                    onValueChange = { timer = it }
                                )
                            }
                        }
                        Box(contentAlignment = Alignment.Center) {
                            Timer(
                                totalTime = convertTimers(timer) * 1000L,
                                handleColor = Color.Green,
                                inactiveBarColor = Color.DarkGray,
                                activeBarColor = Color(0xFF37B900),
                                modifier = Modifier.size(200.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun convertTimers(time: String): Long {
    return try {
        val (min, sec) = time.split(":")
        min.toLong() * 60 + sec.toLong()
    }catch (e: Exception) {
        0L
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlowAppTheme {

    }
}

@Composable
fun Timer(
    totalTime: Long,
    handleColor: Color,
    inactiveBarColor: Color,
    activeBarColor: Color,
    modifier: Modifier,
    initialValue: Float = 1f,
    // si riferisce allo spessore della linea dell'arco
    strokedWidth: Dp = 5.dp,
) {
    // settiamo i valori
    // questo è il size dell'arco che man mano scala
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    // percentuale di completamento
    var value by remember {
        mutableStateOf(initialValue)
    }
    var currentTime by remember {
        mutableStateOf(totalTime)
    }
    var isTimerRunning by remember {
        mutableStateOf(false)
    }

    // Per aggiornare il contatore verrà utilizzato un LaunchedEffect: componente che viene richiamato ogni qualvolta accade un evento
    // verrà triggerata ogni volta cambiano il currentTime o isTimerRunning
    LaunchedEffect(
        key1 = currentTime,
        key2 = isTimerRunning
    ) {
        if (currentTime > 0 && isTimerRunning) {
            delay(100L)
            currentTime -= 100
            value = currentTime / totalTime.toFloat()
        }
        if (currentTime == 0L)
            isTimerRunning = false
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged {
                size = it
            }
    ) {
        Canvas(modifier = modifier) {
            // disegniamo gli archi, uno per l'active e l'altro per l'incactive
            drawArc(
                color = inactiveBarColor,
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokedWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = activeBarColor,
                startAngle = -215f,
                sweepAngle = 250f * value, // si moltiplica per la % di completamento
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokedWidth.toPx(), cap = StrokeCap.Round)
            )
            // ora dobbiamo disegnare il punto che man mano si muove: abbiamo bisogno delle coordinate x ed y che dovrano variare
            val center = Offset(x = size.width.toFloat() / 2, y = size.height.toFloat() / 2)
            // l'angolo che varia mano mano
            val beta = (250f * value + 145f) * (PI / 180f).toFloat()
            val r = size.width / 2f
            val a = cos(beta) * r
            val b = sin(beta) * r
            drawPoints(
                listOf(Offset(x = center.x + a, y = center.y + b)),
                pointMode = PointMode.Points,
                color = handleColor,
                strokeWidth = (strokedWidth * 3f).toPx(),
                cap = StrokeCap.Round
            )
        }
        Text(
            textAlign = TextAlign.Center,
            text = (currentTime / 1000L).toString(),
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        // Sistemare il posizionamento della colonna...si sovrappone al contatore
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    if (currentTime <= 0) {
                        currentTime = totalTime
                        isTimerRunning = true
                    } else isTimerRunning = isTimerRunning.not()
                },
                //modifier = Modifier.align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTimerRunning.not() || currentTime <= 0) {
                        Color.Green
                    } else
                        Color.Red
                )
            ) {
                Text(
                    text = if (isTimerRunning && currentTime >= 0) "Stop"
                    //else if (isTimerRunning.not() && currentTime > 0) "Start"
                    else "Start"
                )
            }
            Button(
                onClick = { currentTime = totalTime },
                //modifier = Modifier.align(Alignment.BottomCenter).padding(40.dp),
                colors = ButtonDefaults.buttonColors(Color.Magenta)
            ) {
                Text(
                    text = "Reset"
                )
            }
        }

    }
}