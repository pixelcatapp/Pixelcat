package at.connyduck.pixelcat.network

import at.connyduck.pixelcat.model.AccessToken
import at.connyduck.pixelcat.model.Account
import at.connyduck.pixelcat.model.AppCredentials
import at.connyduck.pixelcat.model.Attachment
import at.connyduck.pixelcat.model.NewStatus
import at.connyduck.pixelcat.model.Relationship
import at.connyduck.pixelcat.model.Status
import at.connyduck.pixelcat.network.calladapter.NetworkResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FediverseApi {

    companion object {
        const val PLACEHOLDER_DOMAIN = "x.placeholder"
        const val DOMAIN_HEADER = "Domain"
    }

    @FormUrlEncoded
    @POST("api/v1/apps")
    suspend fun authenticateAppAsync(
        @Header(DOMAIN_HEADER) domain: String,
        @Field("client_name") clientName: String,
        @Field("website") clientWebsite: String,
        @Field("redirect_uris") redirectUris: String,
        @Field("scopes") scopes: String
    ): NetworkResponse<AppCredentials>

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun fetchOAuthToken(
        @Header(DOMAIN_HEADER) domain: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code") code: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): NetworkResponse<AccessToken>

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun refreshOAuthToken(
        @Header(DOMAIN_HEADER) domain: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): NetworkResponse<AccessToken>

    @GET("api/v1/accounts/verify_credentials")
    suspend fun accountVerifyCredentials(): NetworkResponse<Account>

    @GET("api/v1/timelines/home")
    suspend fun homeTimeline(
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("limit") limit: Int? = null
    ): NetworkResponse<List<Status>>

    @GET("api/v1/accounts/{id}/statuses")
    suspend fun accountTimeline(
        @Path("id") accountId: String,
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("exclude_replies") excludeReplies: Boolean? = false,
        @Query("only_media") onlyMedia: Boolean? = true,
        @Query("pinned") pinned: Boolean? = false
    ): NetworkResponse<Status>

    @GET("api/v1/accounts/{id}")
    suspend fun account(
        @Path("id") accountId: String
    ): NetworkResponse<Account>

    @GET("api/v1/accounts/{id}/statuses")
    suspend fun accountStatuses(
        @Path("id") accountId: String,
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("only_media") onlyMedia: Boolean? = null,
        @Query("exclude_reblogs") excludeReblogs: Boolean? = null
    ): NetworkResponse<List<Status>>

    @FormUrlEncoded
    @POST("api/v1/accounts/{id}/follow")
    suspend fun followAccount(
        @Path("id") accountId: String,
        @Field("reblogs") showReblogs: Boolean
    ): NetworkResponse<Relationship>

    @POST("api/v1/accounts/{id}/unfollow")
    suspend fun unfollowAccount(
        @Path("id") accountId: String
    ): NetworkResponse<Relationship>

    @POST("api/v1/accounts/{id}/block")
    suspend fun blockAccount(
        @Path("id") accountId: String
    ): NetworkResponse<Relationship>

    @POST("api/v1/accounts/{id}/unblock")
    suspend fun unblockAccount(
        @Path("id") accountId: String
    ): NetworkResponse<Relationship>

    @POST("api/v1/accounts/{id}/mute")
    suspend fun muteAccount(
        @Path("id") accountId: String
    ): NetworkResponse<Relationship>

    @POST("api/v1/accounts/{id}/unmute")
    suspend fun unmuteAccount(
        @Path("id") accountId: String
    ): NetworkResponse<Relationship>

    @GET("api/v1/accounts/relationships")
    suspend fun relationships(
        @Query("id[]") accountIds: List<String>
    ): NetworkResponse<List<Relationship>>

    @Multipart
    @POST("api/v1/media")
    suspend fun uploadMedia(
        @Part file: MultipartBody.Part
    ): NetworkResponse<Attachment>

    @POST("api/v1/statuses")
    suspend fun createStatus(
        @Header("Authorization") auth: String,
        @Header(DOMAIN_HEADER) domain: String,
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body status: NewStatus
    ): NetworkResponse<Status>

    @POST("api/v1/statuses/{id}/favourite")
    suspend fun favouriteStatus(
        @Path("id") statusId: String
    ): NetworkResponse<Status>

    @POST("api/v1/statuses/{id}/unfavourite")
    suspend fun unfavouriteStatus(
        @Path("id") statusId: String
    ): NetworkResponse<Status>

    @POST("api/v1/statuses/{id}/reblog")
    suspend fun reblogStatus(
        @Path("id") statusId: String
    ): NetworkResponse<Status>

    @POST("api/v1/statuses/{id}/unreblog")
    suspend fun unreblogStatus(
        @Path("id") statusId: String
    ): NetworkResponse<Status>
}
