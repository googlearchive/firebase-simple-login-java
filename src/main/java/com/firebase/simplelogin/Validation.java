package com.firebase.simplelogin;

/**
 * Validation utility methods.
 */
class Validation {

  /**
   * Check if an email is valid: not null, not empty, and is generally valid.
   *
   * @param email
   * @return
   */
  static public boolean isValidEmail(String email) {
    return email != null && email.trim().length() > 0 && email.indexOf("@") > 0;
  }

  /**
   * Check if a password is valid; not null and not empty.
   * @param password
   * @return
   */
  static public boolean isValidPassword(String password) {
    return password != null && password.trim().length() > 0;
  }

}
