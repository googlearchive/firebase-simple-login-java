package com.firebase.simplelogin;

import com.firebase.simplelogin.enums.Error;

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
   * @param error Error for callback; will be null if no error.
   * @param success Success status of callback.
   */
  public void completed(Error error, boolean success);

}
