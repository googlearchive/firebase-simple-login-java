package com.firebase.simplelogin;

import com.firebase.simplelogin.enums.Error;

/**
 * Handler for authentication callbacks.
 *
 * @author Firebase
 *
 */
public interface SimpleLoginAuthenticatedHandler {

  /**
   * Method called when handler is invoked.
   *
   * If error is not null, check for error status.
   * If user is null, no user is logged in.
   * If user is not null, there is a logged in user.
   *
   * @param error Error for callback; will be null if no error.
   * @param user The returned User object.
   */
  public void authenticated(Error error, User user);

}
