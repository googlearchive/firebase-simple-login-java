package com.firebase.simplelogin;

import org.json.JSONObject;

interface RequestHandler {

  public void handle(FirebaseSimpleLoginError error, JSONObject data);

}
