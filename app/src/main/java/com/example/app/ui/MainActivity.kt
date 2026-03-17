package com.example.app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.app.R
import com.example.app.data.SpotifyConfig
import com.google.android.material.appbar.MaterialToolbar
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfig: AppBarConfiguration
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfig = AppBarConfiguration(navController.graph)

        val appBar: MaterialToolbar = findViewById(R.id.top_app_bar)
        setSupportActionBar(appBar)
        setupActionBarWithNavController(navController, appBarConfig)

        // Check for manual auth code from RedirectActivity
        val code = intent.getStringExtra("auth_code")
        if (code != null) {
            Log.d("SpotifyAuth", "MainActivity received manual auth code!")
            viewModel.onAuthCodeReceived(code)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val code = intent.getStringExtra("auth_code")
        if (code != null) {
            Log.d("SpotifyAuth", "MainActivity received manual auth code in onNewIntent!")
            viewModel.onAuthCodeReceived(code)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != SpotifyConfig.REQUEST_CODE || data == null) {
            return
        }

        when (val response = AuthorizationClient.getResponse(resultCode, data)) {
            null -> Log.w("SpotifyAuth", "Spotify auth returned a null response.")
            else -> when (response.type) {
                AuthorizationResponse.Type.CODE -> {
                    val code = response.code
                    if (!code.isNullOrBlank()) {
                        Log.d("SpotifyAuth", "MainActivity received auth code from Spotify SDK.")
                        viewModel.onAuthCodeReceived(code)
                    } else {
                        Log.e("SpotifyAuth", "Spotify auth returned CODE without a code value.")
                    }
                }
                AuthorizationResponse.Type.TOKEN -> {
                    Log.d("SpotifyAuth", "MainActivity received access token from Spotify SDK.")
                    viewModel.onAuthTokenReceived(response.accessToken)
                }
                AuthorizationResponse.Type.ERROR -> {
                    Log.e("SpotifyAuth", "Spotify auth error: ${response.error}")
                }
                else -> {
                    Log.w("SpotifyAuth", "Spotify auth finished with response type ${response.type}.")
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }
}
