@file:Suppress("UNUSED")

package ru.grishankov.network.retrofit

/**
 * Network response
 */
sealed class Response<out Data : Any, out Failure : Any> {

    /**
     * Successful response
     *
     * @param data response data
     * @param code response code
     */
    data class Ok<Data : Any>(val data: Data, val code: Int) : Response<Data, Nothing>()

    /**
     * Wrong response / Wrong response API
     *
     * @param code response code
     */
    data class ApiError<Failure : Any>(val error: Failure, val code: Int) : Response<Nothing, Failure>()

    /**
     * Network response error
     *
     * @param error instance of throw
     */
    data class NetworkError(val error: Throwable?) : Response<Nothing, Nothing>()

    /**
     * Unknown response error
     *
     * @param error instance of throw
     */
    data class UnknownError(val error: Throwable?) : Response<Nothing, Nothing>()
}

/**
 * Handling Error
 */
inline fun <Data : Any, Failure : Any> Response<Data, Failure>.handling(
    crossinline onOk: Response.Ok<Data>.() -> Unit = {},
    crossinline onApiError: Response.ApiError<Failure>.() -> Unit = {},
    crossinline onNetworkError: Response.NetworkError.() -> Unit = {},
    crossinline onUnknownError: Response.UnknownError.() -> Unit = {},
) {
    when (this) {
        is Response.Ok -> onOk(this)
        is Response.ApiError -> onApiError(this)
        is Response.NetworkError -> onNetworkError(this)
        is Response.UnknownError -> onUnknownError(this)
    }
}
