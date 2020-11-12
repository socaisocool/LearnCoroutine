package com.cool.cai.example

import com.cool.cai.delay
import com.cool.cai.dispatcher.Dispatchers
import com.cool.cai.launch
import com.cool.cai.runBlocking
import com.cool.cai.utils.log
import sun.rmi.server.Dispatcher


suspend fun main() {
    log(1)
    val job = launch {
        log(2)
        delay(100)
        log(3)
    }
    log(4)
//    job.join()
    log(5)
    delay(100)
    log(6)
}