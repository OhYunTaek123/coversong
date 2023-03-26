package com.example.coversong.main

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import oss.data.REF
import oss.util.SignInIntentContract
import android.content.SharedPreferences

import android.app.Activity




class LoginActivity : AppCompatActivity() {

    private var launcher: ActivityResultLauncher<String>? = null
    private var auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser



        /*todo 스플래시로 이동*/
        //로그인 내역 확인
        if (currentUser != null) {
            val intent = Intent(this, BoardActivity::class.java)
            intent.putExtra(REF.USER.name, auth.currentUser)
            startActivity(intent)
            finish()
        }

        launcher = registerForActivityResult(SignInIntentContract()) { result: String? ->
            result?.let {
                firebaseAuthWithGoogle(it)
            }
        }
        // 구글  로그인 버튼
        findViewById<Button>(R.id.main_google_button).setOnClickListener {
            launcher!!.launch(getString(R.string.default_web_client_id))
        }

        // 익명 버튼
        findViewById<Button>(R.id.main_pass_button).setOnClickListener {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInAnonymously:success")
                        val intent = Intent(this, BoardActivity::class.java)
                        intent.putExtra(REF.USER.name, auth.currentUser)
                        startActivity(intent)
                    } else {
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent = Intent(this, BoardActivity::class.java)
                    intent.putExtra(REF.USER.name, user)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@LoginActivity, R.string.signin_fail, Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

}