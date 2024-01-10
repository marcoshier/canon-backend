package serializables

import kotlinx.serialization.Serializable

@Serializable
data class HAEventObject(
    val type: String = "",
    val event: HAEvent,
    val id: Int
)

@Serializable
data class HAEvent (
    val event_type: String,
    val data: HAEventData,
    val origin: String,
    val time_fired: String,
    val context: HAEventContext
)

@Serializable
data class HAEventData(
    val entity_id: String,
    val old_state: HAEventState?,
    val new_state: HAEventState
)

@Serializable
data class HAEventState(
    val entity_id: String,
    val state: String,
    val attributes: HAEventAttributes,
    val last_changed: String,
    val last_updated: String,
    val context: HAEventContext
)

@Serializable
data class HAEventAttributes(
    val device_class: String,
    val friendly_name: String
)

@Serializable
data class HAEventContext(
    val id: String,
    val parent_id: String?,
    val user_id: String?
)