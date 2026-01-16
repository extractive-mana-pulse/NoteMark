package com.example.domain

interface SessionRepository {
    
    fun saveTokens(accessToken: String, refreshToken: String)
    
    fun saveUserName(userName: String)
    
    fun getUserName(): String?
    
    fun getAccessToken(): String?
    
    fun getRefreshToken(): String?
    
    fun clearUserName()
    
    fun clearTokens()
    
    fun isLoggedIn(): Boolean
    
    fun clearAllData()
}