package com.relex.messenger.repository;

import com.relex.messenger.entity.Group;
import com.relex.messenger.entity.User;
import com.relex.messenger.entity.UserGroup;
import com.relex.messenger.enums.ChatStatus;
import com.relex.messenger.enums.GroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    @Query("SELECT ug.group FROM UserGroup ug WHERE ug.user.id = :userId AND ug.groupStatus = :groupStatus")
    List<Group> findGroupsByUserIdAndStatus(@Param("userId") Long userId,
                                            @Param("groupStatus") GroupStatus groupStatus);

    @Query("SELECT uc.user FROM UserGroup uc WHERE uc.group.id = :groupId AND uc.groupStatus = :groupStatus")
    List<User> findUsersByGroupIdAndStatus(@Param("groupId") Long groupId,
                                           @Param("groupStatus") GroupStatus groupStatus);

    UserGroup getByUserIdAndGroupId(Long userId, Long groupId);

    boolean existsByUserIdAndGroupIdAndGroupStatus(Long userId, Long groupId, GroupStatus groupStatus);
}
