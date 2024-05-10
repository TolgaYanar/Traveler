package com.example.traveler

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.traveler.data.AlarmItem
import com.example.traveler.data.AndroidAlarmSchedular
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, journal : Journal, thatDay : Long, dayNumber : Int,
                  journalPropertiesViewModel: JournalPropertiesViewModel){

    var title by remember {
        mutableStateOf("TITLE")
    }
    var repeat by remember {
        mutableStateOf("Repeat")
    }
    var reminder by remember {
        mutableStateOf("Reminder")
    }
    var notes by remember {
        mutableStateOf("")
    }
    var startTime by remember {
        mutableStateOf("")
    }
    var endTime by remember {
        mutableStateOf("")
    }
    var timePickerExpanded by remember {
        mutableStateOf(false)
    }

    var repeatExpanded by remember {
        mutableStateOf(false)
    }

    var reminderExpanded by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    var permissionGranted by remember {
        mutableStateOf(false)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.POST_NOTIFICATIONS] == true
                && permissions[Manifest.permission.ACCESS_NOTIFICATION_POLICY] == true
                && permissions[Manifest.permission.USE_EXACT_ALARM] == true){
                //I HAVE ACCESS to location
                permissionGranted = true
            } else {
                //Ask for permission
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.USE_EXACT_ALARM
                )

                if(rationaleRequired){
                    Toast.makeText(context, "Notification Permission is required for this feature to work",
                        Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context, "Notification Permission is required. Please enable it in the Android Settings",
                        Toast.LENGTH_LONG).show()
                }
            }
        })

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Add Task", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null,
                            tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.add_journal_color)
                ))
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color(0XFFADD8E6)), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        )
        {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                TextField(value = title, onValueChange = {title = it},
                    placeholder = {
                        Text(text = "Title")
                    },
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(50.dp))
                TextField(value = startTime, onValueChange = {},
                    label = {
                        Text(text = "Starts")
                    }, enabled = false,
                    trailingIcon = {
                        Image(modifier = Modifier.clickable {
                            timePickerExpanded = true
                        },
                            painter = painterResource(id = R.drawable.baseline_access_alarm_24), contentDescription = null)
                    }
                )
                TextField(value = endTime, onValueChange = {},
                    label = {
                        Text(text = "Ends")
                    }, enabled = false,
                    trailingIcon = {
                        Image(modifier = Modifier.clickable {
                            timePickerExpanded = true
                        },
                            painter = painterResource(id = R.drawable.baseline_access_alarm_24), contentDescription = null)
                    }
                )

                if (timePickerExpanded){
                    journalPropertiesViewModel.TimePicker(onDismissRequest = { timePickerExpanded = false },
                        onTimeSelected = { start, end ->
                            startTime = start
                            endTime = end
                            timePickerExpanded = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                TextField(value = repeat, onValueChange = {},
                    trailingIcon = {
                        IconButton(onClick = { repeatExpanded = !repeatExpanded }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_repeat_on_24),
                                contentDescription = null)
                        }
                        journalPropertiesViewModel.DropDownMenu(
                            list = listOf("Once", "Often", "Always"),
                            expanded = repeatExpanded,
                            onDismissRequest = { repeatExpanded = false },
                            onClick = {item->
                                repeat = item.toString()
                                repeatExpanded = false
                            }
                        )
                    })

                Spacer(modifier = Modifier.height(30.dp))

                TextField(value = "$reminder minutes", onValueChange = {},
                    trailingIcon = {
                        IconButton(onClick = { reminderExpanded = !repeatExpanded }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_access_time_24),
                                contentDescription = null)
                        }
                        journalPropertiesViewModel.DropDownMenu(
                            list = listOf("5", "10", "15", "30", "45", "60"),
                            expanded = reminderExpanded,
                            onDismissRequest = { reminderExpanded = false },
                            onClick = {item->
                                reminder = item.toString()
                                reminderExpanded = false
                            }
                        )
                    })

                Spacer(modifier = Modifier.height(50.dp))

                OutlinedTextField(value = notes, onValueChange = {notes = it},
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Gray, focusedContainerColor = Color.Gray),
                    placeholder = {
                        Text(text = "Notes")
                    }, modifier = Modifier.height(200.dp),
                    label = {
                        Text(text = "Notes")
                    }
                )

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp), horizontalArrangement = Arrangement.End) {
                    Button(onClick = {
                        if(title.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty() && repeat.isNotEmpty() && reminder.isNotEmpty() ){

                            requestPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                                    Manifest.permission.POST_NOTIFICATIONS,
                                    Manifest.permission.USE_EXACT_ALARM
                                )
                            )

                            if (permissionGranted) Toast.makeText(context, "Permission Granted!", Toast.LENGTH_SHORT).show()

                            val task = hashMapOf<String, Any>(
                                "title" to title,
                                "startTime" to (journalPropertiesViewModel.timeToLong(startTime) + thatDay),
                                "endTime" to (journalPropertiesViewModel.timeToLong(endTime) + thatDay),
                                "notes" to notes,
                                "remind" to reminder
                            )
                            journalPropertiesViewModel.addTask(context, repeat, reminder = reminder, journal, dayNumber, task = task)
                            navController.currentBackStackEntry?.savedStateHandle?.set("journal", journal)
                            navController.navigate(Screen.TripPlanTodaysPlanScreen.route)

                        }else{
                            Toast.makeText(context, "Please fill the blanks.", Toast.LENGTH_LONG).show()
                        }
                    }, ) {
                        Text(text = "Add")
                    }
                }
            }
        }

    }
}

