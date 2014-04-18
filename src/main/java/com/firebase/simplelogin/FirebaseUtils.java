package com.firebase.simplelogin;

import com.firebase.client.Firebase;
import com.firebase.simplelogin.enums.Provider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.*;

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
