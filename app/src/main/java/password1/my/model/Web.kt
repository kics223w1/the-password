package password1.my.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "web_table")
data class Web(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String
)