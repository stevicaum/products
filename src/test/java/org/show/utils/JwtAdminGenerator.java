package org.show.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Date;

import static org.show.config.security.UserProvider.ADMIN_DETAILS;
import static org.show.config.utils.JwtTokenUtil.SIGNING_KEY;

public class JwtAdminGenerator implements Serializable {

  public static void main(String[] args) {
    System.out.println(JwtAdminGenerator.generateToken(ADMIN_DETAILS, 3600));
  }


  public static String generateToken(UserDetails user, long validitySeconds) {
    Claims claims = Jwts.claims().subject(user.getUsername()).add("authorities", user.getAuthorities()).build();
    return Jwts.builder()
            .setClaims(claims)
            .setIssuer("http://products.com")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + validitySeconds * 1000))
            .signWith(SignatureAlgorithm.HS256, SIGNING_KEY.getBytes())
            .compact();
  }
}