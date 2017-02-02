package lantian.nolitter;

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
    private static String urlEncode(String input) {
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
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        prefs = new XSharedPreferences("lantian.nolitter");
        prefs.makeWorldReadable();
        String banned = prefs.getString("banned", "");
        if (banned.isEmpty()) {
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
                String newPath = doReplace(path);
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
                    String newPath = doReplace(path);
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
                    String newPath = doReplace(path);
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
                    String newPath = doReplace(path);
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
                    String newPath = doReplace(path);
                    if (!path.equals(newPath)) {
                        param.args[0] = null;
                        param.args[1] = newPath;
                        //XposedBridge.log("[NoLitter] " + lpparam.packageName + ": Redirecting " + path + " -> " + newPath);
                    }
                }
            }
        };
        prefs.reload();
        ArrayList<String> banned = new ArrayList<>(Arrays.asList(prefs.getString("banned", "pl.solidexplorer2,com.mixplorer,com.cyanogenmod.filemanager,nextapp.fx,pl.mkexplorer.kormateusz,com.lonelycatgames.Xplore,bin.mt,com.estrongs.android.pop,com.speedsoftware.rootexplorer,bin.mt.plus").split(",")));
        if (!(lpparam.packageName.equals("lantian.nolitter") || banned.contains(lpparam.packageName))) {
            XposedHelpers.findAndHookConstructor(File.class, String.class, noLitterStr);
            XposedHelpers.findAndHookConstructor(File.class, String.class, String.class, noLitterStrStr);
            XposedHelpers.findAndHookConstructor(File.class, File.class, String.class, noLitterFileStr);
        }
    }

    private String doReplace(String path) {
        String storageDir;
        for (String storagePath : prefs.getString("sdcard", "/storage/emulated/0\n/sdcard\n/storage/sdcard0").split("\n")) {
            if(storagePath.isEmpty()) continue;
            if(storagePath.endsWith("/")) {
                storageDir = storagePath.substring(0, storagePath.length() - 1);
            } else {
                storageDir = storagePath;
            }
            if(path.startsWith(storageDir + "/")) {
                // Check if is root dir itself
                if(path.equals(storageDir + "/")) return path;
                if(path.startsWith(storageDir + "/Android")) return path;
                // Split out path after storage dir
                String newPath = path.substring(storageDir.length() + 1, path.length());

                // File to URI: Create a mock file, get its URI, and replace the mock part
                File fPath = new File("/lantian" + storageDir + "/" + newPath.split("/")[0]);
                File fURI = new File(URI.create(fPath.toURI().toString().replace("file:/lantian/", "file:/")));
                //XposedBridge.log(fPath.toURI().toString());

                // Old method: does not support Chinese
                //File fURI = new File(URI.create("file://" + urlEncode(storageDir + "/" + newPath.split("/")[0])));
                if (fURI.exists()) {
                    return path;
                } else {
                    return storageDir + "/Android/files/" + newPath;
                }
            }
        }
        return path;
    }
}