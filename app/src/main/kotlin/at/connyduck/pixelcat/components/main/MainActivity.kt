package at.connyduck.pixelcat.components.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.compose.ComposeActivity
import at.connyduck.pixelcat.components.general.BaseActivity
import at.connyduck.pixelcat.dagger.ViewModelFactory
import at.connyduck.pixelcat.databinding.ActivityMainBinding
import at.connyduck.pixelcat.db.AccountManager
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.ImageQuality
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            return@OnNavigationItemSelectedListener when (item.itemId) {
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
                        .setImageQuality(ImageQuality.HIGH)
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

        binding.container.setOnApplyWindowInsetsListener { _, insets ->
            val top = insets.systemWindowInsetTop

            val viewPagerParams = binding.mainViewPager.layoutParams as LinearLayout.LayoutParams
            viewPagerParams.topMargin = top

            insets.consumeSystemWindowInsets()
        }

        mainFragmentAdapter = MainFragmentAdapter(this)
        binding.mainViewPager.adapter = mainFragmentAdapter
        binding.mainViewPager.isUserInputEnabled = false

        binding.navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

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
}
