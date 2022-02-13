package password.my

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import password1.my.MainActivity
import password1.my.R
import password1.my.databinding.FragmentSignInBinding
import password1.my.interface1.IToast


class Sign_in_Fragment : Fragment(R.layout.fragment_sign_in), IToast {


    private lateinit var mainActivity: MainActivity
    private  var auth: FirebaseAuth
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleSignInClient : GoogleSignInClient
    private val RC_SIGN_IN = 12

    init {
        auth = FirebaseAuth.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //val view = inflater.inflate(R.layout.test_signin, container, false)
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(mainActivity, gso)

        binding.btnSignIn.setOnClickListener {
            var account = binding.tvInputSignInAccount.text.toString()
            val password = binding.tvInputSignInPassword.text.toString()
            Hide_SoftKeyBoard()
            if (account.isEmpty()) {
                Show("Account can not be empty", mainActivity)
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Show("Password can not be empty", mainActivity)
                return@setOnClickListener
            }
            account += "@gmail.com"
            OnClickSignin(email = account, password = password)
        }

        binding.btnSignInGoogle.setOnClickListener {
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    !isOnline(mainActivity)
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")
                }
            ){
                Show("Pls , connect wifi to sign in" , mainActivity)
                return@setOnClickListener
            }
            signInGoogle()
        }

        binding.btnCreateAccount.setOnClickListener {
            Hide_SoftKeyBoard()
            replaceFrag(Sign_up_Fragment(), "Frag_sign_in")
        }


        return binding.root
    }



    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent , RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                firebaseAuthWithGoogle(account)
            }
            if (account == null){
                Show("Fail to login " , mainActivity)
            }
        }
    }


    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try{
                auth.signInWithCredential(credential).await()
                withContext(Dispatchers.Main){
                    Show("Google Login Success" , mainActivity)
                    mainActivity.supportFragmentManager.popBackStack()
                }
            } catch (e : Exception){
                withContext(Dispatchers.Main){
                    Show("Login fail" , mainActivity)
                }
            }
        }
    }


    private fun OnClickSignin(email: String, password: String) {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                !isOnline(mainActivity)
            } else {
                TODO("VERSION.SDK_INT < LOLLIPOP")
            }
        ){
            Show("No wifi" , mainActivity)
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    mainActivity.supportFragmentManager.popBackStack()
                } else {
                    // If sign in fails, display a message to the user.
                    Show("Password or account is wrong", mainActivity)
                }
            }
    }


    private fun replaceFrag(frag: Fragment, tag: String) {
        val fm = mainActivity.supportFragmentManager
        fm.beginTransaction().addToBackStack(tag).replace(R.id.content_frame, frag).commit()
    }

    private fun Hide_SoftKeyBoard() {
        val imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
    }


    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {

                    return true
                }
            }
        }
        return false
    }
}