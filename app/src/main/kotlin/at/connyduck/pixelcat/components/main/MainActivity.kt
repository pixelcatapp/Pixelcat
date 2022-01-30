/*
 * Copyright (C) 2020 Conny Duck
 *
 * This file is part of Pixelcat.
 *
 * Pixelcat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pixelcat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.connyduck.pixelcat.components.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.compose.ComposeActivity
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.ActivityMainBinding
import at.connyduck.pixelcat.db.AccountManager
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.android.material.navigation.NavigationBarView
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var accountManager: AccountManager

    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityMainBinding

    private lateinit var mainFragmentAdapter: MainFragmentAdapter

    private val onNavigationItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            return@OnItemSelectedListener when (item.itemId) {
                R.id.navigation_home -> {
                    binding.mainViewPager.setCurrentItem(0, false)
                    true
                }
                R.id.navigation_search -> {
                    binding.mainViewPager.setCurrentItem(1, false)
                    true
                }
                R.id.navigation_compose -> {
                    val options = Options.init()
                        .setRequestCode(100)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)

                    Pix.start(this, options)
                    false
                }
                R.id.navigation_notifications -> {
                    binding.mainViewPager.setCurrentItem(2, false)
                    true
                }
                R.id.navigation_profile -> {
                    binding.mainViewPager.setCurrentItem(3, false)
                    true
                }
                else -> false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.container) { _, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            val viewPagerParams = binding.mainViewPager.layoutParams as ViewGroup.MarginLayoutParams
            viewPagerParams.topMargin = top
            WindowInsetsCompat.CONSUMED
        }

        mainFragmentAdapter = MainFragmentAdapter(this)
        binding.mainViewPager.adapter = mainFragmentAdapter
        binding.mainViewPager.isUserInputEnabled = false

        binding.navigation.setOnItemSelectedListener(onNavigationItemSelectedListener)

        mainViewModel.whatever()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            val returnValue =
                data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)

            startActivity(ComposeActivity.newIntent(this, returnValue?.firstOrNull()!!))
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}
