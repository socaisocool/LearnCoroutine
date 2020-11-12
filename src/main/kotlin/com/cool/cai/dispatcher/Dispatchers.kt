package com.cool.cai.dispatcher

import com.cool.cai.ui.HandlerDispatcher

object Dispatchers {
    val Default by lazy {
        DispatcherContext(DefaultDispatcher)
    }

    val Android by lazy {
        DispatcherContext(HandlerDispatcher)
    }
}