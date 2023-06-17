import com.example.apz_mobile.MonthlyAveragesRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("api/user/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ApiResponse>

    @Headers("Content-Type: application/json")
    @POST("api/user/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<Void>

    @GET("api/buildings/get_all")
    suspend fun getAllBuildings(): Response<List<BuildingRequest>>

    @GET("api/daily/get_all")
    suspend fun getDailyData(@Query("user_id") userId: String): Response<List<DailyRequest>>

    @GET("api/monthly/averages/get")
    suspend fun getMonthlyAveragesData(@Query("user_id") userId: String): Response<List<MonthlyAveragesRequest>>

    @GET("api/user/get_user")
    suspend fun getUserData(@Header("Authorization") authorization: String): Response<UserDataRequest>

    data class MonthlyAnalyticsRequest(
        val user_id: String
    )

    @POST("api/monthly/averages")
    suspend fun makeMonthlyAnalytics(
        @Body requestBody: ApiService.MonthlyAnalyticsRequest
    ): Response<Unit>
}
