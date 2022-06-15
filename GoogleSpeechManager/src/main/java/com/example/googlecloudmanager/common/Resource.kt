package com.example.googlecloudmanager.common

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Complete<T>(data: T?) : Resource<T>(data)
    class Listen<T>(data: T?) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
