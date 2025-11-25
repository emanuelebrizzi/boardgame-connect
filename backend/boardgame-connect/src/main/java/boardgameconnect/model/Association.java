package boardgameconnect.model;

public class Association extends User{

	private String name;
	private String taxCode;
	private String address;
		
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
