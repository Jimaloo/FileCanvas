package com.jimjuma.filecanvas

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform