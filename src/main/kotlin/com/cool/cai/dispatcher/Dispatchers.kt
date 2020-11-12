package com.cool.cai.dispatcher

object Dispatchers {
    val Default by lazy {
        DispatcherContext(DefaultDispatcher)
    }
}