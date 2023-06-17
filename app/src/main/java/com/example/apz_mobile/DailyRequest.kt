data class DailyRequest(
    val _id: Daily_Id,
    val date: String,
    val day_temperature: Int,
    val electricity_count: Int,
    val gas_count: Int,
    val user_id: String,
    val water_count: Int
)

data class Daily_Id(
    val `$oid`: String
)
