package boardgameconnect.service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import boardgameconnect.model.UserAccount;
import io.jsonwebtoken.Claims;
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

	// Needed because the Spring Bean Lifecycle, because when the constructor runs,
	// the value of secret is null
	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
	}

	void setSecret(String secret) {
		this.secret = secret;
	}

	void setJwtExpiration(long jwtExpiration) {
		this.jwtExpiration = jwtExpiration;
	}

	Key getSecretKey() {
		return secretKey;
	}

	public String generateToken(UserAccount account) {
		return Jwts.builder().setSubject(account.getEmail().toString()).claim("role", account.getUserRole())
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(secretKey, SignatureAlgorithm.HS256).compact();
	}

	public String extractSubject(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractUserRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractSubject(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
	}
}