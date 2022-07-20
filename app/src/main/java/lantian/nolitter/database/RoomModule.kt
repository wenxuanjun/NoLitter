package lantian.nolitter.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule{
    @Singleton
    @Provides
    fun provideMainDatabase(@ApplicationContext context : Context) =
        Room.databaseBuilder(context, MainDatabase::class.java, "database").build()

    @Provides
    fun providePackagePreferenceDao(database: MainDatabase): PackagePreferenceDao {
        return database.packagePreferenceDao()
    }
}