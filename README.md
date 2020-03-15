[![](https://jitpack.io/v/li2/android-oauth.svg)](https://jitpack.io/#li2/android-oauth)


##  Google, Facebook OAuth in Rx

This library is a wrapper of Google & Facebook OAuth SDK in Rx.



## Usage

[MainFragment](https://github.com/li2/android-oauth/blob/master/app/src/main/java/me/li2/android/oauthsample/MainFragment.kt)

For Google OAuth, you need follow [Try Sign-In for Android](https://developers.google.com/identity/sign-in/android/start) to cofig a Google API project and register your App to get client ID of the server that will need the auth code.

```kotlin
val googleOAuth = GoogleOAuth(context, context.getString(R.string.google_server_client_id))

googleOAuth.signIn(this)
    .subscribeBy(onSuccess = { googleSignInAccount ->
        val authCode = googleSignInAccount.serverAuthCode
        val email = googleSignInAccount.email
        // then you can send the auth code to your server to get access token
    }, onError = {
    })
```

For Facebook OAuth, you also need to register your App to Facebook to get ID and protocol scheme and define the following two strings:

```xml
<string name="facebook_app_id">xyz</string>
<string name="facebook_login_protocol_scheme">xyz</string>
```

```kotlin
val facebookOAuth: FacebookOAuth = FacebookOAuth()

facebookOAuth.signIn(this)
    .subscribeBy(onSuccess = { facebookSignInAccount ->
        val authCode = facebookSignInAccount.token
        val email = facebookSignInAccount.email
        // then you can send the auth code to your server to get access token
    }, onError = {
    })

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    facebookOAuth.onActivityResult(requestCode, resultCode, data)
    super.onActivityResult(requestCode, resultCode, data)
}
```



## Download

```gradle
implementation 'com.github.li2:android-oauth:latest_version'
```



## License

```
    Copyright (C) 2020 Weiyi Li

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```