package br.coop.integrada.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration.secret}")
    private Long expirationSecret;

    @Value("${jwt.expiration.refresh}")
    private Long expirationRefresh;

    public Map<String, String> gerarTokens(String subject, String issuer, List<String> perfis) {
        String accessToken = JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationSecret * 60 * 1000))
                .withIssuer(issuer)
                .withClaim("perfilUsuario", perfis)
                .sign(getAlgorithm());

        String refreshToken = JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationRefresh * 60 * 1000))
                .withIssuer(issuer)
                .withClaim("perfilUsuario", perfis)
                .sign(getAlgorithm());
        
        Date date = new Date(System.currentTimeMillis() + expirationSecret * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        String valid = simpleDateFormat.format(date);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("expirationToken", valid);
        return tokens;
    }

    public Boolean tokenValido(String token) {
        DecodedJWT decodedJWT = getDecodeJwt(token);

        if(decodedJWT != null) {
            String username = decodedJWT.getSubject();
            Date expirationDate = decodedJWT.getExpiresAt();
            Date now = new Date(System.currentTimeMillis());

            if (username != null && expirationDate != null && now.before(expirationDate)) {
                return true;
            }
        }

        return false;
    }

    public String getUsername(String token) {
        DecodedJWT decodedJWT = getDecodeJwt(token);

        if(decodedJWT != null) {
            return decodedJWT.getSubject();
        }

        return null;
    }

    public String[] getPerfis(String token) {
        DecodedJWT decodedJWT = getDecodeJwt(token);

        if(decodedJWT != null) {
            return decodedJWT.getClaim("perfilUsuario").asArray(String.class);
        }

        return null;
    }

    private DecodedJWT getDecodeJwt(String token) {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        return verifier.verify(token);
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC512(secret);
    }
}
