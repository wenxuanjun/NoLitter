package lantian.nolitter.xposed

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import app.softwork.serialization.csv.CSVFormat
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import lantian.nolitter.Constants
import java.io.File
import java.net.URI

@Serializable
data class XposedPreference(
    val forcedMode: Boolean, val allowPublicDirs: Boolean,
    val additionalHooks: Boolean, val redirectStyle: String, val debugMode: Boolean
)

// The authority of the content resolver
const val AUTHORITY = "lantian.nolitter.provider"

class XposedHook : IXposedHookLoadPackage {

    // The context of the application that hooked
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
                val newDirPath = getReplacedPath(oldDirPath, storageDir, lpparam.packageName)
                if (oldDirPath != newDirPath) {
                    param.result = File(newDirPath)
                    printDebugLogs(lpparam.packageName, "Redirecting", "$oldDirPath -> $newDirPath")
                }
            }
        }

        // Ignore if it's the NoLitter itself
        if (lpparam.packageName == "lantian.nolitter") return

        // Get the context of package hooked which is used in resolving content
        XposedHelpers.findAndHookMethod(Application::class.java, "attach", Context::class.java, object : XC_MethodHook() {
            @ExperimentalSerializationApi
            override fun beforeHookedMethod(param: MethodHookParam) {
                // This hook may be called more than one time
                if (!this@XposedHook::applicationContext.isInitialized) {
                    if (param.args[0] == null) return
                    applicationContext = param.args[0] as Context
                    xposedPreference = initPreferences(lpparam.packageName)
                    try {
                        XposedHelpers.findAndHookConstructor(File::class.java, String::class.java, hookFileWithString)
                        XposedHelpers.findAndHookConstructor(File::class.java, String::class.java, String::class.java, hookFileWithStringAndString)
                        XposedHelpers.findAndHookConstructor(File::class.java, File::class.java, String::class.java, hookFileWithFileAndString)
                        if (xposedPreference.additionalHooks) {
                            XposedHelpers.findAndHookMethod(Environment::class.java, "getExternalStorageDirectory", changeDirHook)
                            XposedHelpers.findAndHookMethod(Environment::class.java, "getExternalStoragePublicDirectory", String::class.java, changeDirHook)
                        }
                    } catch (npe: NullPointerException) {
                        // Prevent too much log spawn in manager
                    }
                }
            }
        })
    }

    @SuppressLint("SdCardPath")
    private fun getReplacedPath(oldPath: String, storageDir: String, packageName: String): String {

        // Where the basic redirect path should be
        val absoluteRedirectPath = when (xposedPreference.redirectStyle) {
            "data" -> "$storageDir/Android/data/$packageName/sdcard"
            "cache" -> "$storageDir/Android/data/$packageName/cache/sdcard"
            "external" -> "$storageDir/Android/files/$packageName"
            else -> throw IllegalArgumentException("Invalid redirect style")
        }

        // If it's just the root directory
        if (oldPath == storageDir || oldPath == "$storageDir/") {
            return if (xposedPreference.forcedMode) absoluteRedirectPath else storageDir
        }

        // Ignore if it's already redirected
        if (oldPath.startsWith(absoluteRedirectPath)) return oldPath

        // The relative path in the storage directory
        val relativePath = oldPath.substring(storageDir.length)

        // Force mode: Redirect whether or not the directory exists
        if (xposedPreference.forcedMode) return absoluteRedirectPath + relativePath

        // If it's in the public directory
        for (publicDir in Constants.androidPublicDirs.split(":")) {
            if (relativePath.startsWith(publicDir))
                return if (xposedPreference.allowPublicDirs) oldPath else absoluteRedirectPath + relativePath
        }

        // Normal mode: Ignore if the first level directory exists in the root directory already
        val secondSlash = relativePath.indexOf("/", 1)
        val firstDirectoryPath = if (secondSlash == -1) relativePath else relativePath.substring(0, secondSlash)
        val fileExists = File(URI.create("file://$storageDir$firstDirectoryPath")).exists()
        return if (fileExists) oldPath else absoluteRedirectPath + relativePath
    }

    // Get the storage directory of the path, ignore if matches none of them (e.g. /data)
    private fun getStorageDir(oldPath: String): String? {
        for (storageDir in Constants.defaultStorageDirs.split(":"))
            if (oldPath.startsWith(storageDir)) return storageDir
        return null
    }

    private fun printDebugLogs(packageName: String, operation: String, info: String) {
        if (xposedPreference.debugMode) {
            XposedBridge.log("[NoLitter] $packageName: $operation, $info")
        }
    }

    @ExperimentalSerializationApi
    private fun initPreferences(packageName: String): XposedPreference {
        val xposedPreference = resolveContent("content://$AUTHORITY/main/${packageName}").getString(0)
        printDebugLogs(packageName, "Initializing", "Preferences fetched: $xposedPreference")
        return CSVFormat.decodeFromString(XposedPreference.serializer(), xposedPreference)
    }

    @SuppressLint("Recycle")
    private fun resolveContent(uriString: String): Cursor {
        val resolver = applicationContext.contentResolver
        val contentCursor = resolver.query(Uri.parse(uriString), null, null, null, null)
        Log.d("NoLitter", "Cursor: ${contentCursor?.count}, ${contentCursor?.columnNames?.contentToString()}")
        contentCursor?.let { while (it.moveToNext()) {
            Log.d("NoLitter", "Cursor: ${it.getString(0)}, ${it.getString(1)}, ${it.getString(2)}")
            return it
        } }
        throw RuntimeException("Unable to fetch the content, url: $uriString")
    }
}