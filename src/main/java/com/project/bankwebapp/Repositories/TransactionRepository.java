package com.project.bankwebapp.Repositories;

import com.project.bankwebapp.Entities.TransactionEntity;
import com.project.bankwebapp.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.user.user_id = :userId AND t.transaction_id = :transactionId")
    Optional<TransactionEntity> findByUserIdAndTransactionId(@Param("userId") UUID userId, @Param("transactionId") Long transactionId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.user.user_id = :userId")
    List<TransactionEntity> findAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM TransactionEntity t WHERE t.transaction_id = :transactionId AND t.user.user_id = :userId")
    void deleteByUserId(@Param("transactionId") Long transactionId, @Param("userId") UUID userId);

    @Query("SELECT COUNT(t) > 0 FROM TransactionEntity t WHERE t.user.user_id = :userId AND t.transaction_id = :transactionId")
    boolean existsByUserIdAndId(@Param("userId") UUID userId, @Param("transactionId") long transactionId);

    long countByUserAndTimestampAfter(UserEntity user, Instant timestamp);

    //@Modifying(clearAutomatically = true) // ensures its not pulling from cache
    //@Query("UPDATE TransactionEntity t set t.status ='status-clean' where t.transaction_id= :transactionId")
    //int statusResolved(@Param("transactionId") Long transactionId);
}