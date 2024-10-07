package fr.irif.lingeswaran.memorisation.features

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.irif.lingeswaran.memorisation.data.Questions
import fr.irif.lingeswaran.memorisation.features.ui.ParametresViewModel
import fr.irif.lingeswaran.memorisation.features.ui.QuizViewModel
import fr.irif.lingeswaran.memorisation.theme.MemorisationTheme
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.LaunchedEffect
import fr.irif.lingeswaran.memorisation.MainActivity
import fr.irif.lingeswaran.memorisation.features.ui.MainViewModel
import fr.irif.lingeswaran.memorisation.ouvrirActivite
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import kotlin.math.roundToInt

class QuizActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemorisationTheme {
                val model: ParametresViewModel = viewModel()
                val selectedFond by model.fond.collectAsState(initial = "#FFFFFF")
                val selectedTaille by model.taille.collectAsState(initial = 24)
                val selectedTimer = runBlocking { model.timer.first() }
                val backgroundColor = Color(android.graphics.Color.parseColor(selectedFond))

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    QuizScreen(selectedTaille,selectedTimer)
                }
            }
        }
    }
}

@Composable
fun QuizScreen(taille: Int, timerValue: Int, model: QuizViewModel = viewModel()) {

    val ready by model.ready
    val start by model.start
    val updated by model.updated

    val context = LocalContext.current
    val iii = (context as Activity).intent
    val id = iii.getIntExtra("theme", 1)

    val timeLeft by model.timeLeft
    val questionsList by model.questions.value!!.collectAsState(initial = listOf())
    val currentQuestionIndex by model.currentQuestionIndex
    val currentQuestion by model.currentQuestion
    val afficherReponse by model.afficherReponse
    val reponse by model.reponse

    val scoreDiff by model.score

    val modelTrophee: MainViewModel = viewModel()

    val scoreEnCours by model.scoreEnCours

    if (currentQuestionIndex == 0) model.setScoreJeux(id)

    model.changeTheme(id)

    if(ready) {

        if(!start) {
            if (questionsList.isNotEmpty()) {
                model.changeCurrentQuestionIndex(questionsList.indexOf(currentQuestion))
                if (currentQuestionIndex == -1) model.changeCurrentQuestionIndex(0)
                model.changeStart()
            }
        }

        if(start) {

            // Pour afficher le timer en haut à droite de l'écran en direct
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(text = "$timeLeft")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Quiz", fontSize = taille.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                if (questionsList.isNotEmpty()) {

                    if (currentQuestionIndex < questionsList.size) {

                        // Pour les trophées
                        if (scoreDiff > 0) modelTrophee.validateTrophee(6)
                        if (scoreDiff > 2) modelTrophee.validateTrophee(3)
                        if (scoreDiff < -2) modelTrophee.validateTrophee(4)

                        fun startTimer(initialTime: Int) {
                            val timer = object : CountDownTimer(initialTime * 1000L, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    val remainingSeconds = (millisUntilFinished / 1000).toInt()
                                    model.setTimeLeft(remainingSeconds)
                                }

                                override fun onFinish() {
                                    model.clearField()
                                    model.decrementerScore()
                                    nextQuestion(currentQuestionIndex) { model.incrementerCurrentQuestion() }
                                    if(currentQuestionIndex < questionsList.size) model.updateCurrentQuestion(id, questionsList[currentQuestionIndex].idQuestion)
                                    else model.updateCurrentQuestion(id, questionsList[0].idQuestion)
                                }
                            }
                            timer.start()
                        }

                        LaunchedEffect(currentQuestionIndex) {
                            model.setTimeLeft(timerValue)
                            if (currentQuestionIndex < questionsList.size) startTimer(timeLeft)
                        }

                        /* affiche la question actuelle */
                        QuizQuestion(questionsList, currentQuestionIndex, taille)

                        /* affiche la réponse (initialement cachée) */
                        if (afficherReponse) {
                            QuizAnswer(questionsList, currentQuestionIndex, taille)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if(!questionsList[currentQuestionIndex].qcm)
                        QuizOutlinedTextField(
                            reponse,
                            onChange = { model.changeReponse(it) },
                            "Réponse : "
                        )

                        else {

                            model.updateChoix(questionsList[currentQuestionIndex].idQuestion)

                            if(updated) {

                                val listeChoix by model.choix.value!!.collectAsState(initial = listOf())

                                LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 100.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalArrangement = Arrangement.Center) {
                                    items(listeChoix.size) { index ->
                                        ChoixItem(
                                            choix = listeChoix[index],
                                            currentQuestionIndex = currentQuestionIndex,
                                            questionsList = questionsList,
                                            id = id
                                        )
                                    }
                                }

                            }

                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        QuizButtons(
                            afficherReponse,
                            onNextQuestionClick = {
                                nextQuestion(currentQuestionIndex) {
                                    model.incrementerCurrentQuestion()
                                }
                                if(currentQuestionIndex < questionsList.size) model.updateCurrentQuestion(id, questionsList[currentQuestionIndex].idQuestion)
                                else model.updateCurrentQuestion(id, questionsList[0].idQuestion)
                                model.clearField()
                            },
                            onAnswerClick = {
                                val userAnswer = reponse.lowercase()
                                val correctAnswer =
                                    questionsList[currentQuestionIndex].response.lowercase()
                                if (userAnswer == correctAnswer) {
                                    model.incrementerScore()
                                    model.incrementerScoreJeux(id)
                                    showToast(context, "Bravo!")
                                } else {
                                    model.decrementerScore()
                                    showToast(context, "Faux!")
                                }
                                model.clearField()
                                nextQuestion(currentQuestionIndex) { model.incrementerCurrentQuestion() }
                                if(currentQuestionIndex < questionsList.size) model.updateCurrentQuestion(id, questionsList[currentQuestionIndex].idQuestion)
                                else model.updateCurrentQuestion(id, questionsList[0].idQuestion)
                            },
                            onSwitchClick = { model.changeSelected() },
                            question = questionsList[currentQuestionIndex]
                        )

                    } else {

                        model.getScoreEnCours(id)
                        if (scoreEnCours == 0) modelTrophee.validateTrophee(5) // trophee
                        val resultat = scoreEnCours / questionsList.size
                        if (resultat == 1) modelTrophee.validateTrophee(2)
                        Text(
                            text = "Vous avez terminé le Quiz ! Votre score est de : " + "$scoreEnCours/ ${questionsList.size} ",
                            fontSize = taille.sp
                        )
                        QuizButton(text = "ajouter aux statistiques") {
                            afficheStatistique(
                                model = model,
                                scoreEnCours = scoreEnCours,
                                questionsList = questionsList,
                                id = id,
                                context = context
                            )
                        }
                    }
                }
            }
        }
    }
}

