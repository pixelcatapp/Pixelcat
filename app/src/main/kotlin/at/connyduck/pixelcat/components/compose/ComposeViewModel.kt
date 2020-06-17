package at.connyduck.pixelcat.components.compose

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.connyduck.pixelcat.components.compose.ComposeImageAdapter.Companion.ADD_ITEM
import at.connyduck.pixelcat.db.AccountManager
import javax.inject.Inject

class ComposeViewModel @Inject constructor(
    val context: Context,
    val accountManager: AccountManager
) : ViewModel() {

    private val images: MutableList<String> = mutableListOf()

    val imageLiveData = MutableLiveData<List<String>>()

    val visibility = MutableLiveData(VISIBILITY.PUBLIC)

    fun addImage(imageUri: String) {
        images.add(imageUri)
        imageLiveData.value = if (images.size < MAX_IMAGE_COUNT) {
            images + ADD_ITEM
        } else {
            images
        }
    }

    fun setVisibility(visibility: VISIBILITY) {
        this.visibility.value = visibility
    }

    suspend fun sendStatus(caption: String, sensitive: Boolean) {

        val statusToSend = StatusToSend(
            accountId = accountManager.activeAccount()!!.id,
            text = caption,
            visibility = visibility.value!!.serverName,
            sensitive = sensitive,
            mediaUris = images
        )

        val intent = SendStatusService.sendStatusIntent(context, statusToSend)
        ContextCompat.startForegroundService(context, intent)
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 4
    }
}

enum class VISIBILITY(val serverName: String) {
    PUBLIC("public"),
    UNLISTED("unlisted"),
    FOLLOWERS_ONLY("private")
}
