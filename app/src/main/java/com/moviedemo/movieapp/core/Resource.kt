package com.moviedemo.movieapp.core

import java.lang.Exception

sealed class Resource<out T> {
    class Loading<out T>: com.moviedemo.movieapp.core.Resource<T>()
    data class Success<out T>(val data: T): com.moviedemo.movieapp.core.Resource<T>()
    data class Failure(val exception: Exception): com.moviedemo.movieapp.core.Resource<Nothing>()
}