package lantian.nolitter;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedHook implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private XSharedPreferences prefs;

    // From http://stackoverflow.com/questions/4571346/how-to-encode-url-to-avoid-special-characters-in-java
    /*private static String urlEncode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch) {
        return ch > 128 || " %$&+,:;=?@<>#%".indexOf(ch) >= 0;
    }*/

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        prefs = new XSharedPreferences("lantian.nolitter");
        prefs.makeWorldReadable();
        if (prefs.getString("banned", "").isEmpty()) {
            XposedBridge.log("[NoLitter] Failed to load config, running with defaults");
        } else {
            XposedBridge.log("[NoLitter] Config loaded");
        }
    }

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XC_MethodHook noLitterStr = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args[0].toString().startsWith("/lantian")) return;
                String path = param.args[0].toString();
                if(path.startsWith("/data/")) return;
                if(path.startsWith("/system/")) return;
                if(path.startsWith("/cache/")) return;
                if (path.startsWith("/proc/")) return;
                if (path.startsWith("/sys/")) return;
                if (path.startsWith("/vendor/")) return;
                String newPath;
                if (prefs.getBoolean("separate_app", false)) {
                    newPath = doReplace(path, lpparam.packageName, Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                } else {
                    newPath = doReplace(path, "", Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                }
                if (!path.equals(newPath)) {
                    param.args[0] = newPath;
                    //XposedBridge.log("[NoLitter] " + lpparam.packageName + ": Redirecting " + path + " -> " + newPath);
                }
            }
        };
        XC_MethodHook noLitterStrStr = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args[0] == null || param.args[0].toString().isEmpty()) {
                    String path = param.args[1].toString();
                    if (path.startsWith("/data/")) return;
                    if (path.startsWith("/system/")) return;
                    if (path.startsWith("/cache/")) return;
                    if (path.startsWith("/proc/")) return;
                    if (path.startsWith("/sys/")) return;
                    if (path.startsWith("/vendor/")) return;
                    String newPath;
                    if (prefs.getBoolean("separate_app", false)) {
                        newPath = doReplace(path, lpparam.packageName, Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                    } else {
                        newPath = doReplace(path, "", Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                    }
                    if (!path.equals(newPath)) {
                        param.args[1] = newPath;
                        //XposedBridge.log("[NoLitter] " + lpparam.packageName + ": Redirecting " + path + " -> " + newPath);
                    }
                } else {
                    String path = param.args[0].toString() + "/" + param.args[1].toString();
                    path = path.replace("//", "/");
                    if (path.startsWith("/data/")) return;
                    if (path.startsWith("/system/")) return;
                    if (path.startsWith("/cache/")) return;
                    if (path.startsWith("/proc/")) return;
                    if (path.startsWith("/sys/")) return;
                    if (path.startsWith("/vendor/")) return;
                    String newPath;
                    if (prefs.getBoolean("separate_app", false)) {
                        newPath = doReplace(path, lpparam.packageName, Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                    } else {
                        newPath = doReplace(path, "", Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                    }
                    if (!path.equals(newPath)) {
                        param.args[0] = null;
                        param.args[1] = newPath;
                        //XposedBridge.log("[NoLitter] " + lpparam.packageName + ": Redirecting " + path + " -> " + newPath);
                    }
                }
            }
        };
        XC_MethodHook noLitterFileStr = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args[0] == null || ((File) param.args[0]).getAbsolutePath().isEmpty()) {
                    String path = param.args[1].toString();
                    if (path.startsWith("/data/")) return;
                    if (path.startsWith("/system/")) return;
                    if (path.startsWith("/cache/")) return;
                    if (path.startsWith("/proc/")) return;
                    if (path.startsWith("/sys/")) return;
                    if (path.startsWith("/vendor/")) return;
                    String newPath;
                    if (prefs.getBoolean("separate_app", false)) {
                        newPath = doReplace(path, lpparam.packageName, Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                    } else {
                        newPath = doReplace(path, "", Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                    }
                    if (!path.equals(newPath)) {
                        param.args[1] = newPath;
                        //XposedBridge.log("[NoLitter] " + lpparam.packageName + ": Redirecting " + path + " -> " + newPath);
                    }
                } else {
                    String path = ((File) param.args[0]).getAbsolutePath() + "/" + param.args[1].toString();
                    path = path.replace("//", "/");
                    if (path.startsWith("/data/")) return;
                    if (path.startsWith("/system/")) return;
                    if (path.startsWith("/cache/")) return;
                    if (path.startsWith("/proc/")) return;
                    if (path.startsWith("/sys/")) return;
                    if (path.startsWith("/vendor/")) return;
                    String newPath;
                    if (prefs.getBoolean("separate_app", false)) {
                        newPath = doReplace(path, lpparam.packageName, Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                    } else {
                        newPath = doReplace(path, "", Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                    }
                    if (!path.equals(newPath)) {
                        param.args[0] = null;
                        param.args[1] = newPath;
                        //XposedBridge.log("[NoLitter] " + lpparam.packageName + ": Redirecting " + path + " -> " + newPath);
                    }
                }
            }
        };
        // XInternalSD Hook
        XC_MethodHook changeDirHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                File oldFile = (File) param.getResult();
                if (oldFile == null) return;
                String oldDir = oldFile.getAbsolutePath() + "/";
                String newDir;
                if (prefs.getBoolean("separate_app", false)) {
                    newDir = doReplace(oldDir, lpparam.packageName, Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                } else {
                    newDir = doReplace(oldDir, "", Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                }
                File newDirPath = new File(newDir);
                param.setResult(newDirPath);
            }
        };
        XC_MethodHook changeDirsHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // https://github.com/pylerSM/XInternalSD/issues/15
                File[] oldDirPaths = (File[]) param.getResult();
                ArrayList<File> newDirPaths = new ArrayList<>();

                for (int i = 0; i < oldDirPaths.length; ++i) {
                    String oldDir = oldDirPaths[i].getPath() + "/";
                    String newDir;
                    if (prefs.getBoolean("separate_app", false)) {
                        newDir = doReplace(oldDir, lpparam.packageName, Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                    } else {
                        newDir = doReplace(oldDir, "", Arrays.asList(prefs.getString("forced", Constants.forced).split(",")).contains(lpparam.packageName));
                    }

                    File newDirPath = new File(newDir);
                    newDirPaths.add(newDirPath);
                }

                File[] appendedDirPaths = newDirPaths.toArray(new File[newDirPaths.size()]);
                param.setResult(appendedDirPaths);
            }
        };
        prefs.reload();
        ArrayList<String> banned = new ArrayList<>(Arrays.asList(prefs.getString("banned", Constants.banned).split(",")));
        if (!(lpparam.packageName.equals("lantian.nolitter") || banned.contains(lpparam.packageName))) {
            if (prefs.getBoolean("enable_system", false)) {
                // User allows to hook system apps
                XposedHelpers.findAndHookConstructor(File.class, String.class, noLitterStr);
                XposedHelpers.findAndHookConstructor(File.class, String.class, String.class, noLitterStrStr);
                XposedHelpers.findAndHookConstructor(File.class, File.class, String.class, noLitterFileStr);
                if (Arrays.asList(prefs.getString("chsdpath", Constants.chsdpath).split(",")).contains(lpparam.packageName)) {
                    // Copied from XInternalSD
                    XposedHelpers.findAndHookMethod(Environment.class,
                            "getExternalStorageDirectory", changeDirHook);
                    XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                            "android.app.ContextImpl", lpparam.classLoader),
                            "getExternalFilesDir", String.class, changeDirHook);
                    XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                            "android.app.ContextImpl", lpparam.classLoader), "getObbDir",
                            changeDirHook);
                    XposedHelpers.findAndHookMethod(Environment.class,
                            "getExternalStoragePublicDirectory", String.class,
                            changeDirHook);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                                "android.app.ContextImpl", lpparam.classLoader),
                                "getExternalFilesDirs", String.class,
                                changeDirsHook);
                        XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                                "android.app.ContextImpl", lpparam.classLoader),
                                "getObbDirs", changeDirsHook);
                    }
                }
            } else {
                // User don't want to hook system apps
                if ((lpparam.appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    // Not system app
                    XposedHelpers.findAndHookConstructor(File.class, String.class, noLitterStr);
                    XposedHelpers.findAndHookConstructor(File.class, String.class, String.class, noLitterStrStr);
                    XposedHelpers.findAndHookConstructor(File.class, File.class, String.class, noLitterFileStr);
                    if (Arrays.asList(prefs.getString("chsdpath", Constants.chsdpath).split(",")).contains(lpparam.packageName)) {
                        // Copied from XInternalSD
                        XposedHelpers.findAndHookMethod(Environment.class,
                                "getExternalStorageDirectory", changeDirHook);
                        XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                                "android.app.ContextImpl", lpparam.classLoader),
                                "getExternalFilesDir", String.class, changeDirHook);
                        XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                                "android.app.ContextImpl", lpparam.classLoader), "getObbDir",
                                changeDirHook);
                        XposedHelpers.findAndHookMethod(Environment.class,
                                "getExternalStoragePublicDirectory", String.class,
                                changeDirHook);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                                    "android.app.ContextImpl", lpparam.classLoader),
                                    "getExternalFilesDirs", String.class,
                                    changeDirsHook);
                            XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                                    "android.app.ContextImpl", lpparam.classLoader),
                                    "getObbDirs", changeDirsHook);
                        }
                    }
                }
            }
        }
    }

    private String doReplace(String path, String pkgName, Boolean forceMode) {
        String storageDir;
        for (String storagePath : prefs.getString("sdcard", Constants.sdcard).split("\n")) {
            if(storagePath.isEmpty()) continue;
            if (storagePath.trim().endsWith("/")) {
                storageDir = storagePath.trim().substring(0, storagePath.length() - 1);
            } else {
                storageDir = storagePath.trim();
            }
            if (path.startsWith(storageDir)) {
                // Check if is root dir itself
                if (path.equals(storageDir + "/") || path.equals(storageDir)) {
                    if (forceMode) {
                        if (pkgName.isEmpty()) {
                            return storageDir + "/Android/files/";
                        } else {
                            return storageDir + "/Android/files/" + pkgName + "/";
                        }
                    } else {
                        return storageDir;
                    }
                }
                if(path.startsWith(storageDir + "/Android")) return path;
                // Split out path after storage dir
                String newPath = path.substring(storageDir.length() + 1, path.length());

                Boolean fExists;
                if (forceMode) {
                    // Extra check: if folder present in isolate folder, then force redirect
                    File fInside;
                    if (pkgName.isEmpty()) {
                        fInside = new File(storageDir + "/Android/files/" + newPath.split("/")[0]);
                    } else {
                        fInside = new File(storageDir + "/Android/files/" + pkgName + "/" + newPath.split("/")[0]);
                    }
                    if (fInside.exists()) {
                        fExists = false;
                    } else {
                        // File to URI: Create a mock file, get its URI, and replace the mock part
                        File fPath = new File("/lantian" + storageDir + "/" + newPath.split("/")[0]);
                        fExists = new File(URI.create(fPath.toURI().toString().replaceFirst("/lantian", "")).normalize()).exists();
                    }
                } else {
                    // File to URI: Create a mock file, get its URI, and replace the mock part
                    File fPath = new File("/lantian" + storageDir + "/" + newPath.split("/")[0]);
                    fExists = new File(URI.create(fPath.toURI().toString().replaceFirst("/lantian", "")).normalize()).exists();
                }
                if (fExists) {
                    return path;
                } else if (pkgName.isEmpty()) {
                    return storageDir + "/Android/files/" + newPath;
                } else {
                    return storageDir + "/Android/files/" + pkgName + "/" + newPath;
                }
            }
        }
        return path;
    }
}