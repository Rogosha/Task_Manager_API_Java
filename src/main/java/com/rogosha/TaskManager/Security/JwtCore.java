package com.rogosha.TaskManager.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtCore {
    @Value("${testing.app.secret}")
    private String secret;

    @Value("${testing.app.lifetime}")
    private Integer lifetime;

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userDetails.getId())
                .claim("role", userDetails.getRole().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date( (new Date()).getTime() + lifetime ))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getLoginFormJwt(String token){
        return Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody().getSubject();
    }
}
