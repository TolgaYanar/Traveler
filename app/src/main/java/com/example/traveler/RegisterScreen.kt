package com.example.traveler

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.traveler.data.Result


@Composable
fun RegisterScreen(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel,
    profileViewModel: ProfileViewModel,
    context: Context
){

    var name by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var confirmPassword by remember {
        mutableStateOf("")
    }
    var showPassword1 by remember {
        mutableStateOf(false)
    }
    var showPassword2 by remember {
        mutableStateOf(false)
    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(id = R.color.app_bar_color)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        item {
            Image(painter = painterResource(id = R.drawable.rocket), contentDescription = null,
                alignment = Alignment.TopCenter, modifier = Modifier
                    .height(150.dp)
                    .width(150.dp))
            Text(text = "Hi there!", textAlign = TextAlign.Center, fontWeight = FontWeight.Light, fontSize = 16.sp, modifier = Modifier.padding(vertical = 10.dp))
            Text(text = "Let's Get Started", fontWeight = FontWeight.ExtraBold, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Please input your details here", textAlign = TextAlign.Center, fontWeight = FontWeight.Light, fontSize = 16.sp, modifier = Modifier.padding(vertical = 30.dp))
            Spacer(modifier = Modifier.height(10.dp))

            TextField(value = name, onValueChange = {name = it},
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.baseline_account_circle_24), contentDescription = null)
                },
                singleLine = true,
                placeholder = {
                    Text(text = "Full name")
                })
            Spacer(modifier = Modifier.height(20.dp))
            TextField(value = email, onValueChange = {email = it},
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.baseline_email_24), contentDescription = null)
                },
                singleLine = true,
                placeholder = {
                    Text(text = "E-mail")
                })
            Spacer(modifier = Modifier.height(20.dp))
            TextField(value = password, onValueChange = {password = it},
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.baseline_lock_24), contentDescription = null)
                },
                singleLine = true,
                placeholder = {
                    Text(text = "Password")
                },
                visualTransformation = if(showPassword1) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword1 = !showPassword1 }) {
                        if(showPassword1) Icon(painter = painterResource(id = R.drawable.baseline_visibility_off_24), contentDescription = null)
                        else Icon(painter = painterResource(id = R.drawable.baseline_visibility_24), contentDescription = null)
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextField(value = confirmPassword, onValueChange = {confirmPassword = it},
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.baseline_lock_24), contentDescription = null)
                },
                singleLine = true,
                placeholder = {
                    Text(text = "Confirm Password")
                },
                visualTransformation = if(showPassword2) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword2 = !showPassword2 }) {
                        if(showPassword2) Icon(painter = painterResource(id = R.drawable.baseline_visibility_off_24), contentDescription = null)
                        else Icon(painter = painterResource(id = R.drawable.baseline_visibility_24), contentDescription = null)
                    }
                }
            )
            Button(onClick = {
                if(password.isNotEmpty() && confirmPassword.isNotEmpty() && name.isNotEmpty() && email.isNotEmpty() ){
                    if(password == confirmPassword){
                        try {
                            authenticationViewModel.signUp(email,password,name)
                            email = ""
                            name = ""
                            password = ""
                            confirmPassword = ""
                            profileViewModel.loadCurrentUser()
                            navController.navigate(Screen.MainMenuScreen.route)
                        }catch (e : Exception){
                            Toast.makeText(context, "Error occurred!", Toast.LENGTH_LONG).show()
                            println("${e.cause} ${e.message}")
                        }

                    }else{
                        Toast.makeText(context,"Passwords doesn't match!", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(context,"Please fill all the blanks.", Toast.LENGTH_LONG).show()
                }
            }, modifier = Modifier.padding(16.dp), shape = RoundedCornerShape(20.dp)) {
                Text(text = "Create an Account",fontSize = 20.sp,
                    modifier = Modifier.padding(7.dp),
                    fontWeight = FontWeight.Bold, color = Color.White)
            }
            Row {
                Divider(modifier = Modifier
                    .width(80.dp)
                    .padding(top = 8.dp))
                Text(text = "OR", fontWeight = FontWeight.Light, modifier = Modifier.padding(bottom = 5.dp, start = 5.dp, end = 5.dp))
                Divider(modifier = Modifier
                    .width(80.dp)
                    .padding(top = 8.dp))
            }
            Button(onClick = { navController.navigate(Screen.LoginScreen.route) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text(text = "Login",fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 7.dp),
                    fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }

}
