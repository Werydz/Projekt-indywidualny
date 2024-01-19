@file:Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")

package com.example.kolko_i_krzyzyk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kolko_i_krzyzyk.ui.theme.Kolko_i_krzyzykTheme
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

val firebaseDatabase = FirebaseDatabase.getInstance()

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
        composable("rule"){
            Rule(navController)
        }
    }
}

@Composable
fun Rule(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "1. Celem gry jest skreślenie linii poziomej, pionowej albo ukośnej na dużej planszy. \n" +
                "2. Jeśli się nie uda wygrywa osoba, która zdobyła więcej małych pól. \n" +
                "3. Małe pola zdobywa się tak jak w normalnej grze, układając linię z elementów. \n" +
                "4. Gracz rozpoczynający posiada pełną dowolność w wyborze planszy, na której zacznie. \n" +
                "5. Przy kolejnych ruchach gracze nie wybrają samodzielnie pola, pozycja jest determinowana przez to, w którym miejscu małej planszy swój symbol postawił poprzedni gracz. \n " +
                "6. To miejsce określa na którym dużym polu kolejny gracz musi postawić swój symbol \n" +
                "7. Jeśli gracz zostanie skierowany na małą planszę, która jest już rozstrzygnięta, może wybrać, na którym polu postawi swój symbol. \n" +
                "8. Remis na małej planszy, powoduje, że plansza jest spalona – nie liczy się ani dla jednego, ani dla drugiego gracza. \n" +
                "Miłej gry!")
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "<--")
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TicTacToeGame()
           Button(onClick = { navController.popBackStack() }) {
               Text(text = "<--")
           }
    }
}

@Composable
fun Square(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { navController.navigate("rule") }) {
            Text(text = "Zasady")
        }
        SquareGame()
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "<--")
        }
    }
}



@Composable
fun TicTacToeGame() {
    val databaseReference = firebaseDatabase.reference
    val coroutineScope = rememberCoroutineScope()

    // Tablica przechowująca stan planszy (0 - puste pole, 1 - kółko, 2 - krzyżyk)
    var board by remember { mutableStateOf(List(9) { 0 }) }

    // Zmienna przechowująca informację o aktualnym graczu
    var currentPlayer by remember { mutableStateOf(1) }

    // LaunchedEffect do uruchamiania automatycznego odświeżania co 1 sekund
    LaunchedEffect(true) {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                coroutineScope.launch {
                    // Pobranie danych z bazy danych
                    databaseReference.child("KK")
                        .get()
                        .addOnSuccessListener {
                            currentPlayer = it.child("player").value.toString().toInt()
                            board = it.child("board").value as List<Int>
                        }
                }
            }
        }, 0, 1000) // Odświeżanie co 1 sekund (1000 milisekund)
    }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                board = List(9) { 0 }
                currentPlayer = 1
                databaseReference.child("KK").child("board").setValue(board)
                databaseReference.child("KK").child("player").setValue(currentPlayer)
            }) {
                Text(text = "Reset")
            }
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
                    //Wyslanie danych do bazy danych
                    databaseReference.child("KK").child("board").setValue(board)
                    databaseReference.child("KK").child("player").setValue(currentPlayer)
                }
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Komunikat o wygranej lub remisie
            GameResultMessage(board = board, onRestart = {
                // Resetowanie gry
                board = List(9) { 0 }
                currentPlayer = 1
                databaseReference.child("KK").child("board").setValue(board)
                databaseReference.child("KK").child("player").setValue(currentPlayer)
            })
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
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = resultText)
            Spacer(modifier = Modifier.height(6.dp))
            Button(onClick = onRestart) {
                Text("Zagraj jeszcze raz")
            }
        }
    }
}




