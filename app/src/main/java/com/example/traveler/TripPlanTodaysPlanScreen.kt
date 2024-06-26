package com.example.traveler

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traveler.data.AndroidAlarmSchedular
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.example.traveler.data.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalTime
import java.util.Calendar
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlanTodaysPlanScreen(navController: NavController, journal: Journal){


    val journalDuration = getDayDifference(
        startDate = journal.startDateInMillis,
        endDate = journal.endDateInMillis
    )

    var selected by remember {
        mutableStateOf(-1)
    }

    var selectedDay by remember { mutableStateOf<String?>(null) }

    var selectedTasks by remember {
        mutableStateOf(mutableStateListOf<Task>())
    }

    val isLoading : MutableState<Boolean> = remember { mutableStateOf(false) }

    var isEdit : MutableState<Boolean> = remember { mutableStateOf(false) }

    val context = LocalContext.current
    

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Trip Plan") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.UserProfileScreen.route) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White),
                actions = {
                    IconButton(onClick = { isEdit.value = !isEdit.value }) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = null)
                    }
                }
                )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), verticalArrangement = Arrangement.Top
        )
        {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Card(
                        modifier = Modifier
                            .height(45.dp)
                            .width(160.dp),
                        backgroundColor = Color(0xFF728FF3),
                        shape = RoundedCornerShape(25.dp),
                    ) {
                        Text(text = "Today's Plan", textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 10.dp), fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Card(
                        modifier = Modifier
                            .height(45.dp)
                            .width(160.dp)
                            .clickable {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "journal",
                                    journal
                                )
                                navController.navigate(Screen.TripPlanJournalScreen.route)
                            },
                        backgroundColor = Color.Gray.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(text = "Journal", textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 10.dp), fontSize = 16.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier
                            .height(30.dp)
                            .width(340.dp),
                        backgroundColor = Color.Gray.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(25.dp)
                    ) {

                        LazyRow(modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically){

                            items(journalDuration){

                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (it == selected) Color(0xFF728FF3)
                                            else Color(0xFFA5B7F7), shape = RoundedCornerShape(6.dp)
                                        )
                                        .height(25.dp)
                                        .width(60.dp)
                                        .clickable {
                                            selected = it
                                            selectedDay = "day${selected + 1}"
                                            selectedTasks = SnapshotStateList()
                                            loadTasksOfDay(journal, selectedDay!!, selectedTasks)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "Day ${it + 1}")
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .height(575.dp)
                        .padding(start = 20.dp)
                        .padding(vertical = 20.dp), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top
                ) {

                    longToDate(Calendar.getInstance().time.time)?.let {
                            it1 -> Text(text = it1, fontSize = 17.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.alpha(0.8f))}

                    Divider(modifier = Modifier.width(325.dp))

                    Spacer(modifier = Modifier.padding(15.dp))

                    if(isLoading.value == true){
                        CircularProgressIndicator()
                    }else{

                        if(selectedDay != null){

                            if (selectedTasks.isNotEmpty()){

                                LazyColumn(modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxSize(),
                                    horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top){
                                    var index = 0
                                    items(selectedTasks){

                                        Card(modifier = Modifier
                                            .height(120.dp)
                                            .width(360.dp)
                                        )
                                        {
                                            Row {
                                                Box(modifier = Modifier
                                                    .background(Color.Transparent)
                                                    .padding(horizontal = 10.dp)
                                                    .padding(top = 10.dp)) {
                                                    Text(text = longToTime(it.startTime), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Column(modifier = Modifier
                                                    .padding(horizontal = 10.dp)
                                                    .padding(top = 10.dp)) {

                                                    Canvas(modifier = Modifier
                                                        .padding(top = 10.dp)
                                                        .padding(horizontal = 10.dp),
                                                        onDraw = {
                                                            drawCircle(Color.Black, radius = 30f)
                                                        })
                                                    Divider(modifier = Modifier
                                                        .padding(start = 8.dp)
                                                        .width(3.dp)
                                                        .height(120.dp)
                                                        .padding(top = 11.dp))
                                                }
                                                Column(modifier  = Modifier
                                                    .padding(10.dp)
                                                    .width(150.dp)) {
                                                    Text(text = it.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Text(text = it.notes, color = Color(0xFF5581F1))
                                                }

                                                if(isEdit.value){
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.End
                                                    ) {
                                                        Box(
                                                            contentAlignment = Alignment.Center,
                                                            modifier = Modifier.fillMaxHeight().width(40.dp).background(Color.Gray)
                                                                .clickable {

                                                                }
                                                        ) {
                                                            Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                                                        }
                                                        Box(
                                                            contentAlignment = Alignment.Center,
                                                            modifier = Modifier.fillMaxHeight().width(40.dp).background(Color.Red)
                                                                .clickable {
                                                                    deleteTask(selectedDay!!, navController, it, journal, context)
                                                                }
                                                        ) {
                                                            Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        index++
                                    }
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Bottom
                ) {

                    Button(onClick = {
                        if(selected != -1){
                            navController.currentBackStackEntry?.savedStateHandle?.set("thatday", (journal.startDateInMillis + (selected*24*60*60*1000)))
                            navController.currentBackStackEntry?.savedStateHandle?.set("daynumber", selected+1)
                            navController.currentBackStackEntry?.savedStateHandle?.set("journal", journal)
                            navController.navigate(Screen.AddTaskScreen.route)
                        }else{
                            Toast.makeText(context, "You need to choose a day to continue...", Toast.LENGTH_LONG).show()
                        }
                    } ) {
                        Text(text = "Add Task")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview(){
    TripPlanTodaysPlanScreen(navController = rememberNavController(),
        journal = Journal())
}

fun getDayDifference(startDate : Long, endDate : Long) : Int{

    val difference = endDate - startDate

    return (difference / (24*60*60*1000)).toInt() + 1
}


@OptIn(DelicateCoroutinesApi::class)
fun loadTasksOfDay(journal: Journal, day: String, tasks : MutableList<Task>){

    GlobalScope.launch{
        try {
            val user = FirebaseAuth.getInstance().currentUser
            val firestore = Injection.instance()
            //val tasksList =  mutableListOf<Task>()
            //val list: MutableList<Map<String, Any>> = mutableListOf()

            val dayCollectionRef = firestore.collection("users")
                .document(user!!.uid).collection("journals").document(journal.title)
                .collection("days").document(day).collection("tasks").orderBy("startTime")

            val daySnapshot = dayCollectionRef.get().await()


            for (task in daySnapshot.documents){
                val eachTask = task.toObject(Task::class.java)
                if (eachTask != null) {
                    tasks.add(eachTask)
                }
            }


        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}

fun deleteTask(selectedDay : String, navController: NavController,
               task: Task, journal: Journal, context: Context)
{
    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.cancel(task.notificationID.toInt())

    val androidAlarmSchedular = AndroidAlarmSchedular(context)

    androidAlarmSchedular.cancel(task.alarmItemHashCode)

    FirebaseAuth.getInstance().uid?.let { it1 ->
        Injection.instance().collection("users")
            .document(it1).collection("journals").document(journal.title)
            .collection("days").document(selectedDay)
            .collection("tasks").document(task.title).delete()
            .addOnSuccessListener {
                println("Task deleted successfully")
                navController.currentBackStackEntry?.savedStateHandle?.set("journal", journal)
                navController.navigate(Screen.TripPlanTodaysPlanScreen.route)
            }
            .addOnFailureListener { println("Error occurred while trying to delete task") }
    }
}