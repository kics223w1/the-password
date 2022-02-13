package password1.my

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import password.my.HomeFragment
import password.my.List_Fragment
import password.my.Sign_in_Fragment
import password1.my.interface1.IShowInfoUser
import password1.my.interface1.IToast


class MainActivity : AppCompatActivity() , IShowInfoUser , IToast {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private val RC_SIGN_IN = 100
    private lateinit var googleSignInClient : GoogleSignInClient

    private var auth: FirebaseAuth
    var mCurrentFragment = Frag_Home

    companion object {
        var Frag_Home = 0
        var Frag_List = 1
        var Frag_sign_in = 2
    }

    init {
        auth = Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)



        drawerLayout = findViewById(R.id.drawer_layout)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        navView = findViewById(R.id.navigation_view)
        val loadimage =
            registerForActivityResult(ActivityResultContracts.GetContent(), {
                val image_user: ImageView = navView.getHeaderView(0)
                    .findViewById(R.id.imageview_navi_avatar_user)
                if (it != null){
                    image_user.setImageURI(it)
                    UpdateProlifeUser(it)
                }
            })
        val btn_upload_avatar: ImageView = navView.getHeaderView(0)
            .findViewById(R.id.imageview_navi_update_avatar)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout,
            toolbar, R.string.navi_open_drawer, R.string.navi_close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ShowInfoUser(navView , this)

        btn_upload_avatar.setOnClickListener {
            loadimage.launch("image/*")
        }

        navView.setNavigationItemSelectedListener {
            Hide_SoftKeyBoard()
            when (it.itemId) {
                R.id.navi_home -> Check_HomeFragment()
                R.id.navi_list -> Check_FavortieFragment()
                R.id.navi_sign_in -> Check_Sign_in()
                R.id.navi_sign_out -> Check_Sign_out()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        replaceFrag(HomeFragment(), "Frag_home")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun GoogleFireBase(){
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent , RC_SIGN_IN)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
//            account?.let {
//                firebaseAuthWithGoogle(account)
//            }
//            if (account == null){
//                Show("Fail to login " , this)
//            }
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//        try{
//            auth.signInWithCredential(credential)
//            Show("Login Success" , this)
//            this.supportFragmentManager.popBackStack()
//        } catch (e : Exception){
//            Show("Login fail " + e.toString() , this)
//        }
//    }

//    private fun Google(){
//        //GoogleFireBase()
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("825797835498-hn2n4ks86j3beuk6u4hl1u1ap7tpqfki.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//        val signInIntent = mGoogleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
                Show("Vao request 16" , this)
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            Show("Thanh cong ne " , this)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("tas", "signInResult:failed code=" + e.statusCode)
            Show("That bai roi " + e.statusCode + "   " + e.toString() , this)
        }
    }

    private fun Check_Sign_in() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            Show("You already sign in" , this)
            return
        }
        mCurrentFragment = Frag_sign_in
        replaceFrag(Sign_in_Fragment(), "Frag_sign_in")
    }

//    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//        CoroutineScope(Dispatchers.IO).launch {
//            try{
//                auth.signInWithCredential(credential).await()
//                withContext(Dispatchers.Main){
//                    Log.d("tas" , "Login success")
//                    supportFragmentManager.popBackStack()
//                }
//            } catch (e : Exception){
//                withContext(Dispatchers.Main){
//                    Log.d("tas" , "Login fail")
//                }
//            }
//        }
//    }

    private fun Check_Sign_out(){
        val user = Firebase.auth.currentUser
        if (user != null) {
            Firebase.auth.signOut()
            googleSignInClient.signOut()
            ShowInfoUser(navView , this)
        }
    }

    private fun Check_HomeFragment() {
        if (mCurrentFragment != Frag_Home) {
            mCurrentFragment = Frag_Home
            replaceFrag(HomeFragment(), "Frag_home")
        }
    }

    private fun Check_FavortieFragment() {
        if (mCurrentFragment != Frag_List) {
            replaceFrag(List_Fragment(), "Frag_list")
            mCurrentFragment = Frag_List
        }
    }

    private fun UpdateProlifeUser(uri_AvatarUser: Uri) {
        val user = Firebase.auth.currentUser
        if (user == null) {
            return
        }
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri_AvatarUser
        }
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Show("Update Prolife success" , this)
                }
            }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount == 1) {
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(a)
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun replaceFrag(frag: Fragment, tag: String) {
        val fm = supportFragmentManager
        fm.beginTransaction().addToBackStack(tag).replace(R.id.content_frame, frag).commit()
    }

    fun Activity.Hide_SoftKeyBoard() {
        Hide_SoftKeyBoard(currentFocus ?: View(this))
    }


    private fun Context.Hide_SoftKeyBoard(view : View) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }
}