package com.example.traveler

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traveler.data.Injection
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJournal(navController: NavController,
               profileViewModel: ProfileViewModel,
               journalPropertiesViewModel: JournalPropertiesViewModel){

    var title by remember {
        mutableStateOf("")
    }
    var location by remember {
        mutableStateOf("")
    }
    var color by remember {
        mutableStateOf(Color.White)
    }
    var notes by remember {
        mutableStateOf("")
    }
    var private by remember {
        mutableStateOf(false)
    }

    var colorPickerVisibility by remember {
        mutableStateOf(false)
    }
    var datePickerExpanded by remember {
        mutableStateOf(false)
    }

    val dateRangeDialogState = rememberDateRangePickerState()

    var startDateInMillis by remember {
        mutableStateOf(dateRangeDialogState.selectedStartDateMillis)
    }
    var endDateInMillis by remember {
        mutableStateOf(dateRangeDialogState.selectedEndDateMillis)
    }

    val context = LocalContext.current


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Add Journal", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.add_journal_color)
                ))
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(colorResource(id = R.color.add_journal_color)),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                TextField(value = title, onValueChange = {title = it}, modifier = Modifier.padding(vertical = 10.dp),
                    label = {
                        Text(text = "Title", color = Color.Black)
                    }, placeholder = {
                        Text(text = "Enter Title", color = Color.Black)
                    })
                TextField(value = location, onValueChange = {location = it}, modifier = Modifier.padding(vertical = 10.dp),
                    label = {
                        Text(text = "Location", color = Color.Black)
                    }, placeholder = {
                        Text(text = "Enter Location", color = Color.Black)
                    })

                Spacer(modifier = Modifier.height(30.dp))

                Card(modifier = Modifier
                    .height(80.dp)
                    .width(287.dp)
                    .padding(vertical = 10.dp),
                    backgroundColor = color
                ) {
                    Text(text = "Color of Journal", color = Color.Black, modifier = Modifier.padding(6.dp),
                        fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                        Icon(painter = painterResource(id = R.drawable.baseline_color_lens_24),
                            contentDescription = null, tint = Color.Gray,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(40.dp)
                                .clickable {
                                    colorPickerVisibility = true
                                })
                    }
                    if(colorPickerVisibility){
                        journalPropertiesViewModel.ColorPicker(onDismissRequest = { colorPickerVisibility = false },
                            onColorSelected = {
                                color = it
                                colorPickerVisibility = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                TextField(value = "" + journalPropertiesViewModel.longToDate(startDateInMillis), onValueChange = {}, modifier = Modifier.padding(vertical = 10.dp),
                    label = {
                        Text(text = "Starting Time", color = Color.Black)
                    }, enabled = false,
                    trailingIcon = {
                        Icon(imageVector = Icons.Filled.DateRange, contentDescription = null,
                            modifier = Modifier.clickable {
                                datePickerExpanded = true
                            })
                    }
                )

                TextField(value = "" + journalPropertiesViewModel.longToDate(endDateInMillis), onValueChange = {}, modifier = Modifier.padding(vertical = 10.dp),
                    label = {
                        Text(text = "Ending Time", color = Color.Black)
                    }, enabled = false,
                    trailingIcon = {
                        Icon(imageVector = Icons.Filled.DateRange, contentDescription = null,
                            modifier = Modifier.clickable {
                                datePickerExpanded = true
                            })
                    }
                )

                if (datePickerExpanded){

                    DatePickerDialog(onDismissRequest = {
                        datePickerExpanded = false
                    }, confirmButton = {
                        TextButton(onClick = {
                            startDateInMillis = dateRangeDialogState.selectedStartDateMillis
                            endDateInMillis = dateRangeDialogState.selectedEndDateMillis
                            datePickerExpanded = false
                        }) {
                            Text(text = "Select")
                        }
                    }, dismissButton = {
                        TextButton(onClick = { datePickerExpanded = false }) {
                            Text(text = "Cancel")
                        }
                    }
                    ) {
                        DateRangePicker(state = dateRangeDialogState,
//                        dateValidator = {
//                            it >= Calendar.getInstance(TimeZone.getDefault()).time.time
//                        }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(value = notes, onValueChange = {notes = it},
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Gray, focusedContainerColor = Color.Gray),
                    placeholder = {
                        Text(text = "Notes", color = Color.Black)
                    }, modifier = Modifier.height(200.dp),
                    label = {
                        Text(text = "Notes", color = Color.Black)
                    }
                )

                Checkbox(checked = private, onCheckedChange = {
                    private = !private
                })
                Text(text = "Make it private", color = Color.Black)

                Row(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp).padding(end = 30.dp),
                    horizontalArrangement = Arrangement.End)
                {

                    Button(onClick = {

                        if(location.isNotEmpty() && title.isNotEmpty() && startDateInMillis != null &&
                            endDateInMillis != null)
                        {
                            val journal = hashMapOf<String, Any?>(
                                "title" to title,
                                "location" to location,
                                "color" to color.value.toString(),
                                "startDate" to journalPropertiesViewModel.longToDate(startDateInMillis),
                                "endDate" to journalPropertiesViewModel.longToDate(endDateInMillis),
                                "startDateInMillis" to startDateInMillis,
                                "endDateInMillis" to endDateInMillis,
                                "notes" to notes,
                                "private" to private
                            )

                            profileViewModel.currentUser.value?.let { it1 ->
                                Injection.instance().collection("users").document(
                                    it1.uid).collection("journals").document(title).set(journal)
                                    .addOnSuccessListener {
                                        println("Journal added successfully")
                                        navController.navigateUp()
                                    }.addOnFailureListener{
                                        println("Error occurred during journal adding")

                                    }
                            }
                        }
                        else{
                            Toast.makeText(context, "Please fill the blanks.", Toast.LENGTH_LONG).show()
                        }
                    }
                    ) {
                        Text(text = "Add")
                    }
                }
            }

        }
    }
}