/**
 * Firebase Java Simple Login Library
 *
 * Copyright 2013 Firebase - All Rights Reserved https://www.firebase.com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binaryform must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY FIREBASE AS IS AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL FIREBASE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Firebase
 *
 */
package com.firebase.simplelogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.simplelogin.enums.Error;
import com.firebase.simplelogin.enums.Provider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


/**
 * Firebase Java Simple Login Library.
 *
 * Entry point for the Firebase simple login library.
 *
 * Attach an event listener to the .info/authenticated location to get notifications on changes in authentication state.
 *
 * <pre><code>
 * Firebase f = new Firebase("https://example.firebaseio.com/");
 * f.child(".info/authenticated").addValueEventListener(new ValueEventListener() {
 *  &#064;Override
 *  public void onDataChange(DataSnapshot arg0) {
 *    System.out.println("auth!: " + arg0.getValue());
 *  }
 *
 *  &#064;Override
 *  public void onCancelled() {
 *  }
 * });
 * </code></pre>
 *
 * Use the checkAuthStatus() method to retrieve the User object of the logged in user.
 *
 * <pre><code>
 * SimpleLogin simpleLogin = new SimpleLogin(f, this);
 * final SimpleLoginAuthenticatedHandler simpleLoginAuthenticatedHandler = new SimpleLoginAuthenticatedHandler() {
 *  &#064;Override
 *  public void authenticated(Error error, User user) {
 *    System.out.println("Error: " + error);
 *    System.out.println("User: " + user);
 *    System.out.println("3rdP: " + user.getThirdPartyUserData());
 *  }
 * };
 * simpleLogin.checkAuthStatus(simpleLoginAuthenticatedHandler);
 * </code></pre>
 *
 * @author Firebase
 *
 */
public class SimpleLogin {

  private final Firebase ref;
  private final String namespace;
  private final String apiHost;
  private final Context androidContext;
  private final SimpleLoginOptions options;

  /**
   * Simple Login constructor.
   *
   * @param ref Firebase reference against which to login.
   */
  public SimpleLogin(Firebase ref) {
    this(ref, null);
  }

  /**
   * Simple Login constructor; pass an Activity, Context, or Service to enable saving of user tokens between app launches.
   *
   * @param ref Firebase reference against which to login.
   * @param context Android context used to save tokens.
   */
  public SimpleLogin(Firebase ref, Context context) {
    this(ref, Constants.FIREBASE_AUTH_DEFAULT_API_HOST, context, new SimpleLoginOptions());
  }

  /**
   * Simple Login constructor; pass an Activity, Context, or Service to enable saving of user tokens between app launches.
   * Also, include
   *
   * @param ref Firebase reference against which to login.
   * @param context Android context used to save tokens.
   * @param options SimpleLoginOptions instance for advanced configuration.
   */
  public SimpleLogin(Firebase ref, Context context, SimpleLoginOptions options) {
   this(ref, Constants.FIREBASE_AUTH_DEFAULT_API_HOST, context, options);
  }

  private SimpleLogin(Firebase ref, String apiHost, Context context, SimpleLoginOptions options) {
    super();
    this.ref = ref;
    this.apiHost = apiHost;
    this.namespace = FirebaseUtils.namespaceFromRef(ref);
    this.androidContext = context;
    this.options = options;
  }

  /**
   * Logout the user.
   */
  public void logout() {
    this.ref.unauth();
    clearCredentials();
  }

  /**
   * The semver version for this build of the Firebase simple login client.
   *
   * @return Returns the semver
   */
  public static String getSdkVersion() {
    return Constants.FIREBASE_AUTH_SEMVER;
  }

  /**
   * Check the authentication status. If there is a previously signed in user, it will reauthenticate that user.
   *
   * @param handler Handler for asynchronous events.
   */
  public void checkAuthStatus(SimpleLoginAuthenticatedHandler handler) {
    if(androidContext != null) {
      SharedPreferences sharedPreferences = androidContext.getSharedPreferences(Constants.FIREBASE_ANDROID_SHARED_PREFERENCE, Context.MODE_PRIVATE);
      String jsonTokenData = sharedPreferences.getString("jsonTokenData", null);
      if(jsonTokenData != null) {
        try {
          JSONObject jsonObject = new JSONObject(jsonTokenData);
          attemptAuthWithData(jsonObject, handler);
        }
        catch(Exception e) {
          handler.authenticated(null, null);
        }
      }
      else {
        handler.authenticated(null, null);
      }
    }
    else {
      handler.authenticated(null, null);
    }
  }

