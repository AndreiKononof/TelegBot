package com.skillbox.cryptobot.repository;

import com.skillbox.cryptobot.model.Subscribers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscribers, Integer> {
    @Query("from Subscribers where idClient= :id ")
    List<Subscribers> findByWhereIdClient (@Param("id") Long idClient);
}
