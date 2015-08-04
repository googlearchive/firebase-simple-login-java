# Authenticating Users with Facebook - Android


## Configuring Your Application

To get started with Facebook authentication in Firebase Simple Login, you need to first [create a new Facebook application](https://developers.facebook.com/apps). Click the __Create New App__ button in the top right of that page. Choose a name, namespace, and category for your application.

In your Facebook app configuration, click on the __Settings__ tab on the left-hand navigation menu. Then go to the __Advanced__ tab at the top and scroll down to the __Security__ section. At the bottom of that section, add `https://auth.firebase.com/auth/facebook/callback` to your __Valid OAuth redirect URIs__ and click __Save Changes__ at the bottom of the page.

Next, you'll need to get your app credentials from Facebook. Click on the __Basic__ tab at the top of the page. You should still be within the __Settings__ tab. Towards the top of this page, you will see your __App ID__ and __App Secret__. Your __App ID__ will be displayed in plain text and you can view your __App Secret__ by clicking on the __Show__ button and typing in your Facebook password. Copy these Facebook application credentials (__App ID__ and __Secret__) in the __Simple Login__ section in your Firebase Dashboard.

### Adding Contact Information

Facebook requires that you have a valid contact email specified in order to make your app available to all users. You can specify this email address from the same __Basic__ tab within the __Settings__ section. After you have provided your email, click on __Save Changes__. The last thing you need to do to approve your app is click on the __Status & Review__ tab on the left-hand navigation menu and move the slider at the top of that page to the __Yes__ position. When prompted with a popup, click __Confirm__. Your app will now be live and can be used with Firebase Simple Login.

### Facebook SDK

You'll need to use the Facebook SDK for Android to your application. Follow these steps: [Getting Started with the Facebook SDK for Android](https://developers.facebook.com/docs/getting-started/facebook-sdk-for-android/3.0/).

Next, you will need to configure your application to support plugging into the Facebook SDK in order to log users in. Follow these steps: [Facebook Login Flow for Android](https://developers.facebook.com/docs/howtos/androidsdk/3.0/login-with-facebook/).


## Authenticating Facebook users to your Firebase

Once your application has been setup to log users into Facebook, your application can then authenticate them to Firebase in order to take advantage of the security rules used to protect data in Firebase.

To log a user in, you will need your Facebook App ID. In addition, you will need the access token returned by the login flow provided by the Facebook SDK. Following the setup provided by Facebook, you can plug into the Firebase authentication in the `MainFragment.onSessionStateChange()`.

```java
private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    if (state.isOpened()) {
        final String accessToken = session.getAccessToken();
        // authClient is an instance of SimpleLogin
        authClient.loginWithFacebook("APP_ID", session.getAccessToken(), new SimpleLoginAuthenticatedHandler() {
            public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
                if(error != null) {
                    // There was an error
                }
                else {
                    // Logged in with Facebook
                }
            }
        });
    } else if (state.isClosed()) {
        // Logged out of Facebook
        authClient.logout();
    }
}
```

If we're successful `user.getThirdPartyUsedData()` will be a `Map` containing metadata returned from Facebook about this user and `user.getUserId()` will be the user's Facebook ID.


## After Authenticating

Now that the client is logged in, your [Security Rules](https://www.firebase.com/docs/android/guide/securing-data.html) will have access to their verified Facebook user id. Specifically, the `auth` variable will contain the following values:

| Field | Description | Type |
| --- | --- | --- |
| id | The user's Facebook id. | String |
| provider | The authentication method used, in this case: `facebook`. | String |
| uid | A unique id combining the provider and id, intended as the unique key for user data. | String |


## Example

There is an example Android app using Facebook authentication [available on Github](https://github.com/firebase/simple-login-demo-android).