fun afficheStatistique(model: QuizViewModel, scoreEnCours:Int, questionsList: List<Questions>, id: Int,context:Context) {
    val scoreArrondi = ((scoreEnCours.toDouble() / questionsList.size) * 100).roundToInt()
    model.insertStatistique(id,scoreArrondi)
    ouvrirActivite(context = context, activite = MainActivity::class.java)
}

/* affiche la question */
@Composable
fun QuizQuestion(questionsList: List<Questions>, currentQuestionIndex: Int, taille : Int) {
    Text(text = "Question: ${questionsList[currentQuestionIndex].question}", fontSize = taille.sp)
}

/* affiche la réponse */
@Composable
fun QuizAnswer(questionsList: List<Questions>, currentQuestionIndex: Int, taille: Int) {
    Text(text = "Réponse: ${questionsList[currentQuestionIndex].response}", fontSize = taille.sp)
}

/* affiche tous les boutons */
@Composable
fun QuizButtons(
    switchSelected: Boolean,
    onNextQuestionClick: () -> Unit,
    onAnswerClick: () -> Unit,
    onSwitchClick: () -> Unit,
    question : Questions
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if(!question.qcm) QuizButton("valider", onAnswerClick)
        QuizSwitch(switchSelected, "voir réponse", onSwitchClick)
    }
    QuizButton("Question Suivante", onNextQuestionClick)
}

/* permet d'afficher la question suivante */
fun nextQuestion(currentQuestionIndex:Int, onChange: (Int) -> Unit) {
    onChange(currentQuestionIndex)
}

/* permet d'afficher un Button */
@Composable
fun QuizButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick
    ) {
        Text(text = text)
    }
}

/* permet d'afficher un switch */
@Composable
fun QuizSwitch(isSwitchOn: Boolean, text: String, onSwitchClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Switch(
            checked = isSwitchOn,
            onCheckedChange = { onSwitchClick() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Green,
                uncheckedThumbColor = Color.Red
            )
        )
        Text(text = text)
    }
}

/* permet d'afficher un OutlinedTextField */
@Composable
fun QuizOutlinedTextField(
    value: String,
    onChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it) },
        label = { Text(label) }
    )
}

/* permet d'afficher un Toast */
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    MemorisationTheme {
        QuizScreen(24,timerValue=15)
    }
}

@Composable
private fun ChoixItem(model : QuizViewModel = viewModel(), choix : String, currentQuestionIndex: Int, questionsList: List<Questions>, id : Int) {

    Card(
        Modifier
            .fillMaxSize()
            .clickable {
                val userAnswer = choix.lowercase()
                val correctAnswer = questionsList[currentQuestionIndex].response.lowercase()
                if (userAnswer == correctAnswer) {
                    model.incrementerScore()
                    model.incrementerScoreJeux(id)
                } else {
                    model.decrementerScore()
                }
                nextQuestion(currentQuestionIndex) { model.incrementerCurrentQuestion() }
                if(currentQuestionIndex + 1 < questionsList.size) model.updateCurrentQuestion(id, questionsList[currentQuestionIndex + 1].idQuestion)
                else model.updateCurrentQuestion(id, questionsList[0].idQuestion)
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(choix, fontSize = 25.sp, modifier = Modifier.weight(1f))
        }
    }

}
