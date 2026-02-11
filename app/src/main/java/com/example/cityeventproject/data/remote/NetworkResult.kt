package com.example.cityeventproject.data.remote

sealed class NetworkResult<out T> {
    data class Success<T>(val value: T) : NetworkResult<T>()
    data class HttpError(val code: Int, val message: String) : NetworkResult<Nothing>()
    data class NetworkError(val message: String) : NetworkResult<Nothing>()
    data class UnknownError(val message: String) : NetworkResult<Nothing>()
}
