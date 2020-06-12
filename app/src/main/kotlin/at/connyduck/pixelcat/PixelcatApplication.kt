package at.connyduck.pixelcat

import androidx.appcompat.app.AppCompatDelegate
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import at.connyduck.pixelcat.components.settings.AppSettings
import at.connyduck.pixelcat.dagger.DaggerAppComponent
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import okhttp3.OkHttpClient
import javax.inject.Inject

class PixelcatApplication : DaggerApplication() {

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var okhttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

        AppCompatDelegate.setDefaultNightMode(appSettings.getNightMode())

        EmojiCompat.init(BundledEmojiCompatConfig(this))
    }

    override fun applicationInjector(): AndroidInjector<PixelcatApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .build()
    }

}
