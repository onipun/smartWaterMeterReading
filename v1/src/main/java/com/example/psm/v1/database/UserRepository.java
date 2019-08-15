package com.example.psm.v1.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT name FROM User u WHERE u.identityCard = :id")
    String getUserInfo(@Param("id") String id );

    @Query("SELECT u FROM User u WHERE u.identityCard = :id")
    Iterable<User> getOwnerAddress(@Param("id") String id );
}