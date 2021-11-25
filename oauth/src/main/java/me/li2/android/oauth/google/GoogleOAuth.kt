package me.li2.android.oauth.google

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.petarmarijanovic.rxactivityresult.RxActivityResult
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import me.li2.android.oauth.OAuth
import me.li2.android.oauth.encrypt

/**
 * Google OAuth
 *
 * @param context
 * @param serverClientId client ID of the server that will need the auth code. See
 *     <a href="https://developers.google.com/identity/sign-in/android/start-integrating">Start Integrating Google Sign-In into Your Android App</a>
 */
class GoogleOAuth(
    private val context: Context,
    private val serverClientId: String
) : OAuth<GoogleSignInAccount> {

    // Configure sign-in to request the user's ID, email address, and basic
    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
    private var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestServerAuthCode(serverClientId)
        .requestEmail()
        .build()

    // Build a GoogleSignInClient with the options specified by gso.
    private var googleAuthClient = GoogleSignIn.getClient(context, gso)

    override fun getLastSignedInAccount() = GoogleSignIn.getLastSignedInAccount(context)

    override fun signIn(fragment: Fragment): Single<GoogleSignInAccount> {
        if (fragment.activity == null) throw RuntimeException("activity of ${fragment.javaClass::getSimpleName} is null")
        return RxActivityResult(fragment.requireActivity())
            .start(googleAuthClient.signInIntent)
            .`as`(RxJavaBridge.toV3Single())
            .map { result ->
                val resultCode = result.resultCode
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        val accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        val account = accountTask.getResult(ApiException::class.java)
                        account?.let { account }
                    } catch (exception: ApiException) {
                        throw GoogleSignInException(exception)
                    }
                } else {
                    throw GoogleSignInDeniedException
                }
            }
    }

    override fun signOut(): Completable {
        return Completable.create { emitter ->
            googleAuthClient.signOut()
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
                .addOnCanceledListener { emitter.onError(CancelledException) }
        }
    }

    override fun revokeAccess(): Completable {
        return Completable.create { emitter ->
            googleAuthClient.revokeAccess()
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
                .addOnCanceledListener { emitter.onError(CancelledException) }
        }
    }
}

fun GoogleSignInAccount.getDisplayedInfo(): String {
    return "displayName: ${displayName.encrypt()}\nemail: ${email.encrypt()}\nid: ${id.encrypt()}\nserverAuthCode: ${serverAuthCode.encrypt()}\nidToken: ${idToken.encrypt()}"
}
