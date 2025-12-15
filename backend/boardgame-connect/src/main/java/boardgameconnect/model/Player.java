package boardgameconnect.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
@DiscriminatorValue("PLAYER")
@PrimaryKeyJoinColumn(name = "user_id")
public class Player extends User {

    @Column(nullable = false)
    private String username;

    public Player(Email email, String password, String username) {
	super(email, password);
	if ((username == null)) {
	    throw new IllegalArgumentException("Username cannnot be null");
	}
	this.username = username;
    }

    public String getUsername() {
	return username;
    }

}
