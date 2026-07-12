package com.xyz.orderprototype.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.xyz.orderprototype.data.local.DataStoreManager
import com.xyz.orderprototype.data.model.auth.UpdateProfileRequest
import com.xyz.orderprototype.data.model.auth.UserResponse
import com.xyz.orderprototype.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onOrdersClick: () -> Unit = {}
) {
    var user by remember { mutableStateOf<UserResponse?>(null) }
    var editableName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStoreManager = DataStoreManager(context)
    val token by dataStoreManager.tokenFlow.collectAsState(initial = "")
    val cachedName by dataStoreManager.nameFlow.collectAsState(initial = "")
    val cachedEmail by dataStoreManager.emailFlow.collectAsState(initial = "")
    val profileImageUri by dataStoreManager.profileImageFlow.collectAsState(initial = null)
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        scope.launch {
            dataStoreManager.saveProfile(
                name = editableName.ifBlank { cachedName.orEmpty() },
                email = cachedEmail.orEmpty(),
                imageUri = uri?.toString()
            )
        }
    }

    fun loadProfile() {
        val currentToken = token
        if (currentToken.isNullOrBlank()) {
            isLoading = false
            return
        }

        scope.launch {
            isLoading = true
            message = null
            try {
                val freshUser = withContext(Dispatchers.IO) {
                    RetrofitClient.api.getMe("Bearer $currentToken")
                }
                user = freshUser
                editableName = freshUser.name
                dataStoreManager.saveProfile(freshUser.name, freshUser.email, profileImageUri)
            } catch (e: Exception) {
                editableName = cachedName.orEmpty()
                message = "Could not refresh profile. Showing saved details."
            } finally {
                isLoading = false
            }
        }
    }

    fun saveProfile() {
        val currentToken = token
        if (currentToken.isNullOrBlank()) {
            return
        }

        scope.launch {
            message = null
            try {
                val updated = withContext(Dispatchers.IO) {
                    RetrofitClient.api.updateMe(
                        "Bearer $currentToken",
                        UpdateProfileRequest(editableName)
                    )
                }
                user = updated
                dataStoreManager.saveProfile(updated.name, updated.email, profileImageUri)
                message = "Profile updated"
            } catch (e: Exception) {
                dataStoreManager.saveProfile(editableName, cachedEmail.orEmpty(), profileImageUri)
                message = "Saved locally. It will sync when the backend is reachable."
            }
        }
    }

    LaunchedEffect(token) {
        editableName = cachedName.orEmpty()
        loadProfile()
    }

    val displayEmail = user?.email ?: cachedEmail.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text("Profile", style = MaterialTheme.typography.headlineMedium)

        if (profileImageUri.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile picture",
                modifier = Modifier.size(104.dp)
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(profileImageUri),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape)
            )
        }

        Button(onClick = { imagePicker.launch("image/*") }) {
            Text("Change Picture")
        }

        if (isLoading && displayEmail.isBlank()) {
            CircularProgressIndicator()
        }

        OutlinedTextField(
            value = editableName,
            onValueChange = { editableName = it },
            label = { Text("Name") },
            singleLine = true
        )

        Text(displayEmail.ifBlank { "Email unavailable" })

        message?.let {
            Text(
                text = it,
                color = if (it.contains("Could not")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { saveProfile() }) {
                Text("Save")
            }

            Button(onClick = onOrdersClick) {
                Text("My Orders")
            }
        }

        Button(
            onClick = {
                scope.launch {
                    dataStoreManager.clearToken()
                    onLogout()
                }
            }
        ) {
            Text("Logout")
        }
    }
}
