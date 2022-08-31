@file:Suppress("UNUSED")

package ru.grishankov.network.retrofit

import okhttp3.Request
import okhttp3.ResponseBody
import okio.IOException
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import java.util.concurrent.TimeUnit

class NetworkResponseCall<Data : Any, Failure : Any>(
    private val delegate: Call<Data>,
    private val errorConverter: Converter<ResponseBody, Failure>
) : Call<Response<Data, Failure>> {

    /**
     * Asynchronously send the request and notify `callback` of its response or if an error
     * occurred talking to the server, creating the request, or processing the response.
     */
    override fun enqueue(callback: Callback<Response<Data, Failure>>) {
        return delegate.enqueue(object : Callback<Data> {
            /**
             * Invoked for a received HTTP response.
             *
             *
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call Response#isSuccessful to determine if the response indicates success.
             */
            override fun onResponse(call: Call<Data>, response: retrofit2.Response<Data>) {
                val body = response.body()
                val code = response.code()
                val error = response.errorBody()

                if (response.isSuccessful) {
                    if (body != null) {
                        callback.onResponse(this@NetworkResponseCall, retrofit2.Response.success(Response.Ok(body, code)))
                    } else {
                        callback.onResponse(this@NetworkResponseCall, retrofit2.Response.success(Response.UnknownError(null)))
                    }
                } else {
                    val errorBody = when {
                        error == null -> null
                        error.contentLength() == 0L -> null
                        else -> try { errorConverter.convert(error) } catch (ex: Exception) { null }
                    }
                    if (errorBody != null) {
                        callback.onResponse(
                            this@NetworkResponseCall,
                            retrofit2.Response.success(Response.ApiError(errorBody, code))
                        )
                    } else {
                        callback.onResponse(
                            this@NetworkResponseCall,
                            retrofit2.Response.success(Response.UnknownError(null))
                        )
                    }
                }
            }

            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected exception
             * occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<Data>, t: Throwable) {
                val networkResponse = when (t) {
                    is IOException -> Response.NetworkError(t)
                    else -> Response.UnknownError(t)
                }
                callback.onResponse(this@NetworkResponseCall, retrofit2.Response.success(networkResponse))
            }
        })
    }


    /**
     * Returns a timeout that spans the entire call: resolving DNS, connecting, writing the request
     * body, server processing, and reading the response body. If the call requires redirects or
     * retries all must complete within one timeout period.
     */
    override fun timeout() = Timeout().timeout(30, TimeUnit.SECONDS)

    /**
     * Create a new, identical call to this one which can be enqueued or executed even if this call
     * has already been.
     */
    override fun clone() = NetworkResponseCall(delegate.clone(), errorConverter)

    /**
     * Synchronously send the request and return its response.
     *
     * @throws IOException if a problem occurred talking to the server.
     * @throws RuntimeException (and subclasses) if an unexpected error occurs creating the request or
     * decoding the response.
     */
    override fun execute(): retrofit2.Response<Response<Data, Failure>> {
        throw UnsupportedOperationException("NetworkResponseCall doesn't support execute")
    }

    /**
     * Returns true if this call has been either [executed][.execute] or [ ][.enqueue]. It is an error to execute or enqueue a call more than once.
     */
    override fun isExecuted() = delegate.isExecuted

    /**
     * Cancel this call. An attempt will be made to cancel in-flight calls, and if the call has not
     * yet been executed it never will be.
     */
    override fun cancel() = delegate.cancel()

    /** True if [.cancel] was called.  */
    override fun isCanceled() = delegate.isCanceled

    /** The original HTTP request.  */
    override fun request(): Request = delegate.request()
}