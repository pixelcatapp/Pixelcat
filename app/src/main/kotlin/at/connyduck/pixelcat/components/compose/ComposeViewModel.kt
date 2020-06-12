package at.connyduck.pixelcat.components.compose

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.connyduck.pixelcat.db.AccountManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class ComposeViewModel @Inject constructor(
    val context: Context,
    val accountManager: AccountManager
): ViewModel() {


    val images = MutableLiveData<List<String>>()

    val visibility = MutableLiveData(VISIBILITY.PUBLIC)



    fun addImage(imageUri: String) {

        images.value = images.value.orEmpty() + imageUri


    }


    fun setVisibility(visibility: VISIBILITY) {
        this.visibility.value = visibility
    }

    fun sendStatus() {

        viewModelScope.launch {
            val statusToSend = StatusToSend(
                accountId = accountManager.activeAccount()!!.id,
                text = "test",
                visibility = visibility.value!!.serverName,
                sensitive = false,
                mediaUris = images.value!!
            )

            val intent = SendStatusService.sendStatusIntent(context, statusToSend)
            ContextCompat.startForegroundService(context, intent)
        }

    }


}

enum class VISIBILITY(val serverName: String) {
    PUBLIC("public"),
    UNLISTED("unlisted"),
    FOLLOWERS_ONLY("private")
}