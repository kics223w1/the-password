package password1.my.interface1

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import password1.my.MainActivity
import password1.my.R

interface IShowInfoUser {
    fun ShowInfoUser(navView : NavigationView , mainActivity : MainActivity){
        val user = Firebase.auth.currentUser
        val tv_email_name: TextView = navView.getHeaderView(0).findViewById(R.id.tv_user_email)
        val image_user: ImageView =
            navView.getHeaderView(0).findViewById(R.id.imageview_navi_avatar_user)
        val animation = AnimationUtils.loadAnimation(mainActivity, R.anim.roate_forever)
        image_user.startAnimation(animation)
        tv_email_name.visibility = View.VISIBLE
        val email = user?.email
        val photoUrl = user?.photoUrl
        if(email != null){
            tv_email_name.setText(email)
        }else{
            tv_email_name.visibility = View.GONE
        }
        if (photoUrl != null){
            Glide.with(mainActivity).load(photoUrl).error(R.drawable.avatar_default)
                .into(image_user)
        }else{
            Glide.with(mainActivity).load(
                AppCompatResources.getDrawable(
                    mainActivity, R.drawable.avatar_default
                )
            ).into(image_user)
        }
    }
}