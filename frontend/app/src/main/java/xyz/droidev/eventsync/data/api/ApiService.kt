package xyz.droidev.eventsync.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import xyz.droidev.eventsync.data.model.request.GenerationResponse
import xyz.droidev.eventsync.data.model.request.SubmitVoteBody
import xyz.droidev.eventsync.data.model.request.UserData
import xyz.droidev.eventsync.data.model.response.PoliticalParty

interface ApiService {
    @POST("/save-user-data")
    suspend fun postUserData(
        @Body
        userData: UserData
    ): GenerationResponse

    @GET("/political-parties")
    suspend fun getPoliticalParty(): List<PoliticalParty>

    @POST("/votes")
    suspend fun submitVote(
        @Body
        submitVoteBody: SubmitVoteBody
    )
}