  private void attemptAuthWithData(JSONObject data, final SimpleLoginAuthenticatedHandler handler) {
    try {
      String token = data.has("token") ? data.getString("token") : null;
      JSONObject userData = data.has("userData") ? data.getJSONObject("userData") : null;
      if(token != null && userData != null) {
        Provider provider = FirebaseUtils.providerForString(userData.getString("provider"));
        if(provider != Provider.INVALID) {
          // XXX send account for provider in objc
          attemptAuthWithToken(token, provider, userData, handler);
        }
        else {
          clearCredentials();
          handler.authenticated(null, null);
        }
      }
      else {
        handler.authenticated(FirebaseUtils.errorFromResponse(null), null);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
      handler.authenticated(FirebaseUtils.errorFromResponse(null), null);
    }
  }

  private void clearCredentials() {
    if(androidContext != null) {
      SharedPreferences sharedPreferences = androidContext.getSharedPreferences(Constants.FIREBASE_ANDROID_SHARED_PREFERENCE, Context.MODE_PRIVATE);
      Editor editor = sharedPreferences.edit();
      editor.clear();
      editor.commit();
    }
  }

    /**
     * Login anonymously.
     *
     * @param completionHandler Handler for asynchronous events.
     */
    public void loginAnonymously(final SimpleLoginAuthenticatedHandler completionHandler) {
      HashMap<String, String> data = new HashMap<String, String>();
      makeRequest(Constants.FIREBASE_AUTH_ANONYMOUS_PATH, data, new RequestHandler() {

        public void handle(Error error, JSONObject data) {
          if (error != null) {
            completionHandler.authenticated(error, null);
          }
          else {
            try {
              String token = data.has("token") ? data.getString("token") : null;
              if (token == null) {
                JSONObject errorDetails = data.has("error") ? data.getJSONObject("error") : null;
                Error theError = FirebaseUtils.errorFromResponse(errorDetails);
                completionHandler.authenticated(theError, null);
              }
              else {
                JSONObject userData = data.has("user") ? data.getJSONObject("user") : null;
                if (userData == null) {
                  Error theError = FirebaseUtils.errorFromResponse(null);
                  completionHandler.authenticated(theError, null);
                }
                else {
                  attemptAuthWithToken(token, Provider.ANONYMOUS, userData, completionHandler);
                }
              }
            }
            catch (Exception e) {
              e.printStackTrace();
              Error theError = FirebaseUtils.errorFromResponse(null);
              completionHandler.authenticated(theError, null);
            }
          }
        }

      });
    }

  /**
   * Login an existing Firebase "email/password" user.
   *
   * @param email Email address of user.
   * @param password Password for user.
   * @param completionHandler Handler for asynchronous events.
   */
  public void loginWithEmail(String email, String password, final SimpleLoginAuthenticatedHandler completionHandler) {
    if (!Validation.isValidEmail(email)) {
      handleInvalidEmail(completionHandler);
    }
    else if (!Validation.isValidPassword(password)) {
      handleInvalidPassword(completionHandler);
    }
    else {
      HashMap<String, String> data = new HashMap<String, String>();
      data.put("email", email);
      data.put("password", password);

      makeRequest(Constants.FIREBASE_AUTH_PASSWORD_PATH, data, new RequestHandler() {

        public void handle(Error error, JSONObject data) {
          if (error != null) {
            completionHandler.authenticated(error, null);
          }
          else {
            try {
              String token = data.has("token") ? data.getString("token") : null;
              if (token == null) {
                JSONObject errorDetails = data.has("error") ? data.getJSONObject("error") : null;
                Error theError = FirebaseUtils.errorFromResponse(errorDetails);
                completionHandler.authenticated(theError, null);
              }
              else {
                JSONObject userData = data.has("user") ? data.getJSONObject("user") : null;
                if (userData == null) {
                  Error theError = FirebaseUtils.errorFromResponse(null);
                  completionHandler.authenticated(theError, null);
                }
                else {
                  attemptAuthWithToken(token, Provider.PASSWORD, userData, completionHandler);
                }
              }
            }
            catch (Exception e) {
              e.printStackTrace();
              Error theError = FirebaseUtils.errorFromResponse(null);
              completionHandler.authenticated(theError, null);
            }
          }
        }

      });
    }
  }

  private void attemptAuthWithToken(final String token, final Provider provider, final JSONObject userData, final SimpleLoginAuthenticatedHandler completionHandler) {
    this.ref.auth(token, new AuthListener() {

      public void onAuthSuccess(Object authData) {
        User user = saveSession(token, provider, userData);
        if (user != null) {
          final Firebase authRef = ref.getRoot().child(".info/authenticated");
          final ValueEventListener authValueEventListener = new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
              Object value = snapshot.getValue();
              if (value instanceof Boolean) {
                Boolean boolVal = (Boolean) value;
                if (!boolVal.booleanValue()) {
                  clearCredentials();
                  authRef.removeEventListener(this); // can't refer to authValueEventListener here
                }
              }
            }
            public void onCancelled(FirebaseError error) {}
          };
          authRef.addValueEventListener(authValueEventListener);
          completionHandler.authenticated(null, user);
        }
        else {
          completionHandler.authenticated(FirebaseUtils.errorFromResponse(null), null);
        }

      }

      public void onAuthRevoked(FirebaseError error) {
        clearCredentials();
        completionHandler.authenticated(FirebaseUtils.errorFromFirebaseError(error), null);
      }

      public void onAuthError(FirebaseError error) {
        completionHandler.authenticated(FirebaseUtils.errorFromFirebaseError(error), null);
      }
    });
  }

  private User saveSession(String token, Provider provider, JSONObject userData) {
    clearCredentials();
    User user = null;
    try {
      String userId = userData.has("id") ? userData.getString("id") : null;
      if (userId != null) {
        if(provider == Provider.PASSWORD) {
          String email = userData.has("email") ? userData.getString("email") : null;
          if(email != null) {
            user = new User(userId, userData.getString("uid"), token, email);
          }
        }
        else {
          user = new User(userId, userData.getString("uid"), provider, token, FirebaseUtils.toMap(userData));
        }
      }
    }
    catch (Exception e) {}

    // Save to shared prefs
    if(androidContext != null) {
      SharedPreferences sharedPreferences = androidContext.getSharedPreferences(Constants.FIREBASE_ANDROID_SHARED_PREFERENCE, Context.MODE_PRIVATE);
      Editor editor = sharedPreferences.edit();
      try {
        JSONObject jsonTokenData = new JSONObject();
        jsonTokenData.put("token", token);
        jsonTokenData.put("userData", userData);
        editor.putString("jsonTokenData", jsonTokenData.toString());
      }
      catch(Exception e) {
        // TODO: log an error?
      }
      finally {
        editor.commit();
      }
    }
    return user;
  }

  /**
   * Create a Firebase "email/password" user.
   *
   * @param email Email address for user.
   * @param password Password for user.
   * @param completionHandler Handler for asynchronous events.
   */
  public void createUser(String email, String password, final SimpleLoginAuthenticatedHandler completionHandler) {
    if (!Validation.isValidEmail(email)) {
      handleInvalidEmail(completionHandler);
    }
    else if (!Validation.isValidPassword(password)) {
      handleInvalidPassword(completionHandler);
    }
    else {
      HashMap<String, String> data = new HashMap<String, String>();
      data.put("email", email);
      data.put("password", password);

      makeRequest(Constants.FIREBASE_AUTH_CREATEUSER_PATH, data, new RequestHandler() {
        public void handle(Error error, JSONObject data) {
          if (error != null) {
            completionHandler.authenticated(error, null);
          }
          else {
            try {
              JSONObject errorDetails = data.has("error") ? data.getJSONObject("error") : null;
              JSONObject userData = data.has("user") ? data.getJSONObject("user") : null;
              if (errorDetails != null) {
                Error theError = FirebaseUtils.errorFromResponse(errorDetails);
                completionHandler.authenticated(theError, null);
              }
              else if (userData == null) {
                Error theError = FirebaseUtils.errorFromResponse(null);
                completionHandler.authenticated(theError, null);
              }
              else {
                String userId = userData.getString("id");
                                String uid = userData.getString("uid");
                                String email = userData.getString("email");
                User user = new User(userId, uid, null, email);
                completionHandler.authenticated(null, user);
              }
            }
            catch (Exception e) {
              e.printStackTrace();
              Error theError = FirebaseUtils.errorFromResponse(null);
              completionHandler.authenticated(theError, null);
            }
          }
        }
      });
    }
  }

  /**
   * Remove a Firebase "email/password" user.
   *
   * @param email Email address for user.
   * @param password Password for user.
   * @param handler Handler for asynchronous events.
   */
  public void removeUser(String email, String password, final SimpleLoginCompletionHandler handler) {
    final SimpleLoginAuthenticatedHandler authHandler = new SimpleLoginAuthenticatedHandler() {
      public void authenticated(Error error, User user) {
        handler.completed(error, false);
      }
    };

    if (!Validation.isValidEmail(email)) {
      handleInvalidEmail(authHandler);
    }
    else if (!Validation.isValidPassword(password)) {
      handleInvalidPassword(authHandler);
    }
    else {
      HashMap<String, String> data = new HashMap<String, String>();
      data.put("email", email);
      data.put("password", password);

      makeRequest(Constants.FIREBASE_AUTH_REMOVEUSER_PATH, data, new RequestHandler() {
        public void handle(Error error, JSONObject data) {
          if(error != null) {
            handler.completed(error, false);
          }
          else {
            try {
              JSONObject errorDetails = data.has("error") ? data.getJSONObject("error") : null;
              if(errorDetails != null) {
                handler.completed(FirebaseUtils.errorFromResponse(errorDetails), false);
              }
              else {
                handler.completed(null, true);
              }
            }
            catch(Exception e) {
              e.printStackTrace();
              Error theError = FirebaseUtils.errorFromResponse(null);
              handler.completed(theError, false);
            }
          }
        }
      });
    }
  }

  /**
   * Change the password for a Firebase "email/password" user.
   *
   * @param email Email address for user.
   * @param oldPassword User's old password.
   * @param newPassword User's new password.
   * @param handler Handler for asynchronous events.
   */
  public void changePassword(final String email, final String oldPassword, final String newPassword, final SimpleLoginCompletionHandler handler) {
    final SimpleLoginAuthenticatedHandler authHandler = new SimpleLoginAuthenticatedHandler() {
      public void authenticated(Error error, User user) {
        handler.completed(error, false);
      }
    };

    if (!Validation.isValidEmail(email)) {
      handleInvalidEmail(authHandler);
    }
    else if (!Validation.isValidPassword(newPassword)) {
      handleInvalidPassword(authHandler);
    }
    else {
      HashMap<String, String> data = new HashMap<String, String>();
      data.put("email", email);
      data.put("oldPassword", oldPassword);
      data.put("newPassword", newPassword);

      makeRequest(Constants.FIREBASE_AUTH_CHANGEPASSWORD_PATH, data, new RequestHandler() {
        public void handle(Error error, JSONObject data) {
          if(error != null) {
            handler.completed(error, false);
          }
          else {
            handler.completed(null, true);
          }
        }
      });
    }
  }

    /**
     * Send a password reset email for a Firebase "email/password" user.
     *
     * @param email Email address for user.
     * @param handler Handler for asynchronous events.
     */
    public void sendPasswordResetEmail(String email, final SimpleLoginCompletionHandler handler) {
      final SimpleLoginAuthenticatedHandler authHandler = new SimpleLoginAuthenticatedHandler() {
        public void authenticated(Error error, User user) {
        handler.completed(error, false);
        }
      };

      if (!Validation.isValidEmail(email)) {
        handleInvalidEmail(authHandler);
      }
      else {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("email", email);

        makeRequest(Constants.FIREBASE_AUTH_RESETPASSWORD_PATH, data, new RequestHandler() {
          public void handle(Error error, JSONObject data) {
            if(error != null) {
              handler.completed(error, false);
            }
            else {
              try {
                JSONObject errorDetails = data.has("error") ? data.getJSONObject("error") : null;
                if(errorDetails != null) {
                  handler.completed(FirebaseUtils.errorFromResponse(errorDetails), false);
                }
                else {
                  handler.completed(null, true);
                }
              }
              catch(Exception e) {
                e.printStackTrace();
                Error theError = FirebaseUtils.errorFromResponse(null);
                handler.completed(theError, false);
              }
            }
          }
        });
      }
    }

  private void handleInvalidEmail(final SimpleLoginAuthenticatedHandler userHandler) {
    new Handler().post(new Runnable() {
      public void run() {
        if(userHandler != null) {
          userHandler.authenticated(Error.InvalidEmail, null);
        }
      }
    });
  }

  private void handleInvalidPassword(final SimpleLoginAuthenticatedHandler userHandler) {
    new Handler().post(new Runnable() {
      public void run() {
        if(userHandler != null) {
          userHandler.authenticated(Error.InvalidPassword, null);
        }
      }
    });
  }

  private void handleInvalidInvalidToken(final SimpleLoginAuthenticatedHandler userHandler) {
    new Handler().post(new Runnable() {
      public void run() {
        if(userHandler != null) {
          userHandler.authenticated(Error.BadProviderToken, null);
        }
      }
    });
  }

  private void makeRequest(String urlPath, HashMap<String, String> data, final RequestHandler handler) {
    Uri.Builder b = Uri.parse(this.apiHost).buildUpon();
    b.path(urlPath);
    b.appendQueryParameter("firebase", this.namespace);
    b.appendQueryParameter("mobile", "android");
               b.appendQueryParameter("transport", "json");

        if (options.isDebug()) {
            b.appendQueryParameter("debug", "1");
        }

    if (data != null) {
      for (Map.Entry<String, String> entry : data.entrySet()) {
        if (entry != null) {
          b.appendQueryParameter(entry.getKey(), entry.getValue());
        }
      }
    }
    new FetchTask(handler).execute(b.build().toString());
  }

  /**
   * Login to Firebase using a Facebook token. The returned User object will contain pertinent
   * Facebook data accessible with getThirdPartyUserData().
   *
   * @param appId Facebook app id.
   * @param accessToken Access token returned by Facebook SDK.
   * @param completionHandler Handler for asynchronous events.
   */
  public void loginWithFacebook(final String appId, final String accessToken, final SimpleLoginAuthenticatedHandler completionHandler) {
    if(appId == null || accessToken == null) {
      handleInvalidInvalidToken(completionHandler);
    }
    else {
      HashMap<String, String> data = new HashMap<String, String>();
      data.put("access_token", accessToken);

            loginWithToken(Constants.FIREBASE_AUTH_FACEBOOK_PATH, Provider.FACEBOOK, data, completionHandler);
        }
  }

    /**
     * Login to Firebase using a Google access token. The returned User object will contain pertinent
     * Google data accessible with getThirdPartyUserData().
     *
     * @param accessToken Access token returned by Facebook SDK.
     * @param completionHandler Handler for asynchronous events.
     */
    public void loginWithGoogle(final String accessToken, final SimpleLoginAuthenticatedHandler completionHandler) {
        if(accessToken == null) {
            handleInvalidInvalidToken(completionHandler);
        }
        else {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("access_token", accessToken);

            loginWithToken(Constants.FIREBASE_AUTH_GOOGLE_PATH, Provider.GOOGLE, data, completionHandler);
        }
    }

    /**
   * Login to Firebase using a Twitter token. The returned User object will contain pertinent
   * Twitter data accessible with getThirdPartyUserData().
   *
   * @param oauth_token Twitter oauth token.
   * @param oauth_token_secret Twitter token secret.
   * @param user_id Twitter numeric user id.
   * @param completionHandler Handler for asynchronous events.
   */
  public void loginWithTwitter(final String oauth_token, final String oauth_token_secret, final Long user_id, final SimpleLoginAuthenticatedHandler completionHandler) {
    if(oauth_token == null || oauth_token_secret == null || user_id == null) {
      handleInvalidInvalidToken(completionHandler);
    }
    else {
      HashMap<String, String> data = new HashMap<String, String>();
      data.put("oauth_token", oauth_token);
      data.put("oauth_token_secret", oauth_token_secret);
      data.put("user_id", user_id.toString());

      loginWithToken(Constants.FIREBASE_AUTH_TWITTERTOKEN_PATH, Provider.TWITTER, data, completionHandler);
    }
  }

    private void loginWithToken(final String urlPath, final Provider provider, final HashMap<String, String> data, final SimpleLoginAuthenticatedHandler completionHandler) {
      makeRequest(urlPath, data, new RequestHandler() {
        public void handle(Error error, JSONObject data) {
          if (error != null) {
            completionHandler.authenticated(error, null);
          }
          else {
            try {
              String token = data.has("token") ? data.getString("token") : null;
              if (token == null) {
                JSONObject errorDetails = data.has("error") ? data.getJSONObject("error") : null;
                Error theError = FirebaseUtils.errorFromResponse(errorDetails);
                completionHandler.authenticated(theError, null);
              }
              else {
                JSONObject userData = data.has("user") ? data.getJSONObject("user") : null;
                if (userData == null) {
                  Error theError = FirebaseUtils.errorFromResponse(null);
                  completionHandler.authenticated(theError, null);
                }
                else {
                  attemptAuthWithToken(token, provider, userData, completionHandler);
                }
              }
            }
            catch (Exception e) {
              e.printStackTrace();
              Error theError = FirebaseUtils.errorFromResponse(null);
              completionHandler.authenticated(theError, null);
            }
          }
        }
      });
    }

  /**
   * FetchTask class.
   */
  class FetchTask extends AsyncTask<String, Void, JSONObject> {

    private RequestHandler handler;

    public FetchTask(RequestHandler handler) {
      super();
      this.handler = handler;
    }

    @Override
    protected JSONObject doInBackground(String... arg) {
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(arg[0]);
      JSONObject result = null;
      try {
        result = httpClient.execute(httpGet, new JsonBasicResponseHandler());
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      return result;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
      // Method is automatically called on main thread
      super.onPostExecute(result);
      if (result == null) {
        handler.handle(Error.Unknown, null);
      }
      else {
        handler.handle(null, result);
      }
    }
  }
}
