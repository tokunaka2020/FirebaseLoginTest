package com.websarva.wings.android.firebaselogintest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var prmView: View
    private lateinit var progressDialog: OriginalProgressDialog

    // Activity作成時
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Googleログインボタン設定
        val googleSignInButton = findViewById<SignInButton>(R.id.sign_in_button)
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD)
        val listener = LoginListener()
        googleSignInButton.setOnClickListener(listener)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

    }

    // Activity開始時
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    // Activityに戻ってきたときの処理
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    // Firebase Google認証
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        // [START_EXCLUDE silent]
        showProgressDialog()
        // [END_EXCLUDE]

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(prmView, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // [START_EXCLUDE]
                hideProgressDialog()
                // [END_EXCLUDE]
            }
    }

    // ログインボタン押下時
    private inner class LoginListener : View.OnClickListener {
        override fun onClick(view: View) {
            prmView = view

            when(view.id){
                // Google サインイン
                R.id.sign_in_button -> {
                    loginGoogle()
                }
            }
        }
    }

    // ユーザー状態確認
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            if (user.isEmailVerified) {
                Toast.makeText(
                    this@MainActivity, "Login Success.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity, "Your Email is not verified.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(applicationContext,"user is null",Toast.LENGTH_SHORT).show()
        }
    }

    // Google サインイン
    fun loginGoogle(){
        // Googleサインインの構成
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // プログレスダイアログ表示
    fun showProgressDialog() {
        progressDialog = OriginalProgressDialog.newInstance(R.string.message_login)
        progressDialog.show(supportFragmentManager, TAG)
    }

    // プログレスダイアログ非表示（廃棄）
    fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    // 定数宣言
    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}
