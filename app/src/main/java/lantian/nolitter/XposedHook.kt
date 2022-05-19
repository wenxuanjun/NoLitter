package lantian.nolitter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import lantian.nolitter.utilitiy.DataStoreUtil
import java.io.File
import java.net.URI

data class XposedPreference(
    val isForced: Boolean, val debugMode: Boolean,
    val redirectStyle: String
)

// The authority of the content resolver
const val AUTHORITY = "lantian.nolitter.provider"

class XposedHook : IXposedHookLoadPackage {

    private lateinit var applicationContext: Context
    private lateinit var xposedPreference: XposedPreference

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {

        val hookFileWithString: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (param.args[0] == null) return
                val oldPath = param.args[0].toString()
                val storageDir = getStorageDir(oldPath) ?: return
                val newPath = getReplacedPath(param.args[0].toString(), storageDir, lpparam.packageName)
                if (oldPath != newPath) {
                    param.args[0] = newPath
                    printDebugLogs(lpparam.packageName, "Redirecting", "$oldPath -> $newPath")
                }
            }
        }
        val hookFileWithStringAndString: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (param.args[1] == null) return
                if (param.args[0] == null) param.args[0] = ""
                val oldPath = (param.args[0].toString() + "/" + param.args[1].toString()).replace("//", "/")
                val storageDir = getStorageDir(oldPath) ?: return
                val newPath = getReplacedPath(oldPath, storageDir, lpparam.packageName)
                if (oldPath != newPath) {
                    param.args[0] = null
                    param.args[1] = newPath
                    printDebugLogs(lpparam.packageName, "Redirecting", "$oldPath -> $newPath")
                }
            }
        }
        val hookFileWithFileAndString: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (param.args[1] == null) return
                if (param.args[0] == null) param.args[0] = File("")
                val oldPath = ((param.args[0] as File).absolutePath + "/" + param.args[1].toString()).replace("//", "/")
                val storageDir = getStorageDir(oldPath) ?: return
                val newPath = getReplacedPath(oldPath, storageDir, lpparam.packageName)
                if (oldPath != newPath) {
                    param.args[0] = null
                    param.args[1] = newPath
                    printDebugLogs(lpparam.packageName, "Redirecting", "$oldPath -> $newPath")
                }
            }
        }
        val changeDirHook: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                if (param.result == null) return
                val oldDirPath = (param.result as File).absolutePath
                val storageDir = getStorageDir(oldDirPath) ?: return
                val newDirPath = File(getReplacedPath(oldDirPath, storageDir, lpparam.packageName))
                param.result = newDirPath
            }
        }
        val changeDirsHook: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                if (param.result == null) return
                val oldDirPaths = param.result as Array<*>
                val newDirPaths = ArrayList<File>()
                for (oldDirPath in oldDirPaths) {
                    if (oldDirPath == null) continue
                    val nonNullOldDirPath = (oldDirPath as File).absolutePath
                    val storageDir = getStorageDir(nonNullOldDirPath) ?: return
                    newDirPaths.add(File(getReplacedPath(nonNullOldDirPath, storageDir, lpparam.packageName)))
                }
                param.result = newDirPaths.toTypedArray()
            }
        }

        // Ignore if it's the NoLitter itself
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) return

        // Get the context of package hooked which is used in resolving content
        XposedHelpers.findAndHookMethod(Application::class.java, "attach", Context::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                // This hook may be called more than one time
                if (!this@XposedHook::applicationContext.isInitialized) {
                    applicationContext = param.args[0] as Context
                    xposedPreference = initPreferences(lpparam.packageName)
                    try {
                        XposedHelpers.findAndHookConstructor(File::class.java, String::class.java, hookFileWithString)
                        XposedHelpers.findAndHookConstructor(File::class.java, String::class.java, String::class.java, hookFileWithStringAndString)
                        XposedHelpers.findAndHookConstructor(File::class.java, File::class.java, String::class.java, hookFileWithFileAndString)
                        if (xposedPreference.isForced) {
                            printDebugLogs(lpparam.packageName, "Forced", "in force list")
                            XposedHelpers.findAndHookMethod(Environment::class.java, "getExternalStorageDirectory", changeDirHook)
                            XposedHelpers.findAndHookMethod(Environment::class.java, "getExternalStoragePublicDirectory", String::class.java, changeDirHook)
                            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getExternalFilesDir", String::class.java, changeDirHook)
                            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getExternalFilesDirs", String::class.java, changeDirsHook)
                        }
                    } catch (npe: NullPointerException) {
                        // Prevent too much log spawn in manager
                    }
                }
            }
        })
    }

    // Return the path after redirection
    @SuppressLint("SdCardPath")
    private fun getReplacedPath(oldPath: String, storageDir: String, packageName: String): String {

        // Where the basic redirect path should be
        val absoluteRedirectPath = when (xposedPreference.redirectStyle) {
            "rikka" -> "$storageDir/Android/data/$packageName/sdcard"
            "lantian" -> "$storageDir/Android/files/$packageName"
            else -> "$storageDir/Android/data/$packageName/sdcard"
        }

        // If it's just the root directory
        if (oldPath == storageDir || oldPath == "$storageDir/") {
            return if (xposedPreference.isForced) absoluteRedirectPath else storageDir
        }

        // Use the origin path if it's in the "Android" directory
        if (oldPath.startsWith("$storageDir/Android")) return oldPath

        // If it's in a directory that already exists in the root directory
        val relativePath = oldPath.substring(storageDir.length)
        val secondSlash = relativePath.indexOf("/", 1)
        val firstDirectoryPath = if (secondSlash == -1) relativePath else relativePath.substring(0, secondSlash)
        val fileExists = File(URI.create("file://$storageDir$firstDirectoryPath")).exists()
        return if (fileExists) oldPath else absoluteRedirectPath + relativePath
    }

    // Get the storage directory of the path, ignore if matches none of them (e.g. /data)
    private fun getStorageDir(oldPath: String): String? {
        for (storageDir in Constants.defaultStorageDirs.split(":")) {
            if (oldPath.startsWith(storageDir)) return storageDir
        }
        return null
    }

    private fun printDebugLogs(packageName: String, operation: String, info: String) {
        if (xposedPreference.debugMode) {
            XposedBridge.log("[NoLitter] $packageName: $operation, $info")
        }
    }

    private fun initPreferences(packageName: String) = XposedPreference(
        debugMode = getPreference("datastore", "debug_mode", false),
        redirectStyle = getPreference("datastore", "redirect_style", "lantian"),
        isForced = getPreference("datastore", "forced_apps", Constants.defaultForcedList).split(":").contains(packageName)
    )

    @SuppressLint("Recycle")
    private fun resolveContent(uriString: String, projection: Array<String>): Cursor {
        val resolver = applicationContext.contentResolver
        resolver.query(Uri.parse(uriString), projection, null, null, null)?.let { while (it.moveToNext()) { return it } }
        throw RuntimeException("Unable to fetch the content")
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getPreference(source: String, key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> resolveContent("content://$AUTHORITY/$source/string", arrayOf(key, defaultValue)).getString(0)
            is Boolean -> resolveContent("content://$AUTHORITY/$source/boolean", arrayOf(key, defaultValue.toString())).getString(0).toBoolean()
            else -> throw IllegalArgumentException("Wrong value provided with invalid value type")
        } as T
    }
}