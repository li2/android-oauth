/*
 * Created by Weiyi Li on 15/03/20.
 * https://github.com/li2
 */
package me.li2.android.oauth.google

data class GoogleSignInException(
    override val message: String,
    override val cause: Throwable?
) : Exception(message, cause)

object GoogleSignInNoResultException : Exception("No Google account found")

object GoogleSignInDeniedException : Exception("Google Sign in denied")

object GoogleSignInCancelledException : Exception("Google Sign in cancelled")