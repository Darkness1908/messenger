package com.relex.messenger.repository;

import com.relex.messenger.entity.User;
import com.relex.messenger.entity.UserUser;
import com.relex.messenger.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserUserRepository extends JpaRepository<UserUser, Long> {
    @Query("SELECT uu.secondUser FROM UserUser uu WHERE uu.firstUser.id = :userId AND uu.userStatus = 'FRIEND'")
    List<User> findInitiatedFriendshipUsersByUserId(@Param("userId") Long userId);

    @Query("SELECT uu.firstUser FROM UserUser uu WHERE uu.secondUser.id = :userId AND uu.userStatus = 'FRIEND'")
    List<User> findConfirmedFriendshipUsersByUserId(@Param("userId") Long userId);

    boolean existsByFirstUserIdAndSecondUserIdAndUserStatus(Long firstUserId,
                                                            Long secondUserId, UserStatus userStatus);

    UserUser findByFirstUserIdAndSecondUserId(Long firstUserId, Long secondUserId);
}