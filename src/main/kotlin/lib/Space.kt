package lib

import org.openrndr.events.Event
import serializables.EventSubscriber

class Space {
    val sensors = mutableMapOf<Int, Sensor>()

    private var lastActivated = -1
        set(value) {
            if (field != value) {
                field = value
                if (!activeSensors.contains(value)) {
                    println("New sensor found $value")
                    activeSensors.add(value)
                    sensorAdded.trigger(value)
                }
            }
        }
    private var activeSensors = mutableListOf<Int>()

    val sensorAdded = Event<Int>("sensor-added")
    val subscriber = EventSubscriber(1)

    fun updateSensorState(i: Int, state: Boolean) {
        val s = sensors.getOrPut(i) { Sensor(i) }
        lastActivated = i
        s.state = state
    }
}
