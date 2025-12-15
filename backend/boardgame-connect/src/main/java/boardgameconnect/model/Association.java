package boardgameconnect.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "associations")
@DiscriminatorValue("ASSOCIATION")
@PrimaryKeyJoinColumn(name = "user_id")
public class Association extends User {

    @Column(nullable = false)
    private String name;
    @Column(name = "tax_code", nullable = false)
    private String taxCode;

    private String address;

    protected Association() {
    }

    public Association(Email email, String password, String name, String taxCode, String address) {
	super(email, password);
	this.name = name;
	this.taxCode = taxCode;
	this.address = address;
    }

    public String getName() {
	return name;
    }

    public String getTaxCode() {
	return taxCode;
    }

    public String getAddress() {
	return address;
    }

}
