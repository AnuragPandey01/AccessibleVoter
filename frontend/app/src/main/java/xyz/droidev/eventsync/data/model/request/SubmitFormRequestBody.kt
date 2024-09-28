package xyz.droidev.eventsync.data.model.request


data class SubmitVoteBody(
    val user_id: Int,
    val party_code: String,
    val party_name: String
)

data class GenerationResponse(
    val id: Int
)