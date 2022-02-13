package password1.my.interface1

import android.content.Context
import android.widget.Toast

interface IToast {
    fun Show(str : String , context : Context){
        Toast.makeText(context , str , Toast.LENGTH_SHORT).show()
    }
}