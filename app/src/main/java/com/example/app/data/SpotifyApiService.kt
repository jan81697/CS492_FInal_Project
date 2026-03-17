package com.example.app.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.*

/**
 * Retrofit interface for the Spotify Web API.
 */
interface SpotifyApiService {

    @GET("me")
    suspend fun getCurrentUser(
        @Header("Authorization") authHeader: String
    ): SpotifyUser

    @GET("me/top/artists")
    suspend fun getTopArtists(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int = 10
    ): TopArtistsResponse

    @GET("search")
    suspend fun search(
        @Header("Authorization") authHeader: String,
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("limit") limit: Int = 10,
        @Query("market") market: String? = null
    ): SpotifySearchResponse

    @GET("artists/{id}")
    suspend fun getArtist(
        @Header("Authorization") authHeader: String,
        @Path("id") artistId: String
    ): SpotifyArtist

    @GET("artists/{id}/top-tracks")
    suspend fun getArtistTopTracks(
        @Header("Authorization") authHeader: String,
        @Path("id") artistId: String,
        @Query("market") market: String? = null
    ): SpotifyTrackList

    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    suspend fun getToken(
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("client_id") clientId: String,
        @Field("code_verifier") codeVerifier: String
    ): TokenResponse
}

data class SpotifyUser(
    @SerializedName("id") val id: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("country") val country: String
)

data class SpotifyArtistList(
    @SerializedName("artists") val artists: List<SpotifyArtist>
)

data class SpotifyTrackList(
    @SerializedName("tracks") val tracks: List<SpotifyTrack>
)

data class SpotifySearchResponse(
    @SerializedName("tracks") val tracks: SpotifyTrackItems? = null,
    @SerializedName("artists") val artists: ArtistSearchItems? = null
)

data class SpotifyTrackItems(
    @SerializedName("items") val items: List<SpotifyTrack>
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("scope") val scope: String
)

data class TopArtistsResponse(
    @SerializedName("items") val items: List<SpotifyArtist>
)

data class SpotifyTrack(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("artists") val artists: List<SpotifyArtistMinimal>,
    @SerializedName("album") val album: SpotifyAlbum,
    @SerializedName("preview_url") val previewUrl: String? = null,
    @SerializedName("popularity") val popularity: Int? = 0
)

data class SpotifyArtistMinimal(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class SpotifyAlbum(
    @SerializedName("name") val name: String,
    @SerializedName("images") val images: List<SpotifyImage>
)

data class ArtistSearchItems(
    @SerializedName("items") val items: List<SpotifyArtist>
)

data class SpotifyArtist(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("popularity") val popularity: Int? = 0,
    @SerializedName("followers") val followers: Followers? = Followers(),
    @SerializedName("images") val images: List<SpotifyImage>? = emptyList()
)

data class Followers(
    @SerializedName("total") val total: Int? = 0
)

data class SpotifyImage(
    @SerializedName("url") val url: String,
    @SerializedName("height") val height: Int? = 0,
    @SerializedName("width") val width: Int? = 0
)
