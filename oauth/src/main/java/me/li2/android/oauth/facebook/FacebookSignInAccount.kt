/*
 * Created by Weiyi Li on 15/03/20.
 * https://github.com/li2
 */
package me.li2.android.oauth.facebook

import me.li2.android.oauth.encrypt

data class FacebookSignInAccount(val displayName: String? = null,
                                 val email: String? = null,
                                 val photoUrl: String? = null,
                                 val token: String? = null,
                                 val isExpired: Boolean? = false) {

    fun getDisplayedInfo() = "${displayName.encrypt()}\n${email.encrypt()}\n${token.encrypt()}"
}