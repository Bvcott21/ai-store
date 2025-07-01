package com.bucott.store.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.bucott.store.user.exception.UserNotFoundException;

public interface UserService {
    
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    
    UserDetails loadUserByEmail(String email) throws UsernameNotFoundException;
    
    UserDetails loadUserByUsernameOrEmail(String identifier) throws UserNotFoundException;
}