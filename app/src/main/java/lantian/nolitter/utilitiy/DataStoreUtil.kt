package lantian.nolitter.utilitiy

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "settings")

object DataStoreUtil {

    private lateinit var dataStore: DataStore<Preferences>
    fun initialize(context: Context) { dataStore = context.dataStore }

    @Suppress("UNCHECKED_CAST")
    fun <T> getPreference(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> runBlocking { dataStore.data.map { it[stringPreferencesKey(key)] ?: defaultValue }.first() }
            is Boolean -> runBlocking { dataStore.data.map { it[booleanPreferencesKey(key)] ?: defaultValue }.first() }
            else -> throw IllegalArgumentException("Wrong value provided with invalid value type")
        } as T
    }

    fun <T> setPreference(key: String, value: T) {
        when (value) {
            is String -> runBlocking { dataStore.edit { it[stringPreferencesKey(key)] = value } }
            is Boolean -> runBlocking { dataStore.edit { it[booleanPreferencesKey(key)] = value } }
            else -> throw IllegalArgumentException("Wrong value provided with invalid value type")
        }
    }
}