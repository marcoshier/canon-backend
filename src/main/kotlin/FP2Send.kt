import org.openrndr.application
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import lib.*
import org.openrndr.launch
import serializables.AuthMessage
import serializables.HAEventObject
import java.io.ByteArrayOutputStream


@OptIn(DelicateCoroutinesApi::class)
suspend fun main() {
    val client = HttpClient(CIO) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json {
                encodeDefaults = true
            })
        }
    }

        val session = client.webSocketSession(HttpMethod.Get, "localhost", 8123, "/api/websocket")
        session.sendSerialized(AuthMessage(access_token = accessToken))

        val space = Space()
        session.sendSerialized(space.subscriber)


        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).udp().bind(local)

        suspend fun send(state: SensorState) {
            val ba = Json.encodeToString(state).toByteArray()
            val bp = buildPacket { writeFully(ba) }
            serverSocket.send(Datagram(bp, remote))
        }

        space.sensorAdded.listen {
            val s = space.sensors[it]!!
            println("zone ${s.index} added")

            s.stateChanged.listen { b ->
                if(b) println("Entered zone ${s.index}") else println("Exited zone ${s.index}")
                GlobalScope.launch {
                    send(SensorState(s.index, s.state))
                }
            }
        }


            while (true) {

                val msg = session.incoming.receive() as Frame.Text
                val text = msg.readText()

                val json = Json.parseToJsonElement(text)


                val type = json.jsonObject["type"]
                type?.let {
                    when(it.jsonPrimitive.content) {
                        "auth_required" -> println("Authentication required. This should happen automatically")
                        "auth_ok" -> println("Authentication succeeded.")
                        "auth_invalid" -> error("Authentication failed. ${json.jsonObject["message"]}")

                        "result" -> {
                            if(json.jsonObject["success"]!!.jsonPrimitive.boolean) {
                                println("--- Subscribing succeded. Listening to events now")
                                println()
                            } else println("failed to subscribe")
                        }

                        "event" -> {
                            val t = json.jsonObject["event"]!!.jsonObject["data"]!!.jsonObject["entity_id"]!!.jsonPrimitive.content
                            if (!t.contains("button") && t.contains("presence_sensor_fp2")) {
                                val eventObj = Json.decodeFromJsonElement<HAEventObject>(json)
                                val state: Boolean = eventObj.event.data.new_state.state == "on"

                                space.updateSensorState(
                                    eventObj.event.data.entity_id.last().digitToInt(),
                                    state
                                )
                            }

                        }

                        else -> error("type not supported ${it.jsonPrimitive.content}")
                    }

                }


            }
        }
