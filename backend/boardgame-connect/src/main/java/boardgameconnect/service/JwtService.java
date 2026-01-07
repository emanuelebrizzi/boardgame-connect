package boardgameconnect.service;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import boardgameconnect.model.UserAccount;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long jwtExpiration;

	private Key secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(UserAccount account) {
		return Jwts.builder().setSubject(account.getEmail().toString()).claim("role", account.getUserRole())
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(secretKey, SignatureAlgorithm.HS256).compact();
	}
}