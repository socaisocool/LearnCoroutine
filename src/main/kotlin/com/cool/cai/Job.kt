package com.cool.cai

import com.cool.cai.core.Disposable
import kotlin.coroutines.CoroutineContext

typealias OnComplete = () -> Unit


interface Job : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<Job>

    override val key: CoroutineContext.Key<*> get() = Job

    val isActive: Boolean

    val isComplete: Boolean

    fun invokeOnCompletion(onComplete: OnComplete): Disposable
    fun remove(disposable: Disposable)

    suspend fun join()
}