package password1.my

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class FlashActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash)

        val handler = Handler()
        val run = Runnable {
            val intent = Intent(this , MainActivity::class.java)
            startActivity(intent)
        }
        handler.postDelayed(run , 2000)
    }


}