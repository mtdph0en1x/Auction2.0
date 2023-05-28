package com.example.aukcje20.DataClasses

data class Auction(
    val auctionid: String? = null,
    val uid: String? = null,
    var name: String? = null,
    var description: String? = null,
    var startPrice: Double? = null,
    var buyNowPrice: Double? = null,
    var imageUrls: List<String> = ArrayList(6),
    var auctionEnd: String? = null,
    val bidders: List<Map<String, Any>> = emptyList()
)

