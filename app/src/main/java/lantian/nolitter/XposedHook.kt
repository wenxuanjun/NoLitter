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
        val noLitterStr: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                val path = param.args[0].toString()
                val newPath = getNewPath(path, lpparam.packageName)
                if (path != newPath) {
                    param.args[0] = newPath
                    printDebugLogs(lpparam.packageName, "Redirecting", "$path -> $newPath")
                }
            }
        }
        val noLitterStrStr: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (param.args[0] == null || param.args[0].toString().isEmpty()) {
                    val path = param.args[1].toString()
                    val newPath = getNewPath(path, lpparam.packageName)
                    if (path != newPath) {
                        param.args[1] = newPath
                        printDebugLogs(lpparam.packageName, "Redirecting", "$path -> $newPath")
                    }
                } else {
                    var path = param.args[0].toString() + "/" + param.args[1].toString()
                    path = path.replace("//", "/")
                    val newPath = getNewPath(path, lpparam.packageName)
                    if (path != newPath) {
                        param.args[0] = null
                        param.args[1] = newPath
                        printDebugLogs(lpparam.packageName, "Redirecting", "$path -> $newPath")
                    }
                }
            }
        }
        val noLitterFileStr: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (param.args[0] == null || (param.args[0] as File).absolutePath.isEmpty()) {
                    val path = param.args[1].toString()
                    val newPath = getNewPath(path, lpparam.packageName)
                    if (path != newPath) {
                        param.args[1] = newPath
                        printDebugLogs(lpparam.packageName, "Redirecting", "$path -> $newPath")
                    }
                } else {
                    var path = (param.args[0] as File).absolutePath + "/" + param.args[1].toString()
                    path = path.replace("//", "/")
                    val newPath = getNewPath(path, lpparam.packageName)
                    if (path != newPath) {
                        param.args[0] = null
                        param.args[1] = newPath
                        printDebugLogs(lpparam.packageName, "Redirecting", "$path -> $newPath")
                    }
                }
            }
        }
        // XInternalSD Hook
        val changeDirHook: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                val oldFile = param.result as File
                val oldDir = oldFile.absolutePath + "/"
                val newDir = getNewPath(oldDir, lpparam.packageName)
                val newDirPath = File(newDir)
                param.result = newDirPath
            }
        }
        val changeDirsHook: XC_MethodHook = object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                // https://github.com/pylerSM/XInternalSD/issues/15
                val oldDirPaths = param.result as Array<File>
                val newDirPaths = ArrayList<File>()
                for (oldFile in oldDirPaths) {
                    val oldDir = oldFile.path + "/"
                    val newDir = getNewPath(oldDir, lpparam.packageName)
                    val newDirPath = File(newDir)
                    newDirPaths.add(newDirPath)
                }
                val appendedDirPaths = newDirPaths.toTypedArray()
                param.result = appendedDirPaths
            }
        }

        try {
            XposedHelpers.findAndHookConstructor(File::class.java, String::class.java, noLitterStr)
            XposedHelpers.findAndHookConstructor(File::class.java, String::class.java, String::class.java, noLitterStrStr)
            XposedHelpers.findAndHookConstructor(File::class.java, File::class.java, String::class.java, noLitterFileStr)
            if (isForceMode(lpparam.packageName)) {
                printDebugLogs(lpparam.packageName, "Forced", "in forcelist")
                XposedHelpers.findAndHookMethod(Environment::class.java, "getExternalStorageDirectory", changeDirHook)
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getExternalFilesDir", String::class.java, changeDirHook)
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getObbDir", changeDirHook)
                XposedHelpers.findAndHookMethod(Environment::class.java, "getExternalStoragePublicDirectory", String::class.java, changeDirHook)
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getExternalFilesDirs", String::class.java, changeDirsHook)
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader), "getObbDirs", changeDirsHook)
            }
        } catch (npe: NullPointerException) {
            // Prevent too much log spawn
        }
    }

    // Return the real path of redirection
    private fun doReplace(path: String, pkgName: String, forceMode: Boolean): String {
        var storageDir: String
        for (storagePath in prefs!!.getString("sdcard", Constants.sdcard)!!.split("\n".toRegex()).toTypedArray()) {
            if (storagePath.isEmpty()) continue
            storageDir = if (storagePath.trim { it <= ' ' }.endsWith("/")) storagePath.trim { it <= ' ' }.substring(0, storagePath.length - 1) else storagePath.trim { it <= ' ' }
            if (path.startsWith(storageDir)) {
                // Check if is root dir itself
                if (path == "$storageDir/" || path == storageDir) {
                    return if (forceMode) if (pkgName.isEmpty()) "$storageDir/Android/files/" else "$storageDir/Android/files/$pkgName/" else storageDir
                }
                if (path.startsWith("$storageDir/Android")) return path
                val newPath = path.substring(storageDir.length + 1)
                val filePath = File("/lantian" + storageDir + "/" + newPath.split("/".toRegex()).toTypedArray()[0])
                val fileExists = File(URI.create(filePath.toURI().toString().replaceFirst("/lantian".toRegex(), "")).normalize()).exists()
                return when {
                    fileExists -> path
                    pkgName.isEmpty() -> "$storageDir/Android/files/$newPath"
                    else -> "$storageDir/Android/files/$pkgName/$newPath"
                }
            }
        }
        return path
    }

    private fun getNewPath(oldPath: String, packageName: String): String {
        return if (prefs!!.getBoolean("separate_app", true)) doReplace(oldPath, packageName, isForceMode(packageName)) else doReplace(oldPath, "", isForceMode(packageName))
    }

    private fun isForceMode(packageName: String): Boolean {
        return listOf(*prefs!!.getString("forced", Constants.forced)!!.split(",".toRegex()).toTypedArray()).contains(packageName)
    }

    private fun printDebugLogs(packageName: String, operation: String, info: String) {
        if (prefs!!.getBoolean("debug_mode", false)) {
            XposedBridge.log("[NoLitter] $packageName: $operation, $info")
        }
    }
}