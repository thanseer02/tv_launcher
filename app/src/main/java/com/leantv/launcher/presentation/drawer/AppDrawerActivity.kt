package com.leantv.launcher.presentation.drawer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.leantv.launcher.LeanTVApplication
import com.leantv.launcher.R
import com.leantv.launcher.data.model.AppModel
import com.leantv.launcher.databinding.ActivityAppDrawerBinding
import com.leantv.launcher.domain.usecase.GetInstalledAppsUseCase
import com.leantv.launcher.domain.usecase.ToggleFavoriteUseCase
import com.leantv.launcher.domain.usecase.ToggleHideAppUseCase
import com.leantv.launcher.presentation.home.adapter.AppAdapter
import com.leantv.launcher.util.AppConstants
import com.leantv.launcher.util.DPadUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppDrawerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppDrawerBinding
    private lateinit var appAdapter: AppAdapter
    private var allAppsList: List<AppModel> = emptyList()

    private val app = LeanTVApplication.instance
    private val repository = app.appRepository
    private val getInstalledAppsUseCase = GetInstalledAppsUseCase(repository)
    private val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
    private val toggleHideAppUseCase = ToggleHideAppUseCase(repository)

    override fun onCreate(savedInstanceState: Bundle?) {
        applyConfiguredTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityAppDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        loadAllApps()
    }

    private fun applyConfiguredTheme() {
        when (app.preferencesDataSource.getTheme()) {
            AppConstants.THEME_CHARCOAL -> setTheme(R.style.Theme_LeanTV_Charcoal)
            AppConstants.THEME_DEEP_NAVY -> setTheme(R.style.Theme_LeanTV_DeepNavy)
            else -> setTheme(R.style.Theme_LeanTV_Oled)
        }
    }

    private fun setupRecyclerView() {
        val columns = app.preferencesDataSource.getGridColumns()
        appAdapter = AppAdapter(
            iconLoader = { app -> repository.loadAppIcon(app) },
            scope = lifecycleScope,
            onItemClick = { app -> DPadUtils.launchApp(this, app) },
            onItemLongClick = { app, _ -> showAppContextDialog(app) }
        )

        binding.rvAllApps.apply {
            layoutManager = GridLayoutManager(this@AppDrawerActivity, columns)
            adapter = appAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterApps(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadAllApps() {
        binding.drawerProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val apps = getInstalledAppsUseCase(includeHidden = false)
            allAppsList = apps
            withContext(Dispatchers.Main) {
                binding.drawerProgressBar.visibility = View.GONE
                filterApps(binding.etSearch.text.toString())
            }
        }
    }

    private fun filterApps(query: String) {
        val trimmed = query.trim().lowercase()
        val filtered = if (trimmed.isEmpty()) {
            allAppsList
        } else {
            allAppsList.filter {
                it.label.lowercase().contains(trimmed) || it.packageName.lowercase().contains(trimmed)
            }
        }
        appAdapter.submitList(filtered)
    }

    private fun showAppContextDialog(app: AppModel) {
        val options = mutableListOf<String>()
        val actions = mutableListOf<() -> Unit>()

        options.add(getString(R.string.action_open))
        actions.add { DPadUtils.launchApp(this, app) }

        if (app.isFavorite) {
            options.add(getString(R.string.action_remove_favorite))
            actions.add {
                lifecycleScope.launch {
                    toggleFavoriteUseCase(app.componentKey)
                    loadAllApps()
                }
            }
        } else {
            options.add(getString(R.string.action_add_favorite))
            actions.add {
                lifecycleScope.launch {
                    toggleFavoriteUseCase(app.componentKey)
                    loadAllApps()
                }
            }
        }

        options.add(getString(R.string.action_hide_app))
        actions.add {
            lifecycleScope.launch {
                toggleHideAppUseCase(app.componentKey, isHidden = true)
                loadAllApps()
            }
        }

        options.add(getString(R.string.action_app_info))
        actions.add { DPadUtils.openAppInfo(this, app.packageName) }

        options.add(getString(R.string.action_uninstall))
        actions.add { DPadUtils.requestUninstall(this, app.packageName) }

        AlertDialog.Builder(this)
            .setTitle(app.label)
            .setItems(options.toTypedArray()) { dialog, which ->
                actions[which].invoke()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            currentFocus?.performLongClick()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

