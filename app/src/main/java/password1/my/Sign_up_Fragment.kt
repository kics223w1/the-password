package password.my


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import password1.my.MainActivity
import password1.my.R
import password1.my.databinding.FragmentSignUpBinding
import password1.my.interface1.IToast


class Sign_up_Fragment : Fragment(R.layout.fragment_sign_up_), IToast {


    private lateinit var mainActivity: MainActivity
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_sign_up_, container, false)
//        val btnSignUp = view.findViewById<Button>(R.id.btn_sign_up)

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        mainActivity = activity as MainActivity
        auth = Firebase.auth


        binding.btnSignUp.setOnClickListener {
            var account = binding.tvInputSignUpAccount.text.toString()
            val password = binding.tvInputSignUpPassword.text.toString()
            Hide_SoftKeyBoard()
            val check = mutableListOf('.', '#', '$', '[', ']' , '@')
            for(c in check){
                if(account.contains(c)){
                    Show(getString(R.string.check_account) + "$c" , mainActivity)
                    return@setOnClickListener
                }
            }
            if (password.length < 6) {
                Show(getString(R.string.check_password), mainActivity)
                return@setOnClickListener
            }
            account += "@gmail.com"
            OnClick_SignUp(account, password)
        }

        binding.btnBackSignUp.setOnClickListener {
            mainActivity.supportFragmentManager.popBackStack()
        }


        return binding.root
    }


    private fun OnClick_SignUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Show("Sign Up Success", mainActivity)
                    mainActivity.supportFragmentManager.popBackStack()
                } else {
                    Show("Account is already have", mainActivity)
                }
            }
    }

    private fun Hide_SoftKeyBoard() {
        val imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
    }

}


