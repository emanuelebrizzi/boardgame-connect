package boardgameconnect.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private UserAccount account;

    private String username;

    public Player(UserAccount account, String username) {
	if ((username == null)) {
	    throw new IllegalArgumentException("Username cannnot be null");
	}
	this.account = account;
	this.username = username;
    }

    public String getUsername() {
	return username;
    }

}
