import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import lib.SensorState
import lib.local
import lib.remote
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.color.presets.DARK_GRAY
import org.openrndr.extra.color.presets.LIME_GREEN
import org.openrndr.extra.shapes.grid

@OptIn(DelicateCoroutinesApi::class)
fun main() = application {

    program {
        val grid = drawer.bounds.grid(8, 1).flatten()
        val sensorStates = grid.indices.associateWith { false }.toMutableMap()


        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).udp().connect(local, remote)

        GlobalScope.launch {
            while (true) {
                val msg = socket.incoming.receive()
                val bytes = msg.packet.readBytes().decodeToString()
                val state = Json.decodeFromString<SensorState>(bytes)
                sensorStates[state.index] = state.state
                println("$state - msg received")
            }
        }

        extend {

            drawer.rectangles {
                for ((i, s) in sensorStates) {
                    this.fill = if (s) ColorRGBa.LIME_GREEN else ColorRGBa.DARK_GRAY
                    this.rectangle(grid[i])
                }
            }

        }
    }
}