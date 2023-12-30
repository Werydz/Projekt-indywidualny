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
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Kolko_i_krzyzykTheme {
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
        Button(onClick = { navController.navigate("square") }) {
            Text(text = "Gra w kółko, krzyżyk, kwadrat")
        }
    }
}

@Composable
fun TicTacToe(navController: NavHostController) {
    TicTacToeGame()
}

@Composable
fun Square(navController: NavHostController) {
    SquareGame()
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
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Plansza do gry
        Row {
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
        }

        // Komunikat o wygranej lub remisie
        Row(
            modifier = Modifier
                .padding(1.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            GameResultMessage(board = board, onRestart = {
                // Resetowanie gry
                board = List(9) { 0 }
                currentPlayer = 1
            })
        }
    }
}

@Composable
fun TicTacToeBoard(board: List<Int>, onCellClick: (Int) -> Unit) {
    val density = LocalDensity.current.density
    val cellSize = with(density) { 100.dp }

    // Plansza do gry
    Column(
        modifier = Modifier
            .background(Color.Black)
    ) {
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
                            .padding(3.dp)
                            .background(Color.White)
                            .clickable {
                                onCellClick(position)
                            }
                    )
                }
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




@Composable
fun SquareGame() {
    // Tablica przechowująca stan planszy (0 - puste pole, 1 - kółko, 2 - krzyżyk)
    var board0 by remember { mutableStateOf(List(9) { 0 }) }
    var board1 by remember { mutableStateOf(List(9) { 0 }) }
    var board2 by remember { mutableStateOf(List(9) { 0 }) }
    var board3 by remember { mutableStateOf(List(9) { 0 }) }
    var board4 by remember { mutableStateOf(List(9) { 0 }) }
    var board5 by remember { mutableStateOf(List(9) { 0 }) }
    var board6 by remember { mutableStateOf(List(9) { 0 }) }
    var board7 by remember { mutableStateOf(List(9) { 0 }) }
    var board8 by remember { mutableStateOf(List(9) { 0 }) }

    var resultsBoard by remember { mutableStateOf(List(9) { 0 }) }

    // Zmienna przechowująca informację o aktualnym graczu
    var currentPlayer by remember { mutableStateOf(1) }
    var plansza by remember { mutableStateOf(0) }

    // Sprawdzanie wygranej na poszczególnych planszach
    when (plansza) {
        0 -> resultsBoard = resultsBoard.toMutableList().also {
            it[plansza] = SquareGameBoardResult(board = board0)
        }
        1 -> resultsBoard = resultsBoard.toMutableList().also {
            it[plansza] = SquareGameBoardResult(board = board1)
        }
        2 -> resultsBoard = resultsBoard.toMutableList().also {
            it[plansza] = SquareGameBoardResult(board = board2)
        }
        3 -> resultsBoard = resultsBoard.toMutableList().also {
            it[plansza] = SquareGameBoardResult(board = board3)
        }
        4 -> resultsBoard = resultsBoard.toMutableList().also {
            it[plansza] = SquareGameBoardResult(board = board4)
        }
        5 -> resultsBoard = resultsBoard.toMutableList().also {
            it[plansza] = SquareGameBoardResult(board = board5)
        }
        6 -> resultsBoard = resultsBoard.toMutableList().also {
            it[plansza] = SquareGameBoardResult(board = board6)
        }
        7 -> resultsBoard = resultsBoard.toMutableList().also {
            it[plansza] = SquareGameBoardResult(board = board7)
        }
        8 -> resultsBoard = resultsBoard.toMutableList().also {
            it[plansza] = SquareGameBoardResult(board = board8)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Plansza do gry
        Column {
            // Pierwszy rząd
            Row {
                // Zerowa plansza lewy górny róg
                SquareBoard(
                    board = board0,
                    resultsBoard = resultsBoard[0],
                    onCellClick = { position ->
                        // Obsługa kliknięcia w komórkę
                        if (board0[position] == 0) {
                            board0 = board0.toMutableList().also {
                                it[position] = currentPlayer
                            }
                            // Zmiana gracza po kliknięciu
                            currentPlayer = 3 - currentPlayer
                            plansza = 0
                        }
                    }
                )
                // Pierwsza plansza góra środek
                SquareBoard(
                    board = board1,
                    resultsBoard = resultsBoard[1],
                    onCellClick = { position ->
                        // Obsługa kliknięcia w komórkę
                        if (board1[position] == 0) {
                            board1 = board1.toMutableList().also {
                                it[position] = currentPlayer
                            }
                            // Zmiana gracza po kliknięciu
                            currentPlayer = 3 - currentPlayer
                            plansza = 1
                        }
                    }
                )
                // Druga plansz prawy górny róg
                SquareBoard(
                    board = board2,
                    resultsBoard = resultsBoard[2],
                    onCellClick = { position ->
                        // Obsługa kliknięcia w komórkę
                        if (board2[position] == 0) {
                            board2 = board2.toMutableList().also {
                                it[position] = currentPlayer
                            }
                            // Zmiana gracza po kliknięciu
                            currentPlayer = 3 - currentPlayer
                            plansza = 2
                        }
                    }
                )
            }
            // Drugi rząd
            Row {
                // Trzecia plansza środek prawo
                SquareBoard(
                    board = board3,
                    resultsBoard = resultsBoard[3],
                    onCellClick = { position ->
                        // Obsługa kliknięcia w komórkę
                        if (board3[position] == 0) {
                            board3 = board3.toMutableList().also {
                                it[position] = currentPlayer
                            }
                            // Zmiana gracza po kliknięciu
                            currentPlayer = 3 - currentPlayer
                            plansza = 3
                        }
                    }
                )
                // Czwarta plansza w samym środku
                SquareBoard(
                    board = board4,
                    resultsBoard = resultsBoard[4],
                    onCellClick = { position ->
                        // Obsługa kliknięcia w komórkę
                        if (board4[position] == 0) {
                            board4 = board4.toMutableList().also {
                                it[position] = currentPlayer
                            }
                            // Zmiana gracza po kliknięciu
                            currentPlayer = 3 - currentPlayer
                            plansza = 4
                        }
                    }
                )
                // Piąta plansza środek po prawej
                SquareBoard(
                    board = board5,
                    resultsBoard = resultsBoard[5],
                    onCellClick = { position ->
                        // Obsługa kliknięcia w komórkę
                        if (board5[position] == 0) {
                            board5 = board5.toMutableList().also {
                                it[position] = currentPlayer
                            }
                            // Zmiana gracza po kliknięciu
                            currentPlayer = 3 - currentPlayer
                            plansza = 5
                        }
                    }
                )
            }
            // Trzeci rząd
            Row {
                // Szósta plansza lewy dolny róg
                SquareBoard(
                    board = board6,
                    resultsBoard = resultsBoard[6],
                    onCellClick = { position ->
                        // Obsługa kliknięcia w komórkę
                        if (board6[position] == 0) {
                            board6 = board6.toMutableList().also {
                                it[position] = currentPlayer
                            }
                            // Zmiana gracza po kliknięciu
                            currentPlayer = 3 - currentPlayer
                            plansza = 6
                        }
                    }
                )
                // Siódma plansza dół środek
                SquareBoard(
                    board = board7,
                    resultsBoard = resultsBoard[7],
                    onCellClick = { position ->
                        // Obsługa kliknięcia w komórkę
                        if (board7[position] == 0) {
                            board7 = board7.toMutableList().also {
                                it[position] = currentPlayer
                            }
                            // Zmiana gracza po kliknięciu
                            currentPlayer = 3 - currentPlayer
                            plansza = 7
                        }
                    }
                )
                // Ósma plansza prawy dolny róg
                SquareBoard(
                    board = board8,
                    resultsBoard = resultsBoard[8],
                    onCellClick = { position ->
                        // Obsługa kliknięcia w komórkę
                        if (board8[position] == 0) {
                            board8 = board8.toMutableList().also {
                                it[position] = currentPlayer
                            }
                            // Zmiana gracza po kliknięciu
                            currentPlayer = 3 - currentPlayer
                            plansza = 8
                        }
                    }
                )
            }
        }

        // Komunikat o wygranej lub remisie
        Row(
            modifier = Modifier
                .padding(1.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            SquareGameResultMessage(board = resultsBoard, onRestart = {
                // Resetowanie gry
                board0 = List(9) { 0 }
                board1 = List(9) { 0 }
                board2 = List(9) { 0 }
                board3 = List(9) { 0 }
                board4 = List(9) { 0 }
                board5 = List(9) { 0 }
                board6 = List(9) { 0 }
                board7 = List(9) { 0 }
                board8 = List(9) { 0 }
                currentPlayer = 1
                resultsBoard = List(9) { 0 }
            })
        }
    }
}

@Composable
fun SquareBoard(board: List<Int>, resultsBoard: Int, onCellClick: (Int) -> Unit) {
    val density = LocalDensity.current.density
    val cellSize = with(density) { 40.dp }

    if(resultsBoard==0){
        // Plansza do gry
        Column(
            modifier = Modifier
                .background(Color.Black)
        ) {
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
                                .background(Color.White)
                                .clickable {
                                    onCellClick(position)
                                }
                        )
                    }
                }
            }
        }
    }
    else if(resultsBoard!=0){
        TicTacToeCell(
            value = resultsBoard,
            onClick = { },
            modifier = Modifier
                .size(120.dp)
                .padding(2.dp)
                .background(Color.White)
        )
    }
}

