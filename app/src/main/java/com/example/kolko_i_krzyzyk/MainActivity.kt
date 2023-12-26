package com.example.kolko_i_krzyzyk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kolko_i_krzyzyk.ui.theme.Kolko_i_krzyzykTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kolko_i_krzyzykTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    Nawigacja()
}

@Composable
fun Nawigacja(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen" ){
        composable("splash_screen"){
            SplashScreen(navController)
        }
        composable("main_screen"){
            MainScreen(navController)
        }
        composable("tic_tac_toe"){
            TicTacToe(navController)
        }
        composable("square"){
            Square(navController)
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(key1 = true){
        delay(3000L)
        navController.navigate("main_screen")
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Box(Modifier.height(200.dp)){
            Image(painter = painterResource(id = R.drawable.icon), contentDescription = "Logo", Modifier.size(195.dp))
        }
        Spacer(Modifier.height(65.dp))
        Text(text = "Autor: Weronika Rydz", fontSize = 25.sp, color = Color.Black)
        Text(text = "Temat: Kółko i krzyżyk", fontSize = 25.sp, color = Color.Black)
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Button(onClick = { navController.navigate("tic_tac_toe") }) {
            Text(text = "Gra w kółko i krzyżyk")
        }
        Spacer(Modifier.height(65.dp))
        Button(onClick = { /*TODO*/ }) {
            
        }
    }
}

@Composable
fun TicTacToe(navController: NavHostController) {
    TicTacToeGame()
}

@Composable
fun Square(navController: NavHostController) {

}

@Composable
fun TicTacToeGame() {
    // Tablica przechowująca stan planszy (0 - puste pole, 1 - kółko, 2 - krzyżyk)
    var board by remember { mutableStateOf(List(9) { 0 }) }

    // Zmienna przechowująca informację o aktualnym graczu
    var currentPlayer by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Plansza do gry
        TicTacToeBoard(
            board = board,
            onCellClick = { position ->
                // Obsługa kliknięcia w komórkę
                if (board[position] == 0) {
                    board = board.toMutableList().also {
                        it[position] = currentPlayer
                    }
                    // Zmiana gracza po kliknięciu
                    currentPlayer = 3 - currentPlayer
                }
            }
        )

        // Komunikat o wygranej lub remisie
        GameResultMessage(board = board, onRestart = {
            // Resetowanie gry
            board = List(9) { 0 }
            currentPlayer = 1
        })
    }
}

@Composable
fun TicTacToeBoard(board: List<Int>, onCellClick: (Int) -> Unit) {
    val density = LocalDensity.current.density
    val cellSize = with(density) { 50.dp }

    // Plansza do gry
    Column {
        for (row in 0 until 3) {
            Row {
                for (col in 0 until 3) {
                    val position = row * 3 + col
                    val cellValue = board[position]
                    TicTacToeCell(
                        value = cellValue,
                        onClick = { onCellClick(position) },
                        modifier = Modifier
                            .size(cellSize)
                            .padding(2.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = if (row == 0) CornerSize(0) else ZeroCornerSize,
                                    topEnd = if (row == 0) CornerSize(0) else ZeroCornerSize,
                                    bottomStart = if (row == 2) CornerSize(0) else ZeroCornerSize,
                                    bottomEnd = if (row == 2) CornerSize(0) else ZeroCornerSize
                                )
                            )
                            .background(Color.Gray)
                            .clickable {
                                onCellClick(position)
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun TicTacToeCell(value: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val content: @Composable (Modifier) -> Unit = {
        when (value) {
            1 -> Circle(modifier = Modifier
                .size(40.dp)
                .background(Color.Blue)
                .padding(5.dp))
            2 -> Cross(modifier = Modifier
                .size(40.dp)
                .background(Color.Red)
                .padding(5.dp))
        }
    }

    Box(
        modifier = modifier
            .background(Color.Gray)
            .clickable { onClick() }
    ) {
        content(modifier)
    }
}

@Composable
fun Circle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun Cross(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(CornerSize(0), CornerSize(0), CornerSize(8), CornerSize(8)))
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun GameResultMessage(board: List<Int>, onRestart: () -> Unit) {
    var resultText by remember { mutableStateOf("") }

    val aWinner = checkForWinner(board)
    var hasWinner = false

    hasWinner = aWinner!=0

    if (hasWinner) {
        resultText = "Wygrał gracz $aWinner!"
    } else if (board.all { it != 0 }) {
        resultText = "Remis!"
    }

    if (hasWinner || board.all { it != 0 }) {
        // Komunikat o wyniku gry
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(text = resultText)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRestart) {
                Text("Zagraj jeszcze raz")
            }
        }
    }
}

fun checkForWinner(board: List<Int>): Int {
    // Sprawdzenie wyganej w wierszach
    for (i in 0 until 3) {
        if (board[i * 3] == board[i * 3 + 1] && board[i * 3 + 1] == board[i * 3 + 2] && board[i * 3] != 0) {
            return board[i * 3]
        }
    }

    // Sprawdzenie wyganej w kolumnach
    for (i in 0 until 3) {
        if (board[i] == board[i + 3] && board[i + 3] == board[i + 6] && board[i] != 0) {
            return board[i]
        }
    }

    // Sprawdzenie wyganej na skos
    if (board[0] == board[4] && board[4] == board[8] && board[0] != 0) {
        return board[0]
    }
    if (board[2] == board[4] && board[4] == board[6] && board[2] != 0) {
        return board[2]
    }

    return 0
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Kolko_i_krzyzykTheme {
        Greeting()
    }
}