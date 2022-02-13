package password1.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import password1.my.databinding.FragmentInstructionBinding
import password1.my.interface1.IToast

class Instruction_Fragment : Fragment(R.layout.fragment_instruction), IToast {

    private var _binding: FragmentInstructionBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //val view = inflater.inflate(R.layout.test_signin, container, false)
        _binding = FragmentInstructionBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        binding.btnBackToHome.setOnClickListener {
            mainActivity.supportFragmentManager.popBackStack()
        }

        return binding.root
    }

}