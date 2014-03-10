package com.firebase.simplelogin;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.simplelogin.enums.Error;
import com.firebase.simplelogin.enums.Provider;

class FirebaseUtils {

  public static String namespaceFromRef(Firebase ref) {
    URL xurl;
    try {
      xurl = new URL(ref.toString());
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Invalid Firebase reference: " + ref, e);
    }
    String namespace = null;
    if (xurl != null && xurl.getHost() != null) {
      String[] hostFragments = xurl.getHost().split("\\.");
      if (hostFragments != null && hostFragments.length > 1) {
        namespace = hostFragments[0];
      }
    }

    if (namespace == null || namespace.trim().length() < 1) { throw new IllegalArgumentException("Invalid Firebase reference: " + ref); }

    return namespace;
  }

  public static Provider providerForString(String provider) {
    if (provider != null) {
      try {
        return Provider.valueOf(provider.trim().toUpperCase());
      }
      catch (Exception e) {
        return Provider.INVALID;
      }
    }
    else {
      return Provider.INVALID;
    }
  }

  public static Error errorFromResponse(JSONObject response) {
    Error errorCode = Error.Unknown;
    if (response != null) {
      try {
        String codeString = response.getString("code");
        if (codeString != null) {
          if ("190".equals(codeString)) {
            errorCode = Error.BadSystemToken;
          }
          else if ("INVALID_USER".equals(codeString)) {
            errorCode = Error.UserDoesNotExist;
          }
          else if ("INVALID_PASSWORD".equals(codeString)) {
            errorCode = Error.InvalidPassword;
          }
          else if ("NO_ACCESS".equals(codeString)) {
            errorCode = Error.AccessNotGranted;
          }
          else if ("NO_ACCOUNT".equals(codeString)) {
            errorCode = Error.AccountNotFound;
          }
          else if ("AUTHENTICATION_DISABLED".equals(codeString)) {
            errorCode = Error.AuthenticationProviderNotEnabled;
          }
          else if ("INVALID_EMAIL".equals(codeString)) {
            errorCode = Error.InvalidEmail;
          }
          else if ("EMAIL_TAKEN".equals(codeString)) {
            errorCode = Error.EmailTaken;
          }
        }
      }
      catch (Exception e) {
      }
    }
    return errorCode;
  }

  public static Error errorFromFirebaseError(FirebaseError error) {
    Error errorCode = Error.Unknown;
    if (error != null) {
      switch (error.getCode()) {
      case FirebaseError.DATA_STALE:
        errorCode = Error.DataStale;
        break;
      case FirebaseError.OPERATION_FAILED:
        errorCode = Error.OperationFailed;
        break;
      case FirebaseError.PERMISSION_DENIED:
        errorCode = Error.PermissionDenied;
        break;
      case FirebaseError.DISCONNECTED:
        errorCode = Error.Disconnected;
        break;
      case FirebaseError.PREEMPTED:
        errorCode = Error.Preempted;
        break;
      case FirebaseError.EXPIRED_TOKEN:
        errorCode = Error.ExpiredToken;
        break;
      case FirebaseError.INVALID_TOKEN:
        errorCode = Error.InvalidToken;
        break;
      case FirebaseError.MAX_RETRIES:
        errorCode = Error.MaxRetries;
        break;
      case FirebaseError.OVERRIDDEN_BY_SET:
        errorCode = Error.OverriddenBySet;
        break;
      default:
        errorCode = Error.Unknown;
        break;
      }
    }
    return errorCode;
  }

  // https://gist.github.com/codebutler/2339666
  public static Map<String, Object> toMap(JSONObject object) throws JSONException {
    Map<String, Object> map = new HashMap<String, Object>();
    if (object != null) {
      @SuppressWarnings("rawtypes")
      Iterator keys = object.keys();
      while (keys.hasNext()) {
        String key = (String) keys.next();
        map.put(key, fromJson(object.get(key)));
      }
    }
    return map;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static List toList(JSONArray array) throws JSONException {
    List list = new ArrayList();
    if (array != null) {
      for (int i = 0; i < array.length(); i++) {
        list.add(fromJson(array.get(i)));
      }
    }
    return list;
  }

  private static Object fromJson(Object json) throws JSONException {
    if (json == null || json == JSONObject.NULL) {
      return null;
    }
    else if (json instanceof JSONObject) {
      return toMap((JSONObject) json);
    }
    else if (json instanceof JSONArray) {
      return toList((JSONArray) json);
    }
    else {
      return json;
    }
  }
}
