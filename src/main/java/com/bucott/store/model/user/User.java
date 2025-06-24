package com.bucott.store.model.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.bucott.store.model.address.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "users")
@NoArgsConstructor @Data
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;

    @Email @NotNull
    private String email;

    @Size(min=8, max=100) @NotNull
    private String password;

    @Size(min=2, max=50) @NotNull
    private String firstName;

    @Size(min=2, max=50) @NotNull
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Address address;

    @Size(min=10, max=15) @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits long and can start with a '+'")
    private String phoneNumber;
    private boolean expired = false;
    private boolean locked = false;
    private boolean enabled = true;

   @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return roles
        .stream()
        .map(role -> (GrantedAuthority) () -> role.getAuthority().toString())
        .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
