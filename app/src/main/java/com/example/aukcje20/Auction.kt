package com.example.aukcje20

data class Auction(
    val auctionid: String? = null,
    val uid: String? = null,
    var name: String? = null,
    var description: String? = null,
    var startPrice: Double? = null,
    var buyNowPrice: Double? = null,
    var imageUrl: String? = null ,


    ){
}
