package it.prassel.fivedaysforecast.rest

import android.content.Context
import android.util.Log


import java.io.IOException
import java.util.concurrent.TimeUnit

import javax.net.ssl.X509TrustManager


import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http.RealResponseBody
import okio.GzipSource
import okio.Okio
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by ivan on 11/11/16.
 */

interface ICalServerApi {


    @GET("Search")
    fun getProducts(@Query("one") one : String, @Query("two") two : String ,
    @Query("key") key : String ) : Call<List<String>>

    @GET
    fun getICAL(@Url url: String?): Call<ResponseBody>

    class TrustEveryoneManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

        override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate>? {
            return null
        }
    }


    class BasicAuthInterceptor(user: String, password: String) : Interceptor {

        private val credentials: String

        init {
            this.credentials = Credentials.basic(user, password)
        }

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build()
            return chain.proceed(authenticatedRequest)
        }

    }


    object Factory {

        internal val TAG = "ICalServerApi.Factory"

        fun create(context: Context): ICalServerApi? {

            val builder = OkHttpClient().newBuilder()
            builder.readTimeout(30, TimeUnit.SECONDS)
            builder.connectTimeout(60, TimeUnit.SECONDS)
            builder.followRedirects(true)

            //            if (BuildConfig.DEBUG) {
            //                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            //                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //                builder.addInterceptor(interceptor);
            //            }

            //builder.addInterceptor(new UnzippingInterceptor());

            //Extra Headers

            //builder.addNetworkInterceptor().add(chain -> {
            //  Request request = chain.request().newBuilder().addHeader("Authorization", authToken).build();
            //  return chain.proceed(request);
            //});

            //builder.addInterceptor(new UnzippingInterceptor());

            /*
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder builder=original.newBuilder();

                    builder.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON);

                    Request request = builder.method(original.method(), original.body()).build();

                    Log.v(TAG, String.format("\n-- <ZIPVIMSServerApi.Factory> Request:\nheaders:\n%s", request.headers()));

                    return chain.proceed(request);
                }
            });
            */

            val client = builder.build()

            val ipServer = "http://www.mio-ip.it/"
            val serverPort = ""
            //ipServer=ipServer+":"+serverPort;

            Log.i(TAG, "-- <ICalServerApi> ipServer: $ipServer")
            var retrofit: Retrofit? = null
            try {
                retrofit = Retrofit.Builder().baseUrl(ipServer).client(client).addConverterFactory(GsonConverterFactory.create()).build()
                return retrofit!!.create(ICalServerApi::class.java)
            } catch (e: Throwable) {
                e.printStackTrace()
                return null
            }

        }
    }

    companion object {

        val HEADER_USER_AGENT = "User-Agent"
        val HEADER_CONTENT_TYPE = "Content-Type"
        val HEADER_ACCEPT_ENCODING = "Accept-Encoding"
        val HEADER_AUTHORIZATION = "Authorization"

        val CONTENT_TYPE_APPLICATION_JSON = "application/json"
        val CONTENT_TYPE_GZIP_DEFLATE = "gzip, deflate"

        val USER_AGENT_MOBILE = "mobile"
        val USER_AGENT_TABLET = "tablet"


        val TAG = "ZIPVIMSServerApi"
    }

}
