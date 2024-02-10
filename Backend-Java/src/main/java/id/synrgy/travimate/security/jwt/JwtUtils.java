package id.synrgy.travimate.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Key;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${travimate.app.jwtSecret}")
    private String jwtSecret;

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));
    }
    public String getUsername(String jwt) {
//        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();
//        return claims.get("id").toString();
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes("UTF-8"))
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody()
                    .get("id")
                    .toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

//        return  Jwts.parserBuilder()
//                .setSigningKey(key()).build()
//                .parseClaimsJws(jwt)
//                .getBody()
//                .getSubject();
    }

    public boolean validateJwtToken(String token, HttpServletResponse response) {
        try {
            Jwts.parser().setSigningKey(jwtSecret.getBytes("UTF-8")).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            // Invalid signature
            handleJwtError(response, "Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            // Invalid token
            handleJwtError(response, "Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            // Expired token
            handleJwtError(response, "JWT token has expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            // Unsupported token
            handleJwtError(response, "Unsupported JWT token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Token is empty or null
            handleJwtError(response, "JWT token is empty or null: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private void handleJwtError(HttpServletResponse response, String errorMessage) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.print("{\"error\": \"" + errorMessage + "\"}");
        } catch (IOException e) {
            logger.error("Error writing to response: {}", e.getMessage());
        }
    }


}
