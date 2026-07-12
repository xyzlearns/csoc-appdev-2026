package com.xyz.orderprototype.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "auth"
)

class DataStoreManager(
    private val context: Context
) {

    companion object {

        private val TOKEN_KEY =
            stringPreferencesKey("jwt_token")

        private val NAME_KEY =
            stringPreferencesKey("name")

        private val EMAIL_KEY =
            stringPreferencesKey("email")

        private val PROFILE_IMAGE_KEY =
            stringPreferencesKey("profile_image_uri")

        private val ADDRESS_TITLE_KEY =
            stringPreferencesKey("address_title")

        private val ADDRESS_DETAILS_KEY =
            stringPreferencesKey("address_details")

        private val ADDRESS_INSTRUCTIONS_KEY =
            stringPreferencesKey("address_instructions")
    }

    suspend fun saveToken(
        token: String
    ) {

        context.dataStore.edit { prefs ->

            prefs[TOKEN_KEY] = token
        }
    }

    val tokenFlow =
        context.dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }

    suspend fun clearToken() {

        context.dataStore.edit { prefs ->

            prefs.remove(TOKEN_KEY)
        }
    }

    suspend fun saveUser(
        token: String,
        name: String,
        email: String
    ) {

        context.dataStore.edit { prefs ->

            prefs[TOKEN_KEY] = token
            prefs[NAME_KEY] = name
            prefs[EMAIL_KEY] = email
        }
    }

    val nameFlow =
        context.dataStore.data.map {
            it[NAME_KEY]
        }

    val emailFlow =
        context.dataStore.data.map {
            it[EMAIL_KEY]
        }

    suspend fun saveProfile(
        name: String,
        email: String,
        imageUri: String?
    ) {
        context.dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
            prefs[EMAIL_KEY] = email
            if (imageUri.isNullOrBlank()) {
                prefs.remove(PROFILE_IMAGE_KEY)
            } else {
                prefs[PROFILE_IMAGE_KEY] = imageUri
            }
        }
    }

    val profileImageFlow =
        context.dataStore.data.map {
            it[PROFILE_IMAGE_KEY]
        }

    suspend fun saveAddress(
        title: String,
        details: String,
        instructions: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[ADDRESS_TITLE_KEY] = title
            prefs[ADDRESS_DETAILS_KEY] = details
            prefs[ADDRESS_INSTRUCTIONS_KEY] = instructions
        }
    }

    val addressTitleFlow =
        context.dataStore.data.map {
            it[ADDRESS_TITLE_KEY]
        }

    val addressDetailsFlow =
        context.dataStore.data.map {
            it[ADDRESS_DETAILS_KEY]
        }

    val addressInstructionsFlow =
        context.dataStore.data.map {
            it[ADDRESS_INSTRUCTIONS_KEY]
        }
}
