package lantian.nolitter.databases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM ApplicationEntity")
    fun getAll(): List<ApplicationEntity>

    @Insert
    fun insertAll(vararg application: ApplicationEntity)

    @Delete
    fun delete(application: ApplicationEntity)
}