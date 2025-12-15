package boardgameconnect.model;

import static jakarta.persistence.GenerationType.UUID;
import static jakarta.persistence.InheritanceType.JOINED;

import java.util.Objects;

import boardgameconnect.mapper.EmailConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "user_type")
public abstract class User {
    @Id
    @GeneratedValue(strategy = UUID)
    private String id;

    @Column(nullable = false, unique = true)
    @Convert(converter = EmailConverter.class)
    private Email email;

    @Column(nullable = false)
    private String password;

    protected User() {
    }

    public User(Email email, String password) {
	this.email = email;
	this.password = password;
	if (password.isBlank()) {
	    throw new IllegalArgumentException("Password cannot be blank");
	}
    }

    public String getId() {
	return id;
    }

    public Email getEmail() {
	return email;
    }

    public String getPassword() {
	return password;
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
	User other = (User) obj;
	return Objects.equals(email, other.email);
    }

}
