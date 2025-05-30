package com.bucott.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bucott.store.model.user.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByAuthority(String authority);

}
