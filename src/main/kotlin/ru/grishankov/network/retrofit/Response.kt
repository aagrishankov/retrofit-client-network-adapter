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
    data class ApiError<Data : Any, Failure : Any>(val data: Data, val code: Int) : Response<Nothing, Failure>()

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