package lantian.nolitter

import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    private var prefs: SharedPreferences? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.settings, rootKey)
        prefs = context?.getSharedPreferences(BuildConfig.APPLICATION_ID + "_preferences", Context.MODE_WORLD_READABLE)

        findPreference<Preference>("forced_ui")?.setOnPreferenceClickListener {
            chooseApp("forced", Constants.forced)
            false
        }
        findPreference<Preference>("author")?.setOnPreferenceClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lantian.pub"))
            startActivity(browserIntent)
            false
        }
        findPreference<Preference>("source")?.setOnPreferenceClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/xddxdd/lantian-nolitter"))
            startActivity(browserIntent)
            false
        }
        findPreference<Preference>("about_xinternalsd")?.setOnPreferenceClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pylerSM/XInternalSD"))
            startActivity(browserIntent)
            false
        }
        findPreference<Preference>("show_icon")?.setOnPreferenceChangeListener { _, newValue ->
            requireActivity().packageManager.setComponentEnabledSetting(
                ComponentName(requireActivity(), MainActivity::class.java),
                if(newValue as Boolean) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
            true
        }
    }

    private fun chooseApp(key: String?, defaults: String?): Boolean {
        // Get app info
        val appInfo: MutableList<ApplicationInfo>
        if (prefs!!.getBoolean("enable_system", false)) {
            appInfo = requireActivity().packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        } else {
            appInfo = ArrayList()
            for (appInfoSingle in requireActivity().packageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {
                if (appInfoSingle.flags and ApplicationInfo.FLAG_SYSTEM == 0)
                    appInfo.add(appInfoSingle)
            }
        }
        Collections.sort(appInfo, ApplicationInfo.DisplayNameComparator(requireActivity().packageManager))

        // Get current settings
        val currentIgnored = ArrayList(listOf(*prefs!!.getString(key, defaults)!!.split(",".toRegex()).toTypedArray()))

        // Form arrays
        val appTitles = arrayOfNulls<String>(appInfo.size)
        val appCared = BooleanArray(appInfo.size)
        for (index in appInfo.indices) {
            appTitles[index] = appInfo[index].loadLabel(requireActivity().packageManager).toString()
            appCared[index] = currentIgnored.contains(appInfo[index].packageName)
        }

        // Make the dialog
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(R.string.ui_chooseApp)
        alertDialog.setMultiChoiceItems(appTitles,appCared) { _, which: Int, isChecked: Boolean ->
            val ignored = ArrayList(listOf(*prefs!!.getString(key, defaults)!!.split(",".toRegex()).toTypedArray()))
            if (isChecked) {
                ignored.add(appInfo[which].packageName)
            } else {
                ignored.remove(appInfo[which].packageName)
            }
            var newIgnored = ""
            for (ignoredFragment in ignored) {
                if (ignoredFragment.trim { it <= ' ' }.isNotEmpty()) newIgnored += ignoredFragment.trim { it <= ' ' } + ","
            }
            prefs!!.edit().putString(key, newIgnored).apply()
        }
        alertDialog.setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _ -> dialog.dismiss() }
        alertDialog.show()
        return false
    }
}