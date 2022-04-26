package lantian.nolitter

import android.os.Environment
import de.robv.android.xposed.*
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.File
import java.net.URI
import java.util.*

class XposedHook : IXposedHookZygoteInit, IXposedHookLoadPackage {

    private var prefs: XSharedPreferences? = null

    @Throws(Throwable::class)
    override fun initZygote(startupParam: StartupParam) {
        prefs = XSharedPreferences(BuildConfig.APPLICATION_ID)
        prefs!!.makeWorldReadable()
    }

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        val hookFileWithString: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                val oldPath = param.args[0].toString()
                val newPath = doReplace(param.args[0].toString(), lpparam.packageName)
                if (oldPath != newPath) {
                    param.args[0] = newPath
                    printDebugLogs(lpparam.packageName, "Redirecting", "$oldPath -> $newPath")
                }
            }
        }
        val hookFileWithStringAndString: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                val oldPath = (param.args[0].toString() + "/" + param.args[1].toString()).replace("//", "/")
                val newPath = doReplace(oldPath, lpparam.packageName)
                if (oldPath != newPath) {
                    param.args[0] = null
                    param.args[1] = newPath
                    printDebugLogs(lpparam.packageName, "Redirecting", "$oldPath -> $newPath")
                }
            }
        }
        val hookFileWithFileAndString: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                val oldPath = (param.args[0] as File).absolutePath + "/" + param.args[1].toString()
                val newPath = doReplace(oldPath, lpparam.packageName)
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
                val oldDirPath = (param.result as File).absolutePath
                val newDirPath = File(doReplace(oldDirPath, lpparam.packageName))
                param.result = newDirPath
            }
        }
        val changeDirsHook: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                val oldDirPaths = param.result as Array<File>
                val newDirPaths = ArrayList<File>()
                for (oldDirPath in oldDirPaths) {
                    newDirPaths.add(File(doReplace(oldDirPath.absolutePath, lpparam.packageName)))
                }
                param.result = newDirPaths.toTypedArray()
            }
        }

        // Reload the preference when loading the package
        prefs!!.reload()

        try {
            XposedHelpers.findAndHookConstructor(File::class.java, String::class.java, hookFileWithString)
            XposedHelpers.findAndHookConstructor(File::class.java, String::class.java, String::class.java, hookFileWithStringAndString)
            XposedHelpers.findAndHookConstructor(File::class.java, File::class.java, String::class.java, hookFileWithFileAndString)
            if (prefs!!.getString("forced", Constants.defaultForcedList)!!.split(",").contains(lpparam.packageName)) {
                printDebugLogs(lpparam.packageName, "Forced", "in forcelist")
                XposedHelpers.findAndHookMethod(Environment::class.java, "getExternalStorageDirectory", changeDirHook)
                XposedHelpers.findAndHookMethod(Environment::class.java, "getExternalStoragePublicDirectory", String::class.java, changeDirHook)
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getObbDir", changeDirHook)
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getObbDirs", changeDirsHook)
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getExternalFilesDir", String::class.java, changeDirHook)
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getExternalFilesDirs", String::class.java, changeDirsHook)
            }
        } catch (npe: NullPointerException) {
            // Prevent too much log spawn
        }
    }

    // Return the real path of redirection
    private fun doReplace(oldPath: String, packageName: String): String {
        val storageDirs = Constants.defaultStorageDirs.split(",")
        val redirectDir = prefs!!.getString("redirect_dir", Constants.defaultRedirectDir)
        val forceMode = prefs!!.getString("forced", Constants.defaultForcedList)!!.split(",").contains(packageName)
        val separateApp = prefs!!.getBoolean("separate_app", true)

        for (storageDir in storageDirs) {
            if (oldPath.startsWith(storageDir)) {
                var absoluteRedirectPath = storageDir + redirectDir
                if (separateApp) absoluteRedirectPath += "/$packageName"

                // If it's just the root directory
                if (oldPath == storageDir || oldPath == "$storageDir/") {
                    return if (forceMode) absoluteRedirectPath else storageDir
                }

                // Use the origin path if it's in the "Android" directory
                if (oldPath.startsWith("$storageDir/Android")) return oldPath

                // If it's in a directory that already exists in the root directory
                val relativePath = oldPath.substring(storageDir.length)
                val firstDirectoryPath = relativePath.substring(0, relativePath.indexOf("/", 1))
                val fileExists = File(URI.create(File(storageDir + firstDirectoryPath).toURI().toString()).normalize()).exists()

                return if (fileExists) oldPath else absoluteRedirectPath + relativePath
            }
        }
        return oldPath
    }

    private fun printDebugLogs(packageName: String, operation: String, info: String) {
        if (prefs!!.getBoolean("debug_mode", false)) {
            XposedBridge.log("[NoLitter] $packageName: $operation, $info")
        }
    }
}