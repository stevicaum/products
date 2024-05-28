package org.show.utils;

import java.io.Serializable;

import static org.show.config.security.UserProvider.USER_DETAILS;

public class JwtUserGenerator implements Serializable {

  public static void main(String[] args) {
    System.out.println(JwtAdminGenerator.generateToken(USER_DETAILS, 3600));
  }

}