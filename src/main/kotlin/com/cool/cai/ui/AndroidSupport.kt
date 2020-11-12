package com.cool.cai.ui

import android.os.Handler
import android.os.Looper
import com.cool.cai.dispatcher.Dispatcher
import java.rmi.Remote

object HandlerDispatcher : Dispatcher {

    private val handler = Handler(Looper.getMainLooper())
    override fun dispatch(block: () -> Unit) {
        handler.post {
            block()
        }
    }


}