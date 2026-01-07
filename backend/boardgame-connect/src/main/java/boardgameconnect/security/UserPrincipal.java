package boardgameconnect.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import boardgameconnect.model.UserAccount;

public class UserPrincipal implements UserDetails {

	private static final long serialVersionUID = 7054338040507455752L;

	private final UserAccount userAccount;

	public UserPrincipal(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	@Override
	public String getUsername() {
		return userAccount.getEmail().toString();
	}

	@Override
	public String getPassword() {
		return userAccount.getPassword();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(userAccount.getUserRole().name()));
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}