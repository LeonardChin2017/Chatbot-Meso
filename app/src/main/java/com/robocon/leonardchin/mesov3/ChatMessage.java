package com.robocon.leonardchin.mesov3;

/**
 * Created by adnansahinovic on 9/26/17.
 */

public class ChatMessage {

  public String msgText;
  public String msgUser;
  public String photoUrl;

  public ChatMessage(String msgText, String msgUser, String photoUrl) {
    this.msgUser = msgUser;
    this.msgText = msgText;
    this.photoUrl = photoUrl;
  }

  public ChatMessage() {

  }

  public String getMsgText() {
    return msgText;
  }

  public void setMsgText(String msgText) {
    this.msgText = msgText;
  }

  public String getMsgUser() {
    return msgUser;
  }

  public void setMsgUser(String msgUser) {
    this.msgUser = msgUser;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }
}