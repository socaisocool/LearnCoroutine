package com.cool.cai.core

import com.cool.cai.Job


typealias OnCompleteT<T> = (Result<T>) -> Unit

interface Disposable {
    fun dispose()
}

class CompletionHandlerDisposable<T>(val job: Job, val onCompleteT: OnCompleteT<T>) :
    Disposable {

    override fun dispose() {
        job.remove(this)
    }

}

sealed class DisposableList {
    object Nil : DisposableList()
    class Cons(val head: Disposable, val tail: DisposableList) : DisposableList()
}

/**
 * 移除
 */
fun DisposableList.remove(disposable: Disposable): DisposableList {
    return when (this) {
        DisposableList.Nil -> this
        is DisposableList.Cons -> {// a b nil
            if (head == disposable) {
                return tail
            } else {
                DisposableList.Cons(head, tail.remove(disposable))
            }
        }
    }
}

tailrec fun DisposableList.forEach(action: (Disposable) -> Unit): Unit = when (this) {
    DisposableList.Nil -> Unit
    is DisposableList.Cons -> {
        action(this.head)
        this.tail.forEach(action)
    }
}

/**
 * 遍历并回调指定类型的回调方法
 */
inline fun <reified T : Disposable> DisposableList.loopOn(crossinline action: (T) -> Unit) = forEach {
    when (it) {
        is T -> action(it)
    }
}

