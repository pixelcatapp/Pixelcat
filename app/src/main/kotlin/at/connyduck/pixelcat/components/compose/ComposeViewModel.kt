/*
 * Copyright (C) 2020 Conny Duck
 *
 * This file is part of Pixelcat.
 *
 * Pixelcat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pixelcat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
