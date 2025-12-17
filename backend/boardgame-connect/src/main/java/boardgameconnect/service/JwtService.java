package boardgameconnect.service;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import boardgameconnect.model.Email;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

	private static final long EXPIRATION_TIME = 1000 * 60 * 60;

	@Value("${jwt.secret}")
	private String secret;

	private Key secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(Email email) {
		return Jwts.builder().setSubject(email.getEmail()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(secretKey, SignatureAlgorithm.HS256).compact();
	
	}
}