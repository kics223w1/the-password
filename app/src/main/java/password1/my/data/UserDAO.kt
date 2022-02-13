package password1.my.data

import androidx.lifecycle.LiveData
import androidx.room.*
import password1.my.model.Web


@Dao
interface UserDAO {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addWeb(web : Web)

    @Update
    fun updateWeb(web : Web)

    @Delete
    fun deleteWeb(web : Web)

    @Query("SELECT * FROM web_table ORDER BY id ASC")
    fun readLiveDataWeb(): LiveData<List<Web>>

    @Query("SELECT * FROM web_table")
    fun readListDataWeb(): MutableList<Web>

    @Query("SELECT * FROM web_table WHERE name = :name")
    fun checkItemExsit_web_table(name : String) : MutableList<Web>
}