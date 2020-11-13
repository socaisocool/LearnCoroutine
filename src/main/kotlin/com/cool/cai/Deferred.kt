package com.cool.cai

interface Deferred<T> : Job {
    suspend fun await(): T
}