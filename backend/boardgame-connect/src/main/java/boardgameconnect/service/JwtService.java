package boardgameconnect.service;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import boardgameconnect.model.UserAccount;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long jwtExpiration;

	void setSecret(String secret) {
		this.secret = secret;
	}

	void setJwtExpiration(long jwtExpiration) {
		this.jwtExpiration = jwtExpiration;
	}

	Key getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(UserAccount account) {
		return Jwts.builder().setSubject(account.getEmail().toString()) // Email as token subject
				.claim("role", account.getUserRole().name()) // Add user role as claim
				.setIssuedAt(new Date()) // Token issue time
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Token expiration
				.signWith(getSigningKey(), SignatureAlgorithm.HS256) // Signing with HMAC-SHA256
				.compact();
	}
}