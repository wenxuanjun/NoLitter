package lantian.nolitter.database

import androidx.room.*

@Dao
interface PackagePreferenceDao {

    @Query("SELECT * FROM package_preference WHERE package_name = :packageName")
    suspend fun queryPackage(packageName: String): PackagePreference?

    @Insert
    suspend fun insertPackage(installedPackage: PackagePreference)

    @Update
    suspend fun updatePackage(installedPackage: PackagePreference)

    @Delete
    suspend fun deletePackage(installedPackage: PackagePreference)
}