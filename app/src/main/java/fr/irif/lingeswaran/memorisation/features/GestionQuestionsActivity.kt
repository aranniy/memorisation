package fr.irif.lingeswaran.memorisation.features

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import fr.irif.lingeswaran.memorisation.data.Questions
import fr.irif.lingeswaran.memorisation.features.ui.GestionQuestionsViewModel
import fr.irif.lingeswaran.memorisation.features.ui.ParametresViewModel
import fr.irif.lingeswaran.memorisation.theme.MemorisationTheme

class GestionQuestionsActivity : ComponentActivity() {
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
                    GestionQuestions(context = context)
                }
            }
        }
    }
}

@Composable
fun GestionQuestions(model : GestionQuestionsViewModel = viewModel(), context : Context) {

    val intent = (context as Activity).intent
    val idTheme = intent.getIntExtra("idTheme", model.idTheme.value)
    if (idTheme != -1) model.updateId(idTheme)

    var menu by model.menu

    val modifyQuestion by model.modifyQuestionAlert

    if(modifyQuestion) ModifyQuestionDialog()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Surface(modifier = Modifier.fillMaxWidth()) {

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                Box(modifier = Modifier.weight(1f)) {
                    IconButton(onClick = { menu = !menu }) {
                        Icon(Icons.Default.MoreVert, "")
                    }
                    DropdownMenu(
                        expanded = menu,
                        onDismissRequest = { menu = false }) {
                        DropdownMenuItem(onClick = {
                            val intent = Intent(context, AjoutActivity::class.java)
                            intent.putExtra("idTheme", model.idTheme.value)
                            intent.putExtra("QCM", false)
                            context.startActivity(intent)
                        }) {
                            Text(text = "Ajouter une question")
                        }
                        DropdownMenuItem(onClick = {
                            val intent = Intent(context, AjoutActivity::class.java)
                            intent.putExtra("idTheme", model.idTheme.value)
                            intent.putExtra("QCM", true)
                            context.startActivity(intent)
                        }) {
                            Text(text = "Ajouter un QCM")
                        }
                    }
                }


            }

        }

        val questions by model.questions.value!!.collectAsState(listOf())

        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            itemsIndexed(questions) {
                    index, question -> QuestionItem(index, question)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                returnThemes(context)
            }) {
                Text("Retour")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GestionQuestionsPreview() {
    MemorisationTheme {
        GestionQuestions(context = LocalContext.current)
    }
}

@Composable
fun QuestionItem(index : Int, question : Questions, model : GestionQuestionsViewModel = viewModel()) {

    var deleteQuestion by model.deleteQuestionAlert
    var selected by model.selected

    if(deleteQuestion) DeleteQuestionDialog(question = selected!!)

    Card(
        Modifier
            .fillMaxSize()
            .clickable {
                model.changeSelected(question)
            }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { deleteQuestion = !deleteQuestion
                                   selected = question}) {
                Icon(Icons.Default.Clear, "")
            }
            Text(question.question, fontSize = 25.sp, modifier = Modifier.weight(1f))
        }
    }


}
@Composable
fun ModifyQuestionDialog(model : GestionQuestionsViewModel = viewModel()) {

    val question by model.selected

    var modifyQuestion by model.modifyQuestionAlert

    AlertDialog(
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Question : ${question!!.question}")
                Text("Réponse : ${question!!.response}")
                TextField(value = model.status.value,
                    onValueChange = { model.changeStatus(it) },
                    placeholder = { Text("Status" )})
            }
        },
        onDismissRequest =  { modifyQuestion = false } ,
        confirmButton = { Button(onClick = {
            if (model.status.value != "") {
                if(model.status.value.toIntOrNull() != null) {
                    model.modifyQuestion(question!!.idQuestion, model.status.value.toInt())
                    modifyQuestion = false
                }
            }
        }  ) { Text("Modifier") }
        },
        dismissButton = { Button(onClick = { modifyQuestion = false } ) { Text("Annuler") } }
    )
}

@Composable
fun DeleteQuestionDialog(model : GestionQuestionsViewModel = viewModel(), question : Questions ) {

    var delete by model.deleteQuestionAlert

    AlertDialog(
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Souhaitez-vous supprimer la question : ${question.question} ?")
            }
        },
        onDismissRequest =  { delete = false } ,
        confirmButton = { Button(onClick = {
            model.deleteQuestion(question.idQuestion)
            delete = false
        }  ) { Text("Oui") }
        },
        dismissButton = { Button(onClick = { delete = false } ) { Text("Non") } }
    )
}

fun returnThemes(context : Context) {
    val activity = context as Activity
    val intent = Intent()
    activity.setResult(Activity.RESULT_OK, intent)
    activity.finish()
}