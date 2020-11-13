package com.cool.cai.example

import com.cool.cai.delay
import com.cool.cai.dispatcher.Dispatchers
import com.cool.cai.launch
import com.cool.cai.runBlocking
import com.cool.cai.utils.log
import sun.rmi.server.Dispatcher

/*
    runBlocking主要用来替代 suspend main的runSpend方法，也就是导致主线程阻塞，保证内部的协程体先执行完，再回到主线程的
    执行流程
 */
fun main() {
    runBlocking {
        log(1)
//        val job = launch {
//            log(2)
//            delay(100)
//            log(3)
//        }
        log(4)
//        job.join()//挂起点 launch执行完才会触发 添加到队列
        log(5)
//        delay(100)//挂起点
        log(6)
    }
}

