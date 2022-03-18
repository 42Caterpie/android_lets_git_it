package com.github.caterpie.letsgitit.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.github.caterpie.letsgitit.base.BaseActivity
import com.github.caterpie.letsgitit.data.UserRepository
import com.github.caterpie.letsgitit.data.local.UserPreferencesDataStore
import com.github.caterpie.letsgitit.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

	private val userViewModel by viewModels<UserViewModel> {
		UserViewModelFactory(UserRepository(
			UserPreferencesDataStore
		))
	}

	override fun onCreate(savedInstanceState: Bundle?) {

		val splashScreen = installSplashScreen()

		super.onCreate(savedInstanceState)

		userViewModel.isUserLogin.observe(this) { isUserLogin ->
			if (savedInstanceState == null) {
//				if (isUserLogin) {
					replace<MainFragment>(viewBinding.fragmentContainerView.id)
//				} else {
//					replace<LoginFragment>(viewBinding.fragmentContainerView.id)
//				}
			}
		}


	}

}

private inline fun <reified fragment: Fragment> AppCompatActivity.replace(containerId: Int) {
	supportFragmentManager.commit {
		replace<fragment>(containerId)
		setReorderingAllowed(true)
	}
}