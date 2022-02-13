//package password1.my.dialog
//
//import android.app.AlertDialog
//import android.app.Dialog
//import android.content.DialogInterface
//import android.os.Bundle
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.DialogFragment
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//import password1.my.R
//
//
//class Dialog_Add_ItemWeb : DialogFragment() {
//
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return activity?.let {
//            val alertDialog = AlertDialog.Builder(it)
//            val view = requireActivity().layoutInflater.inflate(
//                R.layout.layout_dialog_add_web,
//                null
//            )
//            alertDialog.setView(view)
//
//            var a : AlertDialog = alertDialog.create()
//
//            val user = Firebase.auth.currentUser
//            val title = view.findViewById<TextView>(R.id.dialog_tv_title)
//            val btn_add = view.findViewById<Button>(R.id.dialog_btn_add_web)
//            val btn_cancel = view.findViewById<Button>(R.id.dialog_btn_cancel)
//
//            if(user != null){
//                title.text = getString(R.string.store_in_sever)
//            }else{
//                title.text = getString(R.string.store_in_phone)
//            }
//
//            btn_cancel.setOnClickListener {
//                Toast.makeText(requireContext() ,  "Vao ne" , Toast.LENGTH_SHORT).show()
//
//            }
//
//
//            alertDialog.show()
//        } ?: throw IllegalStateException("Activity is null")
//
//
//
//    }
//
//
//
//    private fun getList(){
//
//    }
//
//
//
//
//}
//
