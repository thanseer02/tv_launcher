package com.leantv.launcher.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.leantv.launcher.LeanTVApplication
import com.leantv.launcher.R
import com.leantv.launcher.data.model.AppModel
import com.leantv.launcher.databinding.ActivityMainBinding
import com.leantv.launcher.domain.usecase.GetInstalledAppsUseCase
import com.leantv.launcher.domain.usecase.ToggleFavoriteUseCase
import com.leantv.launcher.domain.usecase.ToggleHideAppUseCase
import com.leantv.launcher.presentation.drawer.AppDrawerActivity
import com.leantv.launcher.presentation.home.adapter.AppAdapter
import com.leantv.launcher.presentation.settings.SettingsActivity
import com.leantv.launcher.util.AppConstants
import com.leantv.launcher.util.DPadUtils
import com.leantv.launcher.util.FocusAnimationHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var favoritesAdapter: AppAdapter
    private lateinit var installedAdapter: AppAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        applyConfiguredTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDependencies()
        setupTopActions()
        setupRecyclerViews()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        // Refresh apps list upon returning from app or settings
        viewModel.loadApps()
    }

    private fun applyConfiguredTheme() {
        val prefs = LeanTVApplication.instance.preferencesDataSource
        when (prefs.getTheme()) {
            AppConstants.THEME_CHARCOAL -> setTheme(R.style.Theme_LeanTV_Charcoal)
            AppConstants.THEME_DEEP_NAVY -> setTheme(R.style.Theme_LeanTV_DeepNavy)
            else -> setTheme(R.style.Theme_LeanTV_Oled)
        }
    }

    private fun initDependencies() {
        val app = LeanTVApplication.instance
        val repository = app.appRepository
        viewModel = MainViewModel(
            appRepository = repository,
            getInstalledAppsUseCase = GetInstalledAppsUseCase(repository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
            toggleHideAppUseCase = ToggleHideAppUseCase(repository)
        )
    }

    private fun setupTopActions() {
        binding.btnAllApps.setOnFocusChangeListener { v, hasFocus ->
            FocusAnimationHelper.applyFocusScale(v, hasFocus)
        }
        binding.btnAllApps.setOnClickListener {
            startActivity(Intent(this, AppDrawerActivity::class.java))
        }

        binding.btnSettings.setOnFocusChangeListener { v, hasFocus ->
            FocusAnimationHelper.applyFocusScale(v, hasFocus)
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupRecyclerViews() {
        favoritesAdapter = AppAdapter(
            iconLoader = { app -> viewModel.loadIcon(app) },
            scope = lifecycleScope,
            onItemClick = { app -> DPadUtils.launchApp(this, app) },
            onItemLongClick = { app, _ -> showAppContextDialog(app) }
        )

        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = favoritesAdapter
            setHasFixedSize(true)
        }

        val columns = LeanTVApplication.instance.preferencesDataSource.getGridColumns()
        installedAdapter = AppAdapter(
            iconLoader = { app -> viewModel.loadIcon(app) },
            scope = lifecycleScope,
            onItemClick = { app -> DPadUtils.launchApp(this, app) },
            onItemLongClick = { app, _ -> showAppContextDialog(app) }
        )

        binding.rvInstalledApps.apply {
            layoutManager = GridLayoutManager(this@MainActivity, columns)
            adapter = installedAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is HomeUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                        }
                        is HomeUiState.Success -> {
                            binding.progressBar.visibility = View.GONE

                            // Update favorites row
                            if (state.favorites.isEmpty()) {
                                binding.tvFavoritesTitle.visibility = View.GONE
                                binding.rvFavorites.visibility = View.GONE
                            } else {
                                binding.tvFavoritesTitle.visibility = View.VISIBLE
                                binding.rvFavorites.visibility = View.VISIBLE
                                favoritesAdapter.submitList(state.favorites)
                            }

                            // Update installed apps
                            installedAdapter.submitList(state.installedApps)

                            val hasAnyApps = state.favorites.isNotEmpty() || state.installedApps.isNotEmpty()
                            binding.tvEmptyState.visibility = if (hasAnyApps) View.GONE else View.VISIBLE
                            binding.tvInstalledTitle.visibility = if (state.installedApps.isNotEmpty()) View.VISIBLE else View.GONE
                        }
                        is HomeUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun showAppContextDialog(app: AppModel) {
        val options = mutableListOf<String>()
        val actions = mutableListOf<() -> Unit>()

        // 1. Open
        options.add(getString(R.string.action_open))
        actions.add { DPadUtils.launchApp(this, app) }

        // 2. Favorite toggle
        if (app.isFavorite) {
            options.add(getString(R.string.action_remove_favorite))
            actions.add { viewModel.toggleFavorite(app) }
        } else {
            options.add(getString(R.string.action_add_favorite))
            actions.add { viewModel.toggleFavorite(app) }
        }

        // 3. Hide
        options.add(getString(R.string.action_hide_app))
        actions.add { viewModel.hideApp(app) }

        // 4. App Info
        options.add(getString(R.string.action_app_info))
        actions.add { DPadUtils.openAppInfo(this, app.packageName) }

        // 5. Uninstall
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

    /**
     * Intercept TV Remote Menu Key or Home Key to improve Android TV UX.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_MENU -> {
                val focusedView = currentFocus
                focusedView?.performLongClick()
                return true
            }
            KeyEvent.KEYCODE_BACK -> {
                // TV Home screen should not exit on Back press
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}

