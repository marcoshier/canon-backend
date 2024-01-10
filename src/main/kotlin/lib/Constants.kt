package lib

import io.ktor.network.sockets.*

// name: canon
val accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiIzNzYzYjNkOTM1OGI0MTdmOTk5OWI1MWE1ZmViMDM4MiIsImlhdCI6MTcwNDc5ODQ5NCwiZXhwIjoyMDIwMTU4NDk0fQ.hr5oNefqC7dkX1-vjamMJdtPb4cIHV-ECM1_CaRSEpY"



val local = InetSocketAddress(envOrProperty("ha.local") ?: "192.168.0.101", 9002)
val remote = InetSocketAddress(envOrProperty("ha.remote") ?: "192.168.0.101", 9003)


fun envOrProperty(key: String): String? {
    val value =  System.getenv(key.uppercase().replace(".", "_")) ?: System.getProperty(key)
    println("$key - $value")
    return value
}