@Composable
fun SquareGame() {
    val databaseReference = firebaseDatabase.reference
    val coroutineScope = rememberCoroutineScope()
    val backgroundColor = MaterialTheme.colorScheme.background

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

    // LaunchedEffect do uruchamiania automatycznego odświeżania co 1 sekund
    LaunchedEffect(true) {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                coroutineScope.launch {
                    // Pobranie danych z bazy danych
                    databaseReference.child("KKK")
                        .get()
                        .addOnSuccessListener {
                            currentPlayer = it.child("player").value.toString().toInt()
                            resultsBoard = it.child("results").value as List<Int>
                            board0 = it.child("board0").value as List<Int>
                            board1 = it.child("board1").value as List<Int>
                            board2 = it.child("board2").value as List<Int>
                            board3 = it.child("board3").value as List<Int>
                            board4 = it.child("board4").value as List<Int>
                            board5 = it.child("board5").value as List<Int>
                            board6 = it.child("board6").value as List<Int>
                            board7 = it.child("board7").value as List<Int>
                            board8 = it.child("board8").value as List<Int>
                        }
                }
            }
        }, 0, 1000) // Odświeżanie co 1 sekund (1000 milisekund)
    }

    // Sprawdzanie wygranej na poszczególnych planszach
    when (plansza) {
        0 -> {
            resultsBoard = resultsBoard.toMutableList().also {
                it[plansza] = squareGameBoardResult(board = board0)
            }
            databaseReference.child("KKK").child("results").setValue(resultsBoard)
        }
        1 -> {
            resultsBoard = resultsBoard.toMutableList().also {
                it[plansza] = squareGameBoardResult(board = board1)
            }
            databaseReference.child("KKK").child("results").setValue(resultsBoard)
        }
        2 -> {
            resultsBoard = resultsBoard.toMutableList().also {
                it[plansza] = squareGameBoardResult(board = board2)
            }
            databaseReference.child("KKK").child("results").setValue(resultsBoard)
        }
        3 -> {
            resultsBoard = resultsBoard.toMutableList().also {
                it[plansza] = squareGameBoardResult(board = board3)
            }
            databaseReference.child("KKK").child("results").setValue(resultsBoard)
        }
        4 -> {
            resultsBoard = resultsBoard.toMutableList().also {
                it[plansza] = squareGameBoardResult(board = board4)
            }
            databaseReference.child("KKK").child("results").setValue(resultsBoard)
        }
        5 -> {
            resultsBoard = resultsBoard.toMutableList().also {
                it[plansza] = squareGameBoardResult(board = board5)
            }
            databaseReference.child("KKK").child("results").setValue(resultsBoard)
        }
        6 -> {
            resultsBoard = resultsBoard.toMutableList().also {
                it[plansza] = squareGameBoardResult(board = board6)
            }
            databaseReference.child("KKK").child("results").setValue(resultsBoard)
        }
        7 -> {
            resultsBoard = resultsBoard.toMutableList().also {
                it[plansza] = squareGameBoardResult(board = board7)
            }
            databaseReference.child("KKK").child("results").setValue(resultsBoard)
        }
        8 -> {
            resultsBoard = resultsBoard.toMutableList().also {
                it[plansza] = squareGameBoardResult(board = board8)
            }
            databaseReference.child("KKK").child("results").setValue(resultsBoard)
        }
    }

    Column(
        modifier = Modifier
            .background(Color.Green)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
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
                databaseReference.child("KKK").child("board0").setValue(board0)
                databaseReference.child("KKK").child("board1").setValue(board1)
                databaseReference.child("KKK").child("board2").setValue(board2)
                databaseReference.child("KKK").child("board3").setValue(board3)
                databaseReference.child("KKK").child("board4").setValue(board4)
                databaseReference.child("KKK").child("board5").setValue(board5)
                databaseReference.child("KKK").child("board6").setValue(board6)
                databaseReference.child("KKK").child("board7").setValue(board7)
                databaseReference.child("KKK").child("board8").setValue(board8)
                databaseReference.child("KKK").child("player").setValue(currentPlayer)
                databaseReference.child("KKK").child("results").setValue(resultsBoard)
            }) {
                Text(text = "Reset")
            }
        }
        // Plansza do gry
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
                    // Wysyłanie do bazy
                    databaseReference.child("KKK").child("board0").setValue(board0)
                    databaseReference.child("KKK").child("player").setValue(currentPlayer)
                }
            )
            Spacer(modifier = Modifier.width(2.dp))
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
                    // Wysyłanie do bazy
                    databaseReference.child("KKK").child("board1").setValue(board1)
                    databaseReference.child("KKK").child("player").setValue(currentPlayer)
                }
            )
            Spacer(modifier = Modifier.width(2.dp))
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
                    // Wysyłanie do bazy
                    databaseReference.child("KKK").child("board2").setValue(board2)
                    databaseReference.child("KKK").child("player").setValue(currentPlayer)
                }
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
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
                    // Wysyłanie do bazy
                    databaseReference.child("KKK").child("board3").setValue(board3)
                    databaseReference.child("KKK").child("player").setValue(currentPlayer)
                }
            )
            Spacer(modifier = Modifier.width(2.dp))
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
                    // Wysyłanie do bazy
                    databaseReference.child("KKK").child("board4").setValue(board4)
                    databaseReference.child("KKK").child("player").setValue(currentPlayer)
                }
            )
            Spacer(modifier = Modifier.width(2.dp))
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
                    // Wysyłanie do bazy
                    databaseReference.child("KKK").child("board5").setValue(board5)
                    databaseReference.child("KKK").child("player").setValue(currentPlayer)
                }
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
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
                    // Wysyłanie do bazy
                    databaseReference.child("KKK").child("board6").setValue(board6)
                    databaseReference.child("KKK").child("player").setValue(currentPlayer)
                }
            )
            Spacer(modifier = Modifier.width(2.dp))
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
                    // Wysyłanie do bazy
                    databaseReference.child("KKK").child("board7").setValue(board7)
                    databaseReference.child("KKK").child("player").setValue(currentPlayer)
                }
            )
            Spacer(modifier = Modifier.width(2.dp))
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
                    // Wysyłanie do bazy
                    databaseReference.child("KKK").child("board8").setValue(board8)
                    databaseReference.child("KKK").child("player").setValue(currentPlayer)
                }
            )
        }

        // Komunikat o wygranej lub remisie
        Row(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth(),
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
                databaseReference.child("KKK").child("board0").setValue(board0)
                databaseReference.child("KKK").child("board1").setValue(board1)
                databaseReference.child("KKK").child("board2").setValue(board2)
                databaseReference.child("KKK").child("board3").setValue(board3)
                databaseReference.child("KKK").child("board4").setValue(board4)
                databaseReference.child("KKK").child("board5").setValue(board5)
                databaseReference.child("KKK").child("board6").setValue(board6)
                databaseReference.child("KKK").child("board7").setValue(board7)
                databaseReference.child("KKK").child("board8").setValue(board8)
                databaseReference.child("KKK").child("player").setValue(currentPlayer)
                databaseReference.child("KKK").child("results").setValue(resultsBoard)
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
                            onClick = { onCellClick(position)},
                            modifier = Modifier
                                .size(cellSize)
                                .padding(1.dp)
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
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
fun squareGameBoardResult(board: List<Int>): Int {
    var hasWinner = false

    val aWinner = checkForWinnerSquare(board)

    hasWinner = aWinner!=0

    return if (hasWinner) {
        aWinner
    }
    else 0
}




@Composable
fun TicTacToeCell(value: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val content: @Composable (Modifier) -> Unit = {
        when (value) {
            1 -> Circle()
            2 -> Cross()
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
fun Circle() {
    Box{
        Image(painter = painterResource(id = R.drawable.kolo), contentDescription = "Kółko")
    }
}

@Composable
fun Cross() {
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