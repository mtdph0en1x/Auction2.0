package com.example.aukcje20

data class Auction(
    val name: String,
    val description: String,
    val startPrice: Double,
    val buyNowPrice: Double,
    val imageUrl: String
)