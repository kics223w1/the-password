package password1.my.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import password1.my.data.UserDatabase
import password1.my.model.Web
import password1.my.repository.WebRepository

class WebViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WebRepository

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = WebRepository(userDao)

    }

    fun list(check : Int)  = runBlocking {
        val one :Deferred<LiveData<List<Web>>>  = async { getLive() }
        val two : Deferred<MutableList<Web>> = async { getList() }
        if (check == 1){
            Log.d("tas" , one.toString())
        }else{
            Log.d("tas" , two.toString())
        }
    }

    suspend fun getLive() :LiveData<List<Web>>{
        val list = repository.readLiveData()
        return list
    }

    fun getList() : MutableList<Web>{
        val list = repository.readListData()
        return list
    }

    fun checkItemExsit(name : String) : Boolean = runBlocking {
        var listData = mutableListOf<Web>()
        val job =viewModelScope.launch(Dispatchers.IO) {
            listData = repository.checkItemExsit_web_table(name)
        }
        job.join()
        return@runBlocking listData.size > 0
    }

    fun readListData() : MutableList<Web> = runBlocking{
        var listData = mutableListOf<Web>()
        val job =viewModelScope.launch(Dispatchers.IO) {
            listData = repository.readListData()
        }
        job.join()
        return@runBlocking listData
    }

    fun readLiveData() : LiveData<List<Web>> = runBlocking {
        var livedata : LiveData<List<Web>> = MutableLiveData<List<Web>>()
        val job = viewModelScope.launch(Dispatchers.IO) {
           livedata = repository.readLiveData()
        }
        job.join()
        return@runBlocking livedata
    }


    fun addWeb(web: Web) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWeb(web)
        }
    }

    fun updateWeb(web: Web) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWeb(web)
        }
    }

    fun deleteWeb(web: Web) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWeb(web)
        }
    }

}