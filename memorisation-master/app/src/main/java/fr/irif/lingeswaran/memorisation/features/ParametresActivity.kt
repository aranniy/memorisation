package fr.irif.lingeswaran.memorisation.features

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.irif.lingeswaran.memorisation.MainActivity
import fr.irif.lingeswaran.memorisation.R
import fr.irif.lingeswaran.memorisation.data.TimeConfig
import fr.irif.lingeswaran.memorisation.features.ui.ParametresViewModel
import fr.irif.lingeswaran.memorisation.ouvrirActivite
import fr.irif.lingeswaran.memorisation.theme.MemorisationTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

const val CHANNEL_ID = "MY_MEMO"

class ParametresActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel(this)
        setContent {
            MemorisationTheme {
                val model: ParametresViewModel = viewModel()
                //model.clearDataStore()
                val selectedFond by model.fond.collectAsState(initial = "#FFFFFF")
                val backgroundColor = Color(android.graphics.Color.parseColor(selectedFond))

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    ParametresScreen()
                }
            }
        }
    }
}

fun createChannel(c: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "MY_MEMO"
        val descriptionText = "notification channel for Memorisation Project"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        val notificationManager =
            c.getSystemService(ComponentActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}
fun createNotif(c: Context) {
    val intent1 = Intent(c, MainActivity::class.java)
    val intent2 = Intent(c, ParametresActivity::class.java)
    val pending1 = PendingIntent.getActivity(c, 1, intent1, PendingIntent.FLAG_IMMUTABLE)
    val pending2 = PendingIntent.getActivity(c, 1, intent2, PendingIntent.FLAG_IMMUTABLE)
    val builder = NotificationCompat.Builder(c, CHANNEL_ID).setSmallIcon(R.drawable.small)
        .setContentTitle("Rappel du jour").setContentText("Effectue ton entrainement quotidien !")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)
        .setContentIntent(pending1).setCategory(Notification.CATEGORY_REMINDER)
        .addAction(R.drawable.small, "réglages", pending2)
    val myNotif = builder.build()
    val notificationManager =
        c.getSystemService(ComponentActivity.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(44, myNotif)
}
private fun requestNotificationPermission(c: Context, permissionLauncher: ManagedActivityResultLauncher<String, Boolean>): Boolean {

    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)

    // Les notifications ne sont pas autorisées
    return if (!NotificationManagerCompat.from(c).areNotificationsEnabled()) {
        Log.d("NotificationPermission", "Les notifications ne sont pas autorisées..." +
                "on ne vous forcera pas à les activer, mais sachez qu'elles sont essentielles " +
                "pour garder un bon rythme pour vos entrainements. Sans elles, vous pourriez " +
                "oublier de vous connecter et régresser :( ")
        false

    } else {

        Log.d("NotificationPermission", "Les notifications sont autorisées hihi")
        true
    }
}
@Composable
fun ParametresScreen(model: ParametresViewModel = viewModel()) {

    val switchState by model.switchState

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("permissions","granted")
            } else {
                Log.d("permissions","denied")
            }
        }

    val context = LocalContext.current

    val selectedFond by model.fond.collectAsState(initial = "#FFFFFF")
    val selectedTaille by model.taille.collectAsState(initial = 24)
    val selectedTimer by model.timer.collectAsState(initial = 10)
    val couleur = stringArrayResource(R.array.couleur).asList()
    val valeur = stringArrayResource(R.array.values).asList()
    val taille = stringArrayResource(R.array.taille).asList()
    val timer = stringArrayResource(R.array.timer).asList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Paramètres", fontSize = (selectedTaille).sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        ParametresChoix(
            label = "Fond",
            selectedValue = selectedFond,
            choices = couleur,
            onChange = { selectedValue ->
                val i = couleur.indexOf(selectedValue)
                val s = valeur[i]
                model.changeFond(s)
            }
        )

        ParametresChoix(
            label = "Taille",
            selectedValue = selectedTaille.toString(),
            choices = taille,
            onChange = { selectedValue ->
                model.changeTaille(selectedValue.toInt())
            }
        )

        ParametresChoix(
            label = "Durée",
            selectedValue = selectedTimer.toString(),
            choices = timer,
            onChange = { selectedValue ->
                model.changeTimer(selectedValue.toInt())
            }
        )

        Text(text = "Notifications :")

        Switch(
            checked = switchState,
            onCheckedChange = {
                model.changeSwitchState()
            },
            modifier = Modifier.padding(16.dp)
        )

        if (switchState) {
            Notification(context,model,permissionLauncher)
        }

        FloatingActionButton(onClick = {
            ouvrirActivite(context = context, activite = MainActivity::class.java)
        }) {
            Text("retour")
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notification(context:Context, model : ParametresViewModel, permissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
    if (requestNotificationPermission(context,permissionLauncher)) {

        val prefConfig = runBlocking { model.prefConfig.first() }

        val horaire = rememberTimePickerState(
            initialHour = prefConfig.hour,
            initialMinute = prefConfig.min,
            is24Hour = true
        )

        TimeInput(state = horaire)

        val newConfig = TimeConfig(
            horaire.hour,
            horaire.minute,
        )

        model.save(newConfig)
        model.schedule(newConfig)

    } else {
        Text("Les notifications ne sont malheureusement pas autorisées...")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownBasic(choisi: String, choix: List<String>, onChangeGenre: (String) -> Unit) {
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
                            onChangeGenre(c)
                            expanded = false
                        },
                        text = { Text(text = c) }
                    )
                }
            }
        }
    }
}

@Composable
fun ParametresChoix(
    label: String,
    selectedValue: String,
    choices: List<String>,
    onChange: (String) -> Unit
) {
    DropdownBasic(
        choisi = selectedValue,
        choix = choices,
        onChangeGenre = {
            onChange(it)
        }
    )

    Text(text = "$label sélectionné : $selectedValue")

    Spacer(modifier = Modifier.height(16.dp))
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    MemorisationTheme {
        ParametresScreen()
    }
}