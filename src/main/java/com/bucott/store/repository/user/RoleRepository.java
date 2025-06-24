package com.bucott.store.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bucott.store.model.user.Authority;
import com.bucott.store.model.user.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByAuthority(Authority authority);

}
