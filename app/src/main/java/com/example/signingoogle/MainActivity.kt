package com.example.signingoogle

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.signingoogle.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var options: GoogleSignInOptions
    private lateinit var client: GoogleSignInClient

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        options = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestServerAuthCode(resources.getString(R.string.server_client_id))
            .requestEmail()
            .requestIdToken(resources.getString(R.string.server_client_id))
            .build()

        client = GoogleSignIn.getClient(this,options)

        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPreferences.edit()

        binding.putBtn.setOnClickListener {
            val member =  3
            editor.putInt("member",8).apply()
        }

        binding.showBtn.setOnClickListener {
            Toast.makeText(this,sharedPreferences.getInt("member",3).toString(),Toast.LENGTH_LONG).show()
        }


        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account!=null){
            val name = account.displayName
            val email = account.email
            val token = account.idToken
            binding.mainTv.text = "$name \n $email \n $token"
            binding.signOutButton.setOnClickListener {
                signOut()
            }
        }
        if(account==null){
            signOut()
        }


        setContentView(binding.root)
    }

    private fun signOut() {
        client.signOut().addOnCompleteListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}