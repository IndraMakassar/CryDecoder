// AudioApiService.kt
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Response

interface ApiService {
    @Multipart
    @POST("classify/") // Adjust if your endpoint is different
    suspend fun uploadAudio(
        @Part audio: MultipartBody.Part
    ): Response<ServerResult>
}

// ServerResult.kt
data class ServerResult(
    val prediction: String,
    val confidence: Float
)
