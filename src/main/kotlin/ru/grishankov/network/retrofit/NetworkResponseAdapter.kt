package ru.grishankov.network.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import java.lang.reflect.Type

class NetworkResponseAdapter<Data : Any, Failure : Any>(
    private val successType: Type,
    private val errorBodyConverter: Converter<ResponseBody, Failure>
) : CallAdapter<Data, Call<Response<Data, Failure>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<Data>): Call<Response<Data, Failure>> {
        return NetworkResponseCall(call, errorBodyConverter)
    }
}