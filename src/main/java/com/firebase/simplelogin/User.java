package com.firebase.simplelogin;

import com.firebase.simplelogin.enums.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * User object for authenticated simple login calls. This class is a wrapper around the user metadata
 * returned from the Firebase authentication server. It includes a (userId, provider) tuple that is
 * unique. It will also include the token used to authenticate with Firebase.
 */
public class User {

  private String userId;

  private String uid;

  private Provider provider;

  private String authToken;

  private String email;

  private Map<String, Object> thirdPartyUserData;

  /**
   * Constructor for Firebase username/password user object.
   *
   * @param userId
   * @param authToken
   * @param email
   */
  User(String userId, String uid, String authToken, String email) {
    super();
    this.userId = userId;
        this.uid = uid;
    this.provider = Provider.PASSWORD;
    this.authToken = authToken;
    this.email = email;
    this.thirdPartyUserData = new HashMap<String, Object>();
  }

  /**
   * Constructor for third-party user object.
   *
   * @param userId
   * @param provider
   * @param authToken
   * @param thirdPartyUserData
   */
  User(String userId, String uid, Provider provider, String authToken, Map<String, Object> thirdPartyUserData) {
    super();
    this.userId = userId;
        this.uid = uid;
    this.provider = provider;
    this.authToken = authToken;
    this.thirdPartyUserData = thirdPartyUserData;
  }

  /**
   * A userId for this user. It is only unique for the given auth provider.
   *
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

    /**
     * A uid for this user. It is unique across auth providers.
     *
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

  /**
   * The provider that authenticate this user.
   *
   * @return the provider
   */
  public Provider getProvider() {
    return provider;
  }

  /**
   * The token that was user to authenticate this user with Firebase. This token is passed to Firebase.auth().
   *
   * @return the authToken
   */
  public String getAuthToken() {
    return authToken;
  }

  /**
   * Optional; the user's email if this user was authenticated via Firebase's email/password provider; null otherwise.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /** Metadata about the user provided by third party authentication services if such a service was used for
   * this user. Null otherwise.
   *
   * @return the thirdPartyUserData
   */
  public Map<String, Object> getThirdPartyUserData() {
    return thirdPartyUserData;
  }

  @Override
  public String toString() {
    return new StringBuilder("UserId: ").append(getUserId()).append("(").append(getProvider()).append(")").toString();
  }

}
