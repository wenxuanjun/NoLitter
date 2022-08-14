package lantian.nolitter.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import lantian.nolitter.database.MainDatabase
import lantian.nolitter.database.PackagePreferenceDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Database{
    @Provides
    @Singleton
    fun provideMainDatabase(@ApplicationContext context: Context): MainDatabase =
        Room.databaseBuilder(context, MainDatabase::class.java, "database").build()

    @Provides
    fun providePackagePreferenceDao(database: MainDatabase): PackagePreferenceDao {
        return database.packagePreferenceDao()
    }
}