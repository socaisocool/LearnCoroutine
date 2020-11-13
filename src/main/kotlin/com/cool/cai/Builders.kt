package com.cool.cai

import com.cool.cai.core.BlockingCoroutine
import com.cool.cai.core.BlockingQueueDispatcher
import com.cool.cai.core.DeferredCoroutine
import com.cool.cai.core.StandardCoroutine
import com.cool.cai.dispatcher.DispatcherContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

private var coroutineIndex = AtomicInteger(0)
fun launch(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend () -> Unit
): Job {
    val completion = StandardCoroutine(newCoroutineContext(context))
    block.startCoroutine(completion)
    return completion;
}

fun <T> runBlocking(context: CoroutineContext = EmptyCoroutineContext, block: suspend () -> T): T {
    val eventQueue = BlockingQueueDispatcher()
    val newContext = context + DispatcherContext(eventQueue)
    val completion = BlockingCoroutine<T>(newContext, eventQueue)
    block.startCoroutine(completion)
    return completion.joinBlocking()
}

fun <T> async(context: CoroutineContext = EmptyCoroutineContext, block: suspend () -> T): Deferred<T> {
    val completion = DeferredCoroutine<T>(newCoroutineContext(context))
    block.startCoroutine(completion)
    return completion
}


fun newCoroutineContext(context: CoroutineContext): CoroutineContext {
    val combined = context + CoroutineName("@coroutine#${coroutineIndex.getAndIncrement()}")
    //combined既不是Dispatchers.Default子线程调度器也不存在其他拦截器
    return if (combined !== Dispatchers.Default && combined[ContinuationInterceptor] == null)
        combined + Dispatchers.Default else combined + Dispatchers.Default
}