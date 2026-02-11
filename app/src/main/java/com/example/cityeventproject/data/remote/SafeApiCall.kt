package com.example.cityeventproject.data.remote

import retrofit2.HttpException
import java.io.IOException
import kotlinx.coroutines.CancellationException

suspend fun <T> safeApiCall(block: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(block())
    } catch (ce: CancellationException) {
        throw ce
    } catch (he: HttpException) {
        NetworkResult.HttpError(he.code(), he.message())
    } catch (io: IOException) {
        NetworkResult.NetworkError(io.message ?: "Network error")
    } catch (t: Throwable) {
        NetworkResult.UnknownError(t.message ?: "Unknown error")
    }
}
