package com.xyz.orderprototype.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.xyz.orderprototype.data.model.auth.LoginRequest
import com.xyz.orderprototype.data.network.RetrofitClient
import androidx.compose.ui.platform.LocalContext
import com.xyz.orderprototype.data.local.DataStoreManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val isValidEmail = email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            isError = email.isNotBlank() && !isValidEmail,
            supportingText = {
                if (email.isBlank()) {
                    Text("Email is required")
                } else if (!isValidEmail) {
                    Text("Enter a valid email")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (!isValidEmail || password.isBlank()) {
                    Toast.makeText(
                        context,
                        "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    scope.launch {

                        try {

                            val response =
                                RetrofitClient.api.login(
                                    LoginRequest(
                                        email = email,
                                        password = password
                                    )
                                )

                            dataStoreManager.saveUser(
                                response.token,
                                response.name,
                                response.email
                            )

                            println("TOKEN SAVED = ${response.token}")

                            Toast.makeText(
                                context,
                                "Logged in",
                                Toast.LENGTH_SHORT
                            ).show()

                            onLoginSuccess()

                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                context,
                                "Login failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        TextButton(
            onClick = onRegisterClick
        ) {
            Text("Create Account")
        }
    }
}
