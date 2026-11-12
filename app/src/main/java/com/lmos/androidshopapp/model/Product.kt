package com.lmos.androidshopapp.model

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
    val fastDelivery: Boolean = true
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)
