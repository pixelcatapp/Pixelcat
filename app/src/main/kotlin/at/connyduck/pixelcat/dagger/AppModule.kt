package at.connyduck.pixelcat.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import at.connyduck.pixelcat.PixelcatApplication
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun providesApp(app: PixelcatApplication): Application  = app

    @Provides
    fun providesContext(app: Application): Context = app

    @Provides
    fun providesSharedPreferences(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Provides
    @Singleton
    fun providesDatabase(app: PixelcatApplication): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, "pixelcat.db")
            .build()
    }

    @Provides
    @Singleton
    fun providesAccountManager(db: AppDatabase): AccountManager {
        return AccountManager(db)
    }




}