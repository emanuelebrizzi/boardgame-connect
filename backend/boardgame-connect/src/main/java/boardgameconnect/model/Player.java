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

    public Player() {
    }

    public Player(UserAccount account) {
	this.account = account;
    }

    public String getId() {
	return id;
    }

    public UserAccount getAccount() {
	return account;
    }

    public void setAccount(UserAccount account) {
	this.account = account;
    }

}
