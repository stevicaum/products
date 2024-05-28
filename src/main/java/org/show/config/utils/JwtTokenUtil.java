package org.show.config.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

public class JwtTokenUtil implements Serializable {

  public static final String SIGNING_KEY = "stevicaArsicSigningKeyMustHave256ByesInSigningKey";


  public static String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public static Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }


  public static Boolean isTokenExpired(String token) {
    try {
      final Date expiration = getExpirationDateFromToken(token);
      return expiration.before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    }
  }

  private static Claims getAllClaimsFromToken(String token) {
    SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNING_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    return Jwts.parser()
            .verifyWith(secretKeySpec).build()
            .parseSignedClaims(token).getPayload();
  }
}