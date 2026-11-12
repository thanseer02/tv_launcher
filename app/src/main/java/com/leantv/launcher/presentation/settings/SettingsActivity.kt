package com.leantv.launcher.presentation.settings

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.leantv.launcher.LeanTVApplication
import com.leantv.launcher.R
import com.leantv.launcher.databinding.ActivitySettingsBinding
import com.leantv.launcher.util.AppConstants
import com.leantv.launcher.util.DPadUtils
import com.leantv.launcher.util.FocusAnimationHelper
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val app = LeanTVApplication.instance
    private val prefs = app.preferencesDataSource
    private val repository = app.appRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        applyConfiguredTheme()
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindSettings()
    }

    private fun applyConfiguredTheme() {
        when (prefs.getTheme()) {
            AppConstants.THEME_CHARCOAL -> setTheme(R.style.Theme_LeanTV_Charcoal)
            AppConstants.THEME_DEEP_NAVY -> setTheme(R.style.Theme_LeanTV_DeepNavy)
            else -> setTheme(R.style.Theme_LeanTV_Oled)
        }
    }

    private fun bindSettings() {
        // Grid Columns Setting
        binding.settingGridColumns.tvSettingTitle.text = getString(R.string.pref_grid_columns)
        updateGridColumnsSummary()
        binding.settingGridColumns.root.apply {
            setOnFocusChangeListener { v, hasFocus -> FocusAnimationHelper.applyFocusScale(v, hasFocus) }
            setOnClickListener { showGridColumnsDialog() }
        }

        // Theme Setting
        binding.settingTheme.tvSettingTitle.text = getString(R.string.pref_theme)
        updateThemeSummary()
        binding.settingTheme.root.apply {
            setOnFocusChangeListener { v, hasFocus -> FocusAnimationHelper.applyFocusScale(v, hasFocus) }
            setOnClickListener { showThemeDialog() }
        }

        // Sort Order Setting
        binding.settingSortOrder.tvSettingTitle.text = getString(R.string.sort_order)
        updateSortOrderSummary()
        binding.settingSortOrder.root.apply {
            setOnFocusChangeListener { v, hasFocus -> FocusAnimationHelper.applyFocusScale(v, hasFocus) }
            setOnClickListener { showSortOrderDialog() }
        }

        // Hidden Apps Setting
        binding.settingHiddenApps.tvSettingTitle.text = getString(R.string.pref_hidden_apps)
        updateHiddenAppsSummary()
        binding.settingHiddenApps.root.apply {
            setOnFocusChangeListener { v, hasFocus -> FocusAnimationHelper.applyFocusScale(v, hasFocus) }
            setOnClickListener { showUnhideAppsDialog() }
        }

        // Default Launcher Setting
        binding.settingDefaultHome.tvSettingTitle.text = getString(R.string.pref_default_launcher)
        binding.settingDefaultHome.tvSettingSummary.text = getString(R.string.pref_default_launcher_summary)
        binding.settingDefaultHome.root.apply {
            setOnFocusChangeListener { v, hasFocus -> FocusAnimationHelper.applyFocusScale(v, hasFocus) }
            setOnClickListener { DPadUtils.openDefaultHomeSettings(this@SettingsActivity) }
        }

        // About
        binding.settingAbout.tvSettingTitle.text = getString(R.string.pref_about)
        binding.settingAbout.tvSettingSummary.text = getString(R.string.pref_about_summary)
        binding.settingAbout.root.apply {
            setOnFocusChangeListener { v, hasFocus -> FocusAnimationHelper.applyFocusScale(v, hasFocus) }
        }
    }

    private fun updateGridColumnsSummary() {
        binding.settingGridColumns.tvSettingSummary.text =
            "${prefs.getGridColumns()} columns (Recommended for 1080p/4K TV screens)"
    }

    private fun updateThemeSummary() {
        val themeName = when (prefs.getTheme()) {
            AppConstants.THEME_CHARCOAL -> "Charcoal Dark"
            AppConstants.THEME_DEEP_NAVY -> "Deep Navy"
            else -> "OLED Pure Black"
        }
        binding.settingTheme.tvSettingSummary.text = themeName
    }

    private fun updateSortOrderSummary() {
        val sortName = when (prefs.getSortOrder()) {
            AppConstants.SORT_ZA -> getString(R.string.sort_za)
            AppConstants.SORT_RECENT -> getString(R.string.sort_recent)
            else -> getString(R.string.sort_az)
        }
        binding.settingSortOrder.tvSettingSummary.text = sortName
    }

    private fun updateHiddenAppsSummary() {
        val hiddenCount = prefs.getHiddenPackageNames().size
        binding.settingHiddenApps.tvSettingSummary.text = "$hiddenCount app(s) hidden"
    }

    private fun showGridColumnsDialog() {
        val options = arrayOf("4 Columns", "5 Columns (Standard)", "6 Columns (Compact)")
        val columnValues = intArrayOf(4, 5, 6)
        val current = prefs.getGridColumns()
        val checkedItem = columnValues.indexOf(current).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.pref_grid_columns)
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->
                prefs.setGridColumns(columnValues[which])
                updateGridColumnsSummary()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showThemeDialog() {
        val options = arrayOf("OLED Pure Black (Best for OLED/VA panels)", "Charcoal Dark", "Deep Navy")
        val themeValues = arrayOf(AppConstants.THEME_OLED, AppConstants.THEME_CHARCOAL, AppConstants.THEME_DEEP_NAVY)
        val current = prefs.getTheme()
        val checkedItem = themeValues.indexOf(current).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.pref_theme)
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->
                prefs.setTheme(themeValues[which])
                updateThemeSummary()
                dialog.dismiss()
                recreate() // Apply new theme
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showSortOrderDialog() {
        val options = arrayOf(
            getString(R.string.sort_az),
            getString(R.string.sort_za),
            getString(R.string.sort_recent)
        )
        val sortValues = arrayOf(AppConstants.SORT_AZ, AppConstants.SORT_ZA, AppConstants.SORT_RECENT)
        val current = prefs.getSortOrder()
        val checkedItem = sortValues.indexOf(current).coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(R.string.sort_order)
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->
                prefs.setSortOrder(sortValues[which])
                updateSortOrderSummary()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showUnhideAppsDialog() {
        lifecycleScope.launch {
            val allApps = repository.getInstalledApps(includeHidden = true)
            val hiddenApps = allApps.filter { it.isHidden }

            if (hiddenApps.isEmpty()) {
                AlertDialog.Builder(this@SettingsActivity)
                    .setTitle(R.string.pref_hidden_apps)
                    .setMessage(R.string.hidden_apps_empty)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
                return@launch
            }

            val appLabels = hiddenApps.map { it.label }.toTypedArray()
            AlertDialog.Builder(this@SettingsActivity)
                .setTitle("Select app to unhide")
                .setItems(appLabels) { dialog, which ->
                    val selected = hiddenApps[which]
                    lifecycleScope.launch {
                        repository.setAppHidden(selected.componentKey, false)
                        updateHiddenAppsSummary()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }
}

