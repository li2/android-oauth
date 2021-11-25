package me.li2.android.oauth

import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface OAuth<T> {
    /**
     * Check for existing signed in account
     * @return null if the user not sign in.
     */
    fun getLastSignedInAccount(): T?

    /**
     * Make a request to authorization server, such as Google, Facebook, etc, which will display the user
     * an authorization prompt asking the user to authorize your App to access their account.
     *
     * @return Authorization grant, include auth code that your app will use when request an access token.
     */
    fun signIn(fragment: Fragment): Single<T>

    /**
     * Sign out from authorization server.
     */
    fun signOut(): Completable

    /**
     * Revoke access to authorization server.
     */
    fun revokeAccess(): Completable
}

//data class OAuthAccount(val displayName: String? = null,
//                        val firstName: String? = null,
//                        val lastName: String? = null,
//                        val email: String? = null,
//                        val photoUrl: String? = null,
//                        val serverAuthCode: String? = null,
//                        val idToken: String? = null,
//                        val isExpired: Boolean? = false) {
//    fun getDisplayedInfo() = "$displayName\n$email\n$serverAuthCode"
//}
//
//sealed class OAuthSignInResult
//
//data class OAuthSignInSuccess(val account: OAuthAccount) : OAuthSignInResult()
//
//data class OAuthSignInError(val message: String) : OAuthSignInResult()
//
//fun GoogleSignInAccount.toOAuthAccount() =
//        OAuthAccount(displayName, givenName, familyName, email, photoUrl?.toString(), serverAuthCode, idToken, isExpired)
