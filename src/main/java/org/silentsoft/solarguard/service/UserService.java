package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.PersonalTokenEntity;
import org.silentsoft.solarguard.entity.UserEntity;
import org.silentsoft.solarguard.entity.UserRole;
import org.silentsoft.solarguard.exception.UserNotFoundException;
import org.silentsoft.solarguard.repository.PersonalTokenRepository;
import org.silentsoft.solarguard.repository.UserRepository;
import org.silentsoft.solarguard.util.JwtTokenUtil;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.PersonalTokenPostVO;
import org.silentsoft.solarguard.vo.UserPatchVO;
import org.silentsoft.solarguard.vo.UserPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonalTokenRepository personalTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public List<UserEntity> getUsers() {
        List<UserEntity> users = userRepository.findAllByIsDeletedFalse();
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
        return userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() -> new UserNotFoundException(String.format("The user '%d' does not exist.", userId)));
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
        UserUtil.checkIdentity(userId);

        UserEntity entity = findUser(userId);
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
        if (userId == UserUtil.getId()) {
            throw new IllegalArgumentException("You cannot delete your own account.");
        }

        UserEntity entity = findUser(userId);
        entity.setIsDeleted(true);
        entity.setUpdatedBy(UserUtil.getId());
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(entity);
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    public List<PersonalTokenEntity> getPersonalTokens(long userId) {
        UserUtil.checkIdentity(userId);

        findUser(userId);

        return personalTokenRepository.findAllByUserId(userId);
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    public PersonalTokenEntity createPersonalToken(long userId, PersonalTokenPostVO personalToken) {
        UserUtil.checkIdentity(userId);

        UserEntity user = findUser(userId);

        if (!StringUtils.hasLength(personalToken.getNote())) {
            throw new IllegalArgumentException("The note of personal token cannot be empty.");
        }

        PersonalTokenEntity entity = new PersonalTokenEntity();
        entity.setUser(user);
        entity.setNote(personalToken.getNote());
        if (personalToken.getExpiredAt() == null) {
            entity.setAccessToken(jwtTokenUtil.generatePersonalAccessToken(userId));
        } else {
            entity.setAccessToken(jwtTokenUtil.generatePersonalAccessToken(userId, TimeUnit.DAYS.toMillis(personalToken.getExpiredAt().toEpochDay() - LocalDate.now().toEpochDay())));
            entity.setExpiredAt(new Timestamp(TimeUnit.MILLISECONDS.convert(personalToken.getExpiredAt().toEpochDay(), TimeUnit.DAYS)));
        }
        entity.setCreatedBy(UserUtil.getId());
        entity.setUpdatedBy(UserUtil.getId());
        entity = personalTokenRepository.save(entity);

        return entity;
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

    private UserEntity hidePassword(UserEntity userEntity) {
        if (userEntity != null) {
            userEntity.setPassword(null);
        }
        return userEntity;
    }

}
