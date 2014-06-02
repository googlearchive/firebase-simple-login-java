package com.firebase.simplelogin;

import com.firebase.client.FirebaseError;
import com.firebase.simplelogin.enums.FirebaseSimpleLoginErrorCode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Instances of FirebaseSimpleLoginError are passed to callbacks when an operation failed.
 * They contain a description of the specific error that occurred.
 */
public class FirebaseSimpleLoginError {

  private FirebaseSimpleLoginErrorCode code;
  private String message;

  private static final Map<FirebaseSimpleLoginErrorCode, String> errorReasons;
  static {
    errorReasons = new HashMap<FirebaseSimpleLoginErrorCode, String>();
    errorReasons.put(FirebaseSimpleLoginErrorCode.DataStale, "The transaction needs to be run again with current data");
    errorReasons.put(FirebaseSimpleLoginErrorCode.OperationFailed, "The server indicated that this operation failed");
    errorReasons.put(FirebaseSimpleLoginErrorCode.PermissionDenied, "This client does not have permission to perform this operation");
    errorReasons.put(FirebaseSimpleLoginErrorCode.Disconnected, "The operation had to be aborted due to a network disconnect");
    errorReasons.put(FirebaseSimpleLoginErrorCode.Preempted, "The active or pending auth credentials were superseded by another call to auth");
    errorReasons.put(FirebaseSimpleLoginErrorCode.ExpiredToken, "The supplied auth token has expired");
    errorReasons.put(FirebaseSimpleLoginErrorCode.InvalidToken, "The supplied auth token was invalid");
    errorReasons.put(FirebaseSimpleLoginErrorCode.MaxRetries, "The transaction had too many retries");
    errorReasons.put(FirebaseSimpleLoginErrorCode.OverriddenBySet, "The transaction was overridden by a subsequent set");
    errorReasons.put(FirebaseSimpleLoginErrorCode.Unknown, "An unknown error occurred");
  }

  private FirebaseSimpleLoginError(FirebaseSimpleLoginErrorCode code, String message) {
    this.code = code;
    this.message = message;
  }

  /**
   * @return One of the defined status codes, depending on the error
   */
  public FirebaseSimpleLoginErrorCode getCode() {
    return code;
  }

  /**
   * @return A human-readable description of the error
   */
  public String getMessage() {
    return message;
  }


  public static FirebaseSimpleLoginError errorFromCode(FirebaseSimpleLoginErrorCode errorCode) {
    String message = "";
    if (errorCode != null && errorReasons.containsKey(errorCode)) {
      message = errorReasons.get(errorCode);
    }
    return new FirebaseSimpleLoginError(errorCode, message);
  }

  public static FirebaseSimpleLoginError errorFromResponse(JSONObject response) {
    FirebaseSimpleLoginErrorCode errorCode = FirebaseSimpleLoginErrorCode.Unknown;
    String message = "";

    if (response != null) {
      try {
        String codeString = response.getString("code");
        if (codeString != null) {
          if ("190".equals(codeString)) {
            errorCode = FirebaseSimpleLoginErrorCode.BadSystemToken;
          }
          else if ("INVALID_USER".equals(codeString)) {
            errorCode = FirebaseSimpleLoginErrorCode.UserDoesNotExist;
          }
          else if ("INVALID_PASSWORD".equals(codeString)) {
            errorCode = FirebaseSimpleLoginErrorCode.InvalidPassword;
          }
          else if ("NO_ACCESS".equals(codeString)) {
            errorCode = FirebaseSimpleLoginErrorCode.AccessNotGranted;
          }
          else if ("NO_ACCOUNT".equals(codeString)) {
            errorCode = FirebaseSimpleLoginErrorCode.AccountNotFound;
          }
          else if ("AUTHENTICATION_DISABLED".equals(codeString)) {
            errorCode = FirebaseSimpleLoginErrorCode.AuthenticationProviderNotEnabled;
          }
          else if ("INVALID_EMAIL".equals(codeString)) {
            errorCode = FirebaseSimpleLoginErrorCode.InvalidEmail;
          }
          else if ("EMAIL_TAKEN".equals(codeString)) {
            errorCode = FirebaseSimpleLoginErrorCode.EmailTaken;
          }
        }
      }
      catch (JSONException e) {
        // Invalid response. Default 'Unknown' error code will be used.
        e.printStackTrace();
      }
    }

    if (errorCode != null && errorReasons.containsKey(errorCode)) {
      message = errorReasons.get(errorCode);
    }
    return new FirebaseSimpleLoginError(errorCode, message);
  }

  public static FirebaseSimpleLoginError errorFromFirebaseError(FirebaseError error) {
    FirebaseSimpleLoginErrorCode errorCode = FirebaseSimpleLoginErrorCode.Unknown;
    String message = "";

    if (error != null) {
      switch (error.getCode()) {
        case FirebaseError.DATA_STALE:
          errorCode = FirebaseSimpleLoginErrorCode.DataStale;
          break;
        case FirebaseError.OPERATION_FAILED:
          errorCode = FirebaseSimpleLoginErrorCode.OperationFailed;
          break;
        case FirebaseError.PERMISSION_DENIED:
          errorCode = FirebaseSimpleLoginErrorCode.PermissionDenied;
          break;
        case FirebaseError.DISCONNECTED:
          errorCode = FirebaseSimpleLoginErrorCode.Disconnected;
          break;
        case FirebaseError.PREEMPTED:
          errorCode = FirebaseSimpleLoginErrorCode.Preempted;
          break;
        case FirebaseError.EXPIRED_TOKEN:
          errorCode = FirebaseSimpleLoginErrorCode.ExpiredToken;
          break;
        case FirebaseError.INVALID_TOKEN:
          errorCode = FirebaseSimpleLoginErrorCode.InvalidToken;
          break;
        case FirebaseError.MAX_RETRIES:
          errorCode = FirebaseSimpleLoginErrorCode.MaxRetries;
          break;
        case FirebaseError.OVERRIDDEN_BY_SET:
          errorCode = FirebaseSimpleLoginErrorCode.OverriddenBySet;
          break;
        default:
          errorCode = FirebaseSimpleLoginErrorCode.Unknown;
          break;
      }
    }

    if (errorCode != null && errorReasons.containsKey(errorCode)) {
      message = errorReasons.get(errorCode);
    }
    return new FirebaseSimpleLoginError(errorCode, message);
  }
}
