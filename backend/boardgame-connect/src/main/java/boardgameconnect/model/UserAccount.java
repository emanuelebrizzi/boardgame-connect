package boardgameconnect.model;

import static jakarta.persistence.GenerationType.UUID;

import java.util.Objects;

import boardgameconnect.mapper.EmailConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = UUID)
    private String id;

    @Column(nullable = false, unique = true)
    @Convert(converter = EmailConverter.class)
    private Email email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    protected UserAccount() {
    }

    public UserAccount(Email email, String password, String name, UserRole role) {
	this.email = email;
	this.password = password;
	this.name = name;
	this.role = role;
    }

    public String getId() {
	return id;
    }

    public Email getEmail() {
	return email;
    }

    public void setEmail(Email email) {
	this.email = email;
    }

    public String getPassword() {
	return password;
    }

    public UserRole getUserRole() {
	return role;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public int hashCode() {
	return Objects.hash(email);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	UserAccount other = (UserAccount) obj;
	return Objects.equals(email, other.email);
    }

}
