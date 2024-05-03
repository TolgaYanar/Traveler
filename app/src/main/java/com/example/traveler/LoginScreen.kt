package com.example.traveler

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel,
    profileViewModel: ProfileViewModel
){

    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var showPassword by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val result by authenticationViewModel.authResult.observeAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = "Welcome Back", color = colorResource(id = R.color.white),
                    modifier = Modifier
                        .padding(start = 5.dp), fontWeight = FontWeight.Light)
            },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.app_bar_color))
            )
        }
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .background(Color(0XFFADD8E6)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center)
        {

            item {
                Image(painter = painterResource(id = R.drawable.travelericon), contentDescription = null,
                    modifier = Modifier
                        .padding(25.dp)
                        .size(200.dp)
                        .clip(RoundedCornerShape(25.dp)))
                Text(text = "Please, Log In", fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                    color = Color(0x96000000))
                Spacer(modifier = Modifier.height(25.dp))
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
                    visualTransformation = if(showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            if(showPassword) Icon(painter = painterResource(id = R.drawable.baseline_visibility_off_24), contentDescription = null)
                            else Icon(painter = painterResource(id = R.drawable.baseline_visibility_24), contentDescription = null)
                        }
                    }
                )
                Spacer(modifier = Modifier.padding(20.dp))
                Button(onClick = {
                    authenticationViewModel.signIn(email,password)
                }, modifier = Modifier.size(200.dp,40.dp)) {
                    Text(text = "LOGIN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                LaunchedEffect(result) {
                    result?.let { result ->
                        println(result)
                        when (result) {
                            is Result.Success -> {
                                if(result.data){
                                    println("sdadasdsa")
                                    profileViewModel.loadCurrentUser()
                                    navController.navigate(Screen.MainMenuScreen.route)
                                }
                            }
                            is Result.Fail -> {
                                println("trfhyjjut")
                                // Handle failure case if needed
                            }
                            else -> {
                                // Handle other cases if needed
                            }
                        }
                    }
                }

                Text(text = "Or Sign in with", modifier = Modifier.padding(20.dp), fontSize = 16.sp, color = Color(0x80000000))
                Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    authenticationViewModel.GetGoogleSignInClient(
                        context = context,
                        onSignInSuccess = {
                            authenticationViewModel.signInViaGoogle(it)
                        },
                        onSignInFailed = {
                            Toast.makeText(context, "Sign-in via Google Failed", Toast.LENGTH_LONG).show()
                        }
                    )
                    Image(painter = painterResource(id = R.drawable.instagram), contentDescription = null)
                    Image(painter = painterResource(id = R.drawable.twitterx), contentDescription = null, modifier =  Modifier.padding(horizontal = 8.dp))
                }
                Row(modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Don't have an account?", modifier = Modifier.padding(horizontal = 27.dp), fontSize = 16.sp, color = Color(4278190208))
                    Text(text = "Sign Up", fontWeight = FontWeight.Bold, modifier = Modifier
                        .padding(horizontal = 27.dp)
                        .clickable { navController.navigate(Screen.RegisterScreen.route) }, fontSize = 16.sp,
                        color = Color.Black)
                }
            }
        }
    }
}