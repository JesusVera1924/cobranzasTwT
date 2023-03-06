package com.twt.appcobranzas.utils;

import javax.mail.PasswordAuthentication;

/**
 * Created by Devecua on 21/07/2016.
 */
public class SMTPAuthenticator extends javax.mail.Authenticator {
    String user;
    String pwd;
    public SMTPAuthenticator(String user,String pwd) {
        this.user=user;
        this.pwd =pwd;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user,pwd);
    }

}
