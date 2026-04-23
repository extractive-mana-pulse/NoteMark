package com.example.data

import com.example.domain.HttpRoutes
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.cio.Response

//class ReleasesServiceImpl(
//    private val client: HttpClient,
//): ReleasesService {
//
//    //    override suspend fun getLatestUpdate(): Response<ERROR> {
////        return try {
////            client.get(HttpRoutes.UPDATES)
////        } catch (e: Exception) {
////            Response.Failure(e)
////        }
////    }
//    override suspend fun getLatestUpdate() {
//        TODO("Not yet implemented")
//    }
//}