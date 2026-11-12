package com.lmos.androidshopapp.data

import com.lmos.androidshopapp.model.Product

object SampleData {
    val categories = listOf(
        "All",
        "Electronics",
        "Audio",
        "Accessories",
        "Home",
        "Sports"
    )

    val products = listOf(
        Product(
            id = 1,
            name = "Wireless Noise-Canceling Headphones",
            category = "Audio",
            price = 199.99,
            rating = 4.7,
            reviewCount = 1240,
            description = "Over-ear Bluetooth headphones with active noise cancellation, 35-hour battery life, and USB-C fast charging."
        ),
        Product(
            id = 2,
            name = "4K Action Camera Bundle",
            category = "Electronics",
            price = 289.00,
            rating = 4.8,
            reviewCount = 865,
            description = "Waterproof 4K/60fps action camera with stabilization, dual screens, and mounting kit."
        ),
        Product(
            id = 3,
            name = "Portable NVMe SSD 2TB",
            category = "Electronics",
            price = 159.50,
            rating = 4.9,
            reviewCount = 2130,
            description = "High-speed USB 3.2 Gen 2x2 external solid-state drive for 4K video editing and backups."
        ),
        Product(
            id = 4,
            name = "USB-C Studio Condenser Microphone",
            category = "Audio",
            price = 79.99,
            rating = 4.6,
            reviewCount = 540,
            description = "Cardioid desktop microphone for calls, streaming, and voiceovers with zero-latency monitoring."
        ),
        Product(
            id = 5,
            name = "Compact Mechanical Keyboard",
            category = "Accessories",
            price = 114.00,
            rating = 4.7,
            reviewCount = 920,
            description = "75% wireless mechanical keyboard with hot-swappable tactile switches and Mac/Windows layout."
        ),
        Product(
            id = 6,
            name = "Smart LED Desk Lamp",
            category = "Home",
            price = 45.99,
            rating = 4.5,
            reviewCount = 380,
            description = "Adjustable color temperature desk lamp with wireless phone charging pad.",
            fastDelivery = false
        ),
        Product(
            id = 7,
            name = "Insulated Stainless Water Bottle 1L",
            category = "Sports",
            price = 29.99,
            rating = 4.8,
            reviewCount = 1750,
            description = "Double-wall vacuum insulated bottle keeping drinks cold for 24 hours."
        )
    )
}
