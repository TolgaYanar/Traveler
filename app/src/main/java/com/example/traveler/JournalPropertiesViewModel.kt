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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.traveler.data.AlarmItem
import com.example.traveler.data.AndroidAlarmSchedular
import com.example.traveler.data.Injection
import com.example.traveler.data.Journal
import com.example.traveler.data.Notes
import com.example.traveler.data.Task
import com.example.traveler.data.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

class JournalPropertiesViewModel : ViewModel() {

    @Composable
    fun ColorPicker(onDismissRequest: () -> Unit,
                    onColorSelected: (Color) -> Unit)
    {
        var red by remember {
            mutableStateOf(0f)
        }
        var blue by remember {
            mutableStateOf(0f)
        }
        var green by remember {
            mutableStateOf(0f)
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
                        .background(
                            Color(
                                red = red / 255f,
                                blue = blue / 255f,
                                green = green / 255f
                            )
                        )
                    )

                    Text(text = "Red : ${red.toInt()}")
                    Slider(value = red, onValueChange = {
                        red = it
                    }, valueRange = 0f..255f)

                    Text(text = "Blue : ${blue.toInt()}")
                    Slider(value = blue, onValueChange = {
                        blue = it
                    }, valueRange = 0f..255f)

                    Text(text = "Green : ${green.toInt()}")
                    Slider(value = green, onValueChange = {
                        green = it
                    }, valueRange = 0f..255f)

                    Button(onClick = {
                        onColorSelected(Color(red = red/255f, green = green/255f, blue = blue/255f))
                        onDismissRequest() }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Choose")
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

    fun longToDate(longDate: Long?): String? {
        val date = longDate?.let { Date(it) }
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return date?.let { dateFormat.format(it) }
    }

    fun dateToLong(dateString: String): Long {
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(dateString)
        return date?.time ?: 0L
    }

    fun uploadToNotes(imageOrText : String, journal : Journal, context: Context){
        val user = FirebaseAuth.getInstance().currentUser
        val added = Calendar.getInstance().time.time
        val firestore = Injection.instance()
        val notesCollection = firestore.collection("users").document(user!!.uid).collection("journals")
            .document(journal.title).collection("notes")
        val noteDoc = notesCollection.document(added.toString())
        val hashmap = hashMapOf<String, Any>(
            "note" to imageOrText,
            "added" to added
        )
        noteDoc.set(hashmap).addOnSuccessListener {
            Toast.makeText(context, "Added to notes successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Couldn't added to notes. Error.", Toast.LENGTH_SHORT).show()
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

        DropdownMenu(expanded = expanded, onDismissRequest = { onDismissRequest() }
        ){
            list.forEach {item->
                DropdownMenuItem(onClick = { onClick(item) }) {
                    Text(text = item.toString(), fontSize = 16.sp, modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center)
                }
            }
        }
    }

    fun addTask(context: Context, repeat : String = "Once", reminder : String, journal : Journal, dayNum : Int, task : HashMap<String, Any>){

        val daysCollection = Injection.instance().collection("users").document(FirebaseAuth.getInstance().uid!!)
            .collection("journals").document(journal.title).collection("days")

        val journalDuration = getDayDifference(journal.startDateInMillis, journal.endDateInMillis)

        val schedular = AndroidAlarmSchedular(context)

        if(repeat.equals("Always")){
            for (day in 0 until journalDuration){
                if(day != 0){
                    task.set("startTime", task.get("startTime").toString().toLong() + 24 * 60 * 60 * 1000)
                    task.set("endTime", task.get("endTime").toString().toLong() + 24 * 60 * 60 * 1000)
                }

                addAlarm(schedular, (task["startTime"].toString().toLong()-reminder.toLong()*60*1000),
                    "${task["title"]} at ${longToTime(task["startTime"].toString().toLong())}", task)

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
                if(day != 0){
                    task.set("startTime", task.get("startTime").toString().toLong() + 2 * 24 * 60 * 60 * 1000)
                    task.set("endTime", task.get("endTime").toString().toLong() + 2 * 24 * 60 * 60 * 1000)
                }

                addAlarm(schedular, (task["startTime"].toString().toLong()-reminder.toLong()*60*1000),
                    "${task["title"]} at ${longToTime(task["startTime"].toString().toLong())}", task)

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
            notified = time - 3 * 60 * 60 * 1000,
            message = message,
            id = Random.nextInt(10000,99999).toString(),
            startTime = task.get("startTime").toString().toLong(),
            title = task.get("title").toString()
        )
        task.put("notificationID", alarmItem.id)
        task.put("alarmItemHashCode", alarmItem.hashCode())
        alarmItem.let(schedular::schedule)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getRandomUsersWithMatchingLocation(number : Int, location : String, list : MutableList<User>){

        viewModelScope.launch {

            val usersID = mutableListOf<String>()
            val users = mutableListOf<String>()
            val firestore = Injection.instance()

            val usersCollection = firestore.collection("users")
            val usersCollectionSnapshot = usersCollection.get().await()

            usersCollectionSnapshot?.forEach { document->
                val userId = document.reference.id
                if (!usersID.contains(userId) && userId !=  FirebaseAuth.getInstance().uid) {
                    usersID.add(userId)
                    if(usersID.size == number){
                        return@forEach
                    }
                }
            }

            for (id in usersID){
                val journalsShapshot = usersCollection.document(id).collection("journals").get().await()

                journalsShapshot.forEach { journal->
                    val journalRef = journal.toObject(Journal::class.java)

                    val currentDate = Calendar.getInstance().time.time
                    val journalEndDate = journalRef.endDateInMillis
                    if(journalRef.location.contains(location) && !journalRef.private && currentDate>journalEndDate){
                        if(!users.contains(id)){
                            users.add(id)
                        }
                    }
                }
            }
            list.shuffle()
            users.forEach { userID->
                val user = usersCollection.document(userID).get().await().toObject(User::class.java)
                if (user != null) {
                    list.add(user)
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getNotifications(notificationList : MutableState<List<AlarmItem>>){

        viewModelScope.launch {
            val firestore = Injection.instance()
            val userID = FirebaseAuth.getInstance().uid

            val notificationsCollection = firestore.collection("users")
                .document(userID!!).collection("notifications").orderBy("startTime")

            val notificationsCollectionSnap = notificationsCollection.get().await()

            notificationList.value = notificationsCollectionSnap.toObjects(AlarmItem::class.java)
            notificationList.value = notificationList.value.reversed()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun deleteNotification(id : String){

        viewModelScope.launch {
            val firestore = Injection.instance()
            val userID = FirebaseAuth.getInstance().uid

            val notificationDocument = firestore.collection("users")
                .document(userID!!).collection("notifications").document(id)

            val deleteNotification = notificationDocument.delete()

        }
    }

    fun getNotes(journal : Journal, list : MutableState<List<Notes>>, user : User?){

        viewModelScope.launch {
            val firestore = Injection.instance()
            val notesCollection = user?.let {
                firestore.collection("users").document(it.uid)
                    .collection("journals").document(journal.title).collection("notes")
                    .orderBy("added")
            }
            val notesSnapshot = notesCollection?.get()?.await()
            if (notesSnapshot != null) {
                list.value = notesSnapshot.toObjects(Notes::class.java)
            }
        }

    }

    fun deleteNote(journal: Journal, note: Notes, user: User?){

        viewModelScope.launch {
            val firestore = Injection.instance()
            val notesCollection = user?.let {
                firestore.collection("users").document(it.uid)
                    .collection("journals").document(journal.title).collection("notes")
            }
            val noteSnapshot = notesCollection?.document(note.added.toString())?.delete()
                ?.addOnSuccessListener {
                    println("Note deleted successfully.")
                }
                ?.addOnFailureListener {
                    println("Note couldn't deleted successfully.")
                }
        }
    }

    fun getDayDifference(startDate : Long, endDate : Long) : Int{

        val difference = endDate - startDate

        return difference.div((24*60*60*1000)).toInt()

    }


    @OptIn(DelicateCoroutinesApi::class)
    fun loadTasksOfDay(journal: Journal, day: String, tasks : MutableList<Task>){

        viewModelScope.launch{
            try {
                val user = FirebaseAuth.getInstance().currentUser
                val firestore = Injection.instance()

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

    @Composable
    fun UpdateTask(task: Task, journal: Journal, thatDay: Long, dayNum: Int,
                   navController: NavController,
                   onDismissRequest: () -> Unit){

        var title by remember {
            mutableStateOf(task.title)
        }
        var reminder by remember {
            mutableStateOf(task.remind)
        }
        var notes by remember {
            mutableStateOf(task.notes)
        }
        var startTime by remember {
            mutableStateOf(longToTime(task.startTime))
        }
        var endTime by remember {
            mutableStateOf(longToTime(task.endTime))
        }
        var timePickerExpanded by remember {
            mutableStateOf(false)
        }

        val repeatExpanded by remember {
            mutableStateOf(false)
        }

        var reminderExpanded by remember {
            mutableStateOf(false)
        }

        val context = LocalContext.current

        Dialog(onDismissRequest = {
            onDismissRequest()
        })
        {
            Surface(
                shape = MaterialTheme.shapes.medium
            ) {

                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Text(text = "Update Journal", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 20.dp))

                    TextField(value = title, onValueChange = {title = it}, modifier = Modifier.padding(vertical = 5.dp))

                    TextField(value = startTime, onValueChange = {},
                        label = {
                            Text(text = "Starts")
                        }, enabled = false,
                        trailingIcon = {
                            Image(modifier = Modifier.clickable {
                                timePickerExpanded = true
                            },
                                painter = painterResource(id = R.drawable.baseline_access_alarm_24), contentDescription = null)
                        },
                        modifier = Modifier.padding(vertical = 5.dp)
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
                        },
                        modifier = Modifier.padding(vertical = 5.dp)
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

                    TextField(value = "$reminder minutes", onValueChange = {},
                        trailingIcon = {
                            IconButton(onClick = { reminderExpanded = !repeatExpanded }) {
                                Icon(painter = painterResource(id = R.drawable.baseline_access_time_24),
                                    contentDescription = null)
                            }
                            DropDownMenu(
                                list = listOf("5", "10", "15", "30", "45", "60"),
                                expanded = reminderExpanded,
                                onDismissRequest = { reminderExpanded = false },
                                onClick = {item->
                                    reminder = item.toString()
                                    reminderExpanded = false
                                }
                            )
                        },
                        modifier = Modifier.padding(vertical = 5.dp))

                    OutlinedTextField(value = notes, onValueChange = {notes = it},
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Gray, focusedContainerColor = Color.Gray),
                        label = {
                            Text(text = "Notes")
                        }
                    )

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp), horizontalArrangement = Arrangement.End) {
                        Button(onClick = {
                            if(title.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty() && reminder.isNotEmpty() ){

                                deleteTask(
                                    "day$dayNum",
                                    navController,
                                    task,
                                    journal,
                                    context
                                )

                                val taskHash = hashMapOf<String, Any>(
                                    "title" to title,
                                    "startTime" to (timeToLong(startTime) + thatDay),
                                    "endTime" to (timeToLong(endTime) + thatDay),
                                    "notes" to notes,
                                    "remind" to reminder
                                )
                                addTask(context, reminder = reminder, journal = journal, dayNum = dayNum, task = taskHash)
                                onDismissRequest()
                            }else{
                                Toast.makeText(context, "Please fill the blanks.", Toast.LENGTH_LONG).show()
                            }
                        }, ) {
                            Text(text = "Update")
                        }
                    }
                }
            }
        }
    }
}