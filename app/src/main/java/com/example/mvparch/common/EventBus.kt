package com.example.mvparch.common

import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.coroutineContext

class EventBus {
    private val _events =
        MutableSharedFlow<Any>() //protegiendo lo que se va alimentando dentro de events
    val events: SharedFlow<Any> = _events //servirá como modo lectura

    suspend fun publish(event: Any) {
        _events.emit(event)
    }

    //reified: permite usar un tipo en lugar de una clase
    //crossinline: permitir el uso de una función de alto nivel dentro de un lambda
    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }

    }

    //Singleton
    companion object {
        private val _eventBusInstance: EventBus by lazy { EventBus() }
        fun instance() = _eventBusInstance
    }

}