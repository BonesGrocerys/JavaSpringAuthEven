package com.boots.repository;

import com.boots.entity.Privileges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privileges, Long> {
    @Query("SELECT p.id FROM Privileges p WHERE p.name = :name")
    Long findIdPrivilegeByName(@Param("name") String name);
}
