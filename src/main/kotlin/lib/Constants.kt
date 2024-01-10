package lib

import io.ktor.network.sockets.*

// name: canon
val accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI0M2U2MTE4ZWIyNDE0OTcxYThmNDNjMGZlYjIwNDQyOSIsImlhdCI6MTcwNDg5Mzc3MywiZXhwIjoyMDIwMjUzNzczfQ.HuneKQtglCxBELOpyVSBe9oujR--iyk9YeyJT6mWUtQ"



val local = InetSocketAddress(envOrProperty("ha.local") ?: "192.168.0.101", 9002)
val remote = InetSocketAddress(envOrProperty("ha.remote") ?: "192.168.0.101", 9003)


fun envOrProperty(key: String): String? {
    val value =  System.getenv(key.uppercase().replace(".", "_")) ?: System.getProperty(key)
    println("$key - $value")
    return value
}