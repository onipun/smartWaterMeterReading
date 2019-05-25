package com.example.psm.v1.database;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UploadImgRepository extends CrudRepository<UploadImg, Long> {

    Iterable<UploadImg> findById(int id);

    @Query("SELECT u FROM UploadImg u WHERE u.ownerId = :id")
    Iterable<UploadImg> fetchUserHistory(@Param("id") String id );
    
}