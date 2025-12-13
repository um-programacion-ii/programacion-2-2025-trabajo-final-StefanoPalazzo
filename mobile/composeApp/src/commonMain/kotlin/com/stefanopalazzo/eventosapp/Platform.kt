package com.stefanopalazzo.eventosapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform