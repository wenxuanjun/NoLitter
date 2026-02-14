package lantian.nolitter.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import app.softwork.serialization.csv.CSVFormat
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import lantian.nolitter.database.PackagePreference
import lantian.nolitter.database.PackagePreferenceDao
import lantian.nolitter.modules.DataStoreManager
import lantian.nolitter.xposed.XposedPreference

private const val URI_MAIN = 0
private const val AUTHORITY = "lantian.nolitter.provider"

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ContentProviderEntryPoint {
    val dataStoreDataSource: DataStoreManager
    val databaseDataSource: PackagePreferenceDao
}

class MainProvider : ContentProvider() {

    private lateinit var dataStoreDataSource: DataStoreManager
    private lateinit var databaseDataSource: PackagePreferenceDao

    private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init { uriMatcher.addURI(AUTHORITY, "main/*", URI_MAIN) }

    private fun generateCursor(value: String): Cursor {
        return MatrixCursor(arrayOf("value"), 1).apply { addRow(arrayOf(value)) }
    }

    private suspend fun <T> getPreference(key: String, defaultValue: T): T {
        return dataStoreDataSource.getPreference(key, defaultValue)
    }

    private suspend fun getPackagePreference(packageName: String): PackagePreference {
        return databaseDataSource.queryPackage(packageName) ?: PackagePreference(packageName = packageName)
    }

    private suspend fun isCustomizedPackages(packageName: String): Boolean {
        val customizedPackages = dataStoreDataSource.getPreference("customized_packages", "")
        return customizedPackages.split(":").contains(packageName)
    }

    @ExperimentalSerializationApi
    private fun handleContentQuery(uri: Uri): Cursor {
        val packagePreference = runBlocking { getPackagePreference(uri.pathSegments[1]) }
        val isCustomizedPackages = runBlocking { isCustomizedPackages(uri.lastPathSegment!!) }
        val xposedPreference = runBlocking {
            CSVFormat.encodeToString(
                XposedPreference.serializer(),
                if (isCustomizedPackages) XposedPreference(
                    forcedMode = packagePreference.forcedMode,
                    allowPublicDirs = packagePreference.allowPublicDirs,
                    additionalHooks = packagePreference.additionalHooks,
                    redirectStyle = packagePreference.redirectStyle,
                    debugMode = getPreference("debug_mode", false)
                )
                else XposedPreference(
                    forcedMode = getPreference("forced_mode", false),
                    allowPublicDirs = getPreference("allow_public_dirs", false),
                    additionalHooks = getPreference("additional_hooks", false),
                    redirectStyle = getPreference("redirect_style", "data"),
                    debugMode = getPreference("debug_mode", false)
                )
            )
        }
        return generateCursor(xposedPreference)
    }

    override fun onCreate(): Boolean {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context?.applicationContext ?: throw IllegalStateException(),
            ContentProviderEntryPoint::class.java
        )
        dataStoreDataSource = hiltEntryPoint.dataStoreDataSource
        databaseDataSource = hiltEntryPoint.databaseDataSource
        return true
    }

    override fun getType(uri: Uri): String = when (uriMatcher.match(uri)) {
        URI_MAIN -> "vnd.android.cursor.item/vnd.lantian.nolitter.provider.main"
        else -> throw RuntimeException("[NoLitter] Content type can not be matched, with uri: ${uri.path}")
    }

    @ExperimentalSerializationApi
    override fun query(
        uri: Uri, projection: Array<String>?,
        selection: String?, selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor = when (uriMatcher.match(uri)) {
        URI_MAIN -> handleContentQuery(uri)
        else -> throw RuntimeException("[NoLitter] Content uri can not be matched, with uri: ${uri.path}")
    }

    // These methods are not used, but must be implemented.
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
}
