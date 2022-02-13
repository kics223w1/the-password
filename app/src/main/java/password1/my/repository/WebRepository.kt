package password1.my.repository

import androidx.lifecycle.LiveData
import password1.my.data.UserDAO
import password1.my.model.Web

class WebRepository(private val userDao : UserDAO) {




    fun readLiveData() : LiveData<List<Web>> {
        return userDao.readLiveDataWeb()
    }

    fun readListData() : MutableList<Web>{
        return userDao.readListDataWeb()
    }

    fun checkItemExsit_web_table(name : String) : MutableList<Web>{
        return userDao.checkItemExsit_web_table(name)
    }

    fun addWeb(web : Web){
        userDao.addWeb(web)
    }

    fun updateWeb(web : Web){
        userDao.updateWeb(web)
    }

    fun deleteWeb(web : Web){
        userDao.deleteWeb(web)
    }
}