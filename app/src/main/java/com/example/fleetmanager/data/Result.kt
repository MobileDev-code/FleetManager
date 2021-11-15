package com.example.fleetmanager.data

sealed class Result<T> {
    companion object {

        fun <T> success(data: T): Success<T> = Success(data)
        fun <T> failure(error: Throwable?) = Failure<T>(error)
    }

    fun unsafeUnwrap(): T {
        return (this as Success).data
    }

    fun unsafeUnwrapErr(): Throwable? {
        return (this as Failure).error
    }
}

data class Success<T>(val data: T) : Result<T>()

data class Failure<T>(val error: Throwable?) : Result<T>()