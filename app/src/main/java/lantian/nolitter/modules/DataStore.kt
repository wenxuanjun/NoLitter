package lantian.nolitter.modules

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.dataStore

    fun <T> getPreferenceSync(key: String, defaultValue: T): T {
        return runBlocking { getPreference(key, defaultValue) }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> getPreference(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> dataStore.data.map { it[stringPreferencesKey(key)] ?: defaultValue }.first()
            is Boolean -> dataStore.data.map { it[booleanPreferencesKey(key)] ?: defaultValue }.first()
            else -> throw IllegalArgumentException("The type of value provided is invalid")
        } as T
    }

    suspend fun <T> setPreference(key: String, value: T) {
        when (value) {
            is String -> dataStore.edit { it[stringPreferencesKey(key)] = value }
            is Boolean -> dataStore.edit { it[booleanPreferencesKey(key)] = value }
            else -> throw IllegalArgumentException("The type of value provided is invalid")
        }
    }
}
