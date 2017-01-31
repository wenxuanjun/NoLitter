package lantian.nolitter;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
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
    }

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XC_MethodHook noLitter = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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
        prefs.reload();
        ArrayList<String> banned = new ArrayList<>(Arrays.asList(prefs.getString("banned", "pl.solidexplorer2,com.mixplorer,com.cyanogenmod.filemanager,nextapp.fx,pl.mkexplorer.kormateusz,com.lonelycatgames.Xplore,bin.mt,com.estrongs.android.pop,com.speedsoftware.rootexplorer,bin.mt.plus").split(",")));
        if (!(lpparam.packageName.equals("lantian.nolitter") || banned.contains(lpparam.packageName))) {
            XposedHelpers.findAndHookConstructor(File.class, String.class, noLitter);
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
                File f = new File(URI.create("file://" + urlEncode(storageDir + "/" + newPath.split("/")[0])));
                if(f.exists()) {
                    return path;
                } else {
                    return storageDir + "/Android/files/" + newPath;
                }
            }
        }
        return path;
    }
}