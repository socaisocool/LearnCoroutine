package com.cool.cai.dispatcher

import javax.xml.ws.Dispatch
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

interface Dispatcher {
    fun dispatch(block: () -> Unit)
}

open class DispatcherContext(private val dispatcher: Dispatcher) : AbstractCoroutineContextElement(ContinuationInterceptor)
        , ContinuationInterceptor {

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        return DispatchedContinuation(continuation, dispatcher)
    }

}

/**
 * dispatcher包装了delagate
 */
private class DispatchedContinuation<T>(val delagate: Continuation<T>, val dispatcher: Dispatcher) : Continuation<T> {
    override val context: CoroutineContext
        get() = delagate.context

    override fun resumeWith(result: Result<T>) {
        dispatcher.dispatch {
            delagate.resumeWith(result)
        }
    }

}