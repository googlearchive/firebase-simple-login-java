# Authenticating Users with Google - Android


## Configuring Your Application

To get started with Google authentication in Firebase Simple Login, you need to first [create a new Google application](https://cloud.google.com/console). Click the __Create Project__ button on that page and fill in a name and ID for your project. Once your application is created, navigate to __APIs & AUTH → Credentials__ in the left-hand navigation menu, and select __Create New Client ID__.

Simple Login requires web application access, so select __Web application__. Set __Authorized JavaScript origins__ to `https://auth.firebase.com`. Finally, set the __Authorized Redirect URI__ to `https://auth.firebase.com/auth/google/callback`. This allows your application to properly communicate with Firebase.

Make sure your application also has its __Product Name__ set on the __APIs & AUTH → Consent Screen__ or Google will return a `401` error when authenticating.

After configuring your Google application, head on over to the __Simple Login__ section in your Firebase Dashboard. Enable Google authentication and then copy your Google application credentials (__Client ID__ and __Client Secret__) into the appropriate inputs. You can find your Google application's client ID and secret from the same __APIs & AUTH → Credentials__ page you were just on. Look for them under the __Client ID for web application__ header.


## Authenticating Google users to your Firebase

Once your application has been setup to log users into Google, your application can then authenticate them to Firebase in order to take advantage of the security rules used to protect data in Firebase.

To log a user in, you will need a Google OAuth access token returned by the login flow provided by the Google+ SDK:

```java
SimpleLogin authClient = new SimpleLogin(myRef, getApplicationContext());
authClient.loginWithGoogle("<ACCESS-TOKEN>", new SimpleLoginAuthenticatedHandler() {
  public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
    if (error != null) {
      // There was an error
    }
    else {
      // Logged in with Google
    }
  }
});
```

If we're successful `user.getThirdPartyUsedData()` will be a `Map` containing metadata returned from Google about this user and `user.getUserId()` will be the user's Google ID.


## After Authenticating

Now that the client is logged in, your [Security Rules](https://www.firebase.com/docs/android/guide/securing-data.html) will have access to their verified Google user id. Specifically, the `auth` variable will contain the following values:

| Field | Description | Type |
| --- | --- | --- |
| id | The user's Google id. | String |
| provider | The authentication method used, in this case: `google`. | String |
| uid | A unique id combining the provider and id, intended as the unique key for user data. | String |


## Example

There is an example Android app using Google authentication [available on Github](https://github.com/firebase/simple-login-demo-android).
