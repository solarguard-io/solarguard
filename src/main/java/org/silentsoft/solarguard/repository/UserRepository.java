package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<UserEntity> findAllByIsDeletedFalse();

    Optional<UserEntity> findByIdAndIsDeletedFalse(long id);

    Optional<UserEntity> findByUsernameOrEmailAndIsDeletedFalse(String username, String email);

}
