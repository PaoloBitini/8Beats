package com.example.a8beats

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.commit

class ActivityMain : AppCompatActivity(), FragmentLogin.FragmentLoginListener, FragmentRegister.FragmentRegisterListener {
    private val reqPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions ->
        permissions.entries.forEach {
            Log.e("DEBUG_PERMISSIONS", "${it.key} = ${it.value}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        reqPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET))

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragment_container_main, FragmentLogin())
            }
        }
    }

    override fun onRegisterButtonPressed() {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment_container_main, FragmentRegister())
            }
        }

    override fun onBackToLoginButtonPressed() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_main, FragmentLogin())
        }
    }

    override fun onBackPressed() {
    }
}