package com.cool.cai.example

import com.cool.cai.async
import com.cool.cai.delay
import com.cool.cai.utils.log

/*
 internal fun runSuspend(block: suspend () -> Unit) {
    val run = RunSuspend()
    block.startCoroutine(run)
    run.await()

    main{外协程体} async{内协程体}
    外协程中的内容除内协程体的内容外被执行完之后，主线程就处于阻塞状态，等待内协程体执行完后
    才会被唤醒，await之前，外协程体的代码与内协程体的代码分别处于两个线程，相互竞争执行权
}
 */
suspend fun main() {
    log(1)
    val deferred = async {
        log(2)
        delay(1000)
        log(3)
        "hello"
    }
    log(4)
    log(5, deferred.await())
    log("-")//这句执行完，主线程就处于阻塞状态了
}