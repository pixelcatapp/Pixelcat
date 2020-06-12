package at.connyduck.pixelcat.db

import androidx.room.TypeConverter
import at.connyduck.pixelcat.model.Attachment
import at.connyduck.pixelcat.model.Status
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.Date

class Converters {

    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun visibilityToInt(visibility: Status.Visibility): String {
        return visibility.name
    }

    @TypeConverter
    fun stringToVisibility(visibility: String): Status.Visibility {
        return Status.Visibility.valueOf(visibility)
    }

    @TypeConverter
    fun attachmentListToJson(attachmentList: List<Attachment>?): String {
        val type = Types.newParameterizedType(
            List::class.java,
            Attachment::class.java
        )
        return moshi.adapter<List<Attachment>>(type).toJson(attachmentList)
    }

    @TypeConverter
    fun jsonToAttachmentList(attachmentListJson: String?): List<Attachment>? {
        if (attachmentListJson == null) {
            return null
        }
        val type = Types.newParameterizedType(
            List::class.java,
            Attachment::class.java
        )
        return moshi.adapter<List<Attachment>>(type).fromJson(attachmentListJson)
    }

    @TypeConverter
    fun dateToLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun longToDate(date: Long): Date {
        return Date(date)
    }
}
