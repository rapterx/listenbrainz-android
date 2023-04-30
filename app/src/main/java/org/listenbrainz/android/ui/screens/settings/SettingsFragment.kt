package org.listenbrainz.android.ui.screens.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import org.listenbrainz.android.application.App
import org.listenbrainz.android.R
import org.listenbrainz.android.repository.AppPreferences
import org.listenbrainz.android.ui.screens.dashboard.DashboardActivity
import org.listenbrainz.android.util.Constants.Strings.PREFERENCE_SYSTEM_THEME
import org.listenbrainz.android.util.Constants.Strings.PREFERENCE_LISTENING_ENABLED

class SettingsFragment(private val appPreferences: AppPreferences) : PreferenceFragmentCompat() {
    private var preferenceChangeListener: Preference.OnPreferenceChangeListener? = null

    fun setPreferenceChangeListener(preferenceChangeListener: Preference.OnPreferenceChangeListener?) {
        this.preferenceChangeListener = preferenceChangeListener
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference<Preference>(PREFERENCE_SYSTEM_THEME)!!.onPreferenceChangeListener = preferenceChangeListener
        if (!appPreferences.isNotificationServiceAllowed) {
            (findPreference<Preference>(PREFERENCE_LISTENING_ENABLED) as SwitchPreference?)!!.isChecked = false
            appPreferences.preferenceListeningEnabled = false
        }
        findPreference<Preference>(PREFERENCE_LISTENING_ENABLED)!!.onPreferenceChangeListener = preferenceChangeListener
    }

    override fun onResume() {
        super.onResume()
        if (!appPreferences.isNotificationServiceAllowed) {
            (findPreference<Preference>(PREFERENCE_LISTENING_ENABLED) as SwitchPreference?)!!.isChecked = false
            appPreferences.preferenceListeningEnabled = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(context, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                true
            }
            else -> false
        }
    }
}