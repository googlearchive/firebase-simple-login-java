package com.firebase.simplelogin;

import org.json.JSONObject;

import com.firebase.simplelogin.enums.Error;

interface RequestHandler {

  public void handle(Error error, JSONObject data);

}
