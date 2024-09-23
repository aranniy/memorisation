package fr.irif.lingeswaran.memorisation.features

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.irif.lingeswaran.memorisation.data.JeuxQuestions
import fr.irif.lingeswaran.memorisation.features.ui.ChoixViewModel
import fr.irif.lingeswaran.memorisation.features.ui.ParametresViewModel
import fr.irif.lingeswaran.memorisation.theme.MemorisationTheme

class ChoixActivity : ComponentActivity() {
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
                    ChoixScreen(taille = selectedTaille)
                }
            }
        }
    }
}

@Composable
fun ChoixScreen(model: ChoixViewModel = viewModel(), taille : Int) {

    val jeuxQuestionsListFlow by model.listjeuxquestions.collectAsState(initial = listOf())
    val selected by model.selected
    val context = LocalContext.current

    Column {

        Text(text = "Choix du thème de l'apprentissage", fontSize = taille.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        DropdownBasic(
            choisi = selected?.nom ?: "",
            choix = jeuxQuestionsListFlow,
            onChangeNom = { model.changeSelected(it) }
        )

        Button(
            onClick = {
                if (selected != null ) {
                    val intent = Intent(context, QuizActivity::class.java)
                    intent.putExtra("theme",selected!!.id)
                    context.startActivity(intent)
                }
            }
        ) {
            Text(text = "valider")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownBasic(choisi: String, choix: List<JeuxQuestions>, onChangeNom: (JeuxQuestions) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = choisi,
                onValueChange = { },
                label = { Text("Choix") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                choix.forEach { c ->
                    DropdownMenuItem(
                        onClick = {
                            onChangeNom(c)
                            expanded = false
                        },
                        text = { Text(text = c.nom) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MemorisationTheme {
        ChoixScreen(taille = 14)
    }
}