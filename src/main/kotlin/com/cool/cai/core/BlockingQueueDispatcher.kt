package com.cool.cai.core

import com.cool.cai.dispatcher.Dispatcher
import com.sun.jndi.toolkit.ctx.Continuation
import java.awt.EventQueue
import java.util.concurrent.LinkedBlockingDeque
import kotlin.coroutines.CoroutineContext

typealias EventTask = () -> Unit


/**
 * 生产者
 */
class BlockingQueueDispatcher : LinkedBlockingDeque<EventTask>(), Dispatcher {

    override fun dispatch(block: () -> Unit) {
        offer { block() }
    }

}

class BlockingCoroutine<T>(context: CoroutineContext, private val eventQueue: LinkedBlockingDeque<EventTask>) :
    AbstractCoroutine<T>(context) {

    fun joinBlocking(): T {
        while (!isComplete) {
            println("joinBlocking")
            eventQueue.take().invoke()
        }
        return (state.get() as CoroutineState.Complete<T>).let {
            it.value ?: throw it.exception!!
        }
    }

}