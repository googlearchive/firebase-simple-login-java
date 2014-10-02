# Authenticating Users with Twitter - iOS


## Configuring Your Application

To get started with Twitter authentication in Firebase Simple Login, you need to first [create a new Twitter application](https://apps.twitter.com/). Click the __Create New App__ button at the top right of that page and fill in a name, description, and website for your application. Set the application's __Callback URL__ to `https://auth.firebase.com/auth/twitter/callback` so that your application can properly communicate with Firebase.

After configuring your Twitter application, head on over to the __Simple Login__ section in your Firebase Dashboard. Enable Twitter authentication and then copy your Twitter application credentials (__API key__ and __API secret__) into the appropriate inputs. You can find your Twitter application's key and secret at the top of the __API Keys__ tab of the application's Twitter dashboard.



## Authenticating Twitter users to your Firebase

After configuring your app to process the OAuth flow from Twitter, you can use the returned token to authenticate a secure Firebase session. After the user has been authenticated with Twitter, signing them into Firebase can be triggered by:

```java
SimpleLogin authClient = new SimpleLogin(myRef, getApplicationContex());
authClient.loginWithTwitter(oauth_token, oauth_token_secret, user_id, new SimpleLoginAuthenticatedHandler() {
  public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
    if(error != null) {
      // There was an error
    }
    else {
      // Logged in with Twitter
    }
  }
});
```

If we're successful `user.getThirdPartyUsedData()` will be a `Map` containing metadata returned from Twitter about this user and `user.getUserId()` will be the user's Twitter ID.

After Authenticating

Now that the client is logged in, your [Security Rules](https://www.firebase.com/docs/android/guide/securing-data.html) will have access to their verified Twitter user id. Specifically, the `auth` variable] will contain the following values:


| Field | Description | Type |
| --- | --- | --- |
| id | The user's Twitter id. | String |
| provider | The authentication method used, in this case: 'twitter'. | String |
| uid | A unique id combining the provider and id, intended as the unique key for user data (will have the format 'twitter:<id>'). | String |
