package lantian.nolitter.database

import androidx.room.*

@Dao
interface ApplicationDao {

    @Query("SELECT * FROM ApplicationEntity WHERE package_name = :packageName")
    fun queryApplication(packageName: String): List<ApplicationEntity>

    @Insert
    fun insertApplication(application: ApplicationEntity)

    @Update
    fun updateApplication(application: ApplicationEntity)

    @Delete
    fun deleteApplication(application: ApplicationEntity)
}