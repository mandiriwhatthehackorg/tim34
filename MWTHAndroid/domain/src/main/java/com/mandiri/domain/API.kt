package com.mandiri.domain

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import com.mandiri.domain.utils.LiveDataCallAdapterFactory
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class API {

    companion object {
        private val clientId = "571411d8-162f-4315-b0bc-fe24c1f9658c"
        private val clientSecret = "beb9808b-2699-4960-a384-43fe68180f30"
        public val appId = "15875dd4-8022-4a57-9a31-61aad25e6d88"
        private val jwtURL = "https://apigateway.mandiriwhatthehack.com/rest/pub/apigateway/jwt/"
        private val onboardingUrl = "https://oob.mandiriwhatthehack.com/api/"
        private val baseUrl = "https://apigateway.mandiriwhatthehack.com/gateway/"

        val logoutObservable = MediatorLiveData<String>()

        val retrofit: Retrofit
            get() {
                val clientBuilder = OkHttpClient.Builder()

                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                clientBuilder.addInterceptor(interceptor)

                val headerInterceptor = HttpLoggingInterceptor()
                headerInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
                clientBuilder.addInterceptor(headerInterceptor)

                clientBuilder.addInterceptor {


                    val chain = it
                    val request = chain.request()
                    val newRequest: Request
                    var response: Response

                    try {
                        Log.d("addHeader", "Before")
                        newRequest = request.newBuilder()
                            .addHeader("Authorization", "Bearer ${Constants.Token.value}")
                            .build()

                        Log.d("addHeader", "after")
                        response = chain.proceed(newRequest)

                    } catch (e: Exception) {
                        Log.d("addHeader", "Error")
                        e.printStackTrace()
                        response = chain.proceed(request)
                    }

                    if (response.code() == 401) {
                        logoutObservable.postValue("Your session has expired. Please Re-Login")
                    }

                    response

                }


                val client = clientBuilder.build()

                return Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .baseUrl(baseUrl)
                    .client(client)
                    .build()
            }

        val retrofitOnboarding: Retrofit
            get() {
                val clientBuilder = OkHttpClient.Builder()

                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                clientBuilder.addInterceptor(interceptor)

                val headerInterceptor = HttpLoggingInterceptor()
                headerInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
//                clientBuilder.addInterceptor(headerInterceptor)

                clientBuilder.addInterceptor {
                    val chain = it
                    val request = chain.request()
                    val newRequest: Request
                    var response: Response

                    try {
                        Log.d("addHeader", "Before")
                        val requestBuilder = request.newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")

                        Constants.Token.value?.let {
                            requestBuilder.addHeader("Authorization", "Bearer ${it}")
                        }

                        val newRequest = requestBuilder.build()

                        Log.d("addHeader", "after")
                        response = chain.proceed(newRequest)

                    } catch (e: Exception) {
                        Log.d("addHeader", "Error")
                        e.printStackTrace()
                        response = chain.proceed(request)
                    }
                    response
                }


                val client = clientBuilder.build()

                return Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .baseUrl(onboardingUrl)
                    .client(client)
                    .build()
            }


        val retrofitJWT: Retrofit
            get() {
                val clientBuilder = OkHttpClient.Builder()

                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                clientBuilder.addInterceptor(interceptor)
                val headerInterceptor = HttpLoggingInterceptor()
                headerInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
                clientBuilder.addInterceptor(headerInterceptor)

                val authToken = Credentials.basic(clientId, clientSecret)
                clientBuilder.addInterceptor {
                    val chain = it
                    val request = chain.request()
                    val newRequest: Request
                    var response: Response

                    try {
                        newRequest = request.newBuilder()
                            .addHeader("Authorization", authToken)
                            .addHeader("Accept", "application/json")
                            .build()
                        response = chain.proceed(newRequest)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        response = chain.proceed(request)
                    }
                    response

                }

                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }

                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }
                })
                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.getSocketFactory()
                clientBuilder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                clientBuilder.hostnameVerifier { s, sslSession -> true }


                val client = clientBuilder.build()

                return Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .baseUrl(jwtURL)
                    .client(client)
                    .build()
            }


    }
}