package fr.irif.lingeswaran.memorisation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.irif.lingeswaran.memorisation.features.ChoixActivity
import fr.irif.lingeswaran.memorisation.features.GestionThemesActivity
import fr.irif.lingeswaran.memorisation.features.ParametresActivity
import fr.irif.lingeswaran.memorisation.features.ui.MainViewModel
import fr.irif.lingeswaran.memorisation.features.ui.ParametresViewModel
import fr.irif.lingeswaran.memorisation.services.BackgroundMusicService
import fr.irif.lingeswaran.memorisation.theme.MemorisationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemorisationTheme {
                val model: ParametresViewModel = viewModel()
                val selectedFond by model.fond.collectAsState(initial = "#FFFFFF")
                val selectedTaille by model.taille.collectAsState(initial = 24)
                val backgroundColor = Color(android.graphics.Color.parseColor(selectedFond))

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    startService(Intent(this, BackgroundMusicService::class.java))
                    MyScreen(taille=selectedTaille,couleur=backgroundColor)
                }
            }
        }
    }
}
@Composable
fun HomeScreen(taille: Int, backgroundColor: Color) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Bienvenue !", fontSize = taille.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        HomeButton("Gérer les questions", Icons.Default.List) {
            ouvrirActivite(context = context, activite = GestionThemesActivity::class.java)
        }

        HomeButton("Commencer un questionnaire", Icons.Default.PlayArrow) {
            ouvrirActivite(context = context, activite = ChoixActivity::class.java)
        }

        HomeButton("Paramètres", Icons.Default.Settings) {
            ouvrirActivite(context = context, activite = ParametresActivity::class.java)
        }
    }
}

@Composable
fun MyScreen(taille: Int, couleur: Color, model: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val trophee1 by model.trophee1.collectAsState(initial = false)
    val trophee2 by model.trophee2.collectAsState(initial = false)
    val trophee3 by model.trophee3.collectAsState(initial = false)
    val trophee4 by model.trophee4.collectAsState(initial = false)
    val trophee5 by model.trophee5.collectAsState(initial = false)
    val trophee6 by model.trophee6.collectAsState(initial = false)

    Scaffold(
        topBar = { MyTopBar(color = couleur) },
        bottomBar = { MyBottomBar(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize().background(couleur)
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(taille, couleur) }
            composable("trophee") { TropheeScreen(trophee1, trophee2, trophee3, trophee4, trophee5, trophee6,couleur) }
            composable("stats") { StatistiquesScreen(couleur) }
        }
    }
}


@Composable
fun StatistiquesScreen(
    backgroundColor: Color,
    model: MainViewModel = viewModel(),
) {

    Text("Test", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

    val jeuxQuestions by model.listjeuxquestions.collectAsState(initial = listOf())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // Collecter les statistiques une seule fois à l'extérieur de la boucle
        val listeStatistiques by model.liststatistique.value!!.collectAsState(initial = listOf())

        // Afficher les statistiques pour chaque JeuxQuestions
        jeuxQuestions.forEach { jeuxQuestion ->

            // Utiliser les statistiques collectées à l'extérieur de la boucle
            val statistiquesJeuxQuestion = listeStatistiques.filter { it.idJeux == jeuxQuestion.id }

            statistiquesJeuxQuestion.forEach { statistique ->
                Text("JeuxQuestions: ${jeuxQuestion.nom}, Score: ${statistique.score}")
            }
        }
    }
}


@Composable
fun TropheeScreen(trophee1: Boolean, trophee2: Boolean,
                  trophee3: Boolean, trophee4: Boolean, trophee5: Boolean, trophee6: Boolean,couleur:Color) {
    val tropheeIds = listOf(R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5, R.drawable.t6)

    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(couleur)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        tropheeIds.chunked(2).forEachIndexed { rowIndex, rowTrophees ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowTrophees.forEachIndexed { columnIndex, resourceId ->
                        val index = rowIndex * 2 + columnIndex
                        val isUnlocked = when (index) {
                            0 -> trophee1
                            1 -> trophee2
                            2 -> trophee3
                            3 -> trophee4
                            4 -> trophee5
                            5 -> trophee6
                            else -> false
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = resourceId),
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        dialogMessage.value = getTropheeInfoMessage(index + 1)
                                        showDialog.value = true
                                    }
                                    .size(100.dp)
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Trophée ${index + 1}",
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color.Green else Color.Red
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog.value) {
        ShowTropheeInfoDialog(dialogMessage.value) {
            showDialog.value = false
        }
    }
}

@Composable
fun ShowTropheeInfoDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Trophée") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
fun getTropheeInfoMessage(tropheeNumber: Int): String {
    return when (tropheeNumber) {
        1 -> "Terminer tous les thèmes au moins 1x"
        2 -> "Atteindre un score parfait dans un thème"
        3 -> "Réussir 3 questions à la suite"
        4 -> "Répondre faussement à 3 questions à la suite"
        5 -> "Répondre à 0 questions lors d'un quiz"
        6 -> "Répondre correctement à 1 questions"
        else -> ""
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(color:Color) = TopAppBar(
    title = { Text("Memorisation", style = MaterialTheme.typography.displayMedium) },
    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = color)
)

@Composable
fun MyBottomBar(navController: NavHostController) = BottomNavigation(backgroundColor = MaterialTheme.colorScheme.primaryContainer ) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    BottomNavigationItem(selected = currentRoute == "home", onClick = {
        navController.navigate("home") {
            launchSingleTop = true
        }
    }, icon = { Icon(Icons.Default.Home, "Maison") })
    BottomNavigationItem(selected = currentRoute == "trophee", onClick = {
        navController.navigate("trophee") { popUpTo("home") }
    }, icon = { Icon(Icons.Default.Star, "trophee") })
    BottomNavigationItem(selected = currentRoute == "stats",
        onClick = { navController.navigate("stats") { popUpTo("home") } },
        icon = { Icon(Icons.Default.BarChart, "statistiques") })
}

@Composable
fun HomeButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            Text(text, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

fun ouvrirActivite(context: Context, activite: Class<*>) {
    val intent = Intent(context, activite)
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MemorisationTheme {
        HomeScreen(24,Color(android.graphics.Color.parseColor("#FFFFFF")))
    }
}