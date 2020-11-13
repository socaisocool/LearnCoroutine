package com.cool.cai.core

import com.cool.cai.*
import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class AbstractCoroutine<T>(override val context: CoroutineContext) : Job, Continuation<T> {

    protected val state = AtomicReference<CoroutineState>(CoroutineState.InComplete())

    override val isActive: Boolean
        get() = state.get() is CoroutineState.InComplete

    override val isComplete: Boolean
        get() = state.get() is CoroutineState.Complete<*>

    override fun invokeOnCompletion(onComplete: OnComplete): Disposable {
        return doOnCompleted {
            onComplete()
        }
    }

    override fun remove(disposable: Disposable) {
        state.updateAndGet { pre ->
            when (pre) {
                is CoroutineState.InComplete -> {
                    CoroutineState.InComplete().from(pre).without(disposable)
                }
                is CoroutineState.Complete<*> -> {
                    pre
                }
            }
        }
    }

    override suspend fun join() {
        when (state.get()) {
            is CoroutineState.InComplete -> return joinSuspend()
            is CoroutineState.Complete<*> -> return
        }
    }

    /**
     * 这个挂起函数最终在suspend main这个外部协程体中执行
     */
    private suspend fun joinSuspend() = suspendCoroutine<Unit> { continuation ->
        doOnCompleted { result ->//这里的result是内部已执行完协程体的结果
            continuation.resume(Unit)//这句才能唤醒main协程体所在的线程
        }
    }


    protected fun doOnCompleted(block: (Result<T>) -> Unit): Disposable {
        val disposable = CompletionHandlerDisposable(this, block)
        val newState = state.updateAndGet { pre ->
            when (pre) {
                is CoroutineState.InComplete -> {
                    CoroutineState.InComplete().from(pre).with(disposable)
                }
                is CoroutineState.Complete<*> -> {
                    pre
                }
            }
        }
        (newState as? CoroutineState.Complete<T>)?.let {
            block(
                when {
                    it.value != null -> Result.success(it.value)
                    it.exception != null -> Result.failure(it.exception)
                    else -> throw IllegalStateException("won't happen")
                }
            )
        }
        return disposable
    }

    override fun resumeWith(result: Result<T>) {
        val newState = state.updateAndGet { preState ->
            when (preState) {
                is CoroutineState.InComplete ->
                    CoroutineState.Complete(
                        result.getOrNull(),
                        result.exceptionOrNull()
                    ).from(preState)
                is CoroutineState.Complete<*> -> {
                    throw IllegalStateException("already complete")
                }
            }
        }
//        (newState as CoroutineState.Complete<T>).exception?.let {  }
        newState.notifyCompletion(result)
        newState.clear()
    }

}