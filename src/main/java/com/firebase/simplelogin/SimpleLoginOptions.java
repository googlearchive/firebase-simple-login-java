package com.firebase.simplelogin;

public class SimpleLoginOptions {

  private boolean debug;

  /**
   * Simple Login Options constructor.
   */
  public SimpleLoginOptions() {
    debug = false;
  }

  /**
   * @param debug Set to true to enable debug mode (so you can see the results of Rules API operations).
   */
  public SimpleLoginOptions setDebug(boolean debug) {
    this.debug = debug;
    return this;
  }

  /**
   * @return the debug flag value
   */
  public boolean isDebug() {
    return debug;
  }

}
