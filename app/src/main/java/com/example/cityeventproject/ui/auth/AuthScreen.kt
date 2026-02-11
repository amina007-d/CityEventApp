package com.example.cityeventproject.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cityeventproject.ui.components.SimpleTopBar

@Composable
fun AuthScreen(
    onSuccess: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }

    LaunchedEffect(vm.user.value) {
        if (vm.user.value != null) onSuccess()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(title = "Welcome")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (mode == AuthMode.SIGN_IN) "Sign in" else "Create account")
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (error != null) Text(error!!)

                    Button(
                        onClick = {
                            error = null
                            val cb: (String?) -> Unit = { err ->
                                error = err
                                if (err == null) onSuccess()
                            }
                            if (mode == AuthMode.SIGN_IN) vm.signIn(email, password, cb) else vm.signUp(email, password, cb)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) { Text(if (mode == AuthMode.SIGN_IN) "Sign in" else "Sign up") }

                    TextButton(onClick = {
                        error = null
                        mode = if (mode == AuthMode.SIGN_IN) AuthMode.SIGN_UP else AuthMode.SIGN_IN
                    }) {
                        Text(if (mode == AuthMode.SIGN_IN) "No account? Sign up" else "Have an account? Sign in")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Tip: Password must be 6+ chars and include a number.")
        }
    }
}

private enum class AuthMode { SIGN_IN, SIGN_UP }
