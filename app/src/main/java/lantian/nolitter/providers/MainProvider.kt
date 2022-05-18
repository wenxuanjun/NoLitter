package lantian.nolitter.providers

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.room.Room
import lantian.nolitter.BuildConfig
import lantian.nolitter.databases.ApplicationDao
import lantian.nolitter.databases.MainDatabase

private const val AUTHORITY = "lantian.nolitter.provider"

private const val URI_PREFERENCE = 0
private const val URI_DATABASE = 1


class MainProvider : ContentProvider() {

    private lateinit var database: MainDatabase
    private lateinit var preferences: SharedPreferences
    private var applicationDao: ApplicationDao? = null
    private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI(AUTHORITY, "preference/*", URI_PREFERENCE)
        uriMatcher.addURI(AUTHORITY, "database/*", URI_DATABASE)
    }

    private fun generateCursor(value: String): Cursor {
        return MatrixCursor(arrayOf("value"), 1).apply { addRow(arrayOf<Any?>(value)) }
    }

    private fun handleOnDataStore(uri: Uri, projection: Array<String>): Cursor {
        return when (uri.pathSegments[1]) {
            "string" -> generateCursor(preferences.getString(projection[0], projection[1]) ?: projection[1])
            "boolean" -> generateCursor(preferences.getBoolean(projection[0], projection[1].toBoolean()).toString())
            else -> throw RuntimeException("[NoLitter] No matching value type, with uri: ${uri.path}, with projection: $projection")
        }
    }

    private fun handleOnDataBase(uri: Uri, projection: Array<String>): Cursor {
        return when (uri.pathSegments[1]) {
            "string" -> generateCursor(preferences.getString(projection[0], projection[1]) ?: projection[1])
            "boolean" -> generateCursor(preferences.getBoolean(projection[0], projection[1].toBoolean()).toString())
            else -> throw RuntimeException("[NoLitter] No matching value type, with uri: ${uri.path}, with projection: $projection")
        }
    }

    override fun onCreate(): Boolean {
        context?.let {
            val preferencesName = BuildConfig.APPLICATION_ID + "_preferences"
            preferences = it.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
            database = Room.databaseBuilder(it, MainDatabase::class.java, BuildConfig.APPLICATION_ID).build()
            applicationDao = database.applicationDao()
            return true
        }
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor {
        return when (uriMatcher.match(uri)) {
            URI_PREFERENCE -> handleOnDataStore(uri, projection as Array<String>)
            URI_DATABASE -> handleOnDataBase(uri, projection as Array<String>)
            else -> throw RuntimeException("[NoLitter] No matching uri, with uri: ${uri.path}")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {TODO("")}

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {TODO("")}

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {TODO("")}

    override fun getType(uri: Uri): String? {
        when (uriMatcher.match(uri)) {
            URI_PREFERENCE -> return "vnd.android.cursor.item/vnd.$AUTHORITY.datastore"
            URI_DATABASE -> return "vnd.android.cursor.item/vnd.$AUTHORITY.database"
        }
        return null
    }
}
