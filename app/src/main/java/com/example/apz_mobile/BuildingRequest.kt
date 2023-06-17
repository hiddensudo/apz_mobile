data class BuildingRequest(
    val _id: Id,
    val address: String,
    val user_id: String
)

data class Id(
    val `$oid`: String
)
