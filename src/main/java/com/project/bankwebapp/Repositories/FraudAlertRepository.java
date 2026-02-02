package com.project.bankwebapp.Repositories;


import com.project.bankwebapp.Entities.FraudAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlertEntity,Long> {

    @Query("SELECT f FROM FraudAlertEntity f WHERE f.transaction.user.user_id = :userId")
    List<FraudAlertEntity> findAllByUserId(@Param("userId") UUID userId);
}
