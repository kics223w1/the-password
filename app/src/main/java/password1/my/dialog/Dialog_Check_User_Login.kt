//package password1.my.dialog
//
//import android.app.AlertDialog
//import android.app.Dialog
//import android.os.Bundle
//import androidx.fragment.app.DialogFragment
//import password1.my.R
//
//
//class Dialog_Check_User_Login : DialogFragment() {
//
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return activity?.let {
//            val alertDialog = AlertDialog.Builder(it)
//            val view = requireActivity().layoutInflater.inflate(
//                R.layout.layout_dialog_check_user_login,
//                null
//            )
//            alertDialog.setView(view)
//
//
//
////            val btn_ok: Button = view.findViewById(R.id.btn_dialog_ok)
////            val btn_delete : Button = view.findViewById(R.id.btn_dialog_delete)
////            val text_user: EditText = view.findViewById(R.id.edit_text_item_web)
////            btn_ok.setOnClickListener {
////                if (text_user.text.toString().isEmpty()) return@setOnClickListener
////                val value = text_user.text.toString()
////
////                Toast.makeText(requireContext(), "Ok", Toast.LENGTH_SHORT).show()
////
////            }
////            btn_delete.setOnClickListener {
////                if (text_user.text.toString().isEmpty()) return@setOnClickListener
////                val value = text_user.text.toString()
////
////            }
//            alertDialog.create()
//        } ?: throw IllegalStateException("Activity is null")
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
