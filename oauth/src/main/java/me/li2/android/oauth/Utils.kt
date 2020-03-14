/*
 * Created by Weiyi Li on 15/03/20.
 * https://github.com/li2
 */
package me.li2.android.oauth

internal fun Int?.or0() = this ?: 0

internal fun String?.encrypt(): String? {
    return if (this?.length.or0() > 8) {
        "${this?.take(4)}****${this?.takeLast(4)}"
    } else {
        this
    }
}
