package com.terricom.mytype.internet

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.terricom.mytype.data.UserSignInData
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .build()

val BASE_URL = "https://api.appworks-school.tw/api/1.0/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(client)
    .build()


interface RetrofitApiService {
    @FormUrlEncoded
    @POST("user/signin")
    fun postUserSignin(@Field("provider") provider: String = "facebook",
                       @Field("access_token") access_token: String?) : Deferred<UserSignInData>
}


object RetrofitApi {
    val retrofitService : RetrofitApiService by lazy { retrofit.create(
        RetrofitApiService::class.java) }
}