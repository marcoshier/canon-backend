package lib

import org.openrndr.events.Event
import java.io.Serializable

@kotlinx.serialization.Serializable
data class SensorState(val index: Int, val state: Boolean): Serializable

class Sensor(val index: Int) {
    var stateChanged = Event<Boolean>("state-changed")

    var state = false
        set(value) {
            if (field != value) {
                field = value
                stateChanged.trigger(value)
            }
        }
}