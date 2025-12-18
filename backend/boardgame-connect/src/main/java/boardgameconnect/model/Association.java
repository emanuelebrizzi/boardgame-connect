package boardgameconnect.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @Column(name = "tax_code", nullable = false)
    private String taxCode;
    private String address;

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

}
