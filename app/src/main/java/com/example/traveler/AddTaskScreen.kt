package com.example.traveler

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun AddTaskScreen(navController: NavController, journal : Journal, thatDay : Long, dayNumber : Int){

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Add Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
                    }
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
                    TimePicker(onDismissRequest = { timePickerExpanded = false },
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
                        Image(painter = painterResource(id = R.drawable.baseline_repeat_on_24), contentDescription = null,
                            modifier = Modifier.clickable {
                                repeatExpanded = !repeatExpanded
                            })
                    })
                DropDownMenu(
                    list = listOf("Once", "Often", "Always"),
                    expanded = repeatExpanded,
                    onDismissRequest = { repeatExpanded = false },
                    onClick = {item->
                        repeat = item.toString()
                        repeatExpanded = false
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                TextField(value = "$reminder minutes", onValueChange = {},
                    trailingIcon = {
                        Image(painter = painterResource(id = R.drawable.baseline_access_time_24), contentDescription = null,
                            modifier = Modifier.clickable {
                                reminderExpanded = !repeatExpanded
                            })
                    })
                DropDownMenu(
                    list = listOf("5", "10", "15", "30"),
                    expanded = reminderExpanded,
                    onDismissRequest = { reminderExpanded = false },
                    onClick = {item->
                        reminder = item.toString()
                        reminderExpanded = false
                    }
                )
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

                            val task = hashMapOf<String, Any>(
                                "title" to title,
                                "startTime" to (timeToLong(startTime) + thatDay),
                                "endTime" to (timeToLong(endTime) + thatDay),
                                "notes" to notes,
                                "remind" to reminder
                            )
                            addTask(context, repeat, reminder = reminder, journal, dayNumber, task = task)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(onDismissRequest: () -> Unit,
               onTimeSelected: (String, String) -> Unit)
{

    val startTimeDialog = rememberTimePickerState()
    val endTimeDialog = rememberTimePickerState()

    var startHour by remember {
        mutableStateOf(startTimeDialog.hour)
    }
    var startMin by remember {
        mutableStateOf(startTimeDialog.minute)
    }
    var endHour by remember {
        mutableStateOf(endTimeDialog.hour)
    }
    var endMin by remember {
        mutableStateOf(endTimeDialog.minute)
    }

    var startTime by remember {
        mutableStateOf("")
    }
    var endTime by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { onDismissRequest() })
    {
        Surface(
            shape = MaterialTheme.shapes.medium,
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ){
                    TimeInput(state = startTimeDialog,
                        modifier = Modifier.fillMaxSize())
                }

                Text(text = "Starting Time : ${startTimeDialog.hour}:${startTimeDialog.minute}")

                Spacer(modifier = Modifier.height(25.dp))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ){
                    TimeInput(state = endTimeDialog,
                        modifier = Modifier.fillMaxSize())
                }

                Text(text = "Ending Time : ${endTimeDialog.hour}:${endTimeDialog.minute}")

                Spacer(modifier = Modifier.height(25.dp))

                Button(onClick = {
                    startHour = startTimeDialog.hour
                    startMin = startTimeDialog.minute
                    endHour = endTimeDialog.hour
                    endMin = endTimeDialog.minute
                    startTime = "${startHour}:$startMin"
                    endTime = "${endHour}:$endMin"
                    if(startTime != ":" || endTime != ":"){
                        onTimeSelected(startTime, endTime)
                        onDismissRequest()
                    }
                     },
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Text(text = "Select")
                }
            }
        }
    }
}

fun longToTime(longDate: Long): String {
    val date = Date(longDate)
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return date.let { dateFormat.format(it) }
}

fun timeToLong(timeString: String): Long {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = dateFormat.parse(timeString)
    return (date?.time ) ?: 0L
}

@Composable
fun DropDownMenu(list : List<Any>, expanded : Boolean,
                 onDismissRequest: () -> Unit, onClick: (Any) -> Unit){

    DropdownMenu(expanded = expanded, onDismissRequest = { onDismissRequest() }) {

        list.forEach {item->
            DropdownMenuItem(onClick = { onClick(item) }) {
                Text(text = item.toString(), fontSize = 16.sp, modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center)
            }
        }
    }
}

fun addTask(context: Context, repeat : String, reminder : String, journal : Journal, dayNum : Int, task : HashMap<String, Any>){

    val daysCollection = Injection.instance().collection("users").document(FirebaseAuth.getInstance().uid!!)
        .collection("journals").document(journal.title).collection("days")

    val journalDuration = getDayDifference(journal.startDateInMillis, journal.endDateInMillis)

    val schedular = AndroidAlarmSchedular(context)

    if(repeat.equals("Always")){
        for (day in 0 until journalDuration){
            daysCollection.document("day${day + 1}").set({})
            daysCollection.document("day${day+1}").collection("tasks")
                .document(task["title"].toString()).set(task)
                .addOnSuccessListener {
                    println("task added for day${day+1}")
                }
                .addOnFailureListener {
                    println("task couldn't add for day${day+1}")
                }
        }
    }
    else if(repeat.equals("Often")){
        for (day in 0 until journalDuration step 2){
            daysCollection.document("day${day + 1}").set({})
            daysCollection.document("day${day+1}").collection("tasks")
                .document(task["title"].toString()).set(task)
                .addOnSuccessListener {
                    println("task added for day${day+1}")
                }
                .addOnFailureListener {
                    println("task couldn't add for day${day+1}")
                }
        }
    }
    else{
        addAlarm(schedular, (task["startTime"].toString().toLong()-reminder.toLong()*60*1000),
            "${task["title"]} at ${longToTime(task["startTime"].toString().toLong())}", task)

        daysCollection.document("day${dayNum}").set({})
        daysCollection.document("day${dayNum}").collection("tasks")
            .document(task["title"].toString()).set(task)
            .addOnSuccessListener {
                println("task added for day${dayNum}")
            }
            .addOnFailureListener {
                println("task couldn't add for day${dayNum}")
            }
    }
}

fun addAlarm(schedular : AndroidAlarmSchedular,
             time: Long, message : String,
             task : HashMap<String, Any>)
{
    val alarmItem = AlarmItem(
        time = time - 3 * 60 * 60 * 1000,
        message = message,
        id = Random.nextInt(10000,99999).toString()
    )
    task.put("notificationID", alarmItem.id)
    task.put("alarmItemHashCode", alarmItem.hashCode())
    alarmItem.let(schedular::schedule)

}

