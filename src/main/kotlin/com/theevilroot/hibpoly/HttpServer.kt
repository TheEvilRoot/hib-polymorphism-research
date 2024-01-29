package com.theevilroot.hibpoly

import com.theevilroot.hibpoly.model.DiscriminatorAnalyticsEvent
import com.theevilroot.hibpoly.model.DiscriminatorConnectEvent
import com.theevilroot.hibpoly.model.DiscriminatorCrashEvent
import com.theevilroot.hibpoly.model.DiscriminatorDataEvent
import com.theevilroot.hibpoly.model.DiscriminatorDisconnectEvent
import com.theevilroot.hibpoly.model.DiscriminatorEvent
import com.theevilroot.hibpoly.model.EventType
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.hibernate.Session
import org.hibernate.SessionFactory
import java.time.Instant
import java.util.*
import java.util.stream.Collectors

@Serializable
data class ConnectEventDto(
    val instanceId: String,
    val ipAddress: String,
    val success: Boolean
)

@Serializable
data class DisconnectEventDto(
    val instanceId: String,
    val reason: String
)

@Serializable
data class DataEventDto(
    val instanceId: String,
    val sha1: String,
    val data: String,
    val length: Long
)

@Serializable
data class AnalyticsEventDto(
    val instanceId: String,
    val uptime: Long,
    val ramAvailable: Long,
    val cpuLoad: Long
)

@Serializable
data class CrashEventDto(
    val instanceId: String,
    val stackTrace: String,
    val module: String
)

@Serializable
sealed class EventDto

@Serializable
data class ConnectEventInfoDto(
    val eventId: Long,
    val createDate: Long,
    val ipAddress: String,
    val success: Boolean
) : EventDto()

@Serializable
data class DisconnectEventInfoDto(
    val eventId: Long,
    val createDate: Long,
    val reason: String
) : EventDto()

@Serializable
data class AnalyticsEventInfoDto(
    val eventId: Long,
    val createDate: Long,
    val cpuLoad: Long,
    val ramAvailable: Long,
    val uptime: Long
) : EventDto()

@Serializable
data class CrashEventInfoDto(
    val eventId: Long,
    val createDate: Long,
    val stackTrace: String,
    val module: String
) : EventDto()

@Serializable
data class DataEventInfoDto(
    val eventId: Long,
    val createDate: Long,
    val dataLength: Long,
    val dataSha1: String
) : EventDto()

@Serializable
data class InstanceEventsRequest(
    val instanceId: String
)

@Serializable
data class TypeInstanceEventsRequest(
    val instanceId: String,
    val type: EventType
)


class HttpServer (private val sessionFactory: SessionFactory){

    private inline suspend fun <reified T> transactional(crossinline f: suspend (Session) -> T): T {
        return withContext(Dispatchers.IO) {
            sessionFactory.openSession().use { session ->
                session.beginTransaction()
                try {
                    val ret = f(session)
                    session.transaction.commit()
                    ret
                } catch (e: Throwable) {
                    e.printStackTrace()
                    session.transaction.rollback()
                    throw e
                }
            }
        }
    }

