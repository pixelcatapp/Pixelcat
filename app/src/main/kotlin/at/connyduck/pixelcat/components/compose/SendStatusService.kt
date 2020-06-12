package at.connyduck.pixelcat.components.compose

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.getColorForAttr
import at.connyduck.pixelcat.db.AccountManager
import at.connyduck.pixelcat.model.NewStatus
import at.connyduck.pixelcat.network.FediverseApi
import at.connyduck.pixelcat.network.calladapter.NetworkResponseError
import dagger.android.DaggerService
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Timer
import java.util.TimerTask
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SendStatusService : DaggerService(), CoroutineScope {

    @Inject
    lateinit var api: FediverseApi
    @Inject
    lateinit var accountManager: AccountManager

    private val statusesToSend = ConcurrentHashMap<Int, StatusToSend>()
    private val sendJobs = ConcurrentHashMap<Int, Job>()

    private val timer = Timer()

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (intent.hasExtra(KEY_STATUS)) {
            val tootToSend = intent.getParcelableExtra<StatusToSend>(KEY_STATUS)
                ?: throw IllegalStateException("SendTootService started without $KEY_STATUS extra")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, getString(R.string.send_status_notification_channel_name), NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cat)
                .setContentTitle(getString(R.string.send_status_notification_title))
                .setContentText(tootToSend.text)
                .setProgress(1, 0, true)
                .setOngoing(true)
                .setColor(getColorForAttr(android.R.attr.colorPrimary))
                .addAction(0, getString(android.R.string.cancel), cancelSendingIntent(sendingNotificationId))

            if (statusesToSend.size == 0 || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
                startForeground(sendingNotificationId, builder.build())
            } else {
                notificationManager.notify(sendingNotificationId, builder.build())
            }

            statusesToSend[sendingNotificationId] = tootToSend
            sendStatus(sendingNotificationId--)
        } else {

            if (intent.hasExtra(KEY_CANCEL)) {
                cancelSending(intent.getIntExtra(KEY_CANCEL, 0))
            }
        }

        return START_NOT_STICKY
    }

    private fun sendStatus(id: Int) {

        // when tootToSend == null, sending has been canceled
        val statusToSend = statusesToSend[id] ?: return

        // when account == null, user has logged out, cancel sending
        val account = accountManager.getAccountById(statusToSend.accountId)

        if (account == null) {
            statusesToSend.remove(id)
            notificationManager.cancel(id)
            stopSelfWhenDone()
            return
        }

        statusToSend.retries++

        launch {

            val mediaIds = statusToSend.mediaUris.map {

                var type: String? = null
                val extension = MimeTypeMap.getFileExtensionFromUrl(it)
                if (extension != null) {
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                }

                val file = File(it)
                val filePart = file.asRequestBody(type!!.toMediaType())

                val body = MultipartBody.Part.create(filePart)

                api.uploadMedia(body).fold(
                    { attachment ->
                        attachment.id
                    },
                    {
                        ""
                    }
                )
            }

            val newStatus = NewStatus(
                status = statusToSend.text,
                inReplyToId = null,
                visibility = statusToSend.visibility,
                sensitive = statusToSend.sensitive,
                mediaIds = mediaIds
            )

            api.createStatus(
                "Bearer " + account.auth.accessToken,
                account.domain,
                statusToSend.idempotencyKey,
                newStatus
            ).fold<Any?>(
                {
                    statusesToSend.remove(id)
                },
                {
                    when (it) {
                        is NetworkResponseError.ApiError -> {
                            // the server refused to accept the status, save toot & show error message
                            // TODO saveToDrafts

                            val builder = NotificationCompat.Builder(this@SendStatusService, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_cat)
                                .setContentTitle(getString(R.string.send_status_notification_error_title))
                                // .setContentText(getString(R.string.send_toot_notification_saved_content))
                                .setColor(getColorForAttr(android.R.attr.colorPrimary))

                            notificationManager.cancel(id)
                            notificationManager.notify(errorNotificationId--, builder.build())
                        }
                        else -> {
                            var backoff = TimeUnit.SECONDS.toMillis(statusToSend.retries.toLong())
                            if (backoff > MAX_RETRY_INTERVAL) {
                                backoff = MAX_RETRY_INTERVAL
                            }

                            timer.schedule(
                                object : TimerTask() {
                                    override fun run() {
                                        sendStatus(id)
                                    }
                                },
                                backoff
                            )
                        }
                    }
                }
            )
        }.apply {
            sendJobs[id] = this
        }
    }

    private fun stopSelfWhenDone() {

        if (statusesToSend.isEmpty()) {
            coroutineContext.cancel()
            ServiceCompat.stopForeground(this@SendStatusService, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun cancelSending(id: Int) {
        val statusToCancel = statusesToSend.remove(id)
        if (statusToCancel != null) {
            val sendCall = sendJobs.remove(id)
            sendCall?.cancel()

            // saveTootToDrafts(tootToCancel)

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cat)
                .setContentTitle(getString(R.string.send_status_notification_cancel_title))
                //    .setContentText(getString(R.string.send_toot_notification_saved_content))
                .setColor(getColorForAttr(android.R.attr.colorPrimary))

            notificationManager.notify(id, builder.build())

            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        notificationManager.cancel(id)
                        stopSelfWhenDone()
                    }
                },
                5000
            )
        }
    }

    private fun cancelSendingIntent(tootId: Int): PendingIntent {

        val intent = Intent(this, SendStatusService::class.java)

        intent.putExtra(KEY_CANCEL, tootId)

        return PendingIntent.getService(this, tootId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {

        private const val KEY_STATUS = "status"
        private const val KEY_CANCEL = "cancel_id"
        private const val CHANNEL_ID = "send_status"

        private val MAX_RETRY_INTERVAL = TimeUnit.MINUTES.toMillis(1)

        private var sendingNotificationId = -1 // use negative ids to not clash with other notis
        private var errorNotificationId = Int.MIN_VALUE // use even more negative ids to not clash with other notis

        @JvmStatic
        fun sendStatusIntent(
            context: Context,
            statusToSend: StatusToSend
        ): Intent {
            val intent = Intent(context, SendStatusService::class.java)
            intent.putExtra(KEY_STATUS, statusToSend)

            return intent
        }
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO
}

@Parcelize
data class StatusToSend(
    val accountId: Long,
    val idempotencyKey: String = UUID.randomUUID().toString(),
    val text: String,
    val visibility: String,
    val sensitive: Boolean,
    val mediaUris: List<String>,
    val mediaDescriptions: List<String> = emptyList(),
    val inReplyToId: String? = null,
    val savedTootUid: Int = 0,
    var retries: Int = 0
) : Parcelable
