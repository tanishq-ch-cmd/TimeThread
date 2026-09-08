package com.tanishqchcmd.timethread;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    // Finds the full conversation between two specific users and orders it by time
    @Query("SELECT d FROM DirectMessage d WHERE (d.sender = :user1 AND d.receiver = :user2) OR (d.sender = :user2 AND d.receiver = :user1) ORDER BY d.timestamp ASC")
    List<DirectMessage> findConversation(@Param("user1") Student user1, @Param("user2") Student user2);
}