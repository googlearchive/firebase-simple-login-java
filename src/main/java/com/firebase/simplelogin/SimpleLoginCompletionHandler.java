package com.firebase.simplelogin;

/**
 * Handler for completion callbacks.
 *
 * @author Firebase
 *
 */
public interface SimpleLoginCompletionHandler {

  /**
   * Method called when handler is invoked.
   *
   * If error is not null, check for error status.
   *
   * @param error FirebaseSimpleLoginError for callback; will be null if no error.
   * @param success Success status of callback.
   */
  public void completed(FirebaseSimpleLoginError error, boolean success);

}
