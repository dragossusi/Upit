package ro.rachierudragos.upitapi

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Upit
 *
 * @author Dragos
 * @since 17.06.2019
 */
class TokenInterceptor(val tokenSaving: TokenSaving) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.header("No-Authentication") == null) {
            //val token = getTokenFromSharedPreference();
            //or use Token Function
            val token = tokenSaving.token
            if (!token.isNullOrEmpty()) {
                val finalToken = "Bearer $token"
                request = request.newBuilder()
                    .addHeader("Authorization", finalToken)
                    .build()
            }

        }

        return chain.proceed(request)
    }

}