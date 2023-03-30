package com.example.aukcje20

data class Auction(
    val uid : String? = null,
    val name: String? = null,
    val description: String? = null,
    val startPrice: Double? = null,
    val buyNowPrice: Double? = null,
    val imageUrl: String? = null
)
