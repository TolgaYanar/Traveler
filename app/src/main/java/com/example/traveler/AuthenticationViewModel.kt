package com.example.traveler

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.traveler.data.Injection
import com.example.traveler.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.traveler.data.Result
import com.example.traveler.data.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.tasks.await

class AuthenticationViewModel: ViewModel() {

    private val _userRepository : UserRepository

    init {
        _userRepository = UserRepository(
            FirebaseAuth.getInstance(), //default firebase auth instance from abstract class
            Injection.instance() // returns firebase firestore
        )
    }

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult

    fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _authResult.value = _userRepository.signUp(email,password,fullName)
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = _userRepository.signIn(email,password)
        }
    }

    fun signInViaGoogle(account: GoogleSignInAccount){
        viewModelScope.launch {
            _authResult.value = _userRepository.signInViaGoogle(account)
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient =
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(_userRepository.clientID)
                .requestEmail()
                .build()
        )


    @Composable
    fun GetGoogleSignInClient(context: Context,
                              onSignInSuccess: (GoogleSignInAccount) -> Unit,
                              onSignInFailed: () -> Unit) {

        val googleSignInClient = remember {
            getGoogleSignInClient(context)
        }

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    println("success")
                    onSignInSuccess(account)
                } else {
                    println("fail")
                    onSignInFailed()
                }
            } catch (e: ApiException) {
                onSignInFailed()
            }
        }

        Image(painter = painterResource(id = R.drawable.google),
            contentDescription = null,
            modifier =  Modifier
                .padding(horizontal = 8.dp)
                .clickable {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                }
        )

    }

    fun signOut() {
        viewModelScope.launch {
            _authResult.value = _userRepository.signOut()
        }
    }

    fun reauthentication(user: FirebaseUser?, currentPassword: String, newPassword: String, context: Context){

        viewModelScope.launch {
            if(user!= null){
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                user.reauthenticate(credential).addOnCompleteListener{completion ->
                    if(completion.isSuccessful) {
                        //success
                        user.updatePassword(newPassword).addOnCompleteListener {success ->
                            if(success.isSuccessful){
                                user.sendEmailVerification()
                                showToast("Password updated successfully", context)
                            }
                        }
                    }else{
                        //fail
                        showToast("Password couldn't updated", context)
                    }
                }
            } else{
                //Reauthentication failed
                showToast("Reauthentication failed!", context = context )
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateEmail(newEmail: String, context: Context){

        GlobalScope.launch {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                try {
                    currentUser.verifyBeforeUpdateEmail(newEmail).await()
                    // Email verification sent successfully
                    showToast("Verify your new email!", context)
                    println("Email update successful")
                } catch (e: Exception) {
                    // Email verification sending failed
                    showToast("Error occurred while trying to send email verification", context)
                    println("Error sending email verification: ${e.message}")
                }
            }
        }
    }

    private fun showToast(message: String, context: Context) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

}