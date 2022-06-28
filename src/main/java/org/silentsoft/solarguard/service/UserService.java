package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.UserEntity;
import org.silentsoft.solarguard.entity.UserRole;
import org.silentsoft.solarguard.exception.UserNotFoundException;
import org.silentsoft.solarguard.repository.UserRepository;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.UserPatchVO;
import org.silentsoft.solarguard.vo.UserPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public List<UserEntity> getUsers() {
        List<UserEntity> users = userRepository.findAll();
        for (UserEntity user : users) {
            hidePassword(user);
        }
        return users;
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public UserEntity getUser(long userId) {
        return hidePassword(findUser(userId));
    }

    private UserEntity findUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("The user '%d' does not exist.", userId)));
    }

    @PreAuthorize(Authority.Has.Admin)
    public UserEntity createUser(UserPostVO user) {
        String username = user.getUsername();
        checkUsername(username);

        String password = user.getPassword();
        checkPassword(password);

        String email = user.getEmail();
        checkEmail(email);

        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setEmail(email);
        if (StringUtils.hasLength(password)) {
            entity.setPassword(passwordEncoder.encode(password));
            entity.setIsTemporaryPassword(false);
        } else {
            entity.setIsTemporaryPassword(true);
        }
        entity.setIsDeleted(false);
        entity.setRole(UserRole.USER);
        entity.setCreatedBy(UserUtil.getId());
        entity.setUpdatedBy(UserUtil.getId());
        entity = userRepository.save(entity);

        return hidePassword(entity);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public UserEntity patchUser(long userId, UserPatchVO user) {
        if (UserUtil.getId() != userId) {
            UserUtil.checkAdminAuthority();
        }

        UserEntity entity = findUser(userId);
        if (entity.getIsDeleted()) {
            UserUtil.checkAdminAuthority();
        }

        if (StringUtils.hasLength(user.getUsername())) {
            checkUsername(user.getUsername());

            entity.setUsername(user.getUsername());
        }
        if (StringUtils.hasLength(user.getEmail())) {
            checkEmail(user.getEmail());

            entity.setEmail(user.getEmail());
        }
        if (StringUtils.hasLength(user.getPassword())) {
            checkPassword(user.getPassword());

            entity.setPassword(passwordEncoder.encode(user.getPassword()));
            entity.setIsTemporaryPassword(false);
        }
        if (user.getRole() != null) {
            if (UserRole.ADMIN.equals(user.getRole())) {
                UserUtil.checkAdminAuthority();
            }
            entity.setRole(user.getRole());
        }
        entity.setUpdatedBy(UserUtil.getId());
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        entity = userRepository.save(entity);

        return hidePassword(entity);
    }

    @PreAuthorize(Authority.Has.Admin)
    public void deleteUser(long userId) {
        UserEntity entity = findUser(userId);
        checkUserBeforeDelete(entity);
        entity.setIsDeleted(true);
        entity.setUpdatedBy(UserUtil.getId());
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(entity);
    }

    private void checkUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username required.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }
    }

    private void checkPassword(String password) {
        if (StringUtils.hasLength(password) && password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
    }

    private void checkEmail(String email) {
        if (StringUtils.hasLength(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists.");
        }
    }

    private void checkUserBeforeDelete(UserEntity userEntity) {
        if (userEntity.getId() == UserUtil.getId()) {
            throw new IllegalArgumentException("You cannot delete your own account.");
        }

        if (userEntity.getIsDeleted()) {
            throw new IllegalArgumentException("User is already deleted.");
        }
    }

    private UserEntity hidePassword(UserEntity userEntity) {
        if (userEntity != null) {
            userEntity.setPassword(null);
        }
        return userEntity;
    }

}
