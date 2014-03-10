package com.firebase.simplelogin.enums;

/**
 * Error codes that can be returned from Firebase. See more details here:
 *
 * <a href="https://www.firebase.com/docs/security/simple-login-java-overview.html">https://www.firebase.com/docs/security/simple-login-java-overview.html</a>.
 *
 * @author Firebase
 *
 */
public enum Error {
  // Auth errors
  /**
   * The specified user does not exist.
   */
  UserDoesNotExist,
  /**
   * An incorrect password was given.
   */
  InvalidPassword,
  /**
   * The user did not authorize the application.
   */
  AccessNotGranted,
  /**
   * The 3rd party account was not found.
   */
  AccountNotFound,
  /**
   * The specified auth provider is not enabled for your Firebase. Enable it in Forge.
   */
  AuthenticationProviderNotEnabled,
  /**
   * The specified email is invalid.
   */
  InvalidEmail,
  /**
   * The cached system token for the auth provider is no longer valid. The user has most likely disabled the specified auth provider.
   */
  BadSystemToken,
  /**
   * The email address of the new user is already taken.
   */
  EmailTaken,
  /**
   * The Firebase doesn't support the request.
   */
  InvalidFirebase,
  /**
   * The 3rd party token is invalid.
   */
  BadProviderToken,

  // From FirebaseError
  /**
   * The data is stale.
   */
  DataStale,
  /**
   * The operation failed.
   */
  OperationFailed,
  /**
   * Permission was denied for this operation.
   */
  PermissionDenied,
  /**
   * Firebase was disconnected.
   */
  Disconnected,
  /**
   * Another authentication request preempted the previous one.
   */
  Preempted,
  /**
   * The token is expired.
   */
  ExpiredToken,
  /**
   * The token is invalid.
   */
  InvalidToken,
  /**
   * Max retries have been reached.
   */
  MaxRetries,
  /**
   * The operation was overridden by a local set.
   */
  OverriddenBySet,

  // Catch all
  /**
   * Unknown error.
   */
  Unknown;
}
