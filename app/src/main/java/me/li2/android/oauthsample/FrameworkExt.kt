package me.li2.android.oauthsample

import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.throttleFirstShort() = this.throttleFirst(500L, TimeUnit.MILLISECONDS)!!

fun Fragment.toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

@BindingAdapter("android:visibility")
fun setViewVisibility(view: View, value: Boolean?) {
    view.visibility = if (value == true) View.VISIBLE else View.GONE
}

@BindingAdapter("android:src")
fun setImageUrl(view: ImageView, src: String?) {
    Glide.with(view.context).load(src).into(view)
}

fun <T> Single<T>.forUi(): Single<T> =
        this.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

fun Completable.forUi(): Completable =
        this.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
