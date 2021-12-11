package me.li2.android.oauthsample

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_main.*
import me.li2.android.oauth.facebook.FacebookOAuth
import me.li2.android.oauth.google.GoogleOAuth
import me.li2.android.oauthsample.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val compositeDisposable = CompositeDisposable()

    private val facebookOAuth: FacebookOAuth = FacebookOAuth()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val googleOAuth = GoogleOAuth(
            requireContext(),
            requireContext().getString(R.string.google_server_client_id)
        )

        binding.googleAccount = googleOAuth.getLastSignedInAccount()
        binding.facebookAccount = facebookOAuth.getLastSignedInAccount()

        compositeDisposable += btn_google_signin.clicks().throttleFirstShort().subscribe {
            compositeDisposable += googleOAuth.signIn(this)
                .forUi()
                .subscribeBy(
                    onSuccess = { binding.googleAccount = it },
                    onError = { toast(it.message.toString()) }
                )
        }

        compositeDisposable += btn_google_signout.clicks().subscribe {
            compositeDisposable += googleOAuth.signOut().subscribeBy {
                binding.googleAccount = null
            }
        }

        compositeDisposable += btn_google_revoke_access.clicks().subscribe {
            compositeDisposable += googleOAuth.revokeAccess().subscribeBy {
                binding.googleAccount = googleOAuth.getLastSignedInAccount()
            }
        }

        // FACEBOOK
        compositeDisposable += btn_facebook_signin.clicks().throttleFirstShort().subscribe {
            compositeDisposable += facebookOAuth.signIn(this)
                .forUi()
                .subscribeBy(
                    onSuccess = { binding.facebookAccount = it },
                    onError = { toast(it.message.toString()) }
                )
        }

        compositeDisposable += btn_facebook_signout.clicks().subscribe {
            compositeDisposable += facebookOAuth.signOut().subscribeBy {
                binding.facebookAccount = null
            }
        }

        compositeDisposable += btn_facebook_revoke_access.clicks().subscribe {
            compositeDisposable += facebookOAuth.revokeAccess()
                .forUi()
                .subscribeBy(
                    onComplete = { binding.facebookAccount = null },
                    onError = { }
                )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookOAuth.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
