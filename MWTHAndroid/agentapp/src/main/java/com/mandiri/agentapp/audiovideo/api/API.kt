package com.mandiri.agentapp.audiovideo.api

import android.graphics.Bitmap
import android.text.TextUtils

import com.mandiri.agentapp.audiovideo.model.GroupResponse
import com.mandiri.agentapp.audiovideo.model.RegisterResponse
import com.mandiri.agentapp.audiovideo.model.Token
import com.mandiri.agentapp.audiovideo.model.User
import com.mandiri.agentapp.audiovideo.other.ToStringConverterFactory
import com.google.gson.GsonBuilder

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap

import io.realm.Realm
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Created by andreyyoshuamanik on 9/13/17.
 */

object API {

    private var retrofit: Retrofit? = null
//    val API_BASE_URL = "http://10.0.2.2:8080"
        val API_BASE_URL = "http://172.20.10.3:8080"
    //    public static final String API_BASE_URL = "http://104.131.178.168";
    //    public static final String API_BASE_URL = "http://172.168.11.57:8080";


    private val httpClient = OkHttpClient.Builder()

    private val builder = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(ToStringConverterFactory())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .addConverterFactory(ScalarsConverterFactory.create())

    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, null)
    }

    private fun <S> createService(serviceClass: Class<S>, authToken: String?): S {

        if (retrofit == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            interceptor.level = HttpLoggingInterceptor.Level.HEADERS
            val client = httpClient.addInterceptor(interceptor).build()

            retrofit = builder
                .client(client)
                .build()
        }

        if (!TextUtils.isEmpty(authToken)) {
            val iter = httpClient.interceptors().iterator()

            while (iter.hasNext()) {
                val interceptor = iter.next()

                if (interceptor.javaClass == AuthenticationInterceptor::class.java)
                    iter.remove()
            }

            val interceptor = AuthenticationInterceptor(authToken)

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)

                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
                httpClient.addInterceptor(httpLoggingInterceptor)

                builder.client(httpClient.build())
                retrofit = builder.build()
            }
        }

        return retrofit!!.create(serviceClass)
    }

    private class AuthenticationInterceptor internal constructor(internal var jwtToken: String?) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var modifiedRequest = chain.request()
            if (jwtToken != null && jwtToken != "") {
                modifiedRequest = modifiedRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + jwtToken!!)
                    .build()
            }

            return chain.proceed(modifiedRequest)
        }
    }

    // This method  converts Bitmap to RequestBody
    private fun toImageRequestBody(bitmap: Bitmap, name: String): MultipartBody.Part {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()
        val photo = RequestBody.create(MediaType.parse("multipart/form-data"), bitmapdata)
        return MultipartBody.Part.create(
            okhttp3.Headers.of(
                "Content-Disposition",
                "form-data; name=\"$name\"; filename=\"image.png\"\r\nContent-Type: image/png\r\n\r\n\r\n"
            ), photo
        )

    }


    private fun toTextRequestBody(value: String, name: String): MultipartBody.Part {
        return MultipartBody.Part.create(
            okhttp3.Headers.of("Content-Disposition", "form-data; name=\"$name\""),
            RequestBody.create(MediaType.parse("text/plain"), value)
        )
    }


    fun registerPhone(phone: String, responseHandler: ResponseHandler<RegisterResponse>) {
        val body = HashMap<String, String>()
        body["phone"] = phone
        retrofit = null
        createService(Service::class.java).registerPhone(body).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: retrofit2.Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    responseHandler.response(true, response.body(), null)
                    return
                }

                try {
                    val errorBody = response.errorBody()!!.string()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (errorBody != "") errorBody else response.message()
                    )
                } catch (e: IOException) {
                    responseHandler.response(response.isSuccessful, response.body(), response.message())
                    e.printStackTrace()
                } catch (ex: Exception) {
                    val rawMessage = response.raw().message()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (rawMessage != "") rawMessage else response.message()
                    )
                    ex.printStackTrace()
                }

            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                responseHandler.response(false, null, t.message)
            }
        })
    }


    fun verifyCode(code: String, responseHandler: ResponseHandler<User>) {
        val body = HashMap<String, String>()
        body["code"] = code

        val realm = Realm.getDefaultInstance()
        val userToken = realm.where(Token::class.java).findFirst()
        var service: Service? = null
        if (userToken != null) {
            service = createService(Service::class.java, userToken.token)
        } else {
            retrofit = null
            service = createService(Service::class.java)
        }
        realm.close()

        service.verifyCode(body).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {
                if (response.isSuccessful) {
                    responseHandler.response(true, response.body(), null)
                    return
                }

                try {
                    val errorBody = response.errorBody()!!.string()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (errorBody != "") errorBody else response.message()
                    )
                } catch (e: IOException) {
                    responseHandler.response(response.isSuccessful, response.body(), response.message())
                    e.printStackTrace()
                } catch (ex: Exception) {
                    val rawMessage = response.raw().message()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (rawMessage != "") rawMessage else response.message()
                    )
                    ex.printStackTrace()
                }

            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                responseHandler.response(false, null, t.message)
            }
        })
    }

    fun registerFirebaseToken(token: String, responseHandler: ResponseHandler<String>) {
        val body = HashMap<String, String>()
        body["token"] = token

        val realm = Realm.getDefaultInstance()
        val userToken = realm.where(Token::class.java).findFirst()
        var service: Service? = null
        if (userToken != null) {
            service = createService(Service::class.java, userToken.token)
        } else {
            retrofit = null
            service = createService(Service::class.java)
        }
        realm.close()

        service.registerFirebaseToken(body).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if (response.isSuccessful) {
                    responseHandler.response(true, response.body(), null)
                    return
                }

                try {
                    val errorBody = response.errorBody()!!.string()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (errorBody != "") errorBody else response.message()
                    )
                } catch (e: IOException) {
                    responseHandler.response(response.isSuccessful, response.body(), response.message())
                    e.printStackTrace()
                } catch (ex: Exception) {
                    val rawMessage = response.raw().message()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (rawMessage != "") rawMessage else response.message()
                    )
                    ex.printStackTrace()
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                responseHandler.response(false, null, t.message)
            }
        })
    }


    fun getUsers(responseHandler: ResponseHandler<List<User>>) {
        val realm = Realm.getDefaultInstance()
        val userToken = realm.where(Token::class.java).findFirst()
        var service: Service? = null
        if (userToken != null) {
            service = createService(Service::class.java, userToken.token)
        } else {
            retrofit = null
            service = createService(Service::class.java)
        }
        realm.close()

        service.users.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: retrofit2.Response<List<User>>) {
                if (response.isSuccessful) {
                    responseHandler.response(true, response.body(), null)
                    return
                }

                try {
                    val errorBody = response.errorBody()!!.string()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (errorBody != "") errorBody else response.message()
                    )
                } catch (e: IOException) {
                    responseHandler.response(response.isSuccessful, response.body(), response.message())
                    e.printStackTrace()
                } catch (ex: Exception) {
                    val rawMessage = response.raw().message()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (rawMessage != "") rawMessage else response.message()
                    )
                    ex.printStackTrace()
                }

            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                responseHandler.response(false, null, t.message)
            }
        })
    }

    fun getAllUsersWithContactPhones(contactPhones: ArrayList<String>, responseHandler: ResponseHandler<List<User>>) {
        val realm = Realm.getDefaultInstance()
        val userToken = realm.where(Token::class.java).findFirst()
        var service: Service? = null
        if (userToken != null) {
            service = createService(Service::class.java, userToken.token)
        } else {
            retrofit = null
            service = createService(Service::class.java)
        }
        realm.close()

        val bodyMaps = HashMap<String, ArrayList<String>>()
        bodyMaps["phones"] = contactPhones
        service.getAllUsersWithContactPhones(bodyMaps).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: retrofit2.Response<List<User>>) {
                if (response.isSuccessful) {
                    responseHandler.response(true, response.body(), null)
                    return
                }

                try {
                    val errorBody = response.errorBody()!!.string()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (errorBody != "") errorBody else response.message()
                    )
                } catch (e: IOException) {
                    responseHandler.response(response.isSuccessful, response.body(), response.message())
                    e.printStackTrace()
                } catch (ex: Exception) {
                    val rawMessage = response.raw().message()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (rawMessage != "") rawMessage else response.message()
                    )
                    ex.printStackTrace()
                }

            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                responseHandler.response(false, null, t.message)
            }
        })
    }

    fun postReadAChat(chat: String, responseHandler: ResponseHandler<String>) {
        val realm = Realm.getDefaultInstance()
        val userToken = realm.where(Token::class.java).findFirst()
        var service: Service? = null
        if (userToken != null) {
            service = createService(Service::class.java, userToken.token)
        } else {
            retrofit = null
            service = createService(Service::class.java)
        }
        realm.close()

        val content = HashMap<String, String>()
        content["content"] = chat
        service.postDeliveredAChat(content).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if (response.isSuccessful) {
                    responseHandler.response(true, response.body(), null)
                    return
                }

                try {
                    val errorBody = response.errorBody()!!.string()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (errorBody != "") errorBody else response.message()
                    )
                } catch (e: IOException) {
                    responseHandler.response(response.isSuccessful, response.body(), response.message())
                    e.printStackTrace()
                } catch (ex: Exception) {
                    val rawMessage = response.raw().message()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (rawMessage != "") rawMessage else response.message()
                    )
                    ex.printStackTrace()
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                responseHandler.response(false, null, t.message)
            }
        })
    }

    fun postProfile(name: String, profilePict: Bitmap?, responseHandler: ResponseHandler<User>) {
        val realm = Realm.getDefaultInstance()
        val userToken = realm.where(Token::class.java).findFirst()
        var service: Service? = null
        if (userToken != null) {
            service = createService(Service::class.java, userToken.token)
        } else {
            retrofit = null
            service = createService(Service::class.java)
        }
        realm.close()

        val requestBody = ArrayList<MultipartBody.Part>()

        requestBody.add(toTextRequestBody(name, "name"))
        if (profilePict != null) {
            requestBody.add(toImageRequestBody(profilePict, "image"))
        }

        service.postProfile(requestBody).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {
                if (response.isSuccessful) {
                    responseHandler.response(true, response.body(), null)
                    return
                }

                try {
                    val errorBody = response.errorBody()!!.string()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (errorBody != "") errorBody else response.message()
                    )
                } catch (e: IOException) {
                    responseHandler.response(response.isSuccessful, response.body(), response.message())
                    e.printStackTrace()
                } catch (ex: Exception) {
                    val rawMessage = response.raw().message()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (rawMessage != "") rawMessage else response.message()
                    )
                    ex.printStackTrace()
                }

            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                responseHandler.response(false, null, t.message)
            }
        })
    }

    fun postCreateGroup(
        subject: String,
        groupPict: Bitmap?,
        participants: Array<String>?,
        responseHandler: ResponseHandler<GroupResponse>
    ) {
        val realm = Realm.getDefaultInstance()
        val userToken = realm.where(Token::class.java).findFirst()
        var service: Service? = null
        if (userToken != null) {
            service = createService(Service::class.java, userToken.token)
        } else {
            retrofit = null
            service = createService(Service::class.java)
        }
        realm.close()

        val requestBody = ArrayList<MultipartBody.Part>()

        requestBody.add(toTextRequestBody(subject, "subject"))
        if (groupPict != null) {
            requestBody.add(toImageRequestBody(groupPict, "image"))
        }
        if (participants != null) {
            requestBody.add(toTextRequestBody(TextUtils.join(",", participants), "participants"))
        }

        service.postCreateGroup(requestBody).enqueue(object : Callback<GroupResponse> {
            override fun onResponse(call: Call<GroupResponse>, response: retrofit2.Response<GroupResponse>) {
                if (response.isSuccessful) {
                    responseHandler.response(true, response.body(), null)
                    return
                }

                try {
                    val errorBody = response.errorBody()!!.string()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (errorBody != "") errorBody else response.message()
                    )
                } catch (e: IOException) {
                    responseHandler.response(response.isSuccessful, response.body(), response.message())
                    e.printStackTrace()
                } catch (ex: Exception) {
                    val rawMessage = response.raw().message()
                    responseHandler.response(
                        response.isSuccessful,
                        response.body(),
                        if (rawMessage != "") rawMessage else response.message()
                    )
                    ex.printStackTrace()
                }

            }

            override fun onFailure(call: Call<GroupResponse>, t: Throwable) {
                responseHandler.response(false, null, t.message)
            }
        })
    }

    private interface Service {

        @get:GET("users")
        val users: Call<List<User>>

        @POST("registerPhone")
        fun registerPhone(@Body authData: HashMap<String, String>): Call<RegisterResponse>

        @POST("verifyCode")
        fun verifyCode(@Body authData: HashMap<String, String>): Call<User>

        @POST("firebaseToken")
        fun registerFirebaseToken(@Body authData: HashMap<String, String>): Call<String>

        @POST("deliveredChat")
        fun postDeliveredAChat(@Body chat: HashMap<String, String>): Call<String>

        @POST("users")
        fun getAllUsersWithContactPhones(@Body contactPhones: HashMap<String, ArrayList<String>>): Call<List<User>>

        @Multipart
        @POST("profile")
        fun postProfile(@Part params: List<MultipartBody.Part>): Call<User>

        @Multipart
        @POST("createGroup")
        fun postCreateGroup(@Part params: List<MultipartBody.Part>): Call<GroupResponse>

    }

}


