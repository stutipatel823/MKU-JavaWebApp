package com.mku.business;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map.Entry;

public class Authenticate {

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final Key secretKey;

    public Authenticate() {
        // Generate a secure key for HS256
        this.secretKey = Keys.secretKeyFor(signatureAlgorithm);
    }

    // Create JWT
    public String createJWT(String issuer, String subject, long ttlMillis) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(secretKey, signatureAlgorithm);

        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    // Verify JWT
    public Entry<Boolean, String> verify(String jwt) throws UnsupportedEncodingException {
        Jws<Claims> jws = null;
        String username = "";
        try {
            jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);

            username = jws.getBody().getSubject();
        } catch (JwtException ex) {
            System.out.println("Invalid JWT: " + ex.getMessage());
        }

        if (jws == null || jws.getBody().getExpiration().before(new Date())) {
            return new AbstractMap.SimpleEntry<>(false, "");
        }

        return new AbstractMap.SimpleEntry<>(true, username);
    }
}
