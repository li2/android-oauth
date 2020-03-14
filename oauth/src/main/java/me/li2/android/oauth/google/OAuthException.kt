/*
 * Created by Weiyi Li on 15/03/20.
 * https://github.com/li2
 */
package me.li2.android.oauth.google

import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException

data class GoogleSignInException(private val apiException: ApiException)
    : Exception(GoogleSignInStatusCodes.getStatusCodeString(apiException.statusCode), apiException.cause)

object GoogleSignInDeniedException : Exception("Google Sign in action denied")

object CancelledException: Exception("cancelled")