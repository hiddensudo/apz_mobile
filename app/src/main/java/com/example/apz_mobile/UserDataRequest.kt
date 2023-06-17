import com.google.gson.annotations.SerializedName

data class UserDataRequest(
    @SerializedName("_id")
    val id: IdData,
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val password: String
)

data class IdData(
    @SerializedName("\$oid")
    val oid: String
)
