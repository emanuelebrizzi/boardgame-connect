package boardgameconnect.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "associations")
public class Association {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "account_id", referencedColumnName = "id")
	private UserAccount account;

	@Column(nullable = false)
	private String taxCode;
	@Column(nullable = false)
	private String address;

	@OneToMany(mappedBy = "association", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<GameTable> gameTables = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "association_boardgames", joinColumns = @JoinColumn(name = "association_id"), inverseJoinColumns = @JoinColumn(name = "boardgame_id"))
	private Set<Boardgame> boardgames = new HashSet<>();

	public Association() {
	}

	public Association(UserAccount account, String taxCode, String address) {
		this.account = account;
		this.taxCode = taxCode;
		this.address = address;
	}

	public String getId() {
		return id;
	}

	public UserAccount getAccount() {
		return account;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public String getAddress() {
		return address;
	}

	public Set<Boardgame> getBoardgames() {
		return boardgames;
	}

	public void setBoardgames(Set<Boardgame> boardgames) {
		this.boardgames = boardgames;
	}

	public Set<GameTable> getGameTables() {
		return gameTables;
	}

	public void addGameTable(GameTable table) {
		this.gameTables.add(table);
	}

	@Override
	public int hashCode() {
		return Objects.hash(account, address, taxCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Association other = (Association) obj;
		return Objects.equals(account, other.account) && Objects.equals(address, other.address)
				&& Objects.equals(taxCode, other.taxCode);
	}

}
