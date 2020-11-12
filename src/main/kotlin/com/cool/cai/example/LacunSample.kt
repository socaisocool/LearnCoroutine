package com.cool.cai.example
import com.cool.cai.delay
import com.cool.cai.launch
import com.cool.cai.utils.log
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/*
    suspend fun main(){} 会被编译器转换为：
    internal fun runSuspend(block: suspend () -> Unit) {
    val run = RunSuspend()
    block.startCoroutine(run)
    run.await()
    }
    也就是开启了一个外部协程，协程体就是main函数体，外部协程调用了await,
    它让外部协程所在的线程(也就是例子中的主线程)处于等待状态(await调用了wait)
    只有在block中通过suspendCoroutine {coroutine->coroutine.resume}去触发run中的rusumeWith回调，在该回调会重新唤醒
    当前线程(主线程)，在例子中Job的join中的joinSuspend()就是suspendCoroutine {coroutine->coroutine.resume}这个格式

    job.join()会触发唤醒外部协程所在的线程的机制，job.join()的内部又被包装了，保证只有当内部协程流程执行结束后(也就是内部协程的resumeWith被回调)
    才触发外部协程再恢复

 */
suspend fun main() {
    val job = launch {
        log(1)
        log(2)
        delay({
            println("延迟两秒打印")
        }, 2)
        val result = hellow()
        log(2, result)
    }
    log(job.isActive)
    job.join()
    log(job.isActive)
}

/**
 * isDaemon为true 表示线程是守护线程，也就是主线程执行完毕后，
 * 守护线程确认没有其他的子线程后就会自己退出，例子当中只有main线程和一个守护线程
 * 所以只要main一退出，守护线程就会立刻挂逼,也就不会调用it.resume，因此内部的协程就一直处于挂起状态
 */
suspend fun hellow(): Int = suspendCoroutine {
    thread(isDaemon = true) {
        Thread.sleep(1000)
        it.resume(10086)
    }
}