    private val application = embeddedServer(Netty, port = 8383, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }
        install(StatusPages) {
            exception { call: ApplicationCall, cause: Throwable ->
                cause.printStackTrace()
            }
        }
        routing {
            route("api/events") {
                post("type") {
                    val request = call.receive<TypeInstanceEventsRequest>()
                    val events = transactional {
                        it.createQuery("from discriminator_events event where event.instanceId = :instanceId and event.eventType = :eventType order by create_date desc")
                            .setParameter("instanceId", request.instanceId)
                            .setParameter("eventType", request.type)
                            .setMaxResults(100)
                            .resultStream
                            .map { it as DiscriminatorEvent }
                            .map {
                                when (it) {
                                    is DiscriminatorCrashEvent -> CrashEventInfoDto(it.id, it.createDate.toEpochMilli(), it.stackTrace, it.module)
                                    is DiscriminatorDataEvent -> DataEventInfoDto(it.id, it.createDate.toEpochMilli(), it.dataLength, it.dataSha1)
                                    is DiscriminatorConnectEvent -> ConnectEventInfoDto(it.id, it.createDate.toEpochMilli(), it.ipAddress, it.isSuccess)
                                    is DiscriminatorDisconnectEvent -> DisconnectEventInfoDto(it.id, it.createDate.toEpochMilli(), it.reason)
                                    is DiscriminatorAnalyticsEvent -> AnalyticsEventInfoDto(it.id, it.createDate.toEpochMilli(), it.cpuLoad, it.ramAvailable, it.uptime)
                                    else -> throw IllegalStateException("$it")
                                }
                            }.collect(Collectors.toList())
                    }
                    call.respond(events)
                }
                post("instance") {
                    val request = call.receive<InstanceEventsRequest>()
                    val events = transactional {
                        it.createQuery("from discriminator_events event where event.instanceId = :instanceId order by create_date desc")
                            .setParameter("instanceId", request.instanceId)
                            .setMaxResults(100)
                            .resultStream
                            .map { it as DiscriminatorEvent }
                            .map {
                                when (it) {
                                    is DiscriminatorCrashEvent -> CrashEventInfoDto(it.id, it.createDate.toEpochMilli(), it.stackTrace, it.module)
                                    is DiscriminatorDataEvent -> DataEventInfoDto(it.id, it.createDate.toEpochMilli(), it.dataLength, it.dataSha1)
                                    is DiscriminatorConnectEvent -> ConnectEventInfoDto(it.id, it.createDate.toEpochMilli(), it.ipAddress, it.isSuccess)
                                    is DiscriminatorDisconnectEvent -> DisconnectEventInfoDto(it.id, it.createDate.toEpochMilli(), it.reason)
                                    is DiscriminatorAnalyticsEvent -> AnalyticsEventInfoDto(it.id, it.createDate.toEpochMilli(), it.cpuLoad, it.ramAvailable, it.uptime)
                                    else -> throw IllegalStateException("$it")
                                }
                            }.collect(Collectors.toList())
                    }
                    call.respond(events)
                }
            }
            route("api/event") {
                post("connect") {
                    val request = call.receive<ConnectEventDto>()
                    val event = transactional {
                        val event = DiscriminatorConnectEvent(
                            EventType.EVENT_CONNECT,
                            Instant.now(),
                            request.instanceId,
                            request.ipAddress,
                            request.success
                        )
                        it.save(event)
                        event
                    }
                    call.respond(HttpStatusCode.OK, "${event.id}")
                }
                post("disconnect") {
                    val request = call.receive<DisconnectEventDto>()
                    val event = transactional {
                        val event = DiscriminatorDisconnectEvent(
                            EventType.EVENT_DISCONNECT,
                            Instant.now(),
                            request.instanceId,
                            request.reason
                        )
                        it.save(event)
                        event
                    }
                    call.respond(HttpStatusCode.OK, "${event.id}")
                }
                post("data") {
                    val request = call.receive<DataEventDto>()
                    val event = transactional {
                        val event = DiscriminatorDataEvent(
                            EventType.EVENT_DATA,
                            Instant.now(),
                            request.instanceId,
                            request.length,
                            request.sha1,
                            Base64.getDecoder().decode(request.data)
                        )
                        it.save(event)
                        event
                    }
                    call.respond(HttpStatusCode.OK, "${event.id}")
                }
                post("analytics") {
                    val request = call.receive<AnalyticsEventDto>()
                    val event = transactional {
                        val event = DiscriminatorAnalyticsEvent(
                            EventType.EVENT_ANALYTICS,
                            Instant.now(),
                            request.instanceId,
                            request.uptime,
                            request.ramAvailable,
                            request.cpuLoad
                        )
                        it.save(event)
                        event
                    }
                    call.respond(HttpStatusCode.OK, "${event.id}")
                }
                post("crash") {
                    val request = call.receive<CrashEventDto>()
                    val event = transactional {
                        val event = DiscriminatorCrashEvent(
                            EventType.EVENT_CRASH,
                            Instant.now(),
                            request.instanceId,
                            request.stackTrace,
                            request.module
                        )
                        it.save(event)
                        event
                    }
                    call.respond(HttpStatusCode.OK, "${event.id}")
                }
            }
        }
    }

    fun start() {
        application.start(true)
    }
}