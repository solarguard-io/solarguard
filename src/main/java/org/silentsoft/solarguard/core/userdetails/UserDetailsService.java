package org.silentsoft.solarguard.core.userdetails;

import org.silentsoft.solarguard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new org.silentsoft.solarguard.core.userdetails.UserDetails(
                userRepository.findByUsernameOrEmail(username, username)
                        .orElseThrow(() -> new UsernameNotFoundException("404 - User Not Found"))
        );
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        return new org.silentsoft.solarguard.core.userdetails.UserDetails(
                userRepository.findById(id)
                        .orElseThrow(() -> new UsernameNotFoundException("404 - User Not Found"))
        );
    }

}
