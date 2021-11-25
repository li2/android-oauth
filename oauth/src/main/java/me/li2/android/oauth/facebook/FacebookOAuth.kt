package me.li2.android.oauth.facebook

import android.content.Intent
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import me.li2.android.oauth.OAuth
import org.json.JSONException

/**
 * Facebook OAuth.
 *
 * To make this lib works,
 * 1. Two strings facebook_app_id, facebook_login_protocol_scheme must be defined in your application;
 * 2. onActivityResult must be called in your fragment onActivityResult callback.
 */
class FacebookOAuth : OAuth<FacebookSignInAccount> {
    private val callbackManager = CallbackManager.Factory.create()!!

    override fun getLastSignedInAccount(): FacebookSignInAccount? {
        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        val profile: Profile? = Profile.getCurrentProfile()
        return FacebookSignInAccount(
            displayName = profile?.name,
            photoUrl = if (accessToken != null) getPhotoUrl(accessToken.userId) else null,
            token = accessToken?.token
        )
    }

    // https://github.com/pchmn/RxSocialAuth/blob/master/library/src/main/java/com/pchmn/rxsocialauth/auth/RxFacebookAuthFragment.java
    override fun signIn(fragment: Fragment): Single<FacebookSignInAccount> {
        return getAccessToken(fragment)
            .flatMap { accessToken ->
                getUserInfo(accessToken)
            }
    }

    private fun getAccessToken(fragment: Fragment): Single<AccessToken> {
        return Single.create { emitter ->
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    emitter.onSuccess(result.accessToken)
                }

                override fun onCancel() {
                    emitter.onError(FacebookSignInDeniedException)
                }

                override fun onError(error: FacebookException) {
                    emitter.onError(error)
                }
            })
            // https://stackoverflow.com/a/29379794/2722270
            LoginManager.getInstance()
                .logInWithReadPermissions(fragment, listOf(PERMISSIONS_PUBLIC_PROFILE, PERMISSIONS_EMAIL))
        }
    }

    private fun getUserInfo(accessToken: AccessToken): Single<FacebookSignInAccount> {
        return Single.create { emitter ->
            val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
                try {
                    val profile = Profile.getCurrentProfile()
                    val account = FacebookSignInAccount(
                        displayName = profile?.name,
                        email = jsonObject?.getString(PERMISSIONS_EMAIL),
                        photoUrl = getPhotoUrl(accessToken.userId),
                        token = accessToken.token
                    )
                    emitter.onSuccess(account)
                } catch (exception: JSONException) {
                    emitter.onError(exception)
                }
            }
            request.parameters = bundleOf("fields" to "id,name,email,gender,birthday")
            request.executeAsync()
        }
    }

    override fun signOut(): Completable {
        LoginManager.getInstance().logOut()
        return Completable.complete()
    }

    override fun revokeAccess(): Completable {
        return Completable.create { emitter ->
            GraphRequest(
                AccessToken.getCurrentAccessToken(),
                PERMISSIONS_PATH,
                null,
                HttpMethod.DELETE,
                GraphRequest.Callback { response ->
                    try {
                        response?.jsonObject?.getBoolean(SUCCESS)
                    } catch (e: JSONException) {
                        emitter.onError(e)
                    }
                    if (response?.error == null) {
                        emitter.onComplete()
                    } else {
                        emitter.onError(response.error.exception)
                    }
                }).executeAsync()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val PERMISSIONS_PATH = "/me/permissions"
        private const val PERMISSIONS_PUBLIC_PROFILE = "public_profile"
        private const val PERMISSIONS_EMAIL = "email"
        private const val SUCCESS = "success"
        private fun getPhotoUrl(userId: String) = "https://graph.facebook.com/$userId/picture?type=large"
    }
}

object FacebookSignInDeniedException : Exception("Facebook Sign in action denied")

