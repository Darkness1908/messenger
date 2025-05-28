package com.relex.messenger.repository;

import com.relex.messenger.entity.Group;
import com.relex.messenger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByIdAndAdministrator(Long groupId, User user);
}