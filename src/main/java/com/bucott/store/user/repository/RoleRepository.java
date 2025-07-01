package com.bucott.store.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bucott.store.user.model.Authority;
import com.bucott.store.user.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByAuthority(Authority authority);

}