fun checkForWinnerSquare(board: List<Int>): Int {

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

@Composable
fun SquareGameResultMessage(board: List<Int>, onRestart: () -> Unit) {
    var resultText by remember { mutableStateOf("") }
    var hasWinner = false

    val aWinner = checkForWinnerSquare(board)

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

@Composable
fun SquareGameBoardResult(board: List<Int>): Int {
    var hasWinner = false

    val aWinner = checkForWinnerSquare(board)

    hasWinner = aWinner!=0

    if (hasWinner) {
        return aWinner
    }
    else return 0
}




@Composable
fun TicTacToeCell(value: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val content: @Composable (Modifier) -> Unit = {
        when (value) {
            1 -> Circle(modifier = Modifier
                .size(100.dp))
            2 -> Cross(modifier = Modifier
                .size(100.dp))
        }
    }

    Box(
        modifier = modifier
            .background(Color.White)
            .clickable { onClick() }
    ) {
        content(modifier)
    }
}

@Composable
fun Circle(modifier: Modifier = Modifier) {
    Box{
        Image(painter = painterResource(id = R.drawable.kolo), contentDescription = "Kółko")
    }
}

@Composable
fun Cross(modifier: Modifier = Modifier) {
    Box{
        Image(painter = painterResource(id = R.drawable.krzyz), contentDescription = "Krzyżyk")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Kolko_i_krzyzykTheme {
        Greeting()
    }
}