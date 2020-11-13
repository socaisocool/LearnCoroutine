package com.cool.cai.core

import com.cool.cai.Deferred
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine

class DeferredCoroutine<T>(context: CoroutineContext) : AbstractCoroutine<T>(context), Deferred<T> {

    override suspend fun await(): T {
        return when (val currentState = state.get()) {
            is CoroutineState.InComplete -> awaitSuspend()
            is CoroutineState.Complete<*> -> (currentState.value as T?) ?: throw currentState.exception!!
        }
    }

    private suspend fun awaitSuspend() = suspendCoroutine<T> { continuation ->
        doOnCompleted { result -> continuation.resumeWith(result) }
    }

}