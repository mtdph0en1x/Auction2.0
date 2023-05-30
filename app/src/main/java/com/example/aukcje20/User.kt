package com.example.aukcje20

data class User (
    val uid: String? = null,
    var nickname: String? = null,
    var observed: List<String> = emptyList(),
    var email: String? = null,
    var notifications: List<Map<String,Any>> = emptyList()
)