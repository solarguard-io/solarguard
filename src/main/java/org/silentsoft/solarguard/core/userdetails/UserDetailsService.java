package org.silentsoft.solarguard.core.userdetails;

import org.silentsoft.solarguard.entity.UserEntity;
import org.silentsoft.solarguard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsernameOrEmailAndIsDeletedFalse(username, username).orElse(null);
        if (userEntity == null) {
            throw new UsernameNotFoundException("404 - User Not Found");
        }

        return new org.silentsoft.solarguard.core.userdetails.UserDetails(userEntity);
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByIdAndIsDeletedFalse(id).orElse(null);
        if (userEntity == null) {
            throw new UsernameNotFoundException("404 - User Not Found");
        }

        return new org.silentsoft.solarguard.core.userdetails.UserDetails(userEntity);
    }

}
