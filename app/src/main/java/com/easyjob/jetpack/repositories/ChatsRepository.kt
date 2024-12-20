package com.easyjob.jetpack.repositories

import android.util.Log
import com.easyjob.jetpack.data.store.UserPreferencesRepository
import com.easyjob.jetpack.services.Chat
import com.easyjob.jetpack.services.ChatsService
import com.easyjob.jetpack.services.GroupChatChatsResponse
import com.easyjob.jetpack.services.GroupChatResponse
import com.easyjob.jetpack.services.SendMessageDTO
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject


interface ChatsRepository {
    fun initializeSocket()
    fun connect()
    fun disconnect()
    suspend fun sendMessage(message: String, chatRoomId: String)
    fun listen(event: String, callback: (Chat) -> Unit)
    suspend fun retrieveGroupChats(): Response<List<GroupChatResponse>>
    suspend fun retrieveChatsClientProfessional(professionalId: String): Response<GroupChatChatsResponse>
}

class ChatsRepositoryImpl @Inject constructor(
    private val chatsService: ChatsService,
    private val userPreferencesRepository: UserPreferencesRepository
) : ChatsRepository {

    override fun initializeSocket() {
        chatsService.initializeSocket("wss://api.easyjob.com.co")
    }

    override fun connect() {
        chatsService.connect()
    }

    override fun disconnect() {
        chatsService.disconnect()
    }

    override suspend fun sendMessage(message: String, chatRoomId: String) {

        val roles = userPreferencesRepository.rolesFlow.first()
        var sendMessageDTO: SendMessageDTO

        val userId = userPreferencesRepository.userIdFlow.first()

        if(roles[0] == "client") {
            sendMessageDTO = SendMessageDTO(
                chatroom_id = chatRoomId,
                message = message,
                client_id = userId,
                professional_id = null
            )
        } else {
            sendMessageDTO = SendMessageDTO(
                chatroom_id = chatRoomId,
                message = message,
                client_id = null,
                professional_id = userId
            )
        }
        chatsService.sendMessage(sendMessageDTO)
    }

    override fun listen(event: String, callback: (Chat) -> Unit) {
        chatsService.listen(event, callback)
    }

    override suspend fun retrieveGroupChats(): Response<List<GroupChatResponse>> {

        val userId = userPreferencesRepository.userIdFlow.first()
        if(userId ==null) {
            Log.e("GroupChatRepository", "User ID is null")
            val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(),
                """{ "error": "User ID is missing" }"""
            )
            return Response.error(400, errorBody)
        }
        return chatsService.getGroupChatsByUser(userId)
    }

    override suspend fun retrieveChatsClientProfessional(userMessageTo: String): Response<GroupChatChatsResponse> {

        val userId = userPreferencesRepository.userIdFlow.first()
        if(userId ==null) {
            Log.e("GroupChatRepository", "User ID is null")
            val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(),
                """{ "error": "User ID is missing" }"""
            )
            return Response.error(400, errorBody)
        }

        val roles = userPreferencesRepository.rolesFlow.first()
        if(roles[0] == "client") {
            Log.e("The user is a Professional", "YEAHHH")
            return chatsService.getChatsClientProfessional(userId, userMessageTo)
        } else {
            return chatsService.getChatsClientProfessional(userMessageTo, userId )
        }



    }


}