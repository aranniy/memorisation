package fr.irif.lingeswaran.memorisation.features

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.irif.lingeswaran.memorisation.data.JeuxQuestions
import fr.irif.lingeswaran.memorisation.features.ui.GestionThemesViewModel
import fr.irif.lingeswaran.memorisation.features.ui.ParametresViewModel
import fr.irif.lingeswaran.memorisation.theme.MemorisationTheme

class GestionThemesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemorisationTheme {
                val model: ParametresViewModel = viewModel()
                val selectedFond by model.fond.collectAsState(initial = "#FFFFFF")
                val backgroundColor = Color(android.graphics.Color.parseColor(selectedFond))

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    val context = LocalContext.current
                    GestionThemes(context = context)
                }
            }
        }
    }
}

@Composable
fun GestionThemes(model : GestionThemesViewModel = viewModel(), context : Context) {

    val addTheme by model.addThemeAlert
    val modifyTheme by model.modifyThemeAlert

    if(addTheme) AddThemeDialog()

    if(modifyTheme.first) ModifyThemeDialog()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        SearchBar()

        val themes by model.themes.value.collectAsState(listOf())

        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            itemsIndexed(themes) {
                    index, theme -> ThemeItem(index = index, theme = theme, context = context)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GestionThemesPreview() {
    MemorisationTheme {
        GestionThemes(context = LocalContext.current)
    }
}

@Composable
fun SearchBar(model : GestionThemesViewModel = viewModel()) {

    var menu by model.menu

    var addTheme by model.addThemeAlert

    Surface(modifier = Modifier.fillMaxWidth()) {

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            Box(modifier = Modifier.weight(1f)){
                IconButton(onClick = { menu = !menu }) {
                    Icon(Icons.Default.MoreVert, "")
                }
                DropdownMenu(
                    expanded = menu,
                    onDismissRequest = { menu = false }) {
                        DropdownMenuItem(onClick = { addTheme = !addTheme }) {
                            Text(text = "Ajouter un jeu de questions")
                        }
                        DropdownMenuItem(onClick = { /* TODO */ }) {
                            Text(text = "Télécharger un jeu de questions")
                        }
                    }
            }

            TextField(value = model.saisie.value,
                      onValueChange = { model.onSaisie(it) },
                      placeholder = {Text("Recherche")})

        }

    }
}

@Composable
fun ThemeItem(index : Int, theme : JeuxQuestions, model : GestionThemesViewModel = viewModel(), context : Context) {

    var delete by model.deleteThemeAlert
    var selected by model.selected

    if(delete) DeleteThemeDialog(theme = selected!!)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Card(
            Modifier
                .fillMaxSize()
                .clickable {
                    val intent = Intent(context, GestionQuestionsActivity::class.java)
                    intent.putExtra("idTheme", theme.id)
                    context.startActivity(intent)
                },
            colors = CardDefaults.cardColors()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { delete = !delete
                                           selected = theme}) {
                        Icon(Icons.Default.Clear, "")
                    }
                    Text(text = theme.nom, fontSize = 25.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = { model.modifyThemeAlert.value = Pair(true, theme) }) {
                        Icon(Icons.Default.Settings, "")
                    }
                }
            }
        }
    }
}
@Composable
fun AddThemeDialog(model : GestionThemesViewModel = viewModel()) {

    var addTheme by model.addThemeAlert

    AlertDialog(
        text = { TextField(model.name.value, { model.changeName(it) }, placeholder = { Text("Nom du jeu de questions") }) },
        onDismissRequest =  { addTheme = false } ,
        confirmButton = { Button(onClick = { model.addTheme()
            addTheme = false} ) { Text("Ajouter") }
        },
        dismissButton = { Button(onClick = { addTheme = false } ) { Text("Annuler") } }
    )
}

@Composable
fun ModifyThemeDialog(model : GestionThemesViewModel = viewModel()) {

    var modifyTheme by model.modifyThemeAlert

    AlertDialog(
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Jeu de question : ${modifyTheme.second!!.nom}")
                TextField(value = model.name.value,
                    onValueChange = { model.changeName(it) },
                    placeholder = { Text("Nouveau nom") })
            }
        },
        onDismissRequest =  { modifyTheme = Pair(false, null) } ,
        confirmButton = { Button(onClick = {
            if (model.name.value != "") {
                model.modifyTheme(modifyTheme.second!!.id)
                modifyTheme = Pair(false, null)
            }
        }  ) { Text("Modifier") }
        },
        dismissButton = { Button(onClick = { modifyTheme = Pair(false, null) } ) { Text("Annuler") } }
    )
}

@Composable
fun DeleteThemeDialog(model : GestionThemesViewModel = viewModel(), theme : JeuxQuestions) {

    var delete by model.deleteThemeAlert

    if(delete) {
        AlertDialog(
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Souhaitez-vous supprimer le jeu de question : ${theme.nom} ?")
                }
            },
            onDismissRequest =  { delete = false } ,
            confirmButton = { Button(onClick = {
                model.deleteTheme(theme.id)
                delete = false
            }  ) { Text("Oui") }
            },
            dismissButton = { Button(onClick = { delete = false } ) { Text("Non") } }
        )
    }
}