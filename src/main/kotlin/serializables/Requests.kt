package serializables

import kotlinx.serialization.Serializable

@Serializable
data class AuthMessage(val type: String = "auth", val access_token: String)

@Serializable
data class EventSubscriber(val id: Int, val type: String = "subscribe_events", val event_type: String = "state_changed")