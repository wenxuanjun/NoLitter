package lantian.nolitter.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PackagePreference::class],
    version = 1,
    exportSchema = false
)

abstract class MainDatabase : RoomDatabase() {
    abstract fun packagePreferenceDao(): PackagePreferenceDao
}