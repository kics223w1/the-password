package password1.my.interface1

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import password1.my.R

interface IReplaceFrag {
    fun replaceFrag(frag: Fragment, tag: String , fm : FragmentManager) {
        fm.beginTransaction().addToBackStack(tag).replace(R.id.content_frame, frag).commit()
    }
}