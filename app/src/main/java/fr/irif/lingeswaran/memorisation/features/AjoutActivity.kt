package fr.irif.lingeswaran.memorisation.features

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.irif.lingeswaran.memorisation.features.ui.AjoutViewModel
import fr.irif.lingeswaran.memorisation.features.ui.ParametresViewModel
import fr.irif.lingeswaran.memorisation.theme.MemorisationTheme

class AjoutActivity : ComponentActivity() {
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
                    val context = LocalContext.current
                    AjoutScreen(context = context, taille = selectedTaille)
                }
            }
        }
    }
}

@Composable
fun AjoutScreen(model: AjoutViewModel = viewModel(), context : Context, taille : Int) {

    val intent = (context as Activity).intent
    val idTheme = intent.getIntExtra("idTheme", model.theme.value)
    val isQCM = intent.getBooleanExtra("QCM", false)

    var ajout by model.ajout

    val listeChoix = model.listeChoix.toList().toMutableList()
    val nbrChoix by model.nbrChoix

    if (idTheme != -1) model.theme.value = idTheme

    if(ajout) Ajout(context = context, isQCM = isQCM)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {

        Entrees(taille = taille)

        if(isQCM) Choices()

        Row(verticalAlignment = Alignment.CenterVertically) {

            Button({ returnQuestions(context) }) { Text("Quitter") }

            Button(onClick = {
                if(model.question.value == "") {
                    /* TODO */
                } else if(isQCM) {
                    if(checkReponse(listeChoix, model.reponse.value) && checkDoublon(listeChoix, nbrChoix)) {
                        model.alertAjout()
                    }
                    if(!checkReponse(listeChoix, model.reponse.value)) {
                        /* TODO */
                    }
                    if(!checkDoublon(listeChoix, nbrChoix)) {
                        /* TODO */
                    }
                } else model.alertAjout()
            }) { Text("Ajouter") }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun AjoutScreenPreview() {
    MemorisationTheme {
        AjoutScreen(context = LocalContext.current, taille = 24)
    }
}
@Composable
private fun Entrees(taille : Int, model: AjoutViewModel = viewModel()) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = model.question.value,
                onValueChange = { model.changeQuestion(it) },
                placeholder = { Text(text = "Question", fontSize = taille.sp) }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = model.reponse.value,
                onValueChange = { model.changeReponse(it) },
                placeholder = { Text("Réponse", fontSize = taille.sp) }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = model.status.value,
                onValueChange = { model.changeStatus(it) },
                placeholder = { Text("Status", fontSize = taille.sp) }
            )
        }

    }

}

@Composable
fun Ajout(model: AjoutViewModel = viewModel(), context : Context, isQCM : Boolean) {


    Log.d("QCM", isQCM.toString())

    AlertDialog(
        text = { Text("Question : ${ model.question.value }\n" +
                "Réponse : ${ model.reponse.value }\n") },
        onDismissRequest =  { model.alertAjout() } ,
        confirmButton = { Button(onClick = {
            model.alertAjout()
            if(isQCM) model.addQCM()
            else model.addQuestion(false)
            returnQuestions(context)
        } ) { Text("Valider") }},
        dismissButton = { Button(onClick = { model.alertAjout() } ) { Text("Annuler") }}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Choices(model : AjoutViewModel = viewModel()) {

    var nbrChoix by model.nbrChoix
    var expanded by model.expanded

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = nbrChoix.toString(),
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
                model.nbrChoixOptions.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            nbrChoix = option
                            expanded = false
                        },
                        text = { Text(text = option.toString()) }
                    )
                }
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp)
    ) {
        items(nbrChoix) { choix ->
            ChoixItem(choix = choix)
        }
    }

}

@Composable
private fun ChoixItem(model : AjoutViewModel = viewModel(), choix : Int) {

    var listeChoix = model.listeChoix.toList().toMutableList()

    OutlinedTextField(
        value = listeChoix[choix],
        onValueChange = { listeChoix[choix] = model.onChangeChoix(it, choix) },
        placeholder = { Text("Choix n°${choix + 1}") }
    )
}

fun returnQuestions(context : Context) {
    val activity = context as Activity
    val intent = Intent()
    activity.setResult(Activity.RESULT_OK, intent)
    activity.finish()
}

private fun checkReponse(listeChoix : List<String>, reponse : String) : Boolean {
    for(i in listeChoix.indices) {
        if(listeChoix[i] == reponse) return true
    }
    return false
}

private fun checkDoublon(listeChoix : List<String>, nbrChoix : Int) : Boolean {
    for(i in 0 until nbrChoix) {
        for (j in (i + 1) until nbrChoix) {
            if (listeChoix[i] == listeChoix[j]) return false
        }
    }
    return true
}