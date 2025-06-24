package com.bucott.store.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.bucott.store.exception.user.UserNotFoundException;

public interface UserService {
    
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    
    UserDetails loadUserByEmail(String email) throws UsernameNotFoundException;
    
    UserDetails loadUserByUsernameOrEmail(String identifier) throws UserNotFoundException